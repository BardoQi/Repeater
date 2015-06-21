package com.weishang.repeater.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.weishang.repeater.R;
import com.weishang.repeater.listener.Guideable;
import com.weishang.repeater.listener.BackListener;

import java.util.List;


public class FragmentUtils {

	/**
	 * 切换fragment
	 * 
	 * @param to
	 *            :替换的fragment
	 * @params addToBackStact 是否添加到返回栈
	 */
	public static void toFragment(FragmentActivity activity, Fragment to, int id, boolean addToBackStack, boolean animation) {
		if (null != activity && null != to) {
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			String tab = to.getClass().getSimpleName();
			// 设置切换动画
			// if (animation) {
			// transaction.setCustomAnimations(R.anim.push_left_in,
			// R.anim.push_left_out);
			// }
			if (addToBackStack) {
				transaction.addToBackStack(tab);
			}
			id = (-1 == id) ? R.id.container : id;
			transaction.replace(id, to, tab).commitAllowingStateLoss();
		}
	}

	/**
	 * 不加入返回栈
	 */
	public static void toFragment(FragmentActivity activity, Fragment to) {
		toFragment(activity, to, -1, false, false);
	}

	/**
	 * 传递参数
	 */
	public static void toFragment(FragmentActivity activity, Fragment to, boolean addToBackStract) {
		toFragment(activity, to, -1, addToBackStract, false);
	}

	/**
	 * 填充到指定布局
	 */
	public static void toFragment(FragmentActivity activity, Fragment to, int id) {
		toFragment(activity, to, id, false, false);
	}

	/**
	 * 默认不带动画跳转
	 */
	public static void toFragment(FragmentActivity activity, Fragment to, int id, boolean animation) {
		toFragment(activity, to, id, false, animation);
	}

	/**
	 * 移除容器内的fragment
	 */
	public static void removeFragment(FragmentActivity activity, int layoutId) {
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		Fragment fg = fragmentManager.findFragmentById(layoutId);
		if (null != fg) {
			fragmentManager.beginTransaction().remove(fg).commit();
		}
	}

	/**
	 * 当前容器是否存在指定fragment
	 */
	public static boolean hasFragment(FragmentActivity activity, String tag) {
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		return null != fragmentManager.findFragmentByTag(tag);
	}

	/**
	 * 添加后台fragment到容器
	 */
	public static void addFragmentWithNull(FragmentActivity activity, Fragment addFragment, int id) {
		if (null != activity && null != addFragment) {
			String tag = addFragment.getClass().getSimpleName();
			if (!hasFragment(activity, tag)) {
				addFragment(activity, addFragment, id, false);
			}
		}
	}

	/**
	 * 添加一个fragment对象
	 * 
	 * @param fragment
	 */
	public static void addFragment(FragmentActivity activity, Fragment fragment, int layoutId, boolean addBack, boolean showAnimation) {
		if (null != activity && null != fragment) {
			String tag = fragment.getClass().getSimpleName();
			FragmentManager fragmentManager = activity.getSupportFragmentManager();
			FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
			// Fragment findFragment = fragmentManager.findFragmentByTag(tag);
			// if (null != findFragment) {
			// beginTransaction.remove(findFragment);
			// }
			if (addBack) {
				beginTransaction.addToBackStack(tag);
				if (showAnimation) {
					beginTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit, R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
				}
			}
			beginTransaction.add(layoutId, fragment, tag);
			// 附加引导界面
			if (fragment instanceof Guideable) {
				Guideable guideFragment = ((Guideable) fragment);
				if (guideFragment.isGuide()) {
					beginTransaction.add(layoutId, guideFragment.getGuide(), guideFragment.getClass().getSimpleName());
				}
			}
			beginTransaction.commitAllowingStateLoss();
		}
	}

	public static void addFragment(FragmentActivity activity, Fragment fragment, int id, boolean addBack) {
		addFragment(activity, fragment, id, addBack, true);
	}

	public static void addFragment(FragmentActivity activity, Fragment fragment, int id) {
		addFragment(activity, fragment, id, false);
	}

	public static void addFragment(FragmentActivity activity, Fragment fragment) {
		addFragment(activity, fragment, R.id.container, false);
	}

	/**
	 * 根据fragment tag通知事件执行
	 * 
	 * @param activity
	 * @param tag
	 * @param action
	 * @param extras
	 */
	public static void notifyAction(FragmentActivity activity, String tag, int action, Bundle extras) {
		if (null != activity && !TextUtils.isEmpty(tag)) {
			Fragment findFragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
			if (null != findFragment && findFragment.isAdded() && findFragment instanceof BackListener) {
//				((BackListener) findFragment).onBack(action, extras);
			}
		}
	}

	/**
	 * 根据fragment tag通知事件执行
	 * 
	 * @param activity
	 * @param clazz
	 * @param action
	 * @param extras
	 */
	public static void notifyAction(FragmentActivity activity, Class<? extends Fragment> clazz, int action, Bundle extras) {
		if (null != activity && null != clazz) {
			Fragment findFragment = activity.getSupportFragmentManager().findFragmentByTag(clazz.getSimpleName());
			if (null != findFragment && findFragment.isAdded() && findFragment instanceof BackListener) {
//				((BackListener) findFragment).onBack(action, extras);
			}
		}
	}

	public static void notifyAction(FragmentActivity activity, int optionId, Bundle bundle) {
		if (null != activity) {
			List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
			if (null != fragments && !fragments.isEmpty()) {
				int length = fragments.size();
				for (int i = 0; i < length; i++) {
					Fragment fragment = fragments.get(i);
					if (null != fragment && fragment instanceof BackListener) {
//						((BackListener) fragment).onBack(optionId, bundle);
					}
				}
			}
		}
	}
}
