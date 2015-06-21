package com.weishang.repeater.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.weishang.repeater.R;
import com.weishang.repeater.adapter.AllMusicAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.utils.RepeatUtils;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/22.
 * 最近使用,与最新添加列表
 */
@com.weishang.repeater.annotation.Toolbar()
public class UseMusicListFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String PARAMS_LIST_ID = "list_id";
    public static final String PARAMS_TITLE = "title";
    @ID(id = R.id.toolbar)
    private Toolbar mToolBar;
    @ID(id = R.id.list)
    private ListView mListView;
    private AllMusicAdapter mAdapter;
    private String mTitle;
    private int mListId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mTitle = arguments.getString(PARAMS_TITLE);
            mListId = arguments.getInt(PARAMS_LIST_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this,mToolBar,mTitle);
        ArrayList<Music> datas = MyDb.getDatas(DbTable.FILE_URI, new Music(), DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(mListId)}, "ut DESC");
        if (null != datas && !datas.isEmpty()) {
            mListView.setAdapter(mAdapter = new AllMusicAdapter(getActivity(), datas));
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
        //TODO 如果是最近播放,则重复刷新数据列表,逻辑问题,如果这边刷新了列表.但是服务内没有更新,则顺序依然有问题.
        //从外面跳进来时.顺序会不一致
//        if (MyDb.LAST_VISIBLE == mListId) {
//            HandleTask.run(new HandleTask.TaskAction<ArrayList<Music>>() {
//                @Override
//                public ArrayList<Music> run() {
//                    return MyDb.getDatas(DbTable.FILE_URI, new Music(), DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(mListId)}, "ut DESC");
//                }
//
//                @Override
//                public void postRun(ArrayList<Music> musics) {
//                    mAdapter.swrpDatas(musics);
//                }
//            });
//        }
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
