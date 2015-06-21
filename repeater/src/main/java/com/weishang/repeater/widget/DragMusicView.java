package com.weishang.repeater.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.utils.ViewInject;


/**
 * Created by momo on 2015/3/22.
 * 拖动音乐标志
 * 0
 */
public class DragMusicView extends ViewGroup {
    private static final int MAX_SCROLLING_DURATION = 300;
    private static final int MIN_SCROLLING_DURATION = 80;
    @ID(id = R.id.view_transparent)
    private View mTranslationView;//隐藏view
    @ID(id = R.id.ll_panel)
    private View mPanelView;//展示面板
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mStartX;
    private int mCurrentPage;
    private float mFraction;
    private OnDragListener mListener;

    public DragMusicView(Context context) {
        this(context, null, 0);
    }

    public DragMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragMusicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.music_layout_item, this);
        ViewInject.init(this);
        mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return FloatMath.sin(f);
    }

    public void fling(int x, int velocity) {
        if (0 == getChildCount()) {
            setDrawingCacheEnabled(false);
            return;
        }
        int sx = getScrollX();
        int dx = x - sx;

        setDrawingCacheEnabled(true);
        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = (int) ((1f - velocity * 1.0f / mMaximumVelocity) * (MAX_SCROLLING_DURATION - MIN_SCROLLING_DURATION)) + MIN_SCROLLING_DURATION;
        } else {
            duration = MAX_SCROLLING_DURATION;
        }
        duration = Math.min(duration, MAX_SCROLLING_DURATION);
        mScroller.startScroll(sx, 0, dx, 0, duration);
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker(event);
        final int action = event.getAction();
        int width = getWidth();
        int x = (int) event.getX();
        int moveX = (mStartX + mCurrentPage * width - x);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mStartX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                //当页数为1时,且从右向左滑时.不允许滑动
                if (0 < moveX && moveX < width) {
                    mFraction = moveX * 1f / width;
                    scrollTo(moveX, 0);
                    if (null != mListener) {
                        mListener.onDrag(mFraction, 1f == mFraction);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > mMinimumVelocity) {
                    //从左向右
                    mCurrentPage = 0;
                    fling(0, velocityX);
                } else if (velocityX < -mMinimumVelocity) {
                    //从右向左
                    mCurrentPage = 1;
                    fling(getWidth(), velocityX);
                } else {
                    mScroller.startScroll(moveX, 0, moveX < width / 2 ? -moveX : width - moveX, 0);
                }
                releaseVelocityTracker();
                invalidate();

                break;
        }
        return true;
    }

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            scrollTo(currX, 0);
            mFraction = currX * 1f / getWidth();
            if (null != mListener) {
                mListener.onDrag(mFraction, 1f == mFraction);
            }
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), resolveSizeAndState(height, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int height = getHeight();
        mTranslationView.layout(0, 0, width, height);
        mPanelView.layout(width, 0, width * 2, height);
    }

    public View getPanel() {
        return mPanelView;
    }

    public void setOnDragListener(OnDragListener listener) {
        this.mListener = listener;
    }

    public interface OnDragListener {
        void onDrag(float fraction, boolean isOpen);
    }

}
