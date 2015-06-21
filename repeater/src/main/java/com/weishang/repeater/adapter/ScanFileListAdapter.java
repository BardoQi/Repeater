package com.weishang.repeater.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.weishang.repeater.R;
import com.weishang.repeater.utils.ViewInject;

import java.io.File;

/**
 * Created by Administrator on 2015/1/1.
 */
public class ScanFileListAdapter extends BaseAdapter {
    private File[] mFiles;
    private LayoutInflater mInflater;

    public ScanFileListAdapter(Context context, File[] files) {
        mInflater = LayoutInflater.from(context);
        this.mFiles = files;
    }

    @Override
    public int getCount() {
        return mFiles.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AllFileAdapter.ViewHolder holder = null;
        if (null == view) {
            view = mInflater.inflate(R.layout.local_res_item, viewGroup, false);
            holder = new AllFileAdapter.ViewHolder();
            ViewInject.init(holder, view);
            view.setTag(holder);
        }
        holder = (AllFileAdapter.ViewHolder) view.getTag();
        File file = mFiles[i];
        holder.resName.setText(file.getName());
        return view;
    }
}
