package com.weishang.repeater.listener;

import android.view.View;
import android.view.View.OnClickListener;

import com.weishang.repeater.anim.AnimationUtils;


/**
 * 点击图片抖动事件
 * 
 * @author momo
 * @Date 2014/11/21
 * 
 */
public class ViewImageClickListener implements OnClickListener {
	private final OnClickListener listener;

	public ViewImageClickListener(OnClickListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		AnimationUtils.startViewImageAnim(v);
		if (null != listener) {
			listener.onClick(v);
		}
	}

}
