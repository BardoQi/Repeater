package com.weishang.repeater.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.App;
import com.weishang.repeater.PlayActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.AllFileAdapter;
import com.weishang.repeater.adapter.LocalFolderAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.ResultCode;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.utils.HandleTask;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p/>
 */
public class addMusicListFragmentFragment extends Fragment implements View.OnClickListener {

    @ID(id = android.R.id.list)
    private ListView mListView;
    private AllFileAdapter mAdapter;
    @ID(id = R.id.iv_add_item)
    private ImageView mAddItem;
    @ID(id = R.id.rl_add_image, click = true)
    private View mAddLayout;
    @ID(id = R.id.iv_add_image)
    private ImageView mAddImage;
    @ID(id = R.id.progressBar)
    private ProgressBar mProgressBar;
    @ID(id = R.id.tv_count1)
    private TextView mCount1;
    @ID(id = R.id.tv_count2)
    private TextView mCount2;
    private int mCount;
    private int mListId;

    public static addMusicListFragmentFragment newInstance(Bundle args) {
        addMusicListFragmentFragment fragment = new addMusicListFragmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mListId = getArguments().getInt("list_id", MyDb.DEFUALT_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_localfilelistfragment, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BusProvider.getInstance().register(this);
        ViewCompat.setAlpha(mAddItem, 0f);
        Cursor cursor = App.getResolver().query(DbTable.FILE_URI, DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(MyDb.DEFUALT_LIST)}, "word ASC");
        mAdapter = new AllFileAdapter(getActivity(), cursor);
        mAdapter.setOnItemClickListener(new LocalFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, View checkBox, boolean isChecked, int count) {
                mCount = count;
                // 移动动画
                moveItemAnim(view, checkBox, isChecked);
                startFlingAnim(isChecked, count);
            }
        });
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mAddLayout);
    }

    /**
     * 移动条目动画
     *
     * @param view
     * @param checkBox
     * @param isChecked
     */
    private void moveItemAnim(View view, View checkBox, boolean isChecked) {
        if (null == view || null == checkBox) return;
        int[] location = new int[2];
        int[] moveLocation = new int[2];
        checkBox.getLocationOnScreen(location);
        mAddLayout.getLocationOnScreen(moveLocation);
        if (isChecked) {
            ViewCompat.setX(mAddItem, location[0]);
            ViewCompat.setY(mAddItem, location[1] - view.getHeight() - checkBox.getHeight() / 2);

            ViewPropertyAnimator.animate(mAddItem).alpha(1f).x(moveLocation[0] + mAddItem.getWidth() / 2).
                    y(moveLocation[1] - mAddLayout.getHeight() - mAddItem.getHeight() / 2).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewPropertyAnimator.animate(mAddItem).alpha(0f).setDuration(200);
                }
            });
        } else {
            ViewCompat.setX(mAddItem, moveLocation[0] + mAddItem.getWidth() / 2);
            ViewCompat.setY(mAddItem, moveLocation[1] - mAddLayout.getHeight() - mAddItem.getHeight() / 2);

            checkBox.getLocationOnScreen(location);
            ViewPropertyAnimator.animate(mAddItem).alpha(1f).x(location[0]).
                    y(location[1] - view.getHeight() - checkBox.getHeight() / 2).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewPropertyAnimator.animate(mAddItem).alpha(0f).setDuration(200);
                }
            });
        }
        //执行添加动画
        ViewPropertyAnimator.animate(mAddImage).rotation(90 * mCount).setDuration(300);
    }

    private void startFlingAnim(boolean isChecked, int count) {
        mCount = count;
        //执行移动动画,取最大的宽度,取字计算宽度.而不是view width,因为刚设置的count,width并不会马上同步计算出来
        final float width = mCount1.getPaint().measureText(String.valueOf(count));
        float translationX = ViewCompat.getTranslationX(mCount1);
        boolean isCount1 = (0 == translationX);
        mCount1.setText(String.valueOf(isCount1 ? isChecked ? mCount - 1 : mCount + 1 : mCount));
        mCount2.setText(String.valueOf(!isCount1 ? isChecked ? mCount - 1 : mCount + 1 : mCount));
        ViewCompat.setTranslationX(isCount1 ? mCount2 : mCount1, isChecked ? width : -width);
        ViewPropertyAnimator.animate(isCount1 ? mCount2 : mCount1).translationX(0).setDuration(300);
        ViewPropertyAnimator.animate(isCount1 ? mCount1 : mCount2).translationX(isChecked ? -width : width).setDuration(300);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object adapter = parent.getAdapter();
        if (adapter instanceof CursorAdapter) {
            Cursor cursor = ((CursorAdapter) adapter).getCursor();
            if (null != cursor) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                Music music = new Music(cursor.getInt(1),
                        System.currentTimeMillis(),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getLong(6),
                        cursor.getString(7),
                        cursor.getLong(8),
                        cursor.getInt(9),
                        cursor.getInt(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13));
                intent.putExtra(PlayActivity.PLAY_MUSIC, music);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (R.id.rl_add_image == view.getId()) {
            mProgressBar.setVisibility(View.VISIBLE);
            HandleTask.run(new HandleTask.TaskAction<Object>() {
                @Override
                public Object run() {
                    ArrayList<Music> selectMusics = mAdapter.getSelectMusics();
                    //将选中添加到文件夹中
                    ContentResolver resolver = App.getResolver();
                    int length = selectMusics.size();
                    ContentValues[] values = new ContentValues[length];
                    for (int i = 0; i < length; i++) {
                        Music music = selectMusics.get(i);
                        music.listId = mListId;
                        values[i] = music.getContentValues();
                    }
                    resolver.bulkInsert(DbTable.FILE_URI, values);
                    return null;
                }

                @Override
                public void postRun(Object o) {
                    if (null != getActivity()) {
                        getActivity().setResult(ResultCode.ADD_MUSIC);
                        getActivity().finish();
                    }
                }
            });
        }
    }
}
