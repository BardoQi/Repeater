package com.weishang.repeater.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.weishang.repeater.App;
import com.weishang.repeater.bean.DbInterface;
import com.weishang.repeater.bean.PlayList;

import java.util.ArrayList;

/**
 * Created by momo on 2014/12/31.
 */
public class MyDb extends SQLiteOpenHelper {
    //三个默认播放列表id
    public static final int SINGLE_ITEM=-4;//单一条目,用于搜索单个播放歌曲
    public static final int LAST_VISIBLE = -3;
    public static final int NEW_ADD = -2;//最新添加的
    public static final int DEFUALT_LIST = 0;
    public static final int MY_FAVOURITE = 1;
    private static final String DB_NAME = "repeat";
    // 本地资源表
    public static final String LOCAL_RES = "music";
    public static final String PLAY_LIST = "play_list";//播放列表
    public static final String REPEAT = "repeat";//复读列表
    public static final String RECORD = "record";//播放信息记录
    public static final String REMARK = "remark";//备忘信息
    private static final int VERSION = 1;

    public MyDb(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + LOCAL_RES + "(_id INTEGER PRIMARY KEY," + "id INTEGER," + // 文件id
                "ut LONG," + // 更新时间
                "path TEXT," + // 音频文件路径
                "name TEXT," + // 音频文件名称
                "album TEXT," + // 音频专辑名
                "duration LONG," + // 音频文件时长
                "author TEXT," + // 音频文件作者
                "size LONG," + // 音频文件大小
                "list_id INTEGER, " + // 所属列表id
                "favourite BOOLEAN DEFAULT FALSE, " + // 所属列表id
                "directory_name TEXT, " + // 所属目录名称
                "directory TEXT, " + // 所属目录
                "word TEXT " + // 检索字母
                ")");
        //最近播放,收藏,都属于一个单独列表;
        sqLiteDatabase.execSQL("CREATE TABLE " + PLAY_LIST + "(_id INTEGER PRIMARY KEY," +
                "id INTEGER," + // 列表id
                "name TEXT," + // 列表名称
                "ct LONG," + // 创建时间
                "ut LONG," + // 创建时间
                "visible BOOLEAN DEFAULT TRUE" + // 创建时间
                ")");

        sqLiteDatabase.insert(PLAY_LIST, null, new PlayList(MY_FAVOURITE, "我的最爱").getContentValues());

        sqLiteDatabase.execSQL("CREATE TABLE " + REPEAT + "(_id INTEGER PRIMARY KEY," +
                "id INTEGER," + // 列表id
                "name TEXT," + // 列表名称
                "ct LONG," + // 创建时间
                "ut LONG," + // 创建时间
                "start LONG," + // 复读起始时间
                "end LONG," + // 结束时间
                "count INTEGER," + // 复读次数
                "position INTEGER," + // 复读角标位置
                "record TEXT," + // 复读录音
                "record_length LONG," + // 录音长度
                "visible BOOLEAN DEFAULT TRUE" + // 创建时间
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + RECORD + "(_id INTEGER PRIMARY KEY," +
                "id INTEGER," + // 列表id
                "name TEXT," + // 列表名称
                "ct LONG," + // 创建时间
                "ut LONG," + // 创建时间
                "time LONG," + // 操作时间
                "visible BOOLEAN DEFAULT TRUE" + // 创建时间
                ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + REMARK + "(_id INTEGER PRIMARY KEY," +
                "id INTEGER," + // 列表id
                "title TEXT," + // 列表名称
                "content TEXT," + // 列表名称
                "ct LONG," + // 创建时间
                "ut LONG," + // 更新时间
                "size INTEGER," + // 字体大小
                "color INTEGER," + // 字体颜色
                "line_color INTEGER," + // 分隔线颜色
                "line_size TEXT," + // 分隔线颜色
                "bg INTEGER," + // 背景颜色
                "mood INTEGER," + // 心情
                "visible BOOLEAN DEFAULT TRUE" + // 创建时间
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    /**
     * 根据url获得列表数据
     *
     * @param uri
     * @param t
     * @param projection
     * @param selection
     * @param seltionArgs
     * @return
     */
    public static <T extends DbInterface<T>> ArrayList<T> getDatas(Uri uri, T t, String[] projection, String selection, String[] seltionArgs, String order) {
        ContentResolver resolver = App.getAppContext().getContentResolver();
        Cursor cursor = resolver.query(uri, projection, selection, seltionArgs, order);
        ArrayList<T> dataList= t.getDatas(cursor);
        if (null != cursor) {
            cursor.close();
        }
        return dataList;
    }

    /**
     * 根据url获得列表数据
     *
     * @param uri
     * @param t
     * @param projection
     * @param selection
     * @param seltionArgs
     * @return
     */
    public static <T extends DbInterface<T>> ArrayList<T> getDatas(Uri uri, T t, String[] projection, String selection, String... seltionArgs) {
        return getDatas(uri, t, projection, selection, seltionArgs, null);
    }

    /**
     * 根据url获得对象
     *
     * @param uri
     * @param t
     * @param projection
     * @param selection
     * @param seltionArgs
     * @return
     */
    public static <T extends DbInterface<T>> T getData(Uri uri, T t, String[] projection, String selection, String... seltionArgs) {
        return getData(uri, t, projection, selection, seltionArgs, null);
    }

    /**
     * 根据url获得对象
     *
     * @param uri
     * @param t
     * @param projection
     * @param selection
     * @param seltionArgs
     * @return
     */
    public static <T extends DbInterface<T>> T getData(Uri uri, T t, String[] projection, String selection, String[] seltionArgs, String order) {
        ContentResolver resolver = App.getAppContext().getContentResolver();
        Cursor cursor = resolver.query(uri, projection, selection, seltionArgs, order);
        ArrayList<T> datas = t.getDatas(cursor);
        T data = null;
        if (null != datas && !datas.isEmpty()) {
            data = datas.get(0);
        }
        if (null != cursor) {
            cursor.close();
        }
        return data;
    }

    /**
     * 更新或插入数据
     *
     * @param t
     * @param uri
     * @param selection
     * @param seltionArgs
     */
    public static <T extends DbInterface<T>> void repleceData(T t, Uri uri, String selection, String... seltionArgs) {
        ContentResolver resolver = App.getAppContext().getContentResolver();
        Cursor cursor = resolver.query(uri, null, selection, seltionArgs, null);
        if (null != cursor && cursor.moveToFirst()) {
            // update
            resolver.update(uri, t.getContentValues(), selection, seltionArgs);
        } else {
            // insert
            resolver.insert(uri, t.getContentValues());
        }
        if (null != cursor) {
            cursor.close();
        }
    }

    /**
     * 插入数据
     *
     * @param t
     * @param uri
     * @param selection
     * @param seltionArgs
     */
    public static <T extends DbInterface<T>> void insertWithNotFound(T t, Uri uri, String selection, String... seltionArgs) {
        ContentResolver resolver = App.getResolver();
        Cursor cursor = resolver.query(uri, null, selection, seltionArgs, null);
        if (null == cursor || !cursor.moveToFirst()) {
            cursor.close();
            resolver.insert(uri, t.getContentValues());
        }
    }

    /**
     * 插入数据
     *
     * @param t
     * @param uri
     */
    public static <T extends DbInterface<T>> void insertData(T t, Uri uri) {
        ContentResolver resolver = App.getResolver();
        resolver.insert(uri, t.getContentValues());
    }
}
