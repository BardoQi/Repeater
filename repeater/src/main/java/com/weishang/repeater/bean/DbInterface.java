package com.weishang.repeater.bean;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public interface DbInterface<M> {
	/** 获得ContentValues数据体 */
	ContentValues getContentValues();

	/** 根据cursor返回指定对象体 */
	ArrayList<M> getDatas(Cursor cursor);
}
