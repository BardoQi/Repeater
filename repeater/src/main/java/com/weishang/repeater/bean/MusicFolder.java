package com.weishang.repeater.bean;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by momo on 2015/3/9.
 * 音乐文件夹对象
 */
public class MusicFolder implements DbInterface<MusicFolder> {
    public long ct;
    public long ut;
    public String path;
    public String name;
    public int count;

    public MusicFolder() {
    }

    public MusicFolder(String path) {
        this.ct = System.currentTimeMillis();
        this.ut = System.currentTimeMillis();
        this.path = path;
        this.name = new File(path).getName();
    }

    public MusicFolder(String path, String name, int count) {
        this.path = path;
        this.name = name;
        this.count = count;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("ct", ct);
        values.put("ut", ut);
        values.put("path", path);
        values.put("name", name);
        values.put("count", count);
        return values;
    }

    @Override
    public ArrayList<MusicFolder> getDatas(Cursor cursor) {
        ArrayList<MusicFolder> folders = null;
        if (null != cursor) {
            folders = new ArrayList<MusicFolder>();
            while (cursor.moveToNext()) {
                folders.add(new MusicFolder(cursor.getString(1), cursor.getString(2), cursor.getInt(3)));
            }
        }
        return folders;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MusicFolder)) {
            return false;
        }
        return this.path.equals(((MusicFolder) o).path);
    }
}
