package com.weishang.repeater.listener;

import android.support.v4.app.Fragment;

/**
 * 引导类UI接口
 * 
 * @author momo
 * @Date 2014/12/8
 */
public interface Guideable {
	/** 是否可以引导 */
	boolean isGuide();

	/** 获得引导界面 */
	Fragment getGuide();
}
