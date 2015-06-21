package com.weishang.repeater.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.MusicFolder;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2014/12/31.
 * 本地文件数据适配器
 */
public class LocalFolderAdapter extends CursorAdapter {
    private ArrayList<Integer> mSelectFolders;
    private OnItemClickListener mListener;

    public LocalFolderAdapter(Context context, Cursor c) {
        super(context, c, true);
        mSelectFolders = new ArrayList<Integer>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.local_folder_item, parent, false);
        ViewHolder holder = new ViewHolder();
        ViewInject.init(holder, convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final int position = cursor.getPosition();
        String name = cursor.getString(1);
        final String path = cursor.getString(2);
        int count = cursor.getInt(3);
        holder.folderName.setText(name);
        holder.folderItem.setText(context.getString(R.string.total_file, count));
        holder.folderPath.setText(path);
        holder.mChecked.setChecked(mSelectFolders.contains(position));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectFolders.contains(position)) {
                    mSelectFolders.remove(Integer.valueOf(position));
                } else {
                    mSelectFolders.add(position);
                }
                boolean isChecked = mSelectFolders.contains(position);
                holder.mChecked.setChecked(isChecked);
                if (null != mListener) {
                    mListener.onItemClick(view, holder.mChecked, isChecked, mSelectFolders.size());
                }
            }
        });
    }

    /**
     * 全选
     */
    public void checkAll() {
        int count = getCount();
        mSelectFolders.clear();
        for (int i = 0; i < count; i++) {
            mSelectFolders.add(i);
        }
        if (null != mListener) {
            mListener.onItemClick(null, null, true, mSelectFolders.size());
        }
        notifyDataSetChanged();
    }

    public ArrayList<MusicFolder> getSelectFolder() {
        ArrayList<MusicFolder> folders = new ArrayList<MusicFolder>();
        Cursor cursor = getCursor();
        for (Integer position : mSelectFolders) {
            cursor.moveToPosition(position);
            folders.add(new MusicFolder(cursor.getString(2), cursor.getString(1), cursor.getInt(3)));
        }
        return folders;
    }

    public static class ViewHolder {
        @ID(id = R.id.tv_folder_name)
        TextView folderName;
        @ID(id = R.id.tv_folder_item)
        TextView folderItem;
        @ID(id = R.id.tv_folder_path)
        TextView folderPath;
        @ID(id = R.id.cb_checked)
        CheckBox mChecked;
    }

    public void setOnItemCheckListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, View checkBox, boolean isChecked, int count);
    }
}
