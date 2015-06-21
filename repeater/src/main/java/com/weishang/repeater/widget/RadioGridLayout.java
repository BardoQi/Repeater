package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.weishang.repeater.R;
import com.weishang.repeater.utils.UnitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆形多选控件
 *
 * @author momo
 * @Date 2015/2/12
 */
public class RadioGridLayout extends FixedGridLayout {
    // 三种选择状态
    public static final int _CLICK = 0;
    public static final int SINGLE_CHOOSE = 1;
    public static final int MORE_CHOOSE = 2;

    protected ArrayList<Integer> mSelectPosition;// 选中控件集
    protected int mPosition;// 选中位置
    protected View mCurrentSelectView;// 现在选中状态
    protected int mMode;// 选择状态

    private Bitmap mCheckBitmap;// 选中资源
    private Bitmap mDefaultBitmap;// 默认资源
    private float mCheckRigthPadding;//
    private float mCheckBottomPadding;
    private int mCheckSize;
    private boolean isShowDefault;// 是否显示默认check图片
    private Paint mPaint;//
    private OnCheckListener mCheckedListener;
    private View.OnClickListener mClickListener;// 单选事件

    public RadioGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPosition = -1;
        mPaint = new Paint();
        mSelectPosition = new ArrayList<Integer>();
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioGridLayout);
        setCheckRes(a.getResourceId(R.styleable.RadioGridLayout_rl_check_res, -1));
        setDefaultRes(a.getResourceId(R.styleable.RadioGridLayout_rl_default_res, -1));
        setCheckRightPadding(a.getDimension(R.styleable.RadioGridLayout_rl_check_right_padding, 0));
        setCheckBottomPadding(a.getDimension(R.styleable.RadioGridLayout_rl_check_bottom_padding, 0));
        setCheckWidth((int) a.getDimension(R.styleable.RadioGridLayout_rl_check_width, UnitUtils.dip2px(context, 10)));
        setMode(a.getInt(R.styleable.RadioGridLayout_rl_mode, _CLICK));
        a.recycle();
    }

    private void setCheckWidth(int width) {
        this.mCheckSize = width;
    }

    public RadioGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioGridLayout(Context context) {
        this(context, null, 0);
    }

    public void setCheckRes(int res) {
        if (-1 != res) {
            // 释放之前的bitmap资源
            recycleBitmaps(mCheckBitmap);
            mCheckBitmap = BitmapFactory.decodeResource(getResources(), res);
            invalidate();
        }
    }

    private void setDefaultRes(int res) {
        if (-1 != res) {
            // 释放之前的bitmap资源
            recycleBitmaps(mDefaultBitmap);
            mDefaultBitmap = BitmapFactory.decodeResource(getResources(), res);
        }
    }

    public void setCheckRightPadding(float rightPadding) {
        this.mCheckRigthPadding = rightPadding;
        invalidate();
    }

    public void setCheckBottomPadding(float bottomPadding) {
        this.mCheckBottomPadding = bottomPadding;
        invalidate();
    }

    public void setShowDefaultCheck(boolean isShow) {
        this.isShowDefault = isShow;
    }

    public void setMode(int mode) {
        this.mMode = mode;
        this.mPosition = -1;
        this.mSelectPosition.clear();
        invalidate();
    }

    public List<Integer> getSelectPositions() {
        return this.mSelectPosition;
    }

    public int getSelectPosition() {
        return mPosition;
    }

    private void recycleBitmaps(Bitmap... bitmaps) {
        for (int i = 0; i < bitmaps.length; i++) {
            if (null != bitmaps[i] && !bitmaps[i].isRecycled()) {
                bitmaps[i].recycle();
                bitmaps[i] = null;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        recycleBitmaps(mCheckBitmap);
        super.onDetachedFromWindow();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (null != mCheckBitmap) {
            drawSelectBitmap(canvas);
        }
    }

    private void drawSelectBitmap(Canvas canvas) {
        View childView = null;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            if (null != childView) {
                Rect outRect = new Rect(0, 0, mCheckSize, mCheckSize);
                int left = childView.getLeft();
                int width = childView.getWidth();
                int height = childView.getHeight();
                canvas.save();
                canvas.translate(left + (width - mCheckSize) - mCheckRigthPadding, childView.getTop() + (height - mCheckSize) - mCheckBottomPadding);
                if (mSelectPosition.contains(Integer.valueOf(i))) {
                    canvas.drawBitmap(mCheckBitmap, null, outRect, mPaint);
                } else if (isShowDefault && null != mDefaultBitmap) {
                    canvas.drawBitmap(mCheckBitmap, null, outRect, mPaint);
                }
                canvas.restore();
            }
        }
    }

    /**
     * 添加选中控件
     *
     * @param isSelect
     */
    public void addCheckView(View view, boolean isSelect) {
        if (null != view) {
            addView(view);
            if (isSelect) {
                mCurrentSelectView = view;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnClickListenerByMode(v);
                }
            });
        }
    }

    /**
     * 根据res_array资源_添加默认按钮
     *
     * @param id
     */
    public void addDefaultTextViewByResource(int id) {
        String[] values = getResources().getStringArray(id);
        int length = values.length;
        for (int i = 0; i < length; i++) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.circle_btn_selector);
            textView.setTextColor(Color.WHITE);
            textView.setText(values[i]);
            textView.setSingleLine();
            addCheckView(textView, 0 == i);
        }
    }

    /**
     * 根据选择状态设置控件点击
     *
     * @param v
     */
    protected void setOnClickListenerByMode(View v) {
        switch (mMode) {
            case _CLICK:
                if (null != mClickListener) {
                    mClickListener.onClick(v);
                }
                break;
            case SINGLE_CHOOSE:
                mCurrentSelectView = v;
                mSelectPosition.clear();
                int newPosition = indexOfChild(v);
                mSelectPosition.add(newPosition);
                if (null != mCheckedListener) {
                    mCheckedListener.checked(this, newPosition, -1 == mPosition ? 0 : mPosition);
                }
                mPosition = newPosition;
                invalidate();
                break;
            case MORE_CHOOSE:
                mCurrentSelectView = v;
                mPosition = indexOfChild(v);
                if (mSelectPosition.contains(Integer.valueOf(mPosition))) {
                    mSelectPosition.remove(Integer.valueOf(mPosition));
                } else {
                    mSelectPosition.add(mPosition);
                }
                if (null != mCheckedListener) {
                    mCheckedListener.multipleChoice(this, mSelectPosition);
                }
                invalidate();
                break;
            default:
                break;
        }
    }

    public void setCheckedListener(OnCheckListener listener) {
        this.mCheckedListener = listener;
    }

    /**
     * 设置子控件点击事件
     *
     * @param listener
     */
    public void setOnCheckClickListener(View.OnClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnCheckListener {
        void checked(View v, int newPosition, int oldPosition);

        void multipleChoice(View v, ArrayList<Integer> mChoicePositions);
    }

}
