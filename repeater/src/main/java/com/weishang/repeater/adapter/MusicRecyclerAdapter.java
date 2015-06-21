package com.weishang.repeater.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/3/11.
 */
public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder> {
    private final LayoutInflater mInflater;
    private final ArrayList<Music> musics;
    private final ArrayList<Music> mSelects;

    public MusicRecyclerAdapter(Context context, ArrayList<Music> musics) {
        this.mInflater = LayoutInflater.from(context);
        this.musics = new ArrayList<Music>();
        this.mSelects = new ArrayList<Music>();
        if (null != musics) {
            this.musics.addAll(musics);
        }
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MusicViewHolder(mInflater.inflate(R.layout.add_music_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MusicViewHolder viewHolder, int i) {
        final Music music = this.musics.get(i);
        viewHolder.mName.setText(music.name);
        viewHolder.mAuthor.setText(music.author);
        viewHolder.mCheckBox.setSelected(mSelects.contains(music));
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mSelects.add(music);
                } else {
                    mSelects.remove(music);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.musics.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        @ID(id = R.id.ll_container)
        public View mLayout;
        @ID(id = R.id.tv_name)
        public TextView mName;
        @ID(id = R.id.tv_author)
        public TextView mAuthor;
        @ID(id = R.id.cb_checked)
        public android.widget.CheckBox mCheckBox;

        public MusicViewHolder(View itemView) {
            super(itemView);
            ViewInject.init(this, itemView);
            this.mCheckBox.setSelected(true);
        }

    }
}
