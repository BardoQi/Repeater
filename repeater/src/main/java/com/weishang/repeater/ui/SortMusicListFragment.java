package com.weishang.repeater.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.AllMusicAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.utils.RepeatUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.ListIndicator;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/22.
 * 所有音乐列表
 */
@com.weishang.repeater.annotation.Toolbar(title = R.string.local_res)
public class SortMusicListFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String PARAMS_LIST_ID = "list_id";
    public static final String PARAMS_TITLE = "title";
    @ID(id = R.id.toolbar)
    private Toolbar mToolBar;
    @ID(id = R.id.lv_list)
    private ListView mListView;
    @ID(id = R.id.li_list_indicator)
    private ListIndicator mIndicator;
    @ID(id = R.id.tv_indicator_text)
    private TextView mIndicatorText;
    private AllMusicAdapter mAdapter;
    private String mTitle;
    private int mListId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mTitle = arguments.getString(PARAMS_TITLE);
            mListId = arguments.getInt(PARAMS_LIST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allmusic_list, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this, mToolBar);
        mIndicator.setData(App.getStringArray(R.array.words));
        ViewCompat.setAlpha(mIndicatorText, 0f);
        ViewCompat.setScaleX(mIndicatorText, 0.8f);
        ViewCompat.setScaleY(mIndicatorText, 0.8f);
        mIndicator.setOnIndicatorListener(new ListIndicator.OnIndicatorListener() {
            @Override
            public void onPress() {
                ViewCompat.animate(mIndicatorText).alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
            }

            @Override
            public void onSelect(String word, int position) {
                mIndicatorText.setText(word);
                mListView.setSelection(mAdapter.getSelectPosition(position));
            }

            @Override
            public void onCancel() {
                ViewCompat.animate(mIndicatorText).alpha(0f).scaleX(0f).scaleY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
            }
        });
        ArrayList<Music> datas = MyDb.getDatas(DbTable.FILE_URI, new Music(), DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(mListId)}, "word ASC");
        if (null != datas && !datas.isEmpty()) {
            mListView.setAdapter(mAdapter = new AllMusicAdapter(getActivity(), datas));
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == i) {
                        //滑动结束时消失
                        ViewCompat.animate(mIndicatorText).alpha(0f).scaleX(0f).scaleY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
                    } else if (AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == i) {
                        //按下滑动时展示
                        ViewCompat.animate(mIndicatorText).alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                    //根据快速滑动角位置,设置左边指示位置,使用二分查找
                    ArrayList<Integer> selectPositions = mAdapter.getSelectPositions();
                    Integer[] positions = new Integer[selectPositions.size()];
                    selectPositions.toArray(positions);
                    int selectPosition = getSelectPosition(positions, i);
                    mIndicator.setSelectPosition(selectPosition);
                    mIndicatorText.setText(mIndicator.getWrodByPosition(selectPosition));
                }
            });
            mListView.setOnItemClickListener(this);
        }
    }

    /**
     * 使用二分查找法,根据firstVisiblePosition找到SelectPositions中的位置
     *
     * @return
     */
    public static int getSelectPosition(Integer[] positions, int firstVisiblePosition) {
        int start = 0, end = positions.length;
        while (end - start > 1) {
            // 中间位置
            int middle = (start + end) >> 1;
            // 中值
            int middleValue = positions[middle];
            if (firstVisiblePosition > middleValue) {
                start = middle;
            } else if (firstVisiblePosition < middleValue) {
                end = middle;
            } else {
                start = middle;
                break;
            }
        }
        return start;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        RepeatUtils.playMusic(getActivity(), position, mAdapter.getDatas());
    }


    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }
}
