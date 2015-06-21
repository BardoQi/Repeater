package com.weishang.repeater.preference;

import android.os.Environment;

import com.weishang.repeater.App;
import com.weishang.repeater.R;

import java.io.File;

/**
 * Created by momo on 2015/3/9.
 *
 * @author momo
 *         配置项管理
 */
public class ConfigManager {
    public static final File appFile;// 程序SD卡目录
    public static final File imageCache;// 图片缓存
    public static final File musicCover;// 音乐封面
    public static final File record;//录音文件目录
    public static final File logFile;// 日志文件
    public static final File netLog;

    static {
        appFile = new File(Environment.getExternalStorageDirectory(), "/" + App.getStr(R.string.app_name) + "/");
        imageCache = new File(appFile, "/cache/");
        musicCover = new File(appFile, "/cover/");
        record = new File(appFile, "/record/");
        logFile = new File(appFile, "/log/");
        netLog = new File(logFile, "log.txt");
        mkdirs(appFile, musicCover, record, imageCache, logFile);
    }

    public static void mkdirs(File... files) {
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].exists()) {
                    files[i].mkdir();
                }
            }
        }
    }
}
