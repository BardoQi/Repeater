package com.weishang.repeater.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.MusicRecordAdapter;
import com.weishang.repeater.adapter.MyRecyclerAdapter;
import com.weishang.repeater.anim.recycler.SlideInRightAnimator;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.RecordInfo;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.SlidingUpPanelLayout;
import com.weishang.repeater.widget.calendar.CalendarDay;
import com.weishang.repeater.widget.calendar.SimpleMonthView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by momo on 2015/5/6.
 * 音乐统计界面
 */
@com.weishang.repeater.annotation.Toolbar(title = R.string.play_record)
public class MusicRecordFragment extends Fragment {
    @ID(id = R.id.rl_container)
    private View mContainer;
    @ID(id = R.id.toolbar)
    private Toolbar mToolbar;
    @ID(id = R.id.sp_panel)
    private SlidingUpPanelLayout mLayout;

    @ID(id = R.id.mv_view)
    private SimpleMonthView monthView;
    @ID(id = R.id.tv_date)
    private TextView mRecordDate;
    @ID(id = R.id.view_content)
    private RecyclerView mRecyclerView;
    private MusicRecordAdapter mAdapter;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_record, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this,mToolbar);
        monthView.setOnDayClickListener(new SimpleMonthView.OnDayClickListener() {
            @Override
            public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
                setRecordDate(calendarDay.getDate().getTime());
            }
        });
        mLayout.setRecyclerList(mRecyclerView);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                int dayMarginTop = monthView.getDayMarginTop();
                ViewCompat.setTranslationY(monthView, -slideOffset * dayMarginTop);
                int width = mRecordDate.getWidth();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRecordDate.getLayoutParams();
                int translationX = (int) ((App.sWidth - width) / 2 - params.leftMargin);
                ViewCompat.setTranslationX(mRecordDate, translationX * slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
            }

            @Override
            public void onPanelExpanded(View panel) {
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });

        setRecordDate(DateUtils.getToDayStartMillis());

    }

    /**
     *
     * @param timeMillis
     */
    private void setRecordDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day);
        long startDayMillis = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endDayMillis = calendar.getTimeInMillis();
        ArrayList<RecordInfo> recordInfos = MyDb.getDatas(DbTable.RECORD_URI, new RecordInfo(), DbTable.RECORD_SELECTION, "ct>=? and ct<=?", String.valueOf(startDayMillis), String.valueOf(endDayMillis));
        if (null == mAdapter) {
            mAdapter = new MusicRecordAdapter(getActivity(), recordInfos, mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            SlideInRightAnimator itemAnimator = new SlideInRightAnimator();
            mRecyclerView.setItemAnimator(itemAnimator);
            mRecyclerView.setVerticalScrollBarEnabled(true);
            itemAnimator.setAddDuration(300);
            itemAnimator.setRemoveDuration(200);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
                @Override
                public void itemClick(View v, int position) {

                }
            });
        } else {
            mAdapter.swapItems(recordInfos);
        }
        int count = null == recordInfos ? 0 : recordInfos.size();
        String text = null;
        if (0 == count) {
            text = App.getStr(R.string.play_no_record, DateUtils.getFromat(App.getStr(R.string.format_month), timeMillis));
        } else {
            text = App.getStr(R.string.play_record_count, DateUtils.getFromat(App.getStr(R.string.format_month), timeMillis), null == recordInfos ? 0 : recordInfos.size());
        }
        mRecordDate.setText(text);
    }


}
