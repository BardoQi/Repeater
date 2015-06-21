package com.weishang.repeater.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.weishang.repeater.R;


/**
 * 托动控制类,ViewDragHelper使用示例
 *
 * @author momo
 * @Date 2015/2/11
 */
public class DragLayout extends ViewGroup {
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mTop;
    private float mDragOffset;
    private float mDragRange;
    private View mHnalder;
    private View mContent;
    private boolean isExpand;
    private ViewDragHelper mDragHelper;

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
    }

    public DragLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHnalder = findViewById(R.id.view_handle);
        mContent = findViewById(R.id.view_content);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        if (mDragHelper.isViewUnder(mContent, x, y)) {
            return super.onInterceptTouchEvent(ev);
        } else if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            mDragHelper.cancel();
            return false;
        } else {
            return mDragHelper.shouldInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        int action = MotionEventCompat.getActionMasked(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean isViewUnder = mDragHelper.isViewUnder(mHnalder, x, y);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                final int slop = mDragHelper.getTouchSlop();
                if (dx * dx + dy * dy < slop * slop && isViewUnder) {
                    if (mDragOffset == 0) {
                        smoothSlideTo(1f);
                    } else {
                        smoothSlideTo(0f);
                    }
                }
                break;
            default:
                break;
        }
        return isViewUnder;
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRange);
        if (mDragHelper.smoothSlideViewTo(mHnalder, 0, y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    @Override
    @SuppressLint("NewApi")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(DragLayout.resolveSizeAndState(width, widthMeasureSpec, 0), resolveSizeAndState(height, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = (int) mTop;
        int handleHeight = mHnalder.getMeasuredHeight();
        mDragRange = getHeight() - handleHeight;
        mHnalder.layout(0, top, mHnalder.getMeasuredWidth(), handleHeight + top);
        mContent.layout(0, handleHeight + top, r, b + top);

    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public class DragHelperCallback extends ViewDragHelper.Callback {

        public DragHelperCallback() {
            super();
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mHnalder == child;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }

        /**
         * 是否锁定边缘
         */
        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }

        /**
         * view横向拖动
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.min(Math.max(left, getLeft()), getWidth() - child.getWidth());
        }

        /**
         * 控制view纵向滑动
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Math.min(Math.max(top, getTop()), getHeight() - child.getHeight());
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mTop = top;
            mDragOffset = mTop / mDragRange;
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            invalidate();
        }

    }
}
