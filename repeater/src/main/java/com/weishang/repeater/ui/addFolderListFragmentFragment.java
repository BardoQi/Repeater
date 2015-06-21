package com.weishang.repeater.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.LocalFolderAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.MusicFolder;
import com.weishang.repeater.bean.ResultCode;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.provider.DateProvider;
import com.weishang.repeater.utils.HandleTask;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class addFolderListFragmentFragment extends Fragment implements  View.OnClickListener {

    @ID(id = android.R.id.list)
    private ListView mListView;
    private LocalFolderAdapter mAdapter;
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
    private int mListId;

    private int mCount;

    public static addFolderListFragmentFragment newInstance(Bundle args) {
        addFolderListFragmentFragment fragment = new com.weishang.repeater.ui.addFolderListFragmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mListId = getArguments().getInt("list_id", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_localfilelistfragment, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BusProvider.getInstance().register(this);
        ViewHelper.setAlpha(mAddItem, 0f);
//        select name, count (name) as 'count' from test group by name
        Cursor cursor = DateProvider.getDb().rawQuery("select _id,directory_name,directory,count(directory_name) as 'count' from " + MyDb.LOCAL_RES + " where list_id=? group by directory_name", new String[]{"0"});
        MusicFolder musicFolder = new MusicFolder();
        mAdapter = new LocalFolderAdapter(getActivity(), cursor);
        mAdapter.setOnItemCheckListener(new LocalFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, View checkBox, boolean isChecked, int count) {
                mCount = count;
                // 移动动画
                moveItemAnim(view, checkBox, isChecked);

                startFlingAnim(isChecked, mCount);
            }
        });
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mAddLayout);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    private void startFlingAnim(boolean isChecked, int count) {
        mCount = count;
        //执行移动动画,取最大的宽度,因为数值从1位过渡过2位时,宽度会异常
        final int width = Math.max(mCount1.getWidth(), mCount2.getWidth());
        float translationX = ViewHelper.getTranslationX(mCount1);
        boolean isCount1 = (0 == translationX);
        mCount1.setText(String.valueOf(isCount1 ? isChecked ? mCount - 1 : mCount + 1 : mCount));
        mCount2.setText(String.valueOf(!isCount1 ? isChecked ? mCount - 1 : mCount + 1 : mCount));
        ViewHelper.setTranslationX(isCount1 ? mCount2 : mCount1, isChecked ? width : -width);
        ViewPropertyAnimator.animate(isCount1 ? mCount2 : mCount1).translationX(0).setDuration(300);
        ViewPropertyAnimator.animate(isCount1 ? mCount1 : mCount2).translationX(isChecked ? -width : width).setDuration(300);
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
            ViewHelper.setX(mAddItem, location[0]);
            ViewHelper.setY(mAddItem, location[1] - view.getHeight() - checkBox.getHeight() / 2);

            ViewPropertyAnimator.animate(mAddItem).alpha(1f).scaleX(1f).scaleY(1f).x(moveLocation[0] + mAddItem.getWidth() / 2).
                    y(moveLocation[1] - mAddLayout.getHeight() - mAddItem.getHeight() / 2).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewPropertyAnimator.animate(mAddItem).alpha(0f).setDuration(200);
                }
            });
        } else {
            ViewHelper.setX(mAddItem, moveLocation[0] + mAddItem.getWidth() / 2);
            ViewHelper.setY(mAddItem, moveLocation[1] - mAddLayout.getHeight() - mAddItem.getHeight() / 2);

            checkBox.getLocationOnScreen(location);
            ViewPropertyAnimator.animate(mAddItem).alpha(1f).scaleX(1f).scaleY(1f).x(location[0]).
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


    @Override
    public void onClick(View view) {
        if (R.id.rl_add_image == view.getId()) {
            mProgressBar.setVisibility(View.VISIBLE);
            HandleTask.run(new HandleTask.TaskAction<Object>() {
                @Override
                public Object run() {
                    ArrayList<MusicFolder> selectFolder = mAdapter.getSelectFolder();
                    int length = selectFolder.size();
                    for (int i = 0; i < length; i++) {
                        MusicFolder musicFolder = selectFolder.get(i);
                        //将选中添加到文件夹中
                        ContentResolver resolver = App.getResolver();
                        Cursor cursor = resolver.query(DbTable.FILE_URI, DbTable.FILE_SELECTION, "directory_name=?", new String[]{musicFolder.name}, null);
                        if (null != cursor) {
                            int count = 0;
                            ContentValues[] values = new ContentValues[cursor.getCount()];
                            while (cursor.moveToNext()) {
                                values[count++] = new Music(cursor.getInt(1),
                                        System.currentTimeMillis(),
                                        cursor.getString(3),
                                        cursor.getString(4),
                                        cursor.getString(5),
                                        cursor.getLong(6),
                                        cursor.getString(7),
                                        cursor.getLong(8),
                                        mListId,
                                        cursor.getInt(10),
                                        cursor.getString(11),
                                        cursor.getString(12),
                                        cursor.getString(13)).getContentValues();
                            }
                            cursor.close();
                            resolver.bulkInsert(DbTable.FILE_URI, values);
                        }
                    }
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
