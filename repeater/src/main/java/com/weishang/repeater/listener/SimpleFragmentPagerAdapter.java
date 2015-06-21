package com.weishang.repeater.listener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
	private Fragment[] fragments = null;
	private String[] titles;

	public SimpleFragmentPagerAdapter(FragmentManager fm, Fragment[] fragments) {
		super(fm);
		this.fragments = fragments;
	}

	public SimpleFragmentPagerAdapter(FragmentManager fm, Fragment[] fragments, String[] titles) {
		super(fm);
		this.fragments = fragments;
		this.titles = titles;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null != titles ? titles[position] : super.getPageTitle(position);
	}

	@Override
	public Fragment getItem(int position) {
		return fragments[position];
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

}
