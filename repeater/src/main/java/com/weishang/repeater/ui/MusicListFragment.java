package com.weishang.repeater.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.otto.Subscribe;
import com.weishang.repeater.App;
import com.weishang.repeater.MoreActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.adapter.AllMusicAdapter;
import com.weishang.repeater.anim.AnimationUtils;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.ViewClick;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.ResultCode;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.MusicPlayEvent;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.service.IRemoteService;
import com.weishang.repeater.service.PlayService;
import com.weishang.repeater.utils.RepeatUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.MusicImageView;
import com.weishang.repeater.widget.noborder.AlphaForegroundColorSpan;
import com.weishang.repeater.widget.noborder.KenBurnsView;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/11.
 * 歌曲列表
 */
@ViewClick(ids = {R.id.iv_back, R.id.tv_empty_info})
public class MusicListFragment extends Fragment implements View.OnClickListener {
    public static final String LIST_NAME = "list_name";
    public static final String LIST_ID = "list_id";
    public static final String PLAY_LIST_ID = "play_list_id";
    public static final String PLAY_POSITION = "play_position";
    public static final String IS_PLAYING = "is_playing";
    private int mActionBarTitleColor;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    @ID(id = R.id.view_icon)
    private View mMusicIcon;
    @ID(id = R.id.tv_list_name)
    private TextView mListNameView;
    @ID(id = R.id.listview)
    private ListView mListView;
    @ID(id = R.id.header_picture)
    private KenBurnsView mHeaderPicture;
    @ID(id = R.id.header)
    private View mHeader;
    @ID(id = R.id.tv_empty_info)
    private View mEmptyView;
    @ID(id = R.id.mv_music_btn, click = true)
    private MusicImageView mMusicImageView;
    private int mPosition;
    private boolean isPlaying;
    private Music music;
    private AllMusicAdapter mAdapter;
    private View mPlaceHolderView;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    private ServiceConnection mConnection;
    private IRemoteService mService;
    private CountDownTimer mTimer;
    private int mColor;

    private int mPlayListId;//正在播放listId
    private int mListId;//列表id;
    private String mListName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mListId = arguments.getInt(LIST_ID);
            mListName = arguments.getString(LIST_NAME);
            mPosition = arguments.getInt(PLAY_POSITION, -1);
            mPlayListId = arguments.getInt(PLAY_LIST_ID, -1);
            isPlaying = arguments.getBoolean(IS_PLAYING, false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BusProvider.getInstance().register(this);
        mListNameView.setText(mListName);
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mHeaderHeight + getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);

        mHeaderPicture.setResourceIds(R.drawable.picture0, R.drawable.picture1);
        mActionBarTitleColor = getResources().getColor(R.color.title_bg);

        mSpannableString = new SpannableString(mListName);
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);

        mColor = App.getResourcesColor(R.color.dark_yellow);
        mTimer = new CountDownTimer(Integer.MAX_VALUE, 10 * 1000) {
            @Override
            public void onTick(long l) {
                int resId = mHeaderPicture.getActiveImageResource();
                if (-1 == resId) return;
                Bitmap bitmap = BitmapFactory.decodeResource(App.getAppResources(), resId);
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        //有活力的颜色
                        final int vibrantColor = palette.getVibrantColor(mColor);
                        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1f);
                        valueAnimator.setDuration(5 * 1000);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int color = AnimationUtils.evaluate(valueAnimator.getAnimatedFraction(), mColor, vibrantColor);
                                mMusicImageView.setFlatBackgroud(color);
                            }
                        });
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mColor = vibrantColor;
                            }
                        });
                        valueAnimator.start();
                    }
                });
            }

            @Override
            public void onFinish() {
            }
        };
        mTimer.start();

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IRemoteService.Stub.asInterface(service);
            }
        };
        setupListView();
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
        Intent playService = new Intent(PlayService.PLAY_ACTION);
        playService.putExtra(PlayService.PLAY_MUSIC, music);
        playService.putExtra(PlayService.PLAY_LIST_ID, mPlayListId);
        playService.putExtra(PlayService.PLAY_POSITOIN, mPosition);
        getActivity().bindService(playService, mConnection, Context.BIND_AUTO_CREATE);
    }


    private void setupListView() {
        setListAdapter();
        mPlaceHolderView = getActivity().getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(mPlaceHolderView);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
                float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
                interpolate(mMusicImageView, mMusicIcon, mSmoothInterpolator.getInterpolation(ratio));
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                RepeatUtils.playMusic(getActivity(), i - mListView.getHeaderViewsCount(), mAdapter.getDatas());
            }
        });
    }

    private void setListAdapter() {
        ArrayList<Music> musics = MyDb.getDatas(DbTable.FILE_URI, new Music(), DbTable.FILE_SELECTION, "list_id=?", new String[]{String.valueOf(mListId)}, "ut DESC");
        if (null == mAdapter) {
            mAdapter = new AllMusicAdapter(getActivity(), musics);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.swrpDatas(musics);
        }
        //设置音乐显示状态
        mEmptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        //设定初始播放状态
        if (isPlaying && mPlayListId == mListId) {
            mMusicImageView.startPlay();
        }
    }

    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mListNameView.setText(mSpannableString);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        ViewHelper.setTranslationX(mListNameView, (1f - interpolation) * -mListNameView.getWidth());

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        ViewHelper.setTranslationX(view1, translationX);
        ViewHelper.setTranslationY(view1, translationY - ViewHelper.getTranslationY(mHeader));
        ViewHelper.setScaleX(view1, scaleX);
        ViewHelper.setScaleY(view1, scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }
        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ResultCode.ADD_MUSIC == resultCode) {
            //传回desktop
            getActivity().setResult(ResultCode.ADD_MUSIC);
            setListAdapter();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.mv_music_btn:
                try {
                    if (null != mAdapter && 0 < mAdapter.getCount()) {
                        if (mService.isPlaying()) {
                            mService.pause();
                            mMusicImageView.stopPlay();
                        } else {
                            mService.play();
                            mMusicImageView.startPlay();
                        }
                        BusProvider.getInstance().post(new MusicPlayEvent(null, mService.isPlaying()));
                        return;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            case R.id.tv_empty_info:
                Bundle args = new Bundle();
                args.putInt(LIST_ID, mListId);
                MoreActivity.toActivityforResult(this, getActivity(), LocalResFragment.class, args);
                break;
        }
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }


    @Subscribe
    public void onMusicEvent(final MusicPlayEvent musicPlayEvent) {
        if (null == musicPlayEvent) return;
        Music music = musicPlayEvent.getMusic();
        if (null != music) {
            this.music = music;
            try {
                if (!mService.isPlaying()) {
                    this.mMusicImageView.startPlay();
                }
            } catch (RemoteException e) {
            }
        }
    }

}
