package com.weishang.repeater.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * viewPager简单数据适配器
 * 
 * @author momo
 * @Date 2014/10/16
 * 
 */
public class SimpleViewPagerAdapter<V extends View> extends PagerAdapter {
	private final ArrayList<V> mViews;
	private final int mCount;

	public SimpleViewPagerAdapter(List<V> views) {
		this(views, false);
	}

	/**
	 * @param views
	 * @param isLoop
	 *            是否循环
	 */
	public SimpleViewPagerAdapter(List<V> views, boolean isLoop) {
		mViews = new ArrayList<V>();
		if (null != views) {
			mViews.addAll(views);
		}
		this.mCount = isLoop ? Integer.MAX_VALUE : mViews.size();
	}

	@Override
	public int getCount() {
		return this.mCount;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViews.get(position % mViews.size()));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int size = mViews.size();
		container.addView(mViews.get(position % size));
		return mViews.get(position % size);
	}

}
