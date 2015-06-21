package com.weishang.repeater.widget;

import android.app.Activity;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.listener.SimpleAnimatorListener;

/**
 * 弹出菜单组
 * 
 * @author momo
 * @Date 2015/2/28
 * 
 */
public class PopupMenu {
	private Activity mActivity;
	private Gravity mGravity;
	private boolean isClickDismiss;// 点击条目消失
	private boolean isShow;
	private LinearLayout mMenuGroup;
	private View mView;

	private OnMenuClickListener mListener;

	public PopupMenu(Activity activity, View view) {
		super();
		if (null == activity) {
			throw new NullPointerException("activity is Null!");
		}
		this.mActivity = activity;
		this.mView = view;
		this.mGravity = Gravity.TOP;
		addMenuGroup();
	}

	/**
	 * 添加菜单控件
	 */
	private void addMenuGroup() {
		mMenuGroup = new LinearLayout(mActivity);
		mMenuGroup.setVisibility(View.INVISIBLE);
		View decorView = this.mActivity.getWindow().getDecorView();
		if (decorView instanceof ViewGroup) {
			ViewGroup decorViewGroup = (ViewGroup) decorView;
			int childCount = decorViewGroup.getChildCount();
			if (0 < childCount) {
				View childView = decorViewGroup.getChildAt(0);
				if (childView instanceof ViewGroup) {
					ViewGroup childViewGroup = ((ViewGroup) childView);
					childCount = childViewGroup.getChildCount();
					View contentView = childViewGroup.getChildAt(childCount - 1);
					if (contentView instanceof ViewGroup) {
						((ViewGroup) contentView).addView(mMenuGroup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					}
				}
			}
		}
	}

	/**
	 * 设置弹出方向
	 * 
	 * @param gravity
	 */
	public void setGravity(Gravity gravity) {
		this.mGravity = gravity;
		switch (mGravity) {
		case LEFT:
		case RIGHT:
			this.mMenuGroup.setOrientation(LinearLayout.HORIZONTAL);
			break;
		case TOP:
		case BOTTOM:
			this.mMenuGroup.setOrientation(LinearLayout.VERTICAL);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置点击后消失
	 * 
	 * @param clickDismiss
	 */
	public void setClickDismiss(boolean clickDismiss) {
		this.isClickDismiss = clickDismiss;
	}

	public PopupMenu addTextMenu(final int resId, final int backgroupResId) {
		return addTextMenu(mActivity.getString(resId), backgroupResId);
	}

	public PopupMenu addTextMenu(final String value, final int backgroupResId) {
		if (null == mView) {
			throw new NullPointerException("view is Null!");
		}
		int width = mView.getWidth();
		int height = mView.getHeight();
		if (0 != width && 0 != height) {
			addTextView(value, backgroupResId, width, height);
		} else {
			runWithGlobalLayout(new Runnable() {
				@Override
				public void run() {
					addTextView(value, backgroupResId, mView.getWidth(), mView.getHeight());
				}
			});
		}
		return this;
	}

	public PopupMenu addImageMenu(final int resid) {
		if (null == mView) {
			throw new NullPointerException("view is Null!");
		}
		int width = mView.getWidth();
		int height = mView.getHeight();
		if (0 != width && 0 != height) {
			addImageView(resid, width, height);
		} else {
			runWithGlobalLayout(new Runnable() {
				@Override
				public void run() {
					addImageView(resid, mView.getWidth(), mView.getHeight());
				}
			});
		}
		return this;
	}

	private void addTextView(final String value, int backgroupResId, int width, int height) {
		addView(getTextViewFromView(backgroupResId, value), width, height);
	}

	private TextView getTextViewFromView(int backgroupResId, String value) {
		TextView textView = new TextView(mActivity);
		textView.setGravity(android.view.Gravity.CENTER);
		textView.setText(value);
		if (null != mView && mView instanceof TextView) {
			TextView view = (TextView) mView;
			textView.setBackgroundResource(backgroupResId);
			textView.setTextColor(view.getTextColors());
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getTextSize());
			textView.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
		}
		return textView;
	}

	private void addImageView(final int resId, int width, int height) {
		addView(getImageViewFromView(resId), width, height);
	}

	private ImageView getImageViewFromView(int resId) {
		ImageView imageView = new ImageView(mActivity);
		imageView.setImageResource(resId);
		if (null != mView && mView instanceof ImageView) {
			ImageView view = (ImageView) mView;
			imageView.setBackgroundDrawable(view.getBackground());
			imageView.setImageDrawable(view.getDrawable());
			imageView.setScaleType(view.getScaleType());
			imageView.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
		}
		return imageView;
	}

	/**
	 * 添加自定义菜单
	 * 
	 * @param view
	 * @param width
	 * @param height
	 */
	public void addView(View view, int width, int height) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.onMenuClick(mMenuGroup.indexOfChild(v));
				}
				if (isClickDismiss) {
					dismiss();
				}
			}
		});
		switch (mGravity) {
		case LEFT:
		case TOP:
			// 反向添加view
			this.mMenuGroup.addView(view, 0, new LinearLayout.LayoutParams(width, height));
			break;
		case RIGHT:
		case BOTTOM:
			this.mMenuGroup.addView(view, width, height);
			break;
		default:
			break;
		}
	}

	/**
	 * 在OnGlobalLayoutListener监听内执行事件
	 * 
	 * @param runnable
	 */
	private void runWithGlobalLayout(final Runnable runnable) {
		ViewTreeObserver treeObserver = mView.getViewTreeObserver();
		treeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			@SuppressWarnings("deprecation")
			public void onGlobalLayout() {
				mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				if (null != runnable) {
					runnable.run();
				}
			}
		});
	}

	/**
	 * 设置菜单位置
	 */
	private void setMenuLocation() {
		int width = mMenuGroup.getMeasuredWidth();
		int height = mMenuGroup.getMeasuredHeight();
		int[] location = new int[2];
		mView.getLocationInWindow(location);
		int x = location[0], y = location[1];
		Rect outRect = new Rect();
		mView.getWindowVisibleDisplayFrame(outRect);
		// 获得标题栏高
		// TODO 须根据通知栏隐藏与显示再执行以下操作
		// outRect.top=()?0:outRect.top;
		switch (mGravity) {
		case LEFT:
			x -= width;
			y -= outRect.top;
			break;
		case TOP:
			y -= (height + outRect.top);
			break;
		case RIGHT:
			x += mView.getWidth();
			y -= outRect.top;
			break;
		case BOTTOM:
			y += (mView.getHeight() - outRect.top);
			break;
		default:
			break;
		}
		ViewHelper.setX(mMenuGroup, x);
		ViewHelper.setY(mMenuGroup, y);
	}

	/**
	 * 显示弹出菜单
	 */
	public void show() {
		// 设置menu位置
		if (0 == ViewHelper.getX(mMenuGroup) && 0 == ViewHelper.getY(mMenuGroup)) {
			setMenuLocation();
		}
		isShow = true;
		toggleMenuOnAnim(true, true);
	}

	/**
	 * 动画形式隐藏或展开菜单
	 */
	private void toggleMenuOnAnim(boolean reSet, final boolean isShow) {
		mMenuGroup.setVisibility(View.VISIBLE);
		// 动画展开
		int width = mView.getWidth();
		int height = mView.getHeight();
		final int childCount = this.mMenuGroup.getChildCount();
		int translationX = 0, translationY = 0;
		boolean isOpposite = false;
		for (int i = 0; i < childCount; i++) {
			switch (mGravity) {
			case LEFT:
				isOpposite = false;
				translationX = (childCount - i + 1) * width;
				break;
			case TOP:
				isOpposite = false;
				translationY = (childCount - i + 1) * height;
				break;
			case RIGHT:
				isOpposite = true;
				translationX = (childCount - i + 1) * -width;
				break;
			case BOTTOM:
				isOpposite = true;
				translationY = (childCount - i + 1) * -height;
				break;
			default:
				break;
			}
			isOpposite = isShow ? isOpposite : !isOpposite;
			final View childView = this.mMenuGroup.getChildAt(isOpposite ? (childCount - i - 1) : i);
			if (reSet) {
				ViewHelper.setAlpha(childView, 0f);
				ViewHelper.setTranslationX(childView, translationX);
				ViewHelper.setTranslationY(childView, translationY);
			}
			final int index = i;
			ViewPropertyAnimator.animate(childView).translationX(isShow ? 0f : translationX).translationY(isShow ? 0f : translationY).alpha(isShow ? 1f : 0f).setDuration(200)
					.setInterpolator(new AccelerateInterpolator()).setStartDelay(i * 80).setListener(new SimpleAnimatorListener() {
						@Override
						public void onAnimationEnd(Animator animator) {
							// 如果是dismiss控件,且是最后一个执行控件动画结束,隐藏菜单组
							if (!isShow && (childCount - 1 == index) && null != mMenuGroup) {
								mMenuGroup.setVisibility(View.GONE);
							}
						}
					});
		}
	}

	/**
	 * 隐藏弹出菜单
	 */
	public void dismiss() {
		isShow = false;
		mMenuGroup.setVisibility(View.VISIBLE);
		toggleMenuOnAnim(false, false);
	}

	public void clear() {
		dismiss();
		this.mMenuGroup = null;
		this.mActivity = null;
	}

	/**
	 * 切换菜单显示与隐藏状态
	 */
	public void toggle() {
		isShow = !isShow;
		if (isShow) {
			show();
		} else {
			dismiss();
		}
	}

	/**
	 * 设置菜单点击事件
	 * 
	 * @param listener
	 */
	public void setOnMenuClickListener(OnMenuClickListener listener) {
		this.mListener = listener;
	}

	public enum Gravity {
		LEFT, TOP, RIGHT, BOTTOM;
	}

	public interface OnMenuClickListener {
		void onMenuClick(int position);
	}
}
