package com.weishang.repeater.bean;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/8.
 * 播放列表
 */
public class PlayList implements DbInterface<PlayList> {
    public int id;
    public String name;
    public long ct;
    public long ut;
    public int count;

    public PlayList() {
    }

    public PlayList(int id, String name) {
        this.id = id;
        this.name = name;
        this.ct = System.currentTimeMillis();
        this.ut = System.currentTimeMillis();
    }

    public PlayList(int id, String name, long ct, long ut) {
        this.id = id;
        this.name = name;
        this.ct = ct;
        this.ut = ut;
    }


    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("ct", System.currentTimeMillis());
        values.put("ut", System.currentTimeMillis());
        values.put("visible", true);
        return values;
    }

    @Override
    public ArrayList<PlayList> getDatas(Cursor cursor) {
        ArrayList<PlayList> lists = null;
        if (null != cursor) {
            lists = new ArrayList<PlayList>();
            while (cursor.moveToNext()) {
                lists.add(new PlayList(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getLong(3)));
            }
        }
        return lists;
    }
}
