package com.weishang.repeater.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.listener.SimpleFragmentPagerAdapter;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.PagerStrip;

/**
 * 我的抽奖信息界面
 *
 * @author momo
 * @Date 2014/11/5
 */

public abstract class MyPagerFragment extends Fragment implements ViewPager.OnPageChangeListener {
    @ID(id=R.id.toolbar)
    protected Toolbar mToolbar;
    private PagerStrip mStrip;
    @ID(id = R.id.vp_pager)
    protected ViewPager mPager;
    protected SimpleFragmentPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        ViewInject.init(this, view, true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this, mToolbar);

        mStrip=new PagerStrip(getActivity());
        mStrip.setTextColor(App.getResourcesColor(R.color.dark_yellow));
        mStrip.setTextSelectColor(Color.WHITE);
        mStrip.setUnderlineColor(App.getResourcesColor(R.color.dark_yellow));
        mPager.setAdapter(adapter = getPagerAdapter());
        mPager.setOffscreenPageLimit(adapter.getCount());
        mStrip.setViewPager(mPager);
        mStrip.setOnPageChangeListener(this);
        mToolbar.addView(mStrip, Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.MATCH_PARENT);
    }

    /**
     * 获得viewapger数据适配器
     */
    public abstract SimpleFragmentPagerAdapter getPagerAdapter();

}
