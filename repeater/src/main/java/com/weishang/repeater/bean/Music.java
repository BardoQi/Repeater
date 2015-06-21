package com.weishang.repeater.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 音乐对象
 *
 * @author momo
 * @Date 2015/2/26
 */
public class Music implements Parcelable, DbInterface<Music>, Cloneable {
    public int id;
    public long ut;// 收藏时间
    public String path;// 文件路径
    public String name;
    public String album;//专辑名
    public long duration;
    public String author;
    public long size;
    public int listId;//列表id
    public boolean isFavourite;//是否喜爱
    public String directoryName;//文件夹名称
    public String directory;//文位路径
    public String word;//检索字母

    public Music() {
        super();
    }

    public Music(int id, long ut, String path, String name, String album, long duration, String author, long size, int listId, int favourite, String directoryName, String directory, String word) {
        super();
        this.id = id;
        this.ut = ut;
        this.path = path;
        this.name = name;
        this.album = album;
        this.duration = duration;
        this.author = author;
        this.size = size;
        this.listId = listId;
        this.isFavourite = (1 == favourite);
        this.directoryName = directoryName;
        this.directory = directory;
        this.word = word;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel d, int flags) {
        d.writeInt(id);
        d.writeLong(ut);
        d.writeString(path);
        d.writeString(name);
        d.writeString(album);
        d.writeLong(duration);
        d.writeString(author);
        d.writeLong(size);
        d.writeInt(listId);
        d.writeInt(isFavourite ? 1 : 0);
        d.writeString(directoryName);
        d.writeString(directory);
        d.writeString(word);
    }

    public static final Parcelable.Creator<Music> CREATOR = new Creator<Music>() {

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }

        @Override
        public Music createFromParcel(Parcel s) {
            Music m = new Music();
            m.id = s.readInt();
            m.ut = s.readLong();
            m.path = s.readString();
            m.name = s.readString();
            m.album = s.readString();
            m.duration = s.readLong();
            m.author = s.readString();
            m.size = s.readLong();
            m.listId = s.readInt();
            m.isFavourite = (1 == s.readInt());
            m.directoryName = s.readString();
            m.directory = s.readString();
            m.word = s.readString();
            return m;
        }
    };

    @Override
    public ContentValues getContentValues() {
        // "_id", "id", "ut", "path", "name","author", "use", "collect","word
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("ut", System.currentTimeMillis());
        values.put("path", path);
        values.put("name", name);
        values.put("album", album);
        values.put("duration", duration);
        values.put("author", author);
        values.put("size", size);
        values.put("list_id", listId);
        values.put("favourite", isFavourite);
        values.put("directory_name", directoryName);
        values.put("directory", directory);
        values.put("word", word);
        return values;
    }

    @Override
    public ArrayList<Music> getDatas(Cursor cursor) {
        ArrayList<Music> ms = getMusicLists(cursor);
        return ms;
    }

    public ArrayList<Music> getMusicLists(Cursor cursor) {
        //"_id", "id", "ut", "path", "name","album", "duration", "author", "size", "list_id","directory_name","word
        ArrayList<Music> ms = null;
        if (null != cursor) {
            ms = new ArrayList<Music>();
            while (cursor.moveToNext()) {
                ms.add(new Music(cursor.getInt(1),
                        cursor.getLong(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getLong(6),
                        cursor.getString(7),
                        cursor.getLong(8),
                        cursor.getInt(9),
                        cursor.getInt(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13)));
            }
        }
        return ms;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Music)) {
            return false;
        }
        return this.path.equals(((Music) o).path);
    }

}
