package com.weishang.repeater.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ID {
	/** 控件 id */
	public int id();

	/** 是否注册点击事件 */
	public boolean click() default false;

	/** 给子控件设置点击事件 */
	public boolean childClick() default false;
}
