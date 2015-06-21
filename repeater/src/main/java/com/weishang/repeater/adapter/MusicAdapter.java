package com.weishang.repeater.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2014/12/31. 本地文件数据适配器
 */
public class MusicAdapter extends CursorAdapter {

    public MusicAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        ViewHolder holder = new ViewHolder();
        ViewInject.init(holder, convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // "_id", "id", "ut", "path", "name", "duration","author","size", "use",
        // "collect"
        ViewHolder holder = (ViewHolder) view.getTag();
        String name = cursor.getString(4);
        long duration = cursor.getLong(5);
        String author = cursor.getString(6);
        long size = cursor.getLong(7);
        holder.musicName.setText(name);
        holder.musicAuthor.setText(author);
//        holder.resTime.setText(DateUtils.getTimeString(duration));
//        holder.resSize.setText(FileUtils.FormetFileSize(size));
    }

    public ArrayList<Music> getDatas() {
        Cursor cursor = getCursor();
        cursor.moveToFirst();
        Music music = new Music();
        return music.getMusicLists(cursor);
    }

    public static class ViewHolder {
        @ID(id = R.id.tv_name)
        TextView musicName;
        @ID(id = R.id.tv_author)
        TextView musicAuthor;
    }
}
