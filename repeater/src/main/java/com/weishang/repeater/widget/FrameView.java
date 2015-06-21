package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.listener.ViewImageClickListener;
import com.weishang.repeater.utils.PackageUtil;
import com.weishang.repeater.utils.ViewInject;

/**
 * Created by momo on 2015/3/7.
 * 组合各种状态布局,局部空布局,网络error,加载布局体等.
 */
public class FrameView extends RelativeLayout {
    private static final int DELAY_MILLIS = 500;
    //各种状态桢标志
    public final static int CONTAINER_ITEM = 0;
    public final static int PROGRESS_ITEM = 1;
    public final static int EMPTY_ITEM = 2;
    public final static int ERROR_ITEM = 3;
    public final static int REPEAT_ITEM = 4;
    @ID(id = R.id.empty_container)
    private TextView mProgressView;
    @ID(id = R.id.empty_container)
    private TextView mEmptyView;
    @ID(id = R.id.error_container)
    private TextView mErrorView;
    @ID(id = R.id.repeat_container)
    private LinearLayout mRepeatLayout;// 重试
    @ID(id = R.id.load_info)
    private TextView mLoadInfo;
    @ID(id = R.id.iv_repeat_pic)
    private ImageView mRepeatView;
    @ID(id = R.id.tv_try)
    private TextView mRepeatInfo;
    private RelativeLayout mViewContainer;
    private Runnable mRepeatRunnable;
    // 当前布局显示状态
    private int lastItem;
    private ValueAnimator mProgressAnimator;

    public FrameView(Context context) {
        this(context, null, 0);
    }

    public FrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewContainer = new RelativeLayout(context);
        addView(mViewContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        View.inflate(getContext(), R.layout.frame_layout, this);
        ViewInject.init(this);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.FrameView);
        setProgressInfo(a.getString(R.styleable.FrameView_fv_progress_info));
        setEmptyDrawableResource(a.getResourceId(R.styleable.FrameView_fv_empty_res, R.drawable.no_data));
        setEmptyInfo(a.getString(R.styleable.FrameView_fv_empty_info));
        setErrorDrawableResource(a.getResourceId(R.styleable.FrameView_fv_error_res, R.drawable.no_data));
        setErrorInfo(a.getString(R.styleable.FrameView_fv_error_info));
        setRepeatDrawableResource(a.getResourceId(R.styleable.FrameView_fv_repeat_res, R.drawable.details_wifi_icon));
        setRepeatInfo(a.getString(R.styleable.FrameView_fv_repeat_info));
        //这里布局体还没有解析完,所以只能记录用户设置的显示桢,到OnFinishInflate完之后,再设置
        lastItem = a.getInt(R.styleable.FrameView_fv_frame, CONTAINER_ITEM);
        setRepeatListener();
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //这里,自定义的layout才解析完,这时能拿到用户自定义的xml布局体,他的位置在最后
        int index = REPEAT_ITEM + 1;//从此处起始,添加所有用户自定义view
        //这里不能用i++,因为removeView之后,角标自动前移.就能拿到后面的view
        for (int i = index; i < getChildCount();) {
            View childView = getChildAt(i);
            removeView(childView);
            mViewContainer.addView(childView);
        }
        setViewShown(lastItem, false, true);
    }

    /**
     * 设置加载进度信息
     *
     * @param info 加载提示信息
     */
    public void setProgressInfo(String info) {
        if (TextUtils.isEmpty(info)) {
            this.mProgressView.setText(info);
        }
    }


    public void setEmptyInfo(int resId) {
        if (null != mEmptyView) {
            mEmptyView.setText(resId);
        }
    }

    /**
     * 设置空布局信息
     *
     * @param info 空布局提示信息
     */
    public void setEmptyInfo(String info) {
        if (null != mEmptyView && !TextUtils.isEmpty(info)) {
            mEmptyView.setText(info);
        }
    }

    public void setEmptyDrawable(Drawable drawable) {
        if (null != mEmptyView) {
            mEmptyView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    public void setEmptyDrawableResource(int resId) {
        if (null != mEmptyView) {
            mEmptyView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
        }
    }

    /**
     * 设置空布局信息
     *
     * @param info
     */
    public void setErrorInfo(String info) {
        if (null != mErrorView && !TextUtils.isEmpty(info)) {
            mErrorView.setText(info);
        }
    }

    public void setErrorInfo(int resId) {
        if (null != mErrorView) {
            mErrorView.setText(resId);
        }
    }

    public void setErrorDrawable(Drawable drawable) {
        if (null != mErrorView) {
            mErrorView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    /**
     * 设置error提示标志
     *
     * @param resId 异常信息提示引用资源
     */
    public void setErrorDrawableResource(int resId) {
        if (null != mErrorView) {
            mErrorView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
        }
    }

    public void setLoadInfo(int resId) {
        if (null != mLoadInfo) {
            mLoadInfo.setText(resId);
        }
    }

    /**
     * 设置显示空布局体
     *
     * @param animate true:显示 false:隐藏
     */
    public void setEmptyShown(boolean animate) {
        setViewShown(EMPTY_ITEM, animate, false);
    }

    /**
     * 延迟设置空布局体
     *
     * @param animate
     */
    public void delayShowEmpty(final boolean animate) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setEmptyShown(animate);
            }
        }, DELAY_MILLIS);
    }

    /**
     * 设置进度装载显示
     *
     * @param animate true:显示 false:隐藏
     */
    public void setProgressShown(boolean animate) {
        setViewShown(PROGRESS_ITEM, animate, false);
    }

    /**
     * 设置重试布局是否显示
     *
     * @param animate true:显示 false:隐藏
     */
    public void setRepeatShown(boolean animate) {
        setViewShown(REPEAT_ITEM, animate, false);
    }

    /**
     * 设置重试跳转设置事件
     */
    public void setRepeatSetting() {
        setRepeatShown(true);
        setRepeatInfo(R.string.setting);
        mRepeatRunnable = new Runnable() {
            @Override
            public void run() {
                PackageUtil.startSetting(getContext());
            }
        };
    }

    /**
     * 设置重试事件
     *
     * @param runnable
     */
    public void setRepeatRunnable(Runnable runnable) {
        setRepeatShown(true);
        setRepeatInfo(R.string.pull_to_refresh_try);
        mRepeatRunnable = runnable;
    }

    /**
     * 设置指定透明度的进度条
     *
     * @param animate
     */
    public void setScaleProgressShown(boolean animate) {
        int item = PROGRESS_ITEM;
        lastItem = CONTAINER_ITEM;
        if (null != mViewContainer) {
            View showView = getChildAt(item);
            View closeView = getChildAt(lastItem);
            showView.setVisibility(View.VISIBLE);
            closeView.setVisibility(View.VISIBLE);
            if (animate) {
                ViewPropertyAnimator.animate(showView).setDuration(1000).setInterpolator(new AccelerateInterpolator()).alpha(0.4f);
            } else {
                showView.clearAnimation();
            }
            lastItem = item;
        }
    }

    /**
     * 延迟设置进度体
     *
     * @param animate
     */
    public void delayShowProgress(final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setProgressShown(animate);
            }
        }, DELAY_MILLIS);
    }

    /**
     * 延迟设置进度体
     *
     * @param animate
     */
    public void delayShowRepeat(final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setRepeatShown(animate);
            }
        }, DELAY_MILLIS);
    }

    /**
     * 设置错误布局体显示
     *
     * @param animate true:显示 false:隐藏
     */
    public void setErrorShown(boolean animate) {
        setViewShown(ERROR_ITEM, animate, false);
    }

    /**
     * 延迟设置错误布局体
     *
     * @param animate
     */
    public void delayShowError(final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setErrorShown(animate);
            }
        }, DELAY_MILLIS);
    }

    /**
     * 设置布局体显示
     *
     * @param animate true:显示 false:隐藏
     */
    public void setContainerShown(boolean animate) {
        setViewShown(CONTAINER_ITEM, animate, false);
    }

    /**
     * 延迟设置布局体
     *
     * @param animate
     */
    public void delayShowContainer(final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setContainerShown(animate);
            }
        }, DELAY_MILLIS);
    }

    /**
     * 设置重试提示图标信息
     *
     * @param resId 提示图标引用资源id
     */
    public void setRepeatDrawableResource(int resId) {
        this.mRepeatView.setImageResource(resId);
    }

    /**
     * 设置重试按钮文字
     *
     * @param info
     */
    public void setRepeatInfo(String info) {
        if (null != mRepeatInfo && !TextUtils.isEmpty(info)) {
            mRepeatInfo.setText(info);
        }
    }

    /**
     * 设置重试按钮文字
     *
     * @param resid
     */
    public void setRepeatInfo(int resid) {
        if (null != mRepeatInfo) {
            mRepeatInfo.setText(resid);
        }
    }

    /**
     * 设置空布局点击事件
     *
     * @param listener
     */
    public void setEmptyListener(OnClickListener listener) {
        if (null != mEmptyView) {
            mEmptyView.setOnClickListener(new ViewImageClickListener(listener));
        }
    }

    /**
     * 设置异常局点击事件
     *
     * @param listener
     */
    public void setErrorListener(OnClickListener listener) {
        if (null != mErrorView) {
            mErrorView.setOnClickListener(new ViewImageClickListener(listener));
        }
    }

    private void setRepeatListener() {
        if (null != mRepeatLayout) {
            mRepeatLayout.findViewById(R.id.tv_try).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mRepeatRunnable) {
                        mRepeatRunnable.run();
                    }
                }
            });
        }
    }

    public boolean isStatus(int status) {
        return lastItem == status;
    }

    /**
     * 设置展示桢view
     *
     * @param item      当前展示桢
     * @param animate   是否显示动画
     * @param unChecked 无须检测last==当前指定桢
     */
    private void setViewShown(final int item, final boolean animate, boolean unChecked) {
        if (null != mViewContainer && (unChecked || item != lastItem)) {
            View showView = getChildAt(item);
            View closeView = getChildAt(lastItem);
            if (animate) {
                ViewHelper.setAlpha(showView, 0f);
                ViewPropertyAnimator.animate(showView).setDuration(200).alpha(1f);
            } else {
                showView.clearAnimation();
            }
            closeView.setVisibility(View.INVISIBLE);
            showView.setVisibility(View.VISIBLE);
            if (PROGRESS_ITEM == item) {
                setProgressAnimationState(showView, true);
            } else if (PROGRESS_ITEM == lastItem) {
                setProgressAnimationState(showView, false);
            }
            lastItem = item;
        }
    }

    /**
     * 设置背景色
     *
     * @param color
     */
    public void setBackGroundColor(int color) {
        setBackgroundColor(color);
    }

    private void setProgressAnimationState(View showView, boolean isStart) {
        View view = showView.findViewById(R.id.progress);
        if (null != view && view instanceof WheelView) {
            final WheelView wheel = (WheelView) view;
            if (isStart) {
                if (null != mProgressAnimator) {
                    mProgressAnimator.cancel();
                    mProgressAnimator = null;
                }
                mProgressAnimator = ObjectAnimator.ofInt(360);
                mProgressAnimator.setDuration(3 * 1000);
                mProgressAnimator.setRepeatMode(ValueAnimator.RESTART);
                mProgressAnimator.setRepeatCount(-1);
                mProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        wheel.setProgress(Integer.valueOf(animation.getAnimatedValue().toString()));
                    }
                });
                mProgressAnimator.start();
            } else if (null != mProgressAnimator) {
                mProgressAnimator.cancel();
            }
        }
    }

}
