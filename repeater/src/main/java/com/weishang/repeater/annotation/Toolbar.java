package com.weishang.repeater.annotation;

import android.content.pm.ActivityInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Toolbar {
	/** 标题资源文件 */
	public int title() default -1;

	/** 是否启用返回按钮 */
	public boolean displayHome() default true;

	/** 是否隐藏系统图标 */
	public int icon() default -1;

	/** 是否隐藏当前actionBar */
	public boolean hidden() default false;

	/** 设定当前屏幕方向 */
	public int orientation() default ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

}
