package com.weishang.repeater.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kenumir.materialsettings.MaterialSettingsFragment;
import com.weishang.repeater.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends MaterialSettingsFragment {

    public static Fragment newInstance(){
        return new SettingFragment();
    }

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }


}
