package com.weishang.repeater;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.otto.Subscribe;
import com.weishang.repeater.adapter.RepeatAdapter;
import com.weishang.repeater.adapter.SimpleViewPagerAdapter;
import com.weishang.repeater.anim.recycler.SlideInRightAnimator;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.ViewClick;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.PlayList;
import com.weishang.repeater.bean.RepeatInfo;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.MusicPlayEvent;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.service.IRemoteService;
import com.weishang.repeater.service.PlayService;
import com.weishang.repeater.ui.dialog.RecordDialog;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.LoopList;
import com.weishang.repeater.utils.RepeatUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.FlatView;
import com.weishang.repeater.widget.MusicImageView;
import com.weishang.repeater.widget.MusicProgressBar;
import com.weishang.repeater.widget.WaveView;

import java.util.ArrayList;

/**
 * 播放音乐activity
 *
 * @author momo
 * @Date 2015/2/26
 */
@ViewClick(ids = {R.id.iv_music_favorite, R.id.iv_music_rw, R.id.iv_music_ff})
public class PlayActivity extends AppCompatActivity implements OnClickListener, ViewPager.OnPageChangeListener {
    //复读模式
    private static final int REPEAT_PLAY = 0;
    private static final int REPEAT_ONE = 1;
    private static final int REPEAT_THREE = 2;

    public static final String PLAY_LIST = "list";
    public static final String PLAY_MUSIC = "music";
    public static final String PLAY_LIST_ID = "list_id";
    public static final String PLAY_POSITION = "position";
    @ID(id = R.id.rl_container)
    private View mContainer;
    @ID(id = R.id.tv_music_name)
    private TextView mMusicName;

    @ID(id = R.id.iv_play_mode, click = true)
    private ImageView mPlayMode;
    @ID(id = R.id.iv_music_add, click = true)
    private ImageView mAddMenu;
    @ID(id = R.id.iv_music_timer)
    private ImageView mMusicTimer;

    @ID(id = R.id.iv_music_play, click = true)
    private MusicImageView mMusicPlay;
    @ID(id = R.id.iv_music_favorite)
    private ImageView mFavorite;
    @ID(id = R.id.iv_image_flag)
    private ImageView mImageFlag;//音乐标记
    @ID(id = R.id.wv_wave)
    private WaveView mWaveView;// 音乐波浪

    @ID(id = R.id.sliderNumber)
    private MusicProgressBar mProgressBar;
    @ID(id = R.id.vp_pager)
    private ViewPager mPager;
    @ID(id = R.id.iv_add_layout, click = true)
    private FlatView mAddLayout;
    @ID(id = R.id.iv_add_repeat)
    private ImageView mAddView;

    @ID(id = R.id.tv_is_start)
    private TextView mStartAlert;
    @ID(id = R.id.tv_is_end)
    private TextView mEndAlert;

    @ID(id = R.id.tv_play_time)
    private TextView mPlayTime;
    @ID(id = R.id.tv_music_time)
    private TextView mMusicTime;

    private RecyclerView mRecyclerView;
    private RepeatAdapter mRepeatAdapter;




    private CountDownTimer mTimer;
    private ServiceConnection mConnection;
    private IRemoteService mService;
    private ArrayList<PlayList> mPlayLists;
    private int mPlayListId;
    private Music music;
    private int mPosition;
    private LoopList<Integer> mRepeatModes;

    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.activity_play);
        ViewInject.init(this);
        initPager();
        // 初始化动画元素
        Intent intent = getIntent();
        mPlayLists = new ArrayList<PlayList>();
        music = intent.getParcelableExtra(PLAY_MUSIC);
        mPosition = intent.getIntExtra(PLAY_POSITION, 0);
        mPlayListId = intent.getIntExtra(PLAY_LIST_ID, MyDb.DEFUALT_LIST);
        if (null != music) {
            setPlayMusicInfo(music);
        } else {
            mMusicName.setText(R.string.no_play_file);
            mMusicTime.setText(R.string.init_play_time);
        }

        mRepeatModes = new LoopList<Integer>();
        mRepeatModes.offer(REPEAT_ONE);
        mRepeatModes.offer(REPEAT_THREE);
        mRepeatModes.offer(REPEAT_PLAY);
        int repeatMode = PrefernceUtils.getInt(ConfigName.REPEAT_COUNT);
        mRepeatModes.seekTo(repeatMode);
        mPlayMode.setImageLevel(repeatMode);
        mProgressBar.setOnProgressChangeListener(new MusicProgressBar.OnProgressChangeListener() {
            @Override
            public void onSeekChange(MusicProgressBar progressBar, int progress) {
                try {
                    if (null != mService) {
                        mPlayTime.setText(DateUtils.getTimeString(progress * 1000));
                        mService.seekTo(progress * 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mTimer = new CountDownTimer(Integer.MAX_VALUE, 1 * 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    if (null != mService) {
                        int progress = mService.getProgress();
                        mPlayTime.setText(DateUtils.getTimeString(progress));
                        mProgressBar.setProgress(progress / 1000);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
            }
        };


        //初始化三个控件起始动画位置
        runOnGlobal(mAddLayout, new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationX(mAddLayout, App.sWidth - mAddLayout.getRight() + mAddLayout.getWidth());
            }
        });
        runOnGlobal(mStartAlert, new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationX(mStartAlert, -mStartAlert.getWidth());
            }
        });
        runOnGlobal(mEndAlert, new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationX(mEndAlert, mEndAlert.getWidth());
            }
        });


        mConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    mService = IRemoteService.Stub.asInterface(service);
                    if (!mService.isPlaying() || mService.isNewPlay(music.path)) {
                        // 开启服务播放音乐
                        Intent playService = new Intent(PlayService.PLAY_ACTION);
                        playService.putExtra(PlayService.PLAY_MUSIC, music);
                        playService.putExtra(PlayService.PLAY_LIST_ID, mPlayListId);
                        playService.putExtra(PlayService.PLAY_POSITOIN, mPosition);
                        startService(playService);
                    }
                    mTimer.start();
                } catch (RemoteException e) {
                }
            }
        };

        Intent playService = new Intent(PlayService.PLAY_ACTION);
        playService.putExtra(PlayService.PLAY_MUSIC, music);
        playService.putExtra(PlayService.PLAY_POSITOIN, mPosition);
        playService.putExtra(PlayService.PLAY_LIST_ID, mPlayListId);
        bindService(playService, mConnection, Context.BIND_AUTO_CREATE);
        mMusicPlay.startPlay();
        registerForContextMenu(mAddMenu);
    }

    /**
     * 初始化复读分页
     */
    private void initPager() {
        ArrayList<View> views = new ArrayList<View>();

        mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SlideInRightAnimator itemAnimator = new SlideInRightAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(200);

        views.add(new View(this));
        views.add(mRecyclerView);
        setRepeatAdapter(music);

        mPager.setAdapter(new SimpleViewPagerAdapter<View>(views));
        mPager.setOnPageChangeListener(this);
    }

    /**
     * 检测己存在的复读列表,初始化该歌曲复读信息
     */
    private void setRepeatAdapter(Music music) {
        if (null == music) return;
        ArrayList<RepeatInfo> data = MyDb.getDatas(DbTable.REPEAT_URI, new RepeatInfo(), DbTable.REPEAT_SELECTION, "id=?", new String[]{String.valueOf(music.id)}, "position DESC");
        if (null == mRepeatAdapter) {
            mRepeatAdapter = new RepeatAdapter(this, data, mRecyclerView);
            mRecyclerView.setAdapter(mRepeatAdapter);
            mRepeatAdapter.setOnRepeatListener(new RepeatAdapter.OnRepeatListener() {

                @Override
                public void onRepeat(RepeatInfo repeatInfo) {
                    mProgressBar.setStart(repeatInfo.start);
                    mProgressBar.setEnd(repeatInfo.end);
                    try {
                        if (null != mService) {
                            mService.seekTo(repeatInfo.start * 1000);
                        }
                    } catch (RemoteException e) {
                    }
                }

                @Override
                public void onRecord(final RepeatInfo repeatInfo) {
                    //停止音乐播放
                    try {
                        if (null != mService) {
                            mService.pause();
                            mMusicPlay.stopPlay();
                        }
                    } catch (RemoteException e) {
                    }
                    RecordDialog recordDialog = RecordDialog.newInstance(repeatInfo);
                    recordDialog.setPostAction(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!mService.isPlaying()) {
                                    mService.play();
                                }
                            } catch (RemoteException e) {
                            }
                        }
                    });
                    recordDialog.show(getSupportFragmentManager(),null);
                }

                @Override
                public void startRecord() {
                    try {
                        if (mService.isPlaying()) {
                            mService.pause();
                        }
                    } catch (RemoteException e) {
                    }
                }

                @Override
                public void stopRecord() {
                    try {
                        if (!mService.isPlaying()) {
                            mService.play();
                        }
                    } catch (RemoteException e) {
                    }
                }
            });
        } else {
            mRepeatAdapter.clear();
            mRepeatAdapter.addItems(data);
        }
    }

    private void runOnGlobal(final View view, final Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = view.getWidth();
                int height = view.getHeight();
                if (0 != width && 0 != height) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    if (null != runnable) {
                        runnable.run();
                    }
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderIcon(R.drawable.ic_launcher);
        menu.setHeaderTitle(R.string.add_list);
        mPlayLists = MyDb.getDatas(DbTable.LIST_URI, new PlayList(), DbTable.LIST_SELECTION, null);
        if (null != mPlayLists) {
            //添加菜单项
            int size = mPlayLists.size();
            for (int i = 0; i < size; i++) {
                menu.add(ContextMenu.NONE, i, ContextMenu.NONE, mPlayLists.get(i).name);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        PlayList playList = mPlayLists.get(item.getItemId());
        RepeatUtils.insertMusic(music, playList.id, new Runnable() {
            @Override
            public void run() {
                showRightAlert(R.string.add_complate);
            }
        });
        return true;
    }

    /**
     * 设置播放信息
     */
    private void setPlayMusicInfo(Music music) {
        if (null == music) return;
        this.music = music;
        mMusicName.setText(music.name);
        mMusicTime.setText(DateUtils.getTimeString(music.duration));
        mProgressBar.setMax((int) (music.duration / 1000));
        mFavorite.setSelected(RepeatUtils.isFavoriteMusic(music));
        setRepeatAdapter(music);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
        if (null != mConnection) {
            unbindService(mConnection);
            mConnection = null;
        }
        //更新复读数据
        mRepeatAdapter.updateRepeatInfo();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_play:
                try {
                    if (null != mService) {
                        if (mService.isPlaying()) {
                            mService.pause();
                            mMusicPlay.stopPlay();
                        } else {
                            mService.play();
                            mMusicPlay.startPlay();
                        }
                        //更新播放状态
                        BusProvider.getInstance().post(new MusicPlayEvent(null, mService.isPlaying()));
                    }
                } catch (RemoteException e) {
                }
                break;
            case R.id.iv_music_rw:
                try {
                    if (null != mService && !mService.previous()) {
                        ViewPropertyAnimator.animate(mStartAlert).translationX(0).setDuration(300).setStartDelay(0).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ViewPropertyAnimator.animate(mStartAlert).translationX(-mStartAlert.getWidth()).setDuration(300).setStartDelay(1 * 1000);
                            }
                        });
                    }
                } catch (RemoteException e) {
                }
                break;
            case R.id.iv_music_ff:
                try {
                    if (null != mService && !mService.next()) {
                        showRightAlert(R.string.is_end);
                    }
                } catch (RemoteException e) {
                }
                break;
            case R.id.iv_music_add:
                openContextMenu(v);
                break;
            case R.id.iv_music_favorite:
                RepeatUtils.favoriteMusic(v, music, !music.isFavourite);
                break;
            case R.id.iv_play_mode:
                Integer mode = mRepeatModes.next();
                mPlayMode.setImageLevel(mode);
                PrefernceUtils.setInt(ConfigName.REPEAT_COUNT, mode);
                break;
            case R.id.iv_add_layout:
                // 添加复读记录
                int start = mProgressBar.getStart();
                int end = mProgressBar.getEnd();
                if (start != end) {
                    RepeatInfo lastItem = mRepeatAdapter.getLastItem();
                    RepeatInfo repeatInfo = new RepeatInfo(this.music.id, this.music.name,System.currentTimeMillis(), System.currentTimeMillis(), mProgressBar.getStart(), mProgressBar.getEnd(), 0, null != lastItem ? lastItem.position + 1 : 0, null, 0);
                    ViewPropertyAnimator.animate(mAddView).rotation(90 * mRepeatAdapter.getItemCount());
                    mRepeatAdapter.add(repeatInfo);
                } else {
                    showRightAlert(R.string.repeat_time_same);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 显示左边提示信息
     *
     * @param resId
     */
    private void showRightAlert(int resId) {
        mEndAlert.setText(resId);
        ViewPropertyAnimator.animate(mEndAlert).translationX(0).setDuration(300).setStartDelay(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewPropertyAnimator.animate(mEndAlert).translationX(mEndAlert.getWidth()).setDuration(300).setStartDelay(1 * 1000);
            }
        });
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        positionOffset = (1 == position) ? 1f : positionOffset;
        mProgressBar.setSwapFraction(1f - positionOffset);
        ViewHelper.setAlpha(mImageFlag, 0.1f + 0.9f * (1f - positionOffset));
        ViewHelper.setAlpha(mWaveView, 0.1f + 0.9f * (1f - positionOffset));
        ViewHelper.setTranslationX(mAddLayout, (App.sWidth - mAddLayout.getRight() + mAddLayout.getWidth()) * (1f - positionOffset));
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Subscribe
    public void onMusicEvent(final MusicPlayEvent event) {
        if (null != event) {
            Music music = event.getMusic();
            if (null != music) {
                setPlayMusicInfo(music);
                //设置音乐播放
                RepeatUtils.setMusicPlay(music);
            }
        }
    }
}
