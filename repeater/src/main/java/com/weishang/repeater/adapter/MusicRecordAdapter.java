package com.weishang.repeater.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.RecordInfo;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2015/5/6.
 * 音乐统计界面
 */
public class MusicRecordAdapter extends MyRecyclerAdapter<RecordInfo, MusicRecordAdapter.ViewHolder> {


    public MusicRecordAdapter(Context context, ArrayList<RecordInfo> items, RecyclerView recyclerView) {
        super(context, items, recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.music_record_item, parent, false));
    }

    @Override
    public void onBind(ViewHolder holder, RecordInfo recordInfo, int position) {
        holder.name.setText(recordInfo.name);
        holder.time.setText(App.getStr(R.string.play_time_value, DateUtils.getProgressTime(recordInfo.time*1000)));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @ID(id = R.id.tv_music_name)
        private TextView name;
        @ID(id = R.id.tv_music_record)
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewInject.init(this, itemView);
        }
    }
}
