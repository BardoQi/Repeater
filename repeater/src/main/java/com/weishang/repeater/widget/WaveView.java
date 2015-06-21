package com.weishang.repeater.widget;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.weishang.repeater.R;
import com.weishang.repeater.utils.UnitUtils;

/**
 * 自定义音乐播放背景,贝塞尔斯曲线波浪效果
 * 
 * @author momo
 * @Date 2015/3/1
 * 
 */
public class WaveView extends View {

	private static final int NONE=0;
	private static final int RANTANGLE = 1;// 底部方形
	private static final int CIRCLE = 2;// 底部圆形

	private int mColorAlpha;
	private int mWaveColor;
	private float mWaveHeight;
	private float mWaveSpeed;
	private float mOffset;
	private int mWaveType;// 底部绘制类型

	private Paint mPaint;
	private ArrayList<Path> mPaths;

	private ValueAnimator mValueAnimator;

	public WaveView(Context context) {
		this(context, null, 0);
	}

	public WaveView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData();
		setAttribute(context, attrs);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mPaint = new Paint();
		mPaths = new ArrayList<Path>();
	}

	/**
	 * 设置属性集
	 * 
	 * @param context
	 * @param attrs
	 */
	private void setAttribute(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
		setWaveAlpha(a.getInteger(R.styleable.WaveView_wave_alpha, (int) (255 * 0.9f)));
		setWaveColor(a.getColor(R.styleable.WaveView_wave_color, Color.GREEN));
		setWaveHeight(a.getDimension(R.styleable.WaveView_wave_height, UnitUtils.dip2px(context, 10)));
		setWaveCount(a.getInteger(R.styleable.WaveView_wave_count, 1));
		setWaveSpeed(a.getFloat(R.styleable.WaveView_wave_speed, 0.01f));
		setWaveType(a.getInt(R.styleable.WaveView_wave_type, NONE));
		a.recycle();
	}

	/**
	 * 设置底部绘制类型
	 * 
	 * @param type
	 */
	public void setWaveType(int type) {
		this.mWaveType = type;
		invalidate();
	}

	/**
	 * 设置绘制颜色alpha值
	 * 
	 * @param alpha
	 */
	public void setWaveAlpha(int alpha) {
		this.mColorAlpha = alpha;
		invalidate();
	}

	/**
	 * 设置波浪颜色
	 */
	public void setWaveColor(int color) {
		this.mWaveColor = color;
		invalidate();
	}

	/**
	 * 设置波浪个数
	 * 
	 * @param size
	 */
	public void setWaveCount(float size) {
		mPaths.clear();
		for (int i = 0; i < size; i++) {
			mPaths.add(new Path());
		}
		invalidate();
	}

	/**
	 * 设置波浪高
	 * 
	 * @param height
	 */
	public void setWaveHeight(float height) {
		this.mWaveHeight = height;
		mOffset = mWaveHeight * 0.1f;
		invalidate();
	}

	/**
	 * 设置波浪速度
	 * 
	 * @param speed
	 */
	public void setWaveSpeed(float speed) {
		this.mWaveSpeed = speed;
		invalidate();
	}

	/**
	 * calculate wave track
	 */
	private void calculatePath() {
		resetPath();
		if (mOffset > Float.MAX_VALUE) {
			mOffset = 0;
		} else {
			mOffset += mWaveSpeed;
		}
		int size = mPaths.size();
		for (int i = 0; i < size; i++) {
			Path path = mPaths.get(i);
			path.moveTo(0, mWaveHeight * 2);
			int width = getWidth();
			for (float x = 0, y = 0; x <= width; x += 25) {
				y = (float) (mWaveHeight * Math.sin(Math.PI / width * x + mOffset * (i + 1)) + mWaveHeight);
				path.lineTo(x, y);
			}
			path.lineTo(width, mWaveHeight * 2);
		}
	}

	/**
	 * 重置绘制路径
	 */
	private void resetPath() {
		for (Path path : mPaths) {
			path.reset();
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.reset();
		mPaint.setColor(mWaveColor);
		mPaint.setAlpha(mColorAlpha);
		// 绘波浪
		for (Path path : mPaths) {
			canvas.drawPath(path, mPaint);
		}
		// 绘底部形状
		int width = getWidth();
		int height = getHeight();
		switch (mWaveType) {
		case CIRCLE:
			RectF rectF = new RectF(0, mWaveHeight * 2, width, height);
			canvas.drawRect(rectF.left, rectF.top, rectF.right,mWaveHeight*2+height/2, mPaint);
			canvas.drawOval(rectF, mPaint);
			break;
		case RANTANGLE:
			canvas.drawRect(0, mWaveHeight * 2, width, height, mPaint);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (View.VISIBLE == visibility) {
			if (null == mValueAnimator) {
				mValueAnimator = ObjectAnimator.ofFloat(1f);
				mValueAnimator.setDuration(3 * 1000);
				mValueAnimator.setRepeatCount(-1);
				mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animator) {
						calculatePath();
						invalidate();
					}
				});
			}
			mValueAnimator.start();
		} else if (null != mValueAnimator) {
			mValueAnimator.cancel();
		}
	}
}
