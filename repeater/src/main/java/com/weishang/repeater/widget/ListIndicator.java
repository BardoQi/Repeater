package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.weishang.repeater.R;


/**
 * Created by momo on 2015/3/19.
 * ListView 侧边指示器
 * 1:指示图形. 圆,方
 * 2:指示选择颜色
 * 3:指示器字体大小
 * 4:指示器字体颜色
 */
public class ListIndicator extends View {
    private static final int NONE = 0x00;
    private static final int RECTANGLE = 0x01;
    private static final int CIRCLE = 0x02;
    private String[] mDatas;
    private int mType;
    private int mColor;
    private int mTextColor;
    private float mTextSize;
    private int mTextSelectColor;
    private Paint mPaint;
    private int mPosition;//选中位置
    private OnIndicatorListener mListener;

    public ListIndicator(Context context) {
        this(context, null, 0);
    }

    public ListIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListIndicator);
        setType(a.getInt(R.styleable.ListIndicator_li_type, CIRCLE));
        setColor(a.getColor(R.styleable.ListIndicator_li_color, Color.GREEN));
        setTextColor(a.getColor(R.styleable.ListIndicator_li_text_color, Color.DKGRAY));
        setTextSelectColor(a.getColor(R.styleable.ListIndicator_li_select_color, Color.RED));
        setTextSize(a.getDimension(R.styleable.ListIndicator_li_text_size, getResources().getDimension(R.dimen.small_text)));
        a.recycle();
    }

    /**
     * 设置绘制类型
     *
     * @param type 绘制类型
     * @see {@link ListIndicator#RECTANGLE#CIRCLE}
     */
    private void setType(int type) {
        this.mType = type;
        invalidate();
    }

    /**
     * 设置绘制边框颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    /**
     * 设置字体颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        this.mTextColor = color;
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        invalidate();
    }

    /**
     * 设置字体选中颜色
     *
     * @param color
     */
    public void setTextSelectColor(int color) {
        this.mTextSelectColor = color;
        invalidate();
    }

    /**
     * 设置绘制数据列表
     *
     * @param data
     */
    public void setData(String[] data) {
        this.mDatas = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mDatas) return;
        mPaint.reset();
        mPaint.setAntiAlias(true);
        int width = getWidth();
        int height = getHeight();
        int length = mDatas.length;
        int itemHeight = height / length;
        Rect bounds = new Rect();
        for (int i = 0; i < length; i++) {
            if (i == mPosition && NONE != mType) {
                //绘图形
                mPaint.setColor(mColor);
                RectF rect = null;
                if (width > itemHeight) {
                    rect = new RectF((width - itemHeight) / 2, itemHeight * i, width - (width - itemHeight) / 2, itemHeight * (i + 1));
                } else {
                    rect = new RectF(0, itemHeight * i + (itemHeight - width) / 2, width, itemHeight * (i + 1) - (itemHeight - width) / 2);
                }
                switch (mType) {
                    case RECTANGLE:
                        canvas.drawRect(rect, mPaint);
                        break;
                    case CIRCLE:
                        canvas.drawOval(rect, mPaint);
                        break;
                }
            }
            //绘文字
            mPaint.setColor(i == mPosition ? mTextSelectColor : mTextColor);
            mPaint.setTextSize(mTextSize);
            String data = mDatas[i];
            mPaint.getTextBounds(data, 0, data.length(), bounds);
            float x = width / 2 - bounds.centerX();
            float y = itemHeight * i + itemHeight / 2 - bounds.centerY();
            canvas.drawText(mDatas[i], x, y, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == mDatas) return true;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (null != mListener) {
                    mListener.onPress();
                }
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                int position = getPosition(y);
                if (0 > position) {
                    position = 0;
                } else if (position >= mDatas.length) {
                    position = mDatas.length - 1;
                }
                if (position != mPosition && null != mListener) {
                    mListener.onSelect(mDatas[position], position);
                }
                this.mPosition = position;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (null != mListener) {
                    mListener.onCancel();
                }
                break;
        }
        return true;
    }

    public void setSelectPosition(int position) {
        if (0 > position || position >= mDatas.length) return;
        this.mPosition = position;
        invalidate();
    }

    private int getPosition(float y) {
        return (int) y / (getHeight() / mDatas.length);
    }

    public String getWrodByPosition(int position) {
        return mDatas[position];
    }

    public void setOnIndicatorListener(OnIndicatorListener listener) {
        this.mListener = listener;
    }

    public interface OnIndicatorListener {
        /**
         * 开始按下
         */
        void onPress();

        /**
         * 选中位置
         *
         * @param word
         * @param position
         */
        void onSelect(String word, int position);

        /**
         * 取消
         */
        void onCancel();
    }
}
