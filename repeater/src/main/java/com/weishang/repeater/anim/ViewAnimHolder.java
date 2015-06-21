package com.weishang.repeater.anim;

import android.view.View;

import java.util.WeakHashMap;

/**
 * view properti扩展属性集
 * 
 * @author momo
 * @Date 2014/11/18
 * 
 */
public class ViewAnimHolder {
	private static final WeakHashMap<View, AnimValue> animViews = new WeakHashMap<View, AnimValue>();

	public static AnimValue getAnimValue(View view) {
		AnimValue animValue = animViews.get(view);
		if (null == animValue) {
			animValue = new AnimValue();
			animViews.put(view, animValue);
		}
		return animValue;
	}

	public static void remove(View view) {
		animViews.remove(view);
	}
}
