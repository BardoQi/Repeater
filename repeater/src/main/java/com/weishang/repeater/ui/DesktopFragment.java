package com.weishang.repeater.ui;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.otto.Subscribe;
import com.weishang.repeater.App;
import com.weishang.repeater.MoreActivity;
import com.weishang.repeater.PlayActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.anim.AnimationUtils;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.annotation.ViewClick;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.PlayList;
import com.weishang.repeater.bean.ResultCode;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.FavoriteEvent;
import com.weishang.repeater.event.MusicPlayEvent;
import com.weishang.repeater.preference.ConfigManager;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.service.IRemoteService;
import com.weishang.repeater.service.PlayService;
import com.weishang.repeater.translation.TranslationHelper;
import com.weishang.repeater.ui.dialog.CreateListDialog;
import com.weishang.repeater.ui.dialog.MessageDialog;
import com.weishang.repeater.utils.Cn2Spell;
import com.weishang.repeater.utils.HandleTask;
import com.weishang.repeater.utils.ImageUtils;
import com.weishang.repeater.utils.Loger;
import com.weishang.repeater.utils.MedioUtils;
import com.weishang.repeater.utils.RepeatUtils;
import com.weishang.repeater.utils.RunnableUtils;
import com.weishang.repeater.utils.UnitUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.FixedGridLayout;
import com.weishang.repeater.widget.ImageViewFlat;
import com.weishang.repeater.widget.MusicImageView;
import com.weishang.repeater.widget.drawable.MusicDrawable;

import java.io.File;
import java.util.ArrayList;

/**
 * A fragment with a Google +1 button. Use the
 * {@link DesktopFragment#newInstance} factory method to create an instance of
 * this fragment.
 */
@Toolbar(displayHome = false)
@ViewClick(ids = {R.id.iv_local_file, R.id.iv_new_file, R.id.iv_recently_item, R.id.iv_music_favorite, R.id.rl_bottom_layout, R.id.iv_create_list, R.id.iv_recently_item})
public class DesktopFragment extends Fragment implements View.OnClickListener {
    @ID(id = R.id.fl_play_list)
    private FixedGridLayout mPlayListLayout;
    @ID(id = R.id.iv_music_name)
    private TextView mMusicName;
    @ID(id = R.id.iv_music_author)
    private TextView mMusicAuthor;
    @ID(id = R.id.iv_favorite, click = true)
    private ImageView mMeunFavorite;
    @ID(id = R.id.tv_add_count)
    private TextView mLocalFile;
    @ID(id = R.id.tv_new_count)
    private TextView mNewAdd;
    //最近播放数
    @ID(id = R.id.tv_recently_count)
    private TextView mRecentlyCount;

    @ID(id = R.id.iv_play, click = true)
    private MusicImageView mMenuPlay;
    @ID(id = R.id.iv_music_flag)
    private ImageView mMusicFlag;

    //引导控件
    private ShowcaseView mShowcaseView;
    @ID(id = R.id.iv_local_file)
    private View mTargetView1;
    private View mTargetView2;
    @ID(id = R.id.iv_create_list)
    private View mTargetView3;
    private int mStep;

    private MusicDrawable musicDrawable;
    private ArrayList<PlayList> mPlayLists;
    private Music music;

    private ServiceConnection mConnection;
    private IRemoteService mService;
    private int mPlayListId;//当前播放列表

    public static Fragment newInstance() {
        return new DesktopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_desktop, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BusProvider.getInstance().register(this);
        //初始化数据列表,更新数据列表
        updateMusic();
        //初始化封面
//        initCover();
        //初始化播放列表
        initPlayLists();
        //初始化最后播放条目
        initLastPlayLists();
        //设置收藏状态
        mMeunFavorite.setSelected(RepeatUtils.isFavoriteMusic(music));
        //设置最近播放数
        setListCount(mLocalFile, MyDb.DEFUALT_LIST, true);
        setListCount(mNewAdd, MyDb.NEW_ADD, true);
        setListCount(mRecentlyCount, MyDb.LAST_VISIBLE, true);
        updatePlayListCount();
        initData();
    }


    /**
     * 初始化封面
     */
    private void initCover() {
        RunnableUtils.runWithExecutor(new Runnable() {
            @Override
            public void run() {
                Context appContext = App.getAppContext();
                ContentResolver resolver = App.getResolver();
                Cursor cursor = resolver.query(DbTable.FILE_URI, DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(MyDb.DEFUALT_LIST)}, null);
                while (cursor.moveToNext()) {
                    long songId = cursor.getLong(1);
                    long albumId = cursor.getLong(5);
                    String fileName = songId + "_" + albumId + ".png";
                    File file = new File(ConfigManager.musicCover, fileName);
                    if (!file.exists()) {
                        Bitmap bitmap = MedioUtils.getArtwork(appContext, songId, albumId, false, false);
                        if (null != bitmap) {
                            ImageUtils.saveBitmap(bitmap, file.getName());
                            bitmap.recycle();
                        }
                    }
                }
                if (null != cursor) {
                    cursor.close();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        musicDrawable = new MusicDrawable.MusicDrawableBuild().
                setStrokeWidth(UnitUtils.dip2px(getActivity(), 2)).
                setColor(App.getResourcesColor(R.color.gray)).
                setBackgroudColor(App.getResourcesColor(R.color.white)).build();
        mMusicFlag.setImageDrawable(musicDrawable);
        //首次记动,则启动引导,否则则启动桌面动画
        if(PrefernceUtils.getRvsBoolean(ConfigName.GUIDE_DESKTOP)){
            mShowcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(new ViewTarget(mTargetView1))
                    .setContentTitle(R.string.guide_info1)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(getShowListener())
                    .build();
            mShowcaseView.setButtonText(getString(R.string.next));
        } else {
            TranslationHelper.startTranslation(this, R.layout.fragment_desktop);
        }
    }

    private View.OnClickListener getShowListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mStep) {
                    case 0:
                        mShowcaseView.setShowcase(new ViewTarget(mTargetView2), true);
                        mShowcaseView.setContentTitle(App.getStr(R.string.guide_info2));
                        break;
                    case 1:
                        mShowcaseView.setShowcase(new ViewTarget(mTargetView3), true);
                        mShowcaseView.setContentTitle(App.getStr(R.string.guide_info3));
                        break;
                    case 2:
                        mShowcaseView.setTarget(Target.NONE);
                        mShowcaseView.setContentTitle(App.getStr(R.string.guide_info4));
                        mShowcaseView.setButtonText(getString(R.string.complete));
                        setAlpha(0.4f, mTargetView1, mTargetView2, mTargetView3);
                        break;

                    case 3:
                        mShowcaseView.hide();
                        setAlpha(1, mTargetView1, mTargetView2, mTargetView3);
                        PrefernceUtils.setBoolean(ConfigName.GUIDE_DESKTOP,true);
                        break;
                }
                mStep++;
            }
        };
    }
    private void setAlpha(float alpha, View... views) {
        for (View view : views) {
            ViewHelper.setAlpha(view,alpha);
        }
    }

    /**
     * 初始化最后播放列表
     */
    private void initLastPlayLists() {
        final int musicId = PrefernceUtils.getInt(ConfigName.PLAY_ID);
        if (-1 != musicId) {
            HandleTask.run(new HandleTask.TaskAction<Music>() {
                @Override
                public Music run() {
                    int listId = PrefernceUtils.getInt(ConfigName.PLAY_LIST);
                    mPlayListId = (-1 == listId) ? MyDb.DEFUALT_LIST : listId;
                    String order = (listId == MyDb.DEFUALT_LIST) ? "word ASC" : "ut DESC";
                    return MyDb.getData(DbTable.FILE_URI, new Music(), DbTable.FILE_SELECTION, "list_id=? and id=?", new String[]{String.valueOf(mPlayListId), String.valueOf(musicId)}, order);
                }

                @Override
                public void postRun(Music music) {
                    DesktopFragment.this.music = music;
                    if (null != music) {
                        mMusicName.setText(music.name);
                        mMusicAuthor.setVisibility(View.VISIBLE);
                        mMusicAuthor.setText(music.author);
                        mConnection = new ServiceConnection() {

                            @Override
                            public void onServiceDisconnected(ComponentName name) {
                            }

                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                mService = IRemoteService.Stub.asInterface(service);
                            }
                        };
                        //开记服务
                        Intent playService = new Intent(PlayService.PLAY_ACTION);
                        playService.putExtra(PlayService.PLAY_MUSIC, music);
                        playService.putExtra(PlayService.PLAY_LIST_ID, mPlayListId);
                        playService.putExtra(PlayService.PREPARE_PLAY, true);
                        getActivity().bindService(playService, mConnection, Context.BIND_AUTO_CREATE);
                    }
                }
            });
        }
    }

    /**
     * 初始化用户播放列表
     */
    private void initPlayLists() {
        mPlayLists = MyDb.getDatas(DbTable.LIST_URI, new PlayList(), DbTable.LIST_SELECTION, null);
        if (null != mPlayLists && !mPlayLists.isEmpty()) {
            int length = mPlayLists.size();
            for (int i = 0; i < length; i++) {
                setPlayListData(i, mPlayLists.get(i));
            }
        }
    }


    /**
     * 设置播放列表数据
     *
     * @param index    当前位置
     * @param playList 列表对象
     */
    private void setPlayListData(final int index, final PlayList playList) {
        View.inflate(getActivity(), R.layout.repeat_list_item, mPlayListLayout);
        final View view = mPlayListLayout.getChildAt(index);
        View layout = view.findViewById(R.id.fv_list);
        TextView listName = (TextView) view.findViewById(R.id.tv_list_name);
        TextView listCount = (TextView) view.findViewById(R.id.tv_list_count);
        final MusicImageView listPlay = (MusicImageView) view.findViewById(R.id.iv_play_list);
        final ImageViewFlat layoutDelete = (ImageViewFlat) view.findViewById(R.id.iv_repeat_delete);
        view.setTag(listPlay);//记录播放按钮
        listName.setText(playList.name);
        setPlayListCount(playList, listCount);
        if (0 == index) {
            //第二个引导控件
            mTargetView2 = listPlay;
        }
        //TODO 获取音乐封面,动态展示封面内容
        listPlay.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //如果有音乐存在,就直接播放列表.没有,就打开列表
                        if (0 < playList.count) {
                            // 播放列表音乐
                            try {
                                //如果当前点击同一个列表,则执行,暂停,继续播放操作
                                if (mPlayListId == playList.id) {
                                    if (!mService.isPlaying()) {
                                        mService.play();
                                        listPlay.startPlay();
                                        //发送播放事件
                                        BusProvider.getInstance().post(new MusicPlayEvent(music, true));
                                    } else {
                                        mService.pause();
                                        listPlay.stopPlay();
                                        music.listId = mPlayListId;
                                        BusProvider.getInstance().post(new MusicPlayEvent(music, false));
                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), PlayService.class);
                                    intent.putExtra(PlayService.PLAY_LIST_ID, playList.id);
                                    getActivity().startService(intent);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Bundle args = new Bundle();
                            args.putInt(MusicListFragment.LIST_ID, playList.id);
                            args.putString(MusicListFragment.LIST_NAME, playList.name);
                            MoreActivity.toActivityforResult(DesktopFragment.this, getActivity(), MusicListFragment.class, args);
                        }
                    }
                }
        );
        layoutDelete.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = layoutDelete.getWidth();
                if (0 != width) {
                    layoutDelete.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutDelete.getLayoutParams();
                    ViewHelper.setTranslationX(layoutDelete, width + params.rightMargin);
                }
            }
        });
        //删除列表
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (0 == index) {
                    App.toast(R.string.favorite_delete_info);
                } else {
                    int width = listPlay.getWidth();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutDelete.getLayoutParams();
                    final int translationX = width + params.rightMargin;
                    ViewPropertyAnimator.animate(listPlay).translationX(translationX).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ViewPropertyAnimator.animate(layoutDelete).translationX(0).setListener(null);
                            layoutDelete.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ViewPropertyAnimator.animate(layoutDelete).translationX(translationX).setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            ViewPropertyAnimator.animate(listPlay).translationX(0).setListener(null);
                                        }
                                    });
                                }
                            }, 3 * 1000);
                        }
                    });
                }
                return true;
            }
        });

        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog messageDialog = MessageDialog.newInstance(App.getStr(R.string.delete_list), App.getStr(R.string.delete_list_info));
                messageDialog.setPositliveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //TODO 删除列表,所有录音文件
                        HandleTask.run(new HandleTask.TaskAction<Void>() {
                            @Override
                            public Void run() {
                                //删除列表
                                ContentResolver resolver = App.getResolver();
                                resolver.delete(DbTable.LIST_URI, "id=?", new String[]{String.valueOf(playList.id)});
                                //删除列表数据
                                resolver.delete(DbTable.FILE_URI, "list_id=?", new String[]{String.valueOf(playList.id)});
                                return null;
                            }

                            @Override
                            public void postRun(Void value) {
                                mPlayListLayout.removeView(view);
                                dialog.dismiss();
                            }
                        });
                    }
                });
                messageDialog.show(getFragmentManager(), null);
            }
        });
        //点击跳转到列表
        layout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Bundle args = new Bundle();
                            args.putInt(MusicListFragment.LIST_ID, playList.id);
                            args.putString(MusicListFragment.LIST_NAME, playList.name);
                            args.putInt(MusicListFragment.PLAY_LIST_ID, mPlayListId);
                            args.putBoolean(MusicListFragment.IS_PLAYING, mService.isPlaying());
                            MoreActivity.toActivityforResult(DesktopFragment.this, getActivity(), MusicListFragment.class, args);
                        } catch (RemoteException e) {
                        }
                    }
                }

        );
    }

    /**
     * 设置播放列表数
     *
     * @param playList
     * @param listCount
     */

    private void setPlayListCount(final PlayList playList, final TextView listCount) {
        HandleTask.run(new HandleTask.TaskAction<Integer>() {
            @Override
            public Integer run() {
                ContentResolver resolver = App.getResolver();
                Cursor cursor = resolver.query(DbTable.FILE_URI, null, "list_id=?", new String[]{String.valueOf(playList.id)}, null);
                int count = 0;
                if (null != cursor) {
                    count = cursor.getCount();
                    cursor.close();
                }
                return count;
            }

            @Override
            public void postRun(Integer count) {
                listCount.setText(App.getStr(R.string.file_count, count));
                playList.count = count;
            }
        });
    }

    private void updateMusic() {
        HandleTask.run(new HandleTask.TaskAction<Integer>() {
                           @Override
                           public Integer run() {
                               //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                               ContentResolver resolver = App.getResolver();
                               //音乐系统表
                               Cursor audioCursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                                               MediaStore.Audio.Media._ID,//id
                                               MediaStore.Audio.Media.TITLE,//标题
                                               MediaStore.Audio.Media.ALBUM_ID,//专辑
                                               MediaStore.Audio.Media.ARTIST,//歌手
                                               MediaStore.Audio.Media.DATA,//路径
                                               MediaStore.Audio.Media.DURATION,//播放时长
                                               MediaStore.Audio.Media.SIZE,//文件大小
                                               MediaStore.Audio.Media.DATE_ADDED//文件添加时间
                                       },
                                       null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                               int count = (null == audioCursor) ? 0 : audioCursor.getCount();
                               //自身
                               Cursor cursor = resolver.query(DbTable.FILE_URI, null, "list_id=?", new String[]{String.valueOf(MyDb.DEFUALT_LIST)}, null);
                               //当两个表数据不同时,更新列表
                               if (null != audioCursor && null != cursor && audioCursor.getCount() != cursor.getCount()) {
                                   //清空默认音乐表
                                   Loger.appendInfo("开始更新音乐:" + "原数量:" + cursor.getCount() + "个" + " 更新数据:" + audioCursor.getCount());
                                   resolver.delete(DbTable.FILE_URI, "list_id=?", new String[]{String.valueOf(MyDb.DEFUALT_LIST)});
                                   //当当前列表比系统提供少的话,代表有新数据添加,否则则是删除了数据
                                   insertMusicData(audioCursor);
                                   //记录更新时间
                                   PrefernceUtils.setLong(ConfigName.UPDATE_TIME, System.currentTimeMillis() / 1000);
                               }
                               return count;
                           }

                           @Override
                           public void postRun(Integer count) {
                               // 更新扫描总数
                               AnimationUtils.startIntAnimator(mLocalFile, R.string.total_file, count);
                           }
                       }
        );
    }

    /**
     * 插入音乐数据
     *
     * @param audioCursor
     */
    private void insertMusicData(Cursor audioCursor) {
        if (null == audioCursor) return;
        long updateTimeMillis = PrefernceUtils.getLong(ConfigName.UPDATE_TIME);
        ArrayList<ContentValues> values = new ArrayList<ContentValues>();
        while (audioCursor.moveToNext()) {
            String path = audioCursor.getString(4);
            File parentFile = new File(path).getParentFile();
            String name = audioCursor.getString(1);
            //检测字母
            String word = Cn2Spell.cn2First(name);
            //将其他所有字母变为大写:a-z +#
            char[] chars = word.toCharArray();
            char wordChar = 200;//大于z就行了
            if (0 < chars.length) {
                wordChar = chars[0];
                //小写字母
                if (96 < wordChar && 123 > wordChar) {
                    wordChar -= 32;
                } else if (65 > wordChar || 90 < wordChar) {
                    //不为大小写字母
                    wordChar = 200;
                }
            }
            if (-1 != updateTimeMillis && audioCursor.getLong(7) > updateTimeMillis) {
                //添加最新
                values.add(new Music(audioCursor.getInt(0),
                        System.currentTimeMillis(),
                        path,
                        name,
                        audioCursor.getString(2),
                        audioCursor.getLong(5),
                        audioCursor.getString(3),
                        audioCursor.getLong(6),
                        MyDb.NEW_ADD, 0, parentFile.getName(), parentFile.getAbsolutePath(),
                        String.valueOf(wordChar)).getContentValues());
            }
            //添加默认数据
            values.add(new Music(audioCursor.getInt(0),
                    System.currentTimeMillis(),
                    path,
                    name,
                    audioCursor.getString(2),
                    audioCursor.getLong(5),
                    audioCursor.getString(3),
                    audioCursor.getLong(6),
                    MyDb.DEFUALT_LIST, 0, parentFile.getName(), parentFile.getAbsolutePath(),
                    String.valueOf(wordChar)).getContentValues());
        }
        audioCursor.close();
        Loger.appendInfo("插入音乐:" + "数量:" + values.size() + "个");
        App.getResolver().bulkInsert(DbTable.FILE_URI, values.toArray(new ContentValues[values.size()]));
    }

    /**
     * 设置最近播放数
     */
    private void setListCount(final TextView textView, final int listId, final boolean startAnim) {
        HandleTask.run(new HandleTask.TaskAction<Integer>() {
            @Override
            public Integer run() {
                ContentResolver resolver = App.getResolver();
                Cursor cursor = resolver.query(DbTable.FILE_URI, null, "list_id=?", new String[]{String.valueOf(listId)}, null);
                return null != cursor ? cursor.getCount() : 0;
            }

            @Override
            public void postRun(Integer count) {
                if (startAnim) {
                    AnimationUtils.startIntAnimator(textView, R.string.file_count, count);
                } else {
                    textView.setText(App.getStr(R.string.file_count, count));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ResultCode.OPEN_MUSIC:
                //更新最近播放数
                setListCount(mRecentlyCount, MyDb.LAST_VISIBLE, true);
                break;
            case ResultCode.ADD_MUSIC:
                updatePlayListCount();
                break;
        }
    }

    /**
     * 更新播放列表数量
     */
    private void updatePlayListCount() {
        if (null != mPlayLists) {
            for (int i = 0; i < mPlayLists.size(); i++) {
                TextView listCount = (TextView) mPlayListLayout.getChildAt(i).findViewById(R.id.tv_list_count);
                setPlayListCount(mPlayLists.get(i), listCount);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Bundle extras;
        switch (view.getId()) {
            case R.id.iv_local_file:
                extras = new Bundle();
                extras.putInt(SortMusicListFragment.PARAMS_LIST_ID, MyDb.DEFUALT_LIST);
                extras.putString(SortMusicListFragment.PARAMS_TITLE, App.getStr(R.string.local_file));
                MoreActivity.toActivityforResult(this, getActivity(), SortMusicListFragment.class, extras);
                break;
            case R.id.iv_new_file:
                extras = new Bundle();
                extras.putInt(SortMusicListFragment.PARAMS_LIST_ID, MyDb.NEW_ADD);
                extras.putString(SortMusicListFragment.PARAMS_TITLE, App.getStr(R.string.new_files));
                MoreActivity.toActivityforResult(this, getActivity(), UseMusicListFragment.class, extras);
                break;
            case R.id.iv_recently_item:
                extras = new Bundle();
                extras.putInt(SortMusicListFragment.PARAMS_LIST_ID, MyDb.LAST_VISIBLE);
                extras.putString(SortMusicListFragment.PARAMS_TITLE, App.getStr(R.string.recently_play));
                MoreActivity.toActivityforResult(this, getActivity(), UseMusicListFragment.class, extras);
                break;
            case R.id.rl_bottom_layout:
                if (null != music) {
                    setPlayListAnim(mPlayListId, true);
                    Intent intent = new Intent(getActivity(), PlayActivity.class);
                    intent.putExtra(PlayActivity.PLAY_MUSIC, music);
                    intent.putExtra(PlayActivity.PLAY_LIST_ID, mPlayListId);
                    startActivity(intent);
                }
                break;
            case R.id.iv_favorite:
                if (null == music) return;
                music.isFavourite = !music.isFavourite;
                RepeatUtils.favoriteMusic(mMeunFavorite, music, music.isFavourite);
                updatePlayListCount();
                break;
            case R.id.iv_play:
                try {
                    if (null != music) {
                        if (!mService.isPlaying()) {
                            mService.play();
                            mMenuPlay.startPlay();
                            musicDrawable.startAnim();
                            setPlayListAnim(mPlayListId, true);
                        } else {
                            mService.pause();
                            mMenuPlay.stopPlay();
                            musicDrawable.stopAnim();
                            setPlayListAnim(mPlayListId, false);
                        }
                    }
                } catch (RemoteException e) {
                }
                break;
            case R.id.iv_music_rw:
                break;
            case R.id.iv_create_list:
                //创建列表
                final int count = (null != mPlayLists) ? mPlayLists.size() : 0;
                CreateListDialog listDialog = CreateListDialog.newInstance(count);
                listDialog.setOnEditListener(new CreateListDialog.OnEditListener() {
                    @Override
                    public void onEditComplete(String text) {
                        PlayList newList = new PlayList(count + 1, text);
                        if (null == mPlayLists) {
                            mPlayLists = new ArrayList<PlayList>();
                        }
                        mPlayLists.add(newList);
                        MyDb.insertWithNotFound(newList, DbTable.LIST_URI, "name=?", text);
                        setPlayListData(count, newList);
                    }
                });
                listDialog.show(getFragmentManager().beginTransaction());
                break;
        }
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mConnection) {
            getActivity().unbindService(mConnection);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mConnection) {
            Intent playService = new Intent(PlayService.PLAY_ACTION);
            playService.putExtra(PlayService.PLAY_MUSIC, music);
            playService.putExtra(PlayService.PLAY_LIST_ID, mPlayListId);
            playService.putExtra(PlayService.PREPARE_PLAY, true);
            getActivity().bindService(playService, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Subscribe
    public void onMusicEvent(final MusicPlayEvent musicPlayEvent) {
        if (null == musicPlayEvent) return;
        Music music = musicPlayEvent.getMusic();
        if (null != music) {
            this.music = music;
            //如果使用了不同列表,则更新播放音频,因为桌面下角的播放是一个通用的.在任一列表下,点击了播放,回到桌面,需要追随音乐
            if (mPlayListId != music.listId) {
                //控制当前播放按钮播放
                setPlayListAnim(mPlayListId, false);
                mPlayListId = music.listId;
            }
            mMusicName.setText(music.name);
            mMusicAuthor.setVisibility(View.VISIBLE);
            mMusicAuthor.setText(music.author);
            mMeunFavorite.setSelected(music.isFavourite);
        }
        //设置播放按钮状态
        mMenuPlay.post(
                new Runnable() {
                    @Override
                    public void run() {
                        if (musicPlayEvent.isPlaying()) {
                            musicDrawable.startAnim();
                            mMenuPlay.startPlay();
                        } else {
                            musicDrawable.stopAnim();
                            mMenuPlay.stopPlay();
                        }
                        //设置播放列表按钮状态
                        setPlayListAnim(mPlayListId, musicPlayEvent.isPlaying());
                    }
                }

        );
        //更新播放列表
        setListCount(mRecentlyCount, MyDb.LAST_VISIBLE, false);
    }

    private void setPlayListAnim(int listId, boolean isPlay) {
        if (MyDb.MY_FAVOURITE <= listId) {
            View childView = mPlayListLayout.getChildAt(listId - 1);
            MusicImageView musicImageView = (MusicImageView) childView.getTag();
            if (isPlay) {
                musicImageView.startPlay();
            } else {
                musicImageView.stopPlay();
            }
        }
    }

    @Subscribe
    public void onFavoriteEvent(FavoriteEvent event) {
        if (null == event) return;
        Music music = event.getMusic();
        if (null != music) {
            this.music = music;
            //更新收藏状态
            mMeunFavorite.setSelected(event.isFavorite());
            updatePlayListCount();
        }
    }
}
