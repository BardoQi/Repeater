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
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.FileUtils;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2014/12/31.
 * 本地文件数据适配器
 */
public class AllFileAdapter extends CursorAdapter {
    private ArrayList<Integer> mSelectMusics;
    private LocalFolderAdapter.OnItemClickListener mListener;

    public AllFileAdapter(Context context, Cursor c) {
        super(context, c, true);
        mSelectMusics = new ArrayList<Integer>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.local_res_item, parent, false);
        ViewHolder holder = new ViewHolder();
        ViewInject.init(holder, convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int position = cursor.getPosition();
        final ViewHolder holder = (ViewHolder) view.getTag();
//        "_id", "id", "ut", "path", "name", "duration", "author", "size", "list_id","favourite","directory_name","directory"
//        "_id", "id", "ut", "path", "name","album", "duration", "author", "size", "list_id", "favourite", "directory_name", "directory", "word"
        holder.resName.setText(cursor.getString(4));
        holder.resAuthor.setText(cursor.getString(7));
        holder.resTime.setText(DateUtils.getTimeString(cursor.getLong(6)));
        holder.resSize.setText(FileUtils.FormetFileSize(cursor.getLong(8)));
        holder.mCheked.setChecked(mSelectMusics.contains(position));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectMusics.contains(position)) {
                    mSelectMusics.remove(Integer.valueOf(position));
                } else {
                    mSelectMusics.add(position);
                }
                boolean isChecked = mSelectMusics.contains(position);
                holder.mCheked.setChecked(isChecked);
                if (null != mListener) {
                    mListener.onItemClick(view, holder.mCheked, isChecked, mSelectMusics.size());
                }
            }
        });
    }

    /**
     * 全选
     */
    public void checkAll() {
        int count = getCount();
        mSelectMusics.clear();
        for (int i = 0; i < count; i++) {
            mSelectMusics.add(i);
        }
        if (null != mListener) {
            mListener.onItemClick(null, null, true, mSelectMusics.size());
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(LocalFolderAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public ArrayList<Music> getSelectMusics() {
        ArrayList<Music> musics = null;
        if (!mSelectMusics.isEmpty()) {
            Cursor cursor = getCursor();
            musics = new ArrayList<Music>();
            for (int i = 0; i < mSelectMusics.size(); i++) {
                cursor.moveToPosition(mSelectMusics.get(i));
                musics.add(new Music(cursor.getInt(1),
                        cursor.getLong(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getLong(6),
                        cursor.getString(7),
                        cursor.getLong(8),
                        cursor.getInt(9),
                        cursor.getInt(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13)));
            }
        }
        return musics;
    }

    public static class ViewHolder {
        @ID(id = R.id.tv_res_name)
        TextView resName;
        @ID(id = R.id.tv_res_author)
        TextView resAuthor;
        @ID(id = R.id.tv_res_time)
        TextView resTime;
        @ID(id = R.id.tv_res_size)
        TextView resSize;
        @ID(id = R.id.cb_checked)
        CheckBox mCheked;
    }
}
