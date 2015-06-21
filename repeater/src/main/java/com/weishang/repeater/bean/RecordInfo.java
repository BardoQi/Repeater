package com.weishang.repeater.bean;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by momo on 2015/4/16.
 * 统计信息
 */
public class RecordInfo implements DbInterface<RecordInfo> {
    public int id;//音乐id
    public String name;//音乐名称
    public long ct;//创建时间
    public long ut;//更新时间
    public long time;//播放时间

    public RecordInfo() {
    }

    public RecordInfo(int id, String name, long ct, long ut, long time) {
        this.id = id;
        this.name = name;
        this.ct = ct;
        this.ut = ut;
        this.time = time;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("ct", ct);
        values.put("ut", System.currentTimeMillis());
        values.put("time", time);
        return values;
    }

    @Override
    public ArrayList<RecordInfo> getDatas(Cursor cursor) {
        ArrayList<RecordInfo> infos = null;
        if (null != cursor) {
            infos = new ArrayList<RecordInfo>();
            while (cursor.moveToNext()) {
                infos.add(new RecordInfo(cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getLong(4),
                        cursor.getLong(5)));
            }
        }
        return infos;
    }
}
