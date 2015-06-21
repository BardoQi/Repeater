package com.weishang.repeater.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.RecordInfo;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.MusicPlayEvent;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.HandleTask;

import java.util.ArrayList;

/**
 * 播放的服务对象
 *
 * @author momo
 * @Date 2015/2/19
 */
public class PlayService extends Service {
    public static final String PLAY_ACTION = "com.weishang.repeater.service.PlayService";
    public static final String PLAY_MUSIC = "music";
    public static final String PLAY_POSITOIN = "position";
    public static final String PLAY_LIST_ID = "list_id";
    public static final String PREPARE_PLAY = "prepare";
    private MediaPlayer mPlayer;
    private int mPlayListId;//播放列表id
    private ArrayList<Music> musics;
    private int mPosition;
    private Music mPlayMusic;
    private Music mNewMusic;
    private RecordInfo mRecordInfo;
    private CountDownTimer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayListId = -1;
        musics = new ArrayList<Music>();
        mPlayer = new MediaPlayer();
        mRecordInfo = new RecordInfo();
        mTimer = new CountDownTimer(Integer.MAX_VALUE, 1 * 1000) {
            @Override
            public void onTick(long l) {
                if (mPlayer.isPlaying()) {
                    mRecordInfo.time++;
                }
            }

            @Override
            public void onFinish() {
            }
        };
        mTimer.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        int playListId = intent.getIntExtra(PLAY_LIST_ID, -1);
        if (-1 != playListId) {
            initMusicList(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        initMusicList(intent);
        return new IRemoteService.Stub() {
            @Override
            public void play() {
                playMusic(mNewMusic);
            }

            @Override
            public void pause() {
                if (null != mPlayer && mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
            }

            @Override
            public void seekTo(int progress) throws RemoteException {
                if (null != mPlayer) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public boolean next() throws RemoteException {
                boolean result = false;
                int size = musics.size();
                if (mPosition + 1 < size) {
                    result = true;
                    playMusic(musics.get(++mPosition));
                }
                return result;
            }

            @Override
            public boolean previous() throws RemoteException {
                boolean result = false;
                if (mPosition - 1 >= 0) {
                    result = true;
                    playMusic(musics.get(--mPosition));
                }
                return result;
            }

            @Override
            public int getProgress() throws RemoteException {
                return mPlayer.getCurrentPosition();
            }

            @Override
            public boolean isPlaying() throws RemoteException {
                return mPlayer.isPlaying();
            }

            /**
             * 是否为新的播放文件
             */
            @Override
            public boolean isNewPlay(String path) throws RemoteException {
                return null != mPlayMusic && !TextUtils.isEmpty(mPlayMusic.path) && !TextUtils.isEmpty(path) && !mPlayMusic.path.equals(path);
            }

            @Override
            public boolean isPlayComplete() throws RemoteException {
                return !mPlayer.isPlaying();
            }

        };
    }

    private void initMusicList(Intent intent) {
        mNewMusic = intent.getParcelableExtra(PLAY_MUSIC);
        final int playListId = intent.getIntExtra(PLAY_LIST_ID, -1);
        final boolean isPrepare = intent.getBooleanExtra(PREPARE_PLAY, false);
        if (mPlayListId != playListId) {
            HandleTask.run(new HandleTask.TaskAction<ArrayList<Music>>() {
                @Override
                public ArrayList<Music> run() {
                    musics.clear();
                    if (MyDb.SINGLE_ITEM != playListId) {
                        String order = MyDb.DEFUALT_LIST == playListId ? "word ASC" : "ut DESC";
                        ArrayList<Music> datas = MyDb.getDatas(DbTable.FILE_URI, new Music(), DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(playListId)}, order);
                        if (null != datas) {
                            musics.addAll(datas);
                        }
                    } else {
                        musics.add(mNewMusic);
                    }
                    return musics;
                }

                @Override
                public void postRun(ArrayList<Music> musics) {
                    if (null != musics && !musics.isEmpty()) {
                        if (!isPrepare) {
                            mNewMusic = (null == mNewMusic) ? musics.get(0) : mNewMusic;
                            playMusic(mNewMusic);
                        }
                    }
                }
            });
        } else if (!musics.isEmpty()) {
            if (!isPrepare) {
                playMusic(mNewMusic);
            }
        }
    }

    private void playMusic(final Music newMusic) {
        if (null == newMusic) return;
        if (null == this.mPlayMusic || (null != this.mPlayMusic && !newMusic.equals(this.mPlayMusic)) || mPlayMusic.listId != newMusic.listId) {
            // 当两个播放文件不同时,切换播放文件
            this.mPlayMusic = this.mNewMusic = newMusic;
            mPosition = this.musics.indexOf(newMusic);
            mPlayListId = newMusic.listId;
            PrefernceUtils.setInt(ConfigName.PLAY_ID, newMusic.id);
            PrefernceUtils.setInt(ConfigName.PLAY_LIST, newMusic.listId);
            //记录播放信息
            if (null != mRecordInfo && !TextUtils.isEmpty(mRecordInfo.name)) {
                //更新播放信息
                MyDb.repleceData(mRecordInfo, DbTable.RECORD_URI, "id=?", String.valueOf(mRecordInfo.id));
            }
            //记录正在播放对象id,name
            RecordInfo recordInfo = MyDb.getData(DbTable.RECORD_URI, mRecordInfo, DbTable.RECORD_SELECTION, "id=? and ct>?", String.valueOf(newMusic.id), String.valueOf(DateUtils.getToDayStartMillis()));
            if (null != recordInfo) {
                //如果己存在统计信息
                this.mRecordInfo = recordInfo;
            } else {
                //记录新的音乐信息
                this.mRecordInfo.id = newMusic.id;
                this.mRecordInfo.name = newMusic.name;
                this.mRecordInfo.time = 0;
                this.mRecordInfo.ct = System.currentTimeMillis();
            }
            mPlayer.reset();
            try {
                mPlayer.setDataSource(mPlayMusic.path);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 初始化进度,等其他操作
                        mPlayer.start();
                        //开始记录播放时间
                        BusProvider.getInstance().post(new MusicPlayEvent(mPlayMusic, true));
                    }
                });
                mPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //按设置进行循环/随机/停止操作,读到最后一个,回到第0个
                        int size = musics.size();
                        mPosition = (size > mPosition + 1) ? ++mPosition : 0;
                        Music music = musics.get(mPosition);
                        playMusic(music);
                        //替换播放事件
                        BusProvider.getInstance().post(new MusicPlayEvent(music, true));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (null != mPlayer) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
        if (null != mRecordInfo) {
            //更新播放信息
            MyDb.repleceData(mRecordInfo, DbTable.RECORD_URI, "id=?", String.valueOf(mRecordInfo.id));
        }
        super.onDestroy();
    }

}
