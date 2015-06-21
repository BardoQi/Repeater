package com.weishang.repeater.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.RepeatInfo;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.listener.ViewImageClickListener;
import com.weishang.repeater.preference.ConfigManager;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.ClockWheelView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by momo on 2015/3/26.
 * 复读信息的数据适配器
 */
public class RepeatAdapter extends RecyclerView.Adapter<RepeatAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private Context mContext;
    private final ArrayList<RepeatInfo> mInfos;
    private final SparseBooleanArray mInitAnims;
    private RecyclerView mRecyclerView;
    private RepeatInfo mPlayInfo;
    private CountDownTimer mRepeatTimer;
    private OnRepeatListener mListener;
    private MediaPlayer mPlayer;
    private boolean playRecord;

    public RepeatAdapter(Context context, ArrayList<RepeatInfo> infos, RecyclerView recyclerView) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mPlayer = new MediaPlayer();
        this.mRecyclerView = recyclerView;
        mInfos = new ArrayList<RepeatInfo>();
        mInitAnims = new SparseBooleanArray();
        if (null != infos && !infos.isEmpty()) {
            this.mInfos.addAll(infos);
        }
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.repeat_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RepeatInfo repeatInfo = mInfos.get(position);
        // 在添加之前移动其他控件到底部
        holder.repeatCount.setText(App.getStr(R.string.repeat_count, position));
        holder.startTime.setText(App.getStr(R.string.repeat_start_time, DateUtils.getTimeString(repeatInfo.start * 1000)));
        holder.endTime.setText(App.getStr(R.string.repeat_end_time, DateUtils.getTimeString(repeatInfo.end * 1000)));
        if (null != mPlayInfo && mPlayInfo == repeatInfo) {
            if (mInitAnims.get(position)) {
                mInitAnims.delete(position);
                setPlayShowAnim(holder, true);
            } else {
                setPlayStatus(holder, true);
            }
            holder.wheelView.setCount(repeatInfo.count);
            holder.wheelView.setDuration((repeatInfo.end - repeatInfo.start) * 1 * 1000);
            holder.wheelView.startAnim();
        } else if (mInitAnims.get(position)) {
            mInitAnims.delete(position);
            setPlayShowAnim(holder, false);
        } else {
            setPlayStatus(holder, false);
        }
        holder.deleteView.setOnClickListener(new ViewImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(repeatInfo);
                ContentResolver resolver = App.getResolver();
                resolver.delete(DbTable.REPEAT_URI, "id=? and position=?", new String[]{String.valueOf(repeatInfo.id), String.valueOf(repeatInfo.position)});
            }
        }));
        holder.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onRecord(repeatInfo);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mPlayInfo && mPlayInfo == repeatInfo) {
                    //如果点击相同条目,则停止复读
                    clearRepeatInfo(position);
                    return;
                } else if (null != mPlayInfo) {
                    //清除上一个播放状态
                    clearRepeatInfo(mInfos.indexOf(mPlayInfo));
                }
                playRecord = true;
                int count = PrefernceUtils.getInt(ConfigName.REPEAT_COUNT);
                startRepeatPlay(repeatInfo, position, count, 0);
            }
        });
    }

    /**
     * 设置播放展示动画
     *
     * @param holder
     */
    private void setPlayShowAnim(final ViewHolder holder, final boolean show) {
        holder.deleteView.setVisibility(View.VISIBLE);
        holder.wheelView.setVisibility(View.VISIBLE);
        ViewHelper.setScaleX(holder.deleteView, show ? 1f : 0);
        ViewHelper.setScaleY(holder.deleteView, show ? 1f : 0);
        ViewHelper.setScaleX(holder.wheelView, !show ? 1f : 0f);
        ViewHelper.setScaleY(holder.wheelView, !show ? 1f : 0f);
        ViewPropertyAnimator.animate(holder.deleteView).scaleX(!show ? 1f : 0f).scaleY(!show ? 1f : 0f).setDuration(300).setInterpolator(new LinearInterpolator());
        ViewPropertyAnimator.animate(holder.wheelView).scaleX(show ? 1f : 0f).scaleY(show ? 1f : 0f).setDuration(300).setStartDelay(100).setInterpolator(new LinearInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                holder.deleteView.setVisibility(!show ? View.VISIBLE : View.GONE);
                holder.wheelView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setPlayStatus(ViewHolder holder, boolean isPlaying) {
        ViewHelper.setScaleX(holder.wheelView, isPlaying ? 1f : 0);
        ViewHelper.setScaleY(holder.wheelView, isPlaying ? 1f : 0);
        ViewHelper.setScaleX(holder.deleteView, isPlaying ? 0 : 1f);
        ViewHelper.setScaleY(holder.deleteView, isPlaying ? 0 : 1f);
        holder.deleteView.setVisibility(!isPlaying ? View.VISIBLE : View.GONE);
        holder.wheelView.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
    }

    /**
     * 清除播放信息
     */
    private void clearRepeatInfo(int position) {
        if (null != mRepeatTimer) {
            mRepeatTimer.cancel();
            mRepeatTimer = null;
        }
        mPlayInfo = null;
        mInitAnims.append(position, true);
        notifyDataSetChanged();
        if (null != mContext && mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing()) {
                mContext = null;
                mPlayer.release();
                mPlayer = null;
            }
        }
    }


    /**
     * 开始复读播放
     *
     * @param repeatInfo
     */
    private void startRepeatPlay(final RepeatInfo repeatInfo, final int index, final int count, final int i) {
        repeatInfo.count++;
        mPlayInfo = repeatInfo;
        if (null != mListener) {
            mListener.onRepeat(repeatInfo);
        }
        int duration = (repeatInfo.end - repeatInfo.start) * 1000;
        if (null != mRepeatTimer) {
            mRepeatTimer.cancel();
        }
        //开始播放音乐
        mRepeatTimer = new CountDownTimer(duration, 1 * 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                File recordFile = null;
                if (!TextUtils.isEmpty(repeatInfo.record)) {
                    recordFile = new File(ConfigManager.record, repeatInfo.record);
                }
                if (playRecord && i <= count && null != recordFile && recordFile.exists()) {
                    int count = PrefernceUtils.getInt(ConfigName.REPEAT_COUNT);
                    //播放录音
                    playRepeatRecord(repeatInfo, recordFile, index, count, i + 1);
                } else {
                    playNextRepeat(recordFile, i, count, repeatInfo, index, false);
                }
            }
        };
        mRepeatTimer.start();
        if (0 == i) {
            //起始时,加载初始化动画
            mInitAnims.append(index, true);
        }
        playRecord = true;
        notifyDataSetChanged();
    }

    private void playNextRepeat(File recordFile, int i, int count, RepeatInfo repeatInfo, int index, boolean isRecord) {
        if (isRecord ? i <= count : i < count) {
            //按列表复读
            startRepeatPlay(repeatInfo, index, count, null == recordFile || !recordFile.exists() ? i + 1 : i);
        } else if (index < getItemCount() - 1) {
            //清除上一个
            clearRepeatInfo(index);
            //播放下一个
            if (index < getItemCount() - 1) {
                int newIndex = index + 1;
                int repeatCount = PrefernceUtils.getInt(ConfigName.REPEAT_COUNT);
                //复读下一个条目
                startRepeatPlay(mInfos.get(newIndex), newIndex, repeatCount, 0);
            }
        } else {
            clearRepeatInfo(index);
        }
    }

    /**
     * 播放复读录音
     *
     * @param recordFile
     * @param count
     * @param i
     */
    private void playRepeatRecord(final RepeatInfo repeatInfo, final File recordFile, final int index, final int count, final int i) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(recordFile.getPath());
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 初始化进度,等其他操作
                    mPlayer.start();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (null != mListener) {
                        mListener.stopRecord();
                    }
                    //继续播放复读
                    playNextRepeat(recordFile, i, count, repeatInfo, index, true);
                }
            });
            if (null != mListener) {
                mListener.startRecord();
            }
            playRecord = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(RepeatInfo info) {
        if (null != info) {
            int size = this.mInfos.size();
            final int index = this.mInfos.indexOf(info);
            if (-1 != index) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                layoutManager.scrollToPosition(index);
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //抖动条目
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        int position = index - firstVisibleItemPosition;
                        View childView = layoutManager.getChildAt(position);
                        if (null != childView) {
                            Animation animation = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.shake);
                            childView.startAnimation(animation);
                        }
                    }
                }, 100);
            } else {
                this.mInfos.add(info);
                notifyItemInserted(size);
                mRecyclerView.scrollToPosition(size);
                //插入数据库
                ContentResolver resolver = App.getResolver();
                resolver.insert(DbTable.REPEAT_URI, info.getContentValues());
            }
        }
    }

    public void remove(int index) {
        this.mInfos.remove(index);
        notifyItemRemoved(index);
        //通知刷新数据,延持600毫秒,300毫秒消失,300移动
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 600);
    }

    public void remove(RepeatInfo info) {
        int index = this.mInfos.indexOf(info);
        if (-1 != index) {
            remove(index);
        }
    }

    public RepeatInfo getItem(int position) {
        RepeatInfo repeatInfo = null;
        if (0 <= position && position < getItemCount()) {
            repeatInfo = this.mInfos.get(position);
        }
        return repeatInfo;
    }

    public RepeatInfo getLastItem() {
        return getItem(getItemCount() - 1);
    }

    public void clear() {
        notifyItemRangeRemoved(0, getItemCount());
        RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        long moveDuration = itemAnimator.getMoveDuration();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInfos.clear();
                notifyDataSetChanged();
            }
        }, moveDuration);
    }

    public void setOnRepeatListener(OnRepeatListener listener) {
        this.mListener = listener;
    }


    public void updateRepeatInfo() {
        ArrayList<RepeatInfo> items = this.mInfos;
        if (null != items && !items.isEmpty()) {
            int length = items.size();
            ContentResolver resolver = App.getResolver();
            for (int i = 0; i < length; i++) {
                RepeatInfo repeatInfo = items.get(i);
                resolver.update(DbTable.REPEAT_URI, repeatInfo.getContentValues(), "id=? and position=?", new String[]{String.valueOf(repeatInfo.id), String.valueOf(repeatInfo.position)});
            }
        }
    }

    public void addItems(final ArrayList<RepeatInfo> data) {
        if (null != data && !data.isEmpty()) {
            RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
            final long moveDuration = itemAnimator.getMoveDuration();
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int count = getItemCount();
                    mInfos.addAll(data);
                    notifyItemRangeInserted(count, getItemCount());
                }
            }, moveDuration * 2);
        }
    }

    public interface OnRepeatListener {
        void onRepeat(RepeatInfo repeatInfo);

        void onRecord(RepeatInfo repeatInfo);

        void startRecord();

        void stopRecord();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @ID(id = R.id.tv_repeat_count)
        public TextView repeatCount;
        @ID(id = R.id.tv_start_time)
        public TextView startTime;
        @ID(id = R.id.tv_end_time)
        public TextView endTime;
        @ID(id = R.id.iv_repeat_record)
        public ImageView record;
        @ID(id = R.id.cv_wheel_view)
        public ClockWheelView wheelView;
        @ID(id = R.id.iv_repeat_delete)
        public ImageView deleteView;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewInject.init(this, itemView);
        }
    }
}
