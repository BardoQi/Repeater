package com.weishang.repeater.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.CursorRecyclerAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.ResultCode;
import com.weishang.repeater.db.DbTable;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/12.
 * 添加音乐界面
 */
public class AddMusicFragment extends ProgressFragment implements View.OnClickListener {
    private static final int ADD_COMPLETE = 1;
    @ID(id = R.id.rv_recycler)
    private RecyclerView mRecycler;
    private CursorRecyclerAdapter mAdapter;
    private int mListId;

    public static Fragment newInstance() {
        return new AddMusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mListId = getArguments().getInt(MusicListFragment.LIST_ID);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_recycler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setProgressShown(true);
        setTitleShown(true);
        mTitleBar.setTitle(R.string.add_music);
        mTitleBar.setDisplayHome(true);
        mTitleBar.setBackListener(this);
        mTitleBar.addTextMenu(ADD_COMPLETE, R.string.complete, this);

        Cursor cursor = App.getResolver().query(DbTable.FILE_URI, DbTable.FILE_SELECTION, "list_id=?", new String[]{"0"},"DESC name");
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setHasFixedSize(true);
        if (null == cursor || 0 == cursor.getCount()) {
            setEmptyShown(true);
        } else if (null != getActivity()) {
            delayShowContainer(true);
            mAdapter = new CursorRecyclerAdapter(getActivity(), cursor);
            mRecycler.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case ADD_COMPLETE:
                //添加到数据库,并展示列表
                if (0 != mListId && null != mAdapter) {
                    ContentResolver resolver = App.getResolver();
                    ArrayList<Music> selectMusics = mAdapter.getSelectMusics();
                    int length = selectMusics.size();
                    for (int i = 0; i < length; i++) {
                        ContentValues contentValues = selectMusics.get(i).getContentValues();
                        contentValues.put("list_id", mListId);
                        resolver.insert(DbTable.FILE_URI, contentValues);
                    }
                    App.toast(R.string.add_complate);
                    getActivity().setResult(ResultCode.ADD_MUSIC);
                } else {
                    App.toast(R.string.add_list_fail);
                }
            case R.id.titlebar_home:
                getActivity().finish();
                break;
        }
    }
}
