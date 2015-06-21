package com.weishang.repeater.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.weishang.repeater.R;
import com.weishang.repeater.listener.ViewImageClickListener;
import com.weishang.repeater.utils.PackageUtil;
import com.weishang.repeater.utils.ViewInject;
import com.weishang.repeater.widget.TitleBar;
import com.weishang.repeater.widget.WheelView;

/**
 * 自带进度,空布局提示,错误信息提示的fragment
 * 
 * @author momo
 * @Date 2014/5/29
 */
public abstract class ProgressFragment extends Fragment {

	private static final int DELAY_MILLIS = 500;
	protected TitleBar mTitleBar;
	protected TextView mEmptyView;
	protected TextView mErrorView;
	protected LinearLayout mRepeatLayout;// 重试
	protected TextView mLoadInfo;
	private TextView mRepeatInfo;
	protected View mViewContainer;
	protected Runnable mRepeatRunnable;

	public final static int PROGRESS_ITEM = 1;
	public final static int EMPTY_ITEM = 2;
	public final static int ERROR_ITEM = 3;
	public final static int REPEAT_ITEM = 4;
	public final static int CONTAINER_ITEM = 5;

	protected RelativeLayout mRoot = null;
	// 当前布局显示状态
	private int lastItem;
	private ValueAnimator mProgressAnimator;

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = (RelativeLayout) inflater.inflate(R.layout.fragment_progress, container, false);
		mTitleBar = (TitleBar) mRoot.findViewById(R.id.titlebar_container);
		mEmptyView = (TextView) mRoot.findViewById(R.id.empty_container);
		mErrorView = (TextView) mRoot.findViewById(R.id.error_container);
		mRepeatLayout = (LinearLayout) mRoot.findViewById(R.id.repeat_container);
		mRepeatInfo = (TextView) mRoot.findViewById(R.id.tv_try);
		mLoadInfo = (TextView) mRoot.findViewById(R.id.load_info);
		// 添加布局对象
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, R.id.titlebar_container);
		int layout = getLayout();
		if (0 == layout) {
			throw new NullPointerException("布局不能为空");
		}
		mViewContainer = inflater.inflate(getLayout(), mRoot, false);
		mRoot.addView(mViewContainer, lastItem = CONTAINER_ITEM, params);
		ViewInject.init(this, mViewContainer);
		return mRoot;
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRepeatListener();
	}

	/**
	 * 获得布局文件
	 * 
	 * @return
	 */
	public abstract int getLayout();

	@Override
	public void onDestroyView() {
		mEmptyView = null;
		mErrorView = null;
		mRepeatLayout = null;
		mViewContainer = null;
		super.onDestroyView();
	}

	/**
	 * 设置空布局信息
	 * 
	 * @param info
	 */
	protected void setEmptyInfo(String info) {
		if (null != mEmptyView) {
			mEmptyView.setText(info);
		}
	}

	protected void setEmptyInfo(int resId) {
		if (null != mEmptyView) {
			mEmptyView.setText(resId);
		}
	}

	protected void setEmptyIcon(Drawable drawable) {
		if (null != mEmptyView) {
			mEmptyView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
		}
	}

	protected void setEmptyIcon(int resId) {
		if (null != mEmptyView) {
			mEmptyView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
		}
	}

	/**
	 * 设置空布局信息
	 * 
	 * @param info
	 */
	protected void setErrorInfo(String info) {
		if (null != mErrorView) {
			mErrorView.setText(info);
		}
	}

	protected void setErrorInfo(int resId) {
		if (null != mErrorView) {
			mErrorView.setText(resId);
		}
	}

	protected void setErrorIcon(Drawable drawable) {
		if (null != mErrorView) {
			mErrorView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
		}
	}

	protected void setErrorIcon(int resId) {
		if (null != mErrorView) {
			mErrorView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
		}
	}

	protected void setLoadInfo(int resId) {
		if (null != mLoadInfo) {
			mLoadInfo.setText(resId);
		}
	}

	/**
	 * 设置显示空布局体
	 * 
	 * @param animate
	 *            true:显示 false:隐藏
	 */
	protected void setEmptyShown(boolean animate) {
		setViewShown(EMPTY_ITEM, animate);
	}

	/**
	 * 延迟设置空布局体
	 * 
	 * @param animate
	 */
	protected void delayShowEmpty(final boolean animate) {
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
	 * @param animate
	 *            true:显示 false:隐藏
	 */
	protected void setProgressShown(boolean animate) {
		setViewShown(PROGRESS_ITEM, animate);
	}

	/**
	 * 设置重试布局是否显示
	 * 
	 * @param animate
	 *            true:显示 false:隐藏
	 */
	protected void setRepeatShown(boolean animate) {
		setViewShown(REPEAT_ITEM, animate);
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
				PackageUtil.startSetting(getActivity());
			}
		};
	}

	/**
	 * 设置重试事件
	 * 
	 * @param runnable
	 */
	protected void setRepeatRunnable(Runnable runnable) {
		setRepeatShown(true);
		setRepeatInfo(R.string.pull_to_refresh_try);
		mRepeatRunnable = runnable;
	}

	/**
	 * 设置指定透明度的进度条
	 * 
	 * @param animate
	 */
	protected void setScaleProgressShown(boolean animate) {
		int item = PROGRESS_ITEM;
		lastItem = CONTAINER_ITEM;
		if (null != mViewContainer) {
			View showView = mRoot.getChildAt(item);
			View closeView = mRoot.getChildAt(lastItem);
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
	 * 设置标题显示状态
	 * 
	 * @param isShow
	 */
	protected void setTitleShown(boolean isShow) {
		if (null != mTitleBar) {
			if (isShow) {
				mTitleBar.setVisibility(View.VISIBLE);
				mTitleBar.setBackgroundResource(R.color.title_bg);
			} else {
				mTitleBar.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 设置返回结束actvitity事件
	 * 
	 */
	public void setTitleBarBackListener() {
		mTitleBar.setBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != getActivity()) {
					getActivity().finish();
				}
			}
		});
	}

	/**
	 * 延迟设置进度体
	 * 
	 * @param animate
	 */
	protected void delayShowProgress(final boolean animate) {
		if (null != mRoot) {
			mRoot.postDelayed(new Runnable() {
				@Override
				public void run() {
					setProgressShown(animate);
				}
			}, DELAY_MILLIS);
		}
	}

	/**
	 * 延迟设置进度体
	 * 
	 * @param animate
	 */
	public void delayShowRepeat(final boolean animate) {
		if (null != mRoot) {
			mRoot.postDelayed(new Runnable() {
				@Override
				public void run() {
					setRepeatShown(animate);
				}
			}, DELAY_MILLIS);
		}
	}

	/**
	 * 设置错误布局体显示
	 * 
	 * @param animate
	 *            true:显示 false:隐藏
	 */
	protected void setErrorShown(boolean animate) {
		setViewShown(ERROR_ITEM, animate);
	}

	/**
	 * 延迟设置错误布局体
	 * 
	 * @param animate
	 */
	public void delayShowError(final boolean animate) {
		if (null != mRoot) {
			mRoot.postDelayed(new Runnable() {
				@Override
				public void run() {
					setErrorShown(animate);
				}
			}, DELAY_MILLIS);
		}
	}

	/**
	 * 设置布局体显示
	 * 
	 * @param animate
	 *            true:显示 false:隐藏
	 */
	public void setContainerShown(boolean animate) {
		setViewShown(CONTAINER_ITEM, animate);
	}

	/**
	 * 延迟设置布局体
	 * 
	 * @param animate
	 */
	public void delayShowContainer(final boolean animate) {
		if (null != mRoot) {
			mRoot.postDelayed(new Runnable() {
				@Override
				public void run() {
					setContainerShown(animate);
				}
			}, DELAY_MILLIS);
		}
	}

	protected ViewGroup getRootView() {
		return mRoot;
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

	public TitleBar getTitleBar() {
		return mTitleBar;
	}

	private void setViewShown(final int item, final boolean animate) {
		if (null != mViewContainer && item != lastItem) {
			View showView = mRoot.getChildAt(item);
			View closeView = mRoot.getChildAt(lastItem);
			if (animate) {
				ViewHelper.setAlpha(showView, 0f);
				ViewPropertyAnimator.animate(showView).setDuration(200).alpha(1f);
			} else {
				showView.clearAnimation();
			}
			closeView.setVisibility(View.GONE);
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
	protected void setBackGroundColor(int color) {
		if (null != mRoot) {
			mRoot.setBackgroundColor(color);
		}
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
				mProgressAnimator.addUpdateListener(new AnimatorUpdateListener() {
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
