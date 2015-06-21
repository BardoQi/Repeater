package com.weishang.repeater.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.weishang.repeater.R;
import com.weishang.repeater.adapter.RecordAdapter;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.bean.RepeatInfo;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.AnimatedExpandableListView;

import java.util.ArrayList;

/**
 * Created by momo on 2015/5/3.
 * 录音列表
 */

@Toolbar(title = R.string.record_list)
public class RecordListFragment extends Fragment {
    @ID(id = R.id.toolbar)
    private android.support.v7.widget.Toolbar mToolBar;
    @ID(id = R.id.el_list)
    private AnimatedExpandableListView mListView;
    private RecordAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this,mToolBar);
        ArrayList<RepeatInfo> repeatInfos = MyDb.getDatas(DbTable.REPEAT_URI, new RepeatInfo(), DbTable.REPEAT_SELECTION, "record is not null", null, "record DESC");
        mListView.setAdapter(mAdapter = new RecordAdapter(getActivity(), repeatInfos));
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mListView.isGroupExpanded(groupPosition)) {
                    mListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if (null != mAdapter) {
                    mAdapter.playRecord(i, i1);
                }
                return true;
            }
        });
    }
}
