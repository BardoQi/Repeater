package com.weishang.repeater.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.ui.SearchPopupWindow;
import com.weishang.repeater.utils.TextFontUtils;
import com.weishang.repeater.utils.ViewInject;

/**
 * Created by momo on 2015/3/16.
 * 音乐搜索数据适配器
 */
public class SearchAdapter extends CursorAdapter {
    private LayoutInflater mInflate;
    private String mSearchWork;

    public SearchAdapter(Context context, Cursor c) {
        super(context, c, false);
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = mInflate.inflate(R.layout.search_item, viewGroup, false);
        SearchHolder holder = new SearchHolder();
        ViewInject.init(holder, view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
//        "_id", "id", "ut", "path", "name","album", "duration", "author", "size", "list_id", "favourite", "directory_name", "directory", "word"};
        SearchHolder holder = (SearchHolder) view.getTag();
        final Music music = getItem(cursor.getPosition());
        if (null != music) {
            holder.name.setText(music.name);
            TextFontUtils.setWordColorAndTypedFace(holder.name, App.getResourcesColor(R.color.yellow), Typeface.BOLD, mSearchWork);
            holder.author.setText(music.author);
            TextFontUtils.setWordColorAndTypedFace(holder.author, App.getResourcesColor(R.color.yellow), Typeface.BOLD, mSearchWork);
            holder.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SearchPopupWindow(context, music).showAsDropDown(v);
                }
            });
        }
    }

    @Override
    public Music getItem(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        Music music = new Music(cursor.getInt(1),
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
                cursor.getString(13));

        return music;
    }

    public void setSearchWord(String word) {
        this.mSearchWork = word;
    }

    public static class SearchHolder {
        @ID(id = R.id.tv_music_name)
        TextView name;
        @ID(id = R.id.tv_music_author)
        TextView author;
        @ID(id = R.id.iv_music_option)
        View option;
    }
}
