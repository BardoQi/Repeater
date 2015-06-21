package com.weishang.repeater.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.RepeatInfo;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.preference.ConfigManager;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.ImageUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.AnimatedExpandableListView;
import com.weishang.repeater.widget.ClockWheelView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by momo on 2015/5/3.
 * 录音列表数据适配器
 */
public class RecordAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater mInflater;
    private ArrayList<RepeatInfo> mTitleInfos;
    private ArrayList<ArrayList<RepeatInfo>> mInfos;
    private RepeatInfo mPlayInfo;
    private ViewHolder mPlayHolder;
    private MediaPlayer mPlayer;

    public RecordAdapter(Context context, ArrayList<RepeatInfo> infos) {
        this.mInflater = LayoutInflater.from(context);
        mTitleInfos = new ArrayList<RepeatInfo>();
        this.mInfos = new ArrayList<ArrayList<RepeatInfo>>();
        mPlayer = new MediaPlayer();
        int musicId = -1;
        if (null != infos && !infos.isEmpty()) {
            int length = infos.size();
            ArrayList<RepeatInfo> childInfos = null;
            for (int i = 0; i < length; i++) {
                RepeatInfo repeatInfo = infos.get(i);
                if (-1 == musicId || musicId != repeatInfo.id) {
                    musicId = repeatInfo.id;
                    childInfos = new ArrayList<RepeatInfo>();
                    mTitleInfos.add(repeatInfo);
                    childInfos.add(repeatInfo);
                    this.mInfos.add(childInfos);
                } else {
                    childInfos.add(repeatInfo);
                }
            }
        }
    }


    @Override
    public int getRealChildrenCount(int groupPosition) {
        return this.mInfos.get(groupPosition).size();
    }

    @Override
    public int getGroupCount() {
        return this.mTitleInfos.size();
    }

    @Override
    public RepeatInfo getGroup(int i) {
        return mTitleInfos.get(i);
    }

    @Override
    public RepeatInfo getChild(int i, int i1) {
        return this.mInfos.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = initConvertView(parent, R.layout.record_title_item, new TitleHolder());
        }
        TitleHolder holder = (TitleHolder) convertView.getTag();
        RepeatInfo repeatInfo = mTitleInfos.get(i);
        holder.titleView.setText(repeatInfo.name);
        ArrayList<RepeatInfo> repeatInfos = this.mInfos.get(i);
        holder.count.setText("(" + (null == repeatInfos ? 0 : repeatInfos.size()) + ")");
        return convertView;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = initConvertView(parent, R.layout.record_item, new ViewHolder());
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        ArrayList<RepeatInfo> repeatInfos = this.mInfos.get(groupPosition);
        final RepeatInfo repeatInfo = repeatInfos.get(childPosition);
        holder.item.setText(String.valueOf(childPosition));
        holder.time.setText(App.getStr(R.string.record_time, DateUtils.getTimeString(repeatInfo.start * 1000), DateUtils.getTimeString(repeatInfo.end * 1000)));
        holder.clockView.setCount(repeatInfo.count);
        holder.clockView.setDuration((int) repeatInfo.recordLength);
        if (null != mPlayInfo && mPlayInfo.equals(repeatInfo)) {
            if (!holder.clockView.isRunning()) {
                mPlayHolder = holder;
                holder.clockView.startAnim();
            }
        } else {
            holder.clockView.setProgress(0);
            holder.clockView.stopAnim();
        }
        ImageUtils.setDrawableScale(holder.delete, App.getResourcesColor(R.color.dark_gray));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInfos.get(groupPosition).remove(childPosition);
                notifyDataSetChanged();
                //ɾ�����ݿ�
                ContentResolver resolver = App.getResolver();
                resolver.delete(DbTable.REPEAT_URI, "id=? and position=?", new String[]{String.valueOf(repeatInfo.id), String.valueOf(repeatInfo.position)});
                //ɾ���ļ�
                File recordFile = new File(ConfigManager.record, repeatInfo.record);
                recordFile.delete();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    protected View initConvertView(ViewGroup parent, int layout, Object holder) {
        View convertView = mInflater.inflate(layout, parent, false);
        ViewInject.init(holder, convertView);
        convertView.setTag(holder);
        return convertView;
    }

    public void playRecord(final int i, final int i1) {
        final RepeatInfo repeatInfo = mInfos.get(i).get(i1);
        File recordFile = new File(ConfigManager.record, repeatInfo.record);
        if (recordFile.exists()) {
            try {
                if (null != mPlayHolder) {
                    mPlayHolder.clockView.stopAnim();
                    mPlayHolder = null;
                }
                mPlayer.reset();
                mPlayer.setDataSource(recordFile.getAbsolutePath());
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // ��ʼ������,����������
                        mPlayer.start();
                        mPlayInfo = repeatInfo;
                        notifyDataSetChanged();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mPlayInfo = null;
                        repeatInfo.count++;
                        if (null != mPlayHolder) {
                            mPlayHolder.clockView.setCount(repeatInfo.count);
                        }
                        //��������
                        ContentResolver resolver = App.getResolver();
                        resolver.update(DbTable.REPEAT_URI, repeatInfo.getContentValues(), "id=? and position=?", new String[]{String.valueOf(repeatInfo.id), String.valueOf(repeatInfo.position)});
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            App.toast(R.string.file_not_exists);
        }
    }

    public static class TitleHolder {
        @ID(id = R.id.tv_music_name)
        TextView titleView;
        @ID(id = R.id.tv_record_count)
        TextView count;
        @ID(id = R.id.iv_navbar)
        View nav;
    }

    public static class ViewHolder {
        @ID(id = R.id.tv_record_item)
        TextView item;
        @ID(id = R.id.tv_record_time)
        TextView time;
        @ID(id = R.id.cv_wheel_view)
        ClockWheelView clockView;
        @ID(id = R.id.iv_repeat_delete)
        ImageView delete;
    }
}
