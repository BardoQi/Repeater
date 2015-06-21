package com.weishang.repeater.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weishang.repeater.R;
import com.weishang.repeater.bean.Music;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/12.
 * RecyclerCursorAdapter
 */
public class CursorRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder> {
    private final LayoutInflater mInflater;
    private ArrayList<Integer> mSelects;
    private Cursor mCursor;

    public CursorRecyclerAdapter(Context context, Cursor cursor) {
        if (null == cursor) throw new NullPointerException("cursor is Null");
        this.mInflater = LayoutInflater.from(context);
        this.mCursor = cursor;
        mSelects = new ArrayList<Integer>();
    }

    @Override
    public MusicRecyclerAdapter.MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MusicRecyclerAdapter.MusicViewHolder(mInflater.inflate(R.layout.add_music_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final MusicRecyclerAdapter.MusicViewHolder viewHolder, final int i) {
//        if (!mCursor.moveToPosition(i))
//            throw new IllegalArgumentException("cursor can not moveToPositon!");
        //歌曲的名称 ：MediaStore.Audio.Media.TITLE
//        "_id", "id", "ut", "path", "name", "duration", "author", "size", "list_id","favourite","directory_name"
        mCursor.moveToPosition(i);
        String name = mCursor.getString(4);
        String author = mCursor.getString(6);
        viewHolder.mName.setText(name);
        viewHolder.mAuthor.setText(author);
        viewHolder.mCheckBox.setChecked(mSelects.contains(Integer.valueOf(i)));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean contains = mSelects.contains(Integer.valueOf(i));
                viewHolder.mCheckBox.setChecked(!contains);
                if (!contains) {
                    mSelects.add(i);
                } else {
                    mSelects.remove(Integer.valueOf(i));
                }
            }
        });
    }

    public ArrayList<Music> getSelectMusics() {
        ArrayList<Music> musics = new ArrayList<Music>();
        int length = mSelects.size();
        for (int i = 0; i < length; i++) {
            if (mCursor.moveToPosition(mSelects.get(i))) {
//                "_id", "id", "ut", "path", "name", "duration", "author", "size", "list_id","favourite","directory_name"
//                int id, long ut, String path, String name, long duration, String author, long size, int listId, int favourite, String directoryName
                musics.add(new Music(mCursor.getInt(1),
                        System.currentTimeMillis(),
                        mCursor.getString(3),
                        mCursor.getString(4),
                        mCursor.getString(5),
                        mCursor.getLong(6),
                        mCursor.getString(7),
                        mCursor.getLong(8),
                        mCursor.getInt(9),
                        mCursor.getInt(10),
                        mCursor.getString(11),
                        mCursor.getString(12),
                        mCursor.getString(13)));
            }
        }
        return musics;
    }

    @Override
    public int getItemCount() {
        return this.mCursor.getCount();
    }
}
