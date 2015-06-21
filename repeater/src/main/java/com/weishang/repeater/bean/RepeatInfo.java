package com.weishang.repeater.bean;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by momo on 2015/3/26.
 * 复读信息,做记录处理
 */
public class RepeatInfo implements DbInterface<RepeatInfo>,Parcelable{
    public int id;
    public String name;
    public long ct;
    public long ut;
    public int start;
    public int end;
    public int count;
    public int position;
    public String record;
    public long recordLength;

	public RepeatInfo() {
	}

	public RepeatInfo(int id, String name, long ct, long ut, int start, int end, int count, int position, String record, long recordLength) {
		this.id = id;
		this.name = name;
		this.ct = ct;
		this.ut = ut;
		this.start = start;
		this.end = end;
		this.count = count;
		this.position = position;
		this.record = record;
		this.recordLength = recordLength;
	}

	@Override
    public int describeContents(){
    	return 0;
    }
    @Override
    public void writeToParcel(Parcel dest,int flags) {
    	dest.writeInt(id);
    	dest.writeString(name);
    	dest.writeLong(ct);
    	dest.writeLong(ut);
    	dest.writeInt(start);
    	dest.writeInt(end);
    	dest.writeInt(count);
    	dest.writeInt(position);
    	dest.writeString(record);
    	dest.writeLong(recordLength);
    }
    public static final Parcelable.Creator<RepeatInfo> CREATOR = new Parcelable.Creator<RepeatInfo>() {

    	@Override
    	public RepeatInfo[] newArray(int size) {
    		 return new RepeatInfo[size];
    	}

    	@Override
    	public RepeatInfo createFromParcel(Parcel s) {
    		RepeatInfo repeatInfo = new RepeatInfo();
    		repeatInfo.id = s.readInt();
    		repeatInfo.name = s.readString();
    		repeatInfo.ct = s.readLong();
    		repeatInfo.ut = s.readLong();
    		repeatInfo.start = s.readInt();
    		repeatInfo.end = s.readInt();
    		repeatInfo.count = s.readInt();
    		repeatInfo.position = s.readInt();
    		repeatInfo.record = s.readString();
    		repeatInfo.recordLength = s.readLong();
    		return repeatInfo;
    	}

    };
    @Override
    public ContentValues getContentValues() {
//		"_id", "id", "name", "ct", "ut", "start", "end", "count","position","record","record_length"
    	ContentValues values = new ContentValues();
    	values.put("id",id);
    	values.put("name",name);
    	values.put("ct",ct);
    	values.put("ut",ut);
    	values.put("start",start);
    	values.put("end",end);
    	values.put("count",count);
    	values.put("position",position);
    	values.put("record",record);
    	values.put("record_length",recordLength);
    	return values;
    }
    @Override
    public ArrayList<RepeatInfo> getDatas(Cursor cursor) {
    	ArrayList<RepeatInfo> lists = null;
    	if(null!=cursor){
    		lists = new ArrayList<RepeatInfo>();
    		while(cursor.moveToNext()){
    			lists.add(new RepeatInfo(cursor.getInt(1),
    				cursor.getString(2),
    				cursor.getLong(3),
    				cursor.getLong(4),
    				cursor.getInt(5),
    				cursor.getInt(6),
    				cursor.getInt(7),
    				cursor.getInt(8),
    				cursor.getString(8),
    				cursor.getLong(10)));
    			}
    		}
    	return lists;
    }



}
