package com.weishang.repeater.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.weishang.repeater.R;
import com.weishang.repeater.anim.AnimationUtils;
import com.weishang.repeater.utils.UnitUtils;

/**
 * 首页顶部滚动条，配合viewpager使用。
 * 
 * @author momo
 * @Date 2015/1/20
 * 
 */
public class PagerStrip extends HorizontalScrollView implements OnPageChangeListener {

	private static final int DEFAULT_LINE_WIDTH = 1;
	private static final int DEFAULT_ITEM_PADDING = 15;
	private static final int TAB_VIEW_TEXT_SIZE_SP = 16;
	private static final int INDICATOR_HEIGHT = 2;
	private int mDefaultColor;// 默认颜色
	private Paint mPaint = null;
	private int mIndicatorColor;// 指针颜色
	private Bitmap mIndicatorBitmap = null;
	private int mTextColor;// 文字默认颜色
	private int mTextSelectColor;// 文字选中颜色
	private int mDividerColor;// 分隔线颜色
	private int mUnderlineColor;// 底部绘制线颜色
	private int mItemPadding;// 条目左右边距
	private LinearLayout mTabsContainer;// HorizontalScrollView容器

	private ViewPager mPager;
	private int mTabCount;
	private int mPosition;
	private int mSelectPosition;
	private float mScale;
	private float mPositionOffset;
	private int mScrollOffset;// 移动偏移量
	private int mLastScrollX;
	private OnPageChangeListener mPageChangeListener;
	private boolean isExpand;

	public PagerStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWillNotDraw(false);
		mPaint = new Paint();
		mTabsContainer = new LinearLayout(context);
		mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		mTabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTabsContainer);
		initAttrs(context, attrs);
	}

	public PagerStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerStrip(Context context) {
		this(context, null, 0);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		Resources resources = getResources();
		mDefaultColor = android.R.color.white;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagerStrip);
		setTextColor(resources.getColor(a.getResourceId(R.styleable.PagerStrip_pagerstrip_text_color, mDefaultColor)));
		setTextSelectColor(resources.getColor(a.getResourceId(R.styleable.PagerStrip_pagerstrip_text_selected, R.color.blue)));
		setDiviersColor(resources.getColor(a.getResourceId(R.styleable.PagerStrip_pagerstrip_item_divier, android.R.color.transparent)));
		setUnderlineColor(resources.getColor(a.getResourceId(R.styleable.PagerStrip_pagerstrip_underline_color, mDefaultColor)));
		setItemPadding(a.getDimension(R.styleable.PagerStrip_pagerstrip_item_padding, UnitUtils.dip2px(context, DEFAULT_ITEM_PADDING)));
		setIndicatorColor(resources.getColor(a.getResourceId(R.styleable.PagerStrip_pagerstrip_indicator, mDefaultColor)));
		setIndicatorRes(a.getResourceId(R.styleable.PagerStrip_pagerstrip_indicator, -1));
		setTabScale(a.getFloat(R.styleable.PagerStrip_pagerstrip_scale, 0f));
		setExpand(a.getBoolean(R.styleable.PagerStrip_pagerstrip_expand, false));
		a.recycle();
	}

	public void setTextColor(int color) {
		this.mTextColor = color;
	}

	public void setTextSelectColor(int color) {
		this.mTextSelectColor = color;
	}

	public void setDiviersColor(int color) {
		this.mDividerColor = color;
		invalidate();
	}

	public void setUnderlineColor(int color) {
		this.mUnderlineColor = color;
		invalidate();
	}

	private void setItemPadding(float padding) {
		this.mItemPadding = (int) padding;
	}

	/**
	 * 设置底部滑块颜色
	 * 
	 * @param color
	 */
	private void setIndicatorColor(int color) {
		this.mIndicatorColor = color;
		invalidate();
	}

	private void setIndicatorRes(int res) {
		if (-1 != res) {
			mIndicatorBitmap = BitmapFactory.decodeResource(getResources(), res);
			invalidate();
		}
	}

	private void setTabScale(float scale) {
		this.mScale = scale;
	}

	/**
	 * 是否展开条目
	 * 
	 * @param isExpand
	 */
	private void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	public void setViewPager(final ViewPager pager) {
		if (null == pager)
			throw new NullPointerException("View Pager 不能为空!");
		mTabsContainer.removeAllViews();
		this.mPager = pager;
		PagerAdapter adapter = pager.getAdapter();
		if (null == adapter)
			throw new NullPointerException("View Pager 未设置Adapter!");
        notifyDataChange();
	}

	public void notifyDataChange() {
		if(null==mPager){
			throw new NullPointerException("ViewPager 不能为空!");
		}
		mTabsContainer.removeAllViews();
		CharSequence title = null;
		PagerAdapter adapter = mPager.getAdapter();
		mTabCount = adapter.getCount();
		int width = getWidth() / mTabCount;
		for (int i = 0; i < mTabCount; i++) {
			title = adapter.getPageTitle(i);
			TextView tab = new TextView(getContext());
			tab.setGravity(Gravity.CENTER);
			tab.setText(title);
			tab.setCompoundDrawablePadding(UnitUtils.dip2px(getContext(), 5));
			tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
			tab.setTextColor(mTextColor);
			tab.setPadding(mItemPadding, 0, mItemPadding, 0);
			tab.setTextColor(0 != i ? mTextColor : mTextSelectColor);
			final int positoin = i;
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPager.setCurrentItem(positoin);
				}
			});
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(isExpand ? width : LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER;
			mTabsContainer.addView(tab, params);
		}
		View selectView = mTabsContainer.getChildAt(mSelectPosition);
		ViewHelper.setScaleX(selectView, 1f + mScale);
		ViewHelper.setScaleY(selectView, 1f + mScale);
		this.mPager.setOnPageChangeListener(this);
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.mPageChangeListener = listener;
	}

	@Override
	protected void onDetachedFromWindow() {
		// 释放位图
		if (null != mIndicatorBitmap && !mIndicatorBitmap.isRecycled()) {
			mIndicatorBitmap.recycle();
			mIndicatorBitmap = null;
		}
		super.onDetachedFromWindow();
	}

	/**
	 * 设置标题选中状态
	 * 
	 * @param position
	 */
	public void setTabTextColor(int position, boolean selected) {
		TextView textView = (TextView) mTabsContainer.getChildAt(position);
		if (null != textView) {
			textView.setTextColor(selected ? mTextSelectColor : mTextColor);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// !!!解决fragment装载后,但并没有马上显示情况下.view不绘制导致宽高无法获取情况,也是蛮拼的...
		if (null != mPager && 0 == mTabsContainer.getChildCount()) {
			notifyDataChange();
		}
		// 画移动标记
		final int height = getHeight();
		View view = mTabsContainer.getChildAt(mPosition);
		View nextView = mTabsContainer.getChildAt(mPosition + 1);
		int divierHeight = UnitUtils.dip2px(getContext(), DEFAULT_LINE_WIDTH);
		if (null != view) {
			int left = view.getLeft();
			int right = view.getRight();
			if (null != nextView) {
				left = (int) (mPositionOffset * nextView.getLeft() + (1.0f - mPositionOffset) * left);
				right = (int) (mPositionOffset * nextView.getRight() + (1.0f - mPositionOffset) * right);
			}
			mPaint.setColor(mUnderlineColor);
			// 绘底部线
			mPaint.setStrokeWidth(divierHeight);
			canvas.drawLine(0, height, mTabsContainer.getWidth(), height, mPaint);
			// 画矩阵
			if (null != mIndicatorBitmap) {
				// 画图片
				canvas.drawBitmap(mIndicatorBitmap, null, new RectF(left, getHeight() - UnitUtils.dip2px(getContext(), INDICATOR_HEIGHT), right, getHeight()), mPaint);
			} else {
				mPaint.setColor(mIndicatorColor);
				canvas.drawRect(left + mItemPadding, height - UnitUtils.dip2px(getContext(), INDICATOR_HEIGHT), right - mItemPadding, height, mPaint);
			}
		}
		// 画divider
		mPaint.setColor(mDividerColor);
		mPaint.setStrokeWidth(divierHeight);
		int childCount = mTabsContainer.getChildCount();
		for (int i = 0; i < childCount - 1; i++) {
			View childView = mTabsContainer.getChildAt(i);
			int right = childView.getRight();
			canvas.drawLine(right, 10, right, height - 10, mPaint);
		}
	}

	private void scrollToChild(int position, int offset) {
		if (mTabCount == 0) {
			return;
		}
		int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;
		if (position > 0 || offset > 0) {
			newScrollX -= mScrollOffset;
		}
		if (newScrollX != mLastScrollX) {
			mLastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (null != mPageChangeListener) {
			mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
		scrollToChild(position, (int) (positionOffset * mTabsContainer.getChildAt(position).getWidth()));
		mPosition = position;
		mPositionOffset = positionOffset;
		if (position < mTabCount) {
			TextView lastView = null;
			TextView textView = null;
			if (position + 1 < mTabCount) {
				lastView = (TextView) mTabsContainer.getChildAt(position + 1);
			}
			textView = (TextView) mTabsContainer.getChildAt(position);
			if (null != lastView) {
				// 设置缩小
				ViewHelper.setScaleX(lastView, 1f + positionOffset * mScale);
				ViewHelper.setScaleY(lastView, 1f + positionOffset * mScale);
				// 颜色回退
				lastView.setTextColor(AnimationUtils.evaluate(1f - positionOffset, mTextSelectColor, mTextColor));
			}
			// 设置控件放大
			ViewHelper.setScaleX(textView, 1f + ((1f - positionOffset) * mScale));
			ViewHelper.setScaleY(textView, 1f + ((1f - positionOffset) * mScale));
			// 颜色选中
			textView.setTextColor(AnimationUtils.evaluate(1f - positionOffset, mTextColor, mTextSelectColor));
		}
		// 当位置完成移动完成之后,重置各个tab颜色,否则颜色会显示不正常
		if (mSelectPosition == position && 0 == mPositionOffset) {
			for (int i = 0; i < mTabCount; i++) {
				setTabTextColor(i, i == position);
			}
		}
		invalidate();
	}

	@Override
	public void onPageSelected(int position) {
		if (null != mPageChangeListener) {
			mPageChangeListener.onPageSelected(position);
		}
		this.mSelectPosition = position;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			scrollToChild(mPager.getCurrentItem(), 0);
		}
		if (null != mPageChangeListener) {
			mPageChangeListener.onPageScrollStateChanged(state);
		}
	}

}
