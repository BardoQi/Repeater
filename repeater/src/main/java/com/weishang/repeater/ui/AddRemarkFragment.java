package com.weishang.repeater.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.anim.AnimationUtils;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.annotation.MethodClick;
import com.weishang.repeater.annotation.Toolbar;
import com.weishang.repeater.bean.Remark;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.AddRemarkEvent;
import com.weishang.repeater.listener.BackListener;
import com.weishang.repeater.listener.SimpleTextWatcher;
import com.weishang.repeater.preference.ConfigName;
import com.weishang.repeater.preference.PrefernceUtils;
import com.weishang.repeater.provider.BusProvider;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.ImageUtils;
import com.weishang.repeater.utils.UnitUtils;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.DivideLinearLayout;
import com.weishang.repeater.widget.ImageViewFlat;
import com.weishang.repeater.widget.MusicProgressBar;
import com.weishang.repeater.widget.RadioGridLayout;

import java.util.ArrayList;

/**
 * Created by momo on 2015/4/25.
 * 添加备忘界面
 */
@Toolbar(title = R.string.add_remark)
public class AddRemarkFragment extends Fragment implements View.OnClickListener, BackListener {
    public static final int UPDATE_ITEM = 2;
    private static final int MIN_TEXT_FONT = 15;
    @ID(id=R.id.toolbar)
    private android.support.v7.widget.Toolbar mToolBar;
    @ID(id = R.id.rl_container)
    private View mContainer;
    @ID(id = R.id.et_remark_title)
    private EditText mTitleEditor;
    @ID(id = R.id.et_content_editor)
    private EditText mContentEditor;
    @ID(id = R.id.tv_mod_time)
    private TextView modTime;
    @ID(id = R.id.ll_option_container)
    private View mOptionContainer;
    @ID(id = R.id.ll_remark_title)
    private DivideLinearLayout mDivideContainer;
    @ID(id = R.id.view_divier2)
    private View view2;
    @ID(id = R.id.mp_font_size)
    private MusicProgressBar mFontProgress;
    @ID(id = R.id.iv_add_remark, click = true)
    private ImageViewFlat mAddRemark;
    @ID(id = R.id.iv_remark_set, click = true)
    private ImageView mRemarkSet;
    @ID(id = R.id.ll_option_panel)
    private View mLayout;
    @ID(id = R.id.rl_color)
    private RadioGridLayout mGridLayout;
    private CountDownTimer mTimer;
    private Remark mRemark;
    private int[] mColors;
    private int[] mFontColor;
    private int mTextFont;
    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mRemark = getArguments().getParcelable("item");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_remark, container, false);
        ViewInject.init(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewInject.initToolBar(this,mToolBar);
        preAnim();
        addColorItem();
        mTextFont = MIN_TEXT_FONT;
        mFontProgress.setOnProgressChangeListener(new MusicProgressBar.OnProgressChangeListener() {
            @Override
            public void onSeekChange(MusicProgressBar progressBar, int progress) {
                //15sp-30sp;
                mTextFont = MIN_TEXT_FONT + progress;
                mContentEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextFont);
            }
        });
        if (null != mRemark) {
            mTitleEditor.setText(mRemark.title);
            mTitleEditor.setSelection(mRemark.title.length());
            mContentEditor.setText(mRemark.content);
            mContentEditor.setSelection(mRemark.content.length());
            mContentEditor.setTextSize(mRemark.size);
            mAddRemark.setVisibility(View.INVISIBLE);
            ValueAnimator animator = ObjectAnimator.ofFloat(1f);
            animator.setDuration(800);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setViewColor(valueAnimator.getAnimatedFraction(), 0, mRemark.bg);
                }
            });
            animator.start();
            mPosition = mRemark.bg;
        } else {
            String cacheTitle = PrefernceUtils.getString(ConfigName.REMARK_TITLE);
            String cacheContent = PrefernceUtils.getString(ConfigName.REMARK_CONTENT);
            int color=PrefernceUtils.getInt(ConfigName.REMARK_COLOR);
            color=(-1==color)?0:color;
            if (!TextUtils.isEmpty(cacheTitle)) {
                mTitleEditor.setText(cacheTitle);
                mTitleEditor.setSelection(cacheTitle.length());
            }
            if (!TextUtils.isEmpty(cacheContent)) {
                mContentEditor.setText(cacheContent);
                mContentEditor.setSelection(cacheContent.length());
            }
            final int fColor=color;
            ValueAnimator animator = ObjectAnimator.ofFloat(1f);
            animator.start();
            animator.setDuration(800);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setViewColor(valueAnimator.getAnimatedFraction(), 0, fColor);
                }
            });
            mAddRemark.setVisibility(View.VISIBLE);
        }
        SimpleTextWatcher watcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                boolean isEnable = !TextUtils.isEmpty(mTitleEditor.getText()) && !TextUtils.isEmpty(mContentEditor.getText());
                mAddRemark.setEnabled(isEnable);
                mAddRemark.setFlatColor(App.getResourcesColor(isEnable ? R.color.white : R.color.gray));
            }
        };
        mTitleEditor.addTextChangedListener(watcher);
        mContentEditor.addTextChangedListener(watcher);

        mTimer = new CountDownTimer(Integer.MAX_VALUE, 5 * 1000) {
            @Override
            public void onTick(long l) {
                PrefernceUtils.setString(ConfigName.REMARK_TITLE, mTitleEditor.getText().toString());
                PrefernceUtils.setString(ConfigName.REMARK_CONTENT, mContentEditor.getText().toString());
                modTime.setText(App.getStr(R.string.last_mod_time, DateUtils.getFromat("HH:mm:ss", System.currentTimeMillis())));
            }

            @Override
            public void onFinish() {
            }
        };
        mTimer.start();
    }

    private void addColorItem() {
        mColors = new int[]{0xFFFFFAE6, 0xFFF7EAD0, 0xFFE7D3AA, 0xFFDECFB2, 0xFFA5A395, 0xFFE7E6BA, 0xFFCEEABA, 0xFF3E4F43, 0xFF293B2C, 0xFF293343};
        mFontColor = new int[]{0xFF312F2C, 0xFF393325, 0xFF393340, 0xFF393365, 0xFF2D2C29, 0xFF424433, 0xFF46573F, 0xFF909F8D, 0xFF90A487, 0xFFADB6B2};
        for (int i = 0; i < mColors.length; i++) {
            mGridLayout.addCheckView(getColorView(mColors[i]), mPosition == i);
        }
        mGridLayout.setCheckedListener(new RadioGridLayout.OnCheckListener() {
            @Override
            public void checked(View v, final int newPosition, final int oldPosition) {
                mPosition = newPosition;
                PrefernceUtils.setInt(ConfigName.REMARK_COLOR,newPosition);
                AnimationUtils.startFractionAnim(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setViewColor(valueAnimator.getAnimatedFraction(), oldPosition, newPosition);

                    }
                });
            }

            @Override
            public void multipleChoice(View v, ArrayList<Integer> mChoicePositions) {
            }
        });
    }

    /**
     * 设置控件颜色
     *
     * @param fraction
     */
    private void setViewColor(float fraction, int oldPosition, int newPosition) {
        //设置背景颜色
        int color = AnimationUtils.evaluate(fraction, mColors[oldPosition], mColors[newPosition]);
        mContainer.setBackgroundColor(color);
        mOptionContainer.setBackgroundColor(color);
        mGridLayout.setBackgroundColor(color);
        mToolBar.setBackgroundColor(color);
        //设置字体颜色
        color = AnimationUtils.evaluate(fraction, mFontColor[oldPosition], mFontColor[newPosition]);
        mTitleEditor.setTextColor(color);
        mContentEditor.setTextColor(color);
        mToolBar.setTitleTextColor(color);
        modTime.setTextColor(color);
        ImageUtils.setDrawableScale(mToolBar.getNavigationIcon(),color);
    }

    private void preAnim() {
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = mLayout.getHeight();
                if (0 != height) {
                    mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewCompat.setTranslationY(mLayout, height);
                }
            }
        });

    }

    private View getColorView(int color) {
        ImageView imageView = new ImageView(getActivity());
        Resources appResources = App.getAppResources();
        Drawable drawable = appResources.getDrawable(R.drawable.color_item);
        if (drawable instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            stateListDrawable.selectDrawable(0);
            Drawable current = stateListDrawable.getCurrent();
            if (current instanceof LayerDrawable) {
                LayerDrawable layer = (LayerDrawable) current;
                GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.oval_select);
                shape.setStroke(UnitUtils.dip2px(getActivity(), 1), getDarkColor(color));
                shape.setColor(color);
            }
            stateListDrawable.selectDrawable(1);
            current = stateListDrawable.getCurrent();
            if (current instanceof LayerDrawable) {
                LayerDrawable layer = (LayerDrawable) current;
                GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.oval_default);
                shape.setColor(color);
            }
        }
        imageView.setImageDrawable(drawable);
        return imageView;
    }

    private int getDarkColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.rgb(r + 30 > 0xFF ? r - 30 : r + 30, g + 30 > 0xFF ? g - 30 : g + 30, b + 30 > 0xFF ? b - 30 : b + 30);
    }

    @Override
    @MethodClick(ids = {R.id.view_block, R.id.iv_chararter_dec, R.id.iv_chararter_inc})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_remark:
//                "_id", "id", "title", "content", "ct", "ut","size","color","line_color","line_size","bg","mood"
                Remark remark = new Remark(0,
                        mTitleEditor.getText().toString(),
                        mContentEditor.getText().toString(),
                        System.currentTimeMillis(),
                        mTextFont,
                        mPosition,
                        mPosition,
                        0,
                        mPosition, 1);
                MyDb.insertData(remark, DbTable.REMARK_URI);
                PrefernceUtils.setString(ConfigName.REMARK_TITLE, null);
                PrefernceUtils.setString(ConfigName.REMARK_CONTENT, null);
                PrefernceUtils.setInt(ConfigName.REMARK_COLOR,-1);
                BusProvider.getInstance().post(new AddRemarkEvent(remark));
                getActivity().finish();
                break;
            case R.id.iv_remark_set:
                //打开书签设置
                int width = (int) App.sWidth;
                ViewCompat.animate(mLayout).translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600);
                ViewCompat.animate(mRemarkSet).translationX(width - mRemarkSet.getLeft()).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
                ViewCompat.animate(mAddRemark).translationX(width - mAddRemark.getLeft()).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).setStartDelay(100);
                break;
            case R.id.view_block:
                ViewCompat.animate(mLayout).translationY(mLayout.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600);
                ViewCompat.animate(mRemarkSet).translationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
                ViewCompat.animate(mAddRemark).translationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).setStartDelay(100);
                break;
            case R.id.iv_chararter_dec:
                if (mTextFont - 1 >= MIN_TEXT_FONT) {
                    mContentEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, --mTextFont);
                    mFontProgress.setProgress(mTextFont - MIN_TEXT_FONT);
                }
                break;
            case R.id.iv_chararter_inc:
                if (mTextFont + 1 <= 30) {
                    mContentEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, ++mTextFont);
                    mFontProgress.setProgress(mTextFont - MIN_TEXT_FONT);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (null != mTimer) {
            mTimer.cancel();
            modTime = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onBack() {
        //更新内容
        mRemark.size = mTextFont;
        mRemark.title = mTitleEditor.getText().toString();
        mRemark.content = mContentEditor.getText().toString();
        mRemark.bg = mPosition;
        mRemark.color = mPosition;
        mRemark.ut = System.currentTimeMillis();
        ContentResolver resolver = App.getResolver();
        resolver.update(DbTable.REMARK_URI, mRemark.getContentValues(), "_id=?", new String[]{String.valueOf(mRemark.id)});
        Intent data = new Intent();
        data.putExtra("item", mRemark);
        getActivity().setResult(UPDATE_ITEM, data);
        getActivity().finish();
    }
}
