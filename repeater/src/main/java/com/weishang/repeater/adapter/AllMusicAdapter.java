package com.weishang.repeater.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;

import java.util.ArrayList;

/**
 * Created by momo on 2014/12/31. 本地文件数据适配器
 */
public class AllMusicAdapter extends MyBaseAdapter<Music, AllMusicAdapter.ViewHolder> {
    private ArrayList<Integer> mIndicatorPositions;

    public AllMusicAdapter(Context context, ArrayList<Music> musics) {
        super(context, musics);
        mIndicatorPositions = new ArrayList<Integer>();
        if (null != musics) {
            String word = null;
            int length = musics.size();
            for (int i = 0; i < length; i++) {
                Music music = musics.get(i);
                if (TextUtils.isEmpty(word)) {
                    word = music.word;
                    mIndicatorPositions.add(i);
                } else {
                    String newWord = music.word;
                    if (!word.equals(newWord)) {
                        word = newWord;
                        mIndicatorPositions.add(i);
                    }
                }
            }
        }
    }

    @Override
    public View newView(int type, int position, View convertView, ViewGroup parent) {
        return initConvertView(parent, R.layout.music_item, new ViewHolder());
    }


    @Override
    public void initView(int type, int position, View view, ViewHolder holder) {
        Music music = ts.get(position);
        holder.musicName.setText(music.name);
        holder.musicAuthor.setText(music.author);
    }

    public int getSelectPosition(int position) {
        int indicatorPosition = 0;
        if (position < mIndicatorPositions.size()) {
            indicatorPosition = mIndicatorPositions.get(position);
        }
        return indicatorPosition;
    }

    public ArrayList<Integer> getSelectPositions() {
        return mIndicatorPositions;
    }

    public static class ViewHolder {
        @ID(id = R.id.tv_name)
        TextView musicName;
        @ID(id = R.id.tv_author)
        TextView musicAuthor;
    }
}
