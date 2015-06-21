package com.weishang.repeater.db;

import android.net.Uri;

/**
 * Created by momo on 2015/1/1.
 */
public class DbTable {
    public static final String AUTHORITY;
    public static final Uri FILE_URI;
    public static final Uri LIST_URI;
    public static final Uri REPEAT_URI;
    public static final Uri RECORD_URI;
    public static final Uri REMARK_URI;

    public static String[] FILE_SELECTION;
    public static String[] LIST_SELECTION;
    public static String[] REPEAT_SELECTION;
    public static String[] RECORD_SELECTION;
    public static String[] REMARK_SELECTION;

    static {
        AUTHORITY = "com.weishang.repeater";
        FILE_URI = Uri.parse("content://" + AUTHORITY + "/" + MyDb.LOCAL_RES);
        LIST_URI = Uri.parse("content://" + AUTHORITY + "/" + MyDb.PLAY_LIST);
        REPEAT_URI = Uri.parse("content://" + AUTHORITY + "/" + MyDb.REPEAT);
        RECORD_URI = Uri.parse("content://" + AUTHORITY + "/" + MyDb.RECORD);
        REMARK_URI = Uri.parse("content://" + AUTHORITY + "/" + MyDb.REMARK);


        FILE_SELECTION = new String[]{"_id", "id", "ut", "path", "name","album", "duration", "author", "size", "list_id", "favourite", "directory_name", "directory", "word"};
        LIST_SELECTION = new String[]{"_id", "name", "ct", "ut", "visible"};
        REPEAT_SELECTION = new String[]{"_id", "id", "name", "ct", "ut", "start", "end", "count","position","record","record_length"};
        RECORD_SELECTION = new String[]{"_id", "id", "name", "ct", "ut", "time"};
        REMARK_SELECTION = new String[]{"_id", "id", "title", "content", "ct", "ut","size","color","line_color","line_size","bg","mood"};
    }
}
