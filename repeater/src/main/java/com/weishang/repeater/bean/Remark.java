package com.weishang.repeater.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by momo on 2015/4/25.
 */
public class Remark implements Parcelable, DbInterface<Remark> {
    //    "_id", "id", "title", "content", "ct", "ut","size","color","line_color","line_size","bg","mood"
    public int id;
    public String title;
    public String content;
    public long ct;
    public long ut;
    public int size;
    public int color;
    public int lineColor;
    public int lineSize;
    public int bg;
    public int mood;

    public Remark() {
    }

    public Remark(int id, String title, String content, long ut, int size, int color, int lineColor, int lineSize, int bg, int mood) {
        this(id, title, content, 0, ut, size, color, lineColor, lineSize, bg, mood);
    }

    public Remark(int id, String title, String content, long ct, long ut, int size, int color, int lineColor, int lineSize, int bg, int mood) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.ct = ct;
        this.ut = ut;
        this.size = size;
        this.color = color;
        this.lineColor = lineColor;
        this.lineSize = lineSize;
        this.bg = bg;
        this.mood = mood;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int i) {
        p.writeInt(id);
        p.writeString(title);
        p.writeString(content);
        p.writeLong(ct);
        p.writeLong(ut);
        p.writeInt(size);
        p.writeInt(color);
        p.writeInt(lineColor);
        p.writeInt(lineSize);
        p.writeInt(bg);
        p.writeInt(mood);
    }

    public static final Creator<Remark> CREATOR = new Creator<Remark>() {
        @Override
        public Remark createFromParcel(Parcel p) {
            Remark r = new Remark();
            r.id = p.readInt();
            r.title = p.readString();
            r.content = p.readString();
            r.ct = p.readLong();
            r.ut = p.readLong();
            r.size = p.readInt();
            r.color = p.readInt();
            r.lineColor = p.readInt();
            r.lineSize = p.readInt();
            r.bg = p.readInt();
            r.mood = p.readInt();
            return r;
        }

        @Override
        public Remark[] newArray(int i) {
            return new Remark[i];
        }
    };

    @Override
    public ContentValues getContentValues() {
//        "_id", "id", "title", "content", "ct", "ut", "size", "color", "line_color", "line_size", "bg", "mood"
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("title", title);
        values.put("content", content);
        values.put("ct", ct);
        values.put("ut", ut);
        values.put("size", size);
        values.put("color", color);
        values.put("line_color", lineColor);
        values.put("line_size", lineSize);
        values.put("bg", bg);
        values.put("mood", mood);
        return values;
    }

    @Override
    public ArrayList<Remark> getDatas(Cursor cursor) {
        ArrayList<Remark> remarks = null;
        if (null != cursor) {
            remarks = new ArrayList<Remark>();
            while (cursor.moveToNext()) {
                remarks.add(new Remark(cursor.getInt(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getLong(4),
                        cursor.getLong(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getInt(8),
                        cursor.getInt(9),
                        cursor.getInt(9),
                        cursor.getInt(10)));
            }
        }
        return remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Remark)) {
            return false;
        }
        Remark o1 = (Remark) o;
        return this.id == o1.id;
    }
}
