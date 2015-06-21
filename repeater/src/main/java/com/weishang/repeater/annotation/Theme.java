package com.weishang.repeater.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主题配置 
 * 
 * @author momo
 * @Date 2014/6/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Theme {
	/** 获得设置点击事件按钮 */
	public int layout();
}
