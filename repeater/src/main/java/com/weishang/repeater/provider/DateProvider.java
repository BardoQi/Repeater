package com.weishang.repeater.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.utils.Loger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by momo on 2015/1/1.
 */
public class DateProvider extends ContentProvider {
    private UriMatcher matcher = null;
    private List<Integer> matchIds = null;

    // uri匹配标记
    private static final int FILE_ITEM = 0;
    private static final int LIST_ITEM = 1;
    private static final int REPEAT_ITEM = 2;
    private static final int RECORD_ITEM = 3;
    private static final int REMARK_ITEM = 4;

    public static SQLiteDatabase db;// 数据库操作对象

    private HashMap<String, String> FILE_MAP, LIST_MAP, REPEAT_MAP, RECORD_MAP, REMARK_MAP;

    public DateProvider() {
        addUris(MyDb.LOCAL_RES, MyDb.PLAY_LIST, MyDb.REPEAT, MyDb.RECORD, MyDb.REMARK);
        addMap(FILE_MAP, DbTable.FILE_SELECTION);
        addMap(LIST_MAP, DbTable.LIST_SELECTION);
        addMap(REPEAT_MAP, DbTable.REPEAT_SELECTION);
        addMap(RECORD_MAP, DbTable.RECORD_SELECTION);
        addMap(REMARK_MAP, DbTable.REMARK_SELECTION);
    }

    private void addMap(HashMap<String, String> map, String[] selection) {
        if (null == map) {
            map = new HashMap<String, String>();
        }
        for (int i = 0; i < selection.length; i++) {
            map.put(selection[i], selection[i]);
        }
    }

    private void addUris(String... tables) {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matchIds = new ArrayList<Integer>();
        if (null != tables) {
            for (int i = 0; i < tables.length; i++) {
                matchIds.add(i);
                matcher.addURI(DbTable.AUTHORITY, tables[i], i);
            }
        }
    }

    @Override
    public boolean onCreate() {
        db = new MyDb(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (matcher.match(uri)) {
            case FILE_ITEM:
                builder.setTables(MyDb.LOCAL_RES);
                builder.setProjectionMap(FILE_MAP);
                break;
            case LIST_ITEM:
                builder.setTables(MyDb.PLAY_LIST);
                builder.setProjectionMap(LIST_MAP);
                break;
            case REPEAT_ITEM:
                builder.setTables(MyDb.REPEAT);
                builder.setProjectionMap(REPEAT_MAP);
                break;
            case RECORD_ITEM:
                builder.setTables(MyDb.RECORD);
                builder.setProjectionMap(RECORD_MAP);
                break;
            case REMARK_ITEM:
                builder.setTables(MyDb.REMARK);
                builder.setProjectionMap(REMARK_MAP);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // 判断uid
        Cursor cursor = null;
        try {
            cursor = builder.query(db, strings, s, strings2, null, null, s2);
        } catch (Exception e) {
            Loger.i("query_db", e);
        }
        if (null != cursor) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // 插入表名
        String tableName = null;
        switch (ensureUri(uri)) {
            case FILE_ITEM:
                tableName = MyDb.LOCAL_RES;
                break;
            case LIST_ITEM:
                tableName = MyDb.PLAY_LIST;
                break;
            case REPEAT_ITEM:
                tableName = MyDb.REPEAT;
                break;
            case RECORD_ITEM:
                tableName = MyDb.RECORD;
                break;
            case REMARK_ITEM:
                tableName = MyDb.REMARK;
                break;
            default:
                break;
        }
        ContentValues contentValues = null;
        if (null != values) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }
        long rowId = -1;
        try {
            rowId = db.insert(tableName, null, contentValues);
        } catch (Exception e) {
            Loger.i("insert_db", e);
        }
        if (rowId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        String tableName = null;
        switch (ensureUri(uri)) {
            case FILE_ITEM:
                tableName = MyDb.LOCAL_RES;
                break;
            case LIST_ITEM:
                tableName=MyDb.PLAY_LIST;
                break;
            case REPEAT_ITEM:
                tableName = MyDb.REPEAT;
                break;
            case RECORD_ITEM:
                tableName = MyDb.RECORD;
                break;
            case REMARK_ITEM:
                tableName = MyDb.REMARK;
                break;
            default:
                break;
        }
        int count = -1;
        if (!TextUtils.isEmpty(tableName) && null != db) {
            try {
                count = db.delete(tableName, s, strings);
            } catch (Exception e) {
                Loger.i("delete_db", e);
            }
        }
        if (-1 == count) {
            throw new SQLiteException("delete fail!");
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        String tableName = null;
        switch (ensureUri(uri)) {
            case FILE_ITEM:
                tableName = MyDb.LOCAL_RES;
                break;
            case REPEAT_ITEM:
                tableName = MyDb.REPEAT;
                break;
            case RECORD_ITEM:
                tableName = MyDb.RECORD;
                break;
            case REMARK_ITEM:
                tableName = MyDb.REMARK;
                break;
            default:
                break;
        }
        int count = -1;
        if (!TextUtils.isEmpty(tableName)) {
            try {
                count = db.update(tableName, contentValues, s, strings);
            } catch (Exception e) {
                Loger.i("update_db", e);
            }
        }
        if (-1 == count) {
            throw new SQLiteException("update fail!");
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        // 插入表名
        String tableName = null;
        switch (ensureUri(uri)) {
            case FILE_ITEM:
                tableName = MyDb.LOCAL_RES;
                break;
            case LIST_ITEM:
                tableName = MyDb.PLAY_LIST;
                break;
            case REPEAT_ITEM:
                tableName = MyDb.REPEAT;
                break;
            case RECORD_ITEM:
                tableName = MyDb.RECORD;
                break;
            case REMARK_ITEM:
                tableName = MyDb.REMARK;
                break;
            default:
                break;
        }
        long rowId = -1;

        db.beginTransaction();
        for (int i = 0; i < values.length; i++) {
            try {
                rowId = db.insert(tableName, null, values[i]);
            } catch (Exception e) {
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        if (rowId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return (int) rowId;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    private int ensureUri(Uri uri) {
        int match = matcher.match(uri);
        if (!matchIds.contains(match)) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return match;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }
}
