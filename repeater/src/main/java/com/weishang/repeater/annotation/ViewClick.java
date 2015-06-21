package com.weishang.repeater.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewClick {
	/** 获得设置点击事件按钮 */
	public int[] ids() default -1;

    public int[] childClick() default -1;
    
    public boolean isImageView() default true;
}
