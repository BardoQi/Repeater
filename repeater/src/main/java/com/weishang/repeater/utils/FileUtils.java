package com.weishang.repeater.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 文件帮助类
 *
 * @author momo
 * @Date 2014/6/9
 */
public class FileUtils {
    public static final int KB = 1024;
    public static final long MB = 1024 * KB;
    public static final long GB = 1024 * MB;

    public static final String[] FILTER_FILES;

    static {
        ;//通话录音,//录音机
        FILTER_FILES = new String[]{"call", "Recorder"};
    }

    /**
     * 过滤指定文件
     * @param path
     * @return
     */
    public static boolean isFilter(String path) {
        boolean result = false;
        for (int i = 0; i < FILTER_FILES.length; i++) {
            if (path.endsWith(FILTER_FILES[i])) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 检测SD卡否挂载
     *
     * @return
     */
    public static boolean isAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获得指定文件内,文件总大小
     */
    public static long getFileLength(File file) {
        long length = 0;
        if (file.isDirectory()) {
            ScanListener<File> listener = new ScanListener<File>() {
                public long total;

                @Override
                public void scan(File f) {
                    total += f.length();
                }

                @Override
                public Long result() {
                    return total;
                }
            };
            scanFile(file, listener);
            length = listener.result();
        } else {
            length = file.length();
        }
        return length;
    }

    /**
     * 将大小转为文件大小格式
     *
     * @param length
     * @return
     */
    public static String FormetFileSize(long length) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = null;
        if (100 < length) {
            if (length < KB) {
                fileSizeString = df.format((double) length) + "B";
            } else if (length < MB) {
                fileSizeString = df.format((double) length / KB) + "K";
            } else if (length < GB) {
                fileSizeString = df.format((double) length / MB) + "M";
            } else {
                fileSizeString = df.format((double) length / GB) + "G";
            }
        }
        return fileSizeString;
    }

    /**
     * 遍历文件操作
     *
     * @param file
     * @param listener
     */
    public static void scanFile(File file, ScanListener<File> listener) {
        LinkedList<File> files = new LinkedList<File>();
        files.add(file);
        while (files.size() > 0) {
            File f = files.removeFirst();
            if (f.isDirectory()) {
                File[] listFiles = f.listFiles();
                if (null != listFiles && 0 < listFiles.length) {
                    files.addAll(Arrays.asList(listFiles));
                }
            } else if (null != listener) {
                listener.scan(f);
            }
        }
        if (null != listener) {
            listener.result();
        }
    }

    /**
     * 清空目录内所有文件
     */
    public static void clearFile(File file) {
        File[] listFiles = null;
        if (file.exists()) {
            listFiles = file.listFiles();
            if (null != listFiles && 0 < listFiles.length) {
                int length = listFiles.length;
                for (int i = 0; i < length; i++) {
                    listFiles[i].delete();
                }
            }
        }
    }

    /**
     * 扫措监听
     *
     * @param <E>
     */
    public interface ScanListener<E> {
        void scan(E e);

        <T> T result();
    }
}
