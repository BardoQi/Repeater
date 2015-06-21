package com.weishang.repeater.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.listener.SimpleFragmentPagerAdapter;

/**
 * 检索本地资音频资料列表,直接获取medieProvider内容
 *
 * @author momo
 * @version 1.0
 * @date 2014/12/31
 */
@com.weishang.repeater.annotation.Toolbar(title = R.string.add_music)
public class LocalResFragment extends MyPagerFragment {
    private int mPosition;

    @Override
    public SimpleFragmentPagerAdapter getPagerAdapter() {
        return new SimpleFragmentPagerAdapter(getChildFragmentManager(), new Fragment[]{addFolderListFragmentFragment.newInstance(getArguments()), addMusicListFragmentFragment.newInstance(getArguments())},
                App.getStringArray(R.array.local_file_title));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.mPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
