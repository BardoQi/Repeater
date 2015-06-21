package com.weishang.repeater.utils;

import android.media.MediaRecorder;

import com.weishang.repeater.preference.ConfigManager;

import java.io.File;

/**
 * 录音工具类
 *
 * @author momo
 * @Date 2014/6/9
 */
public class RecorderUtils {

    private static MediaRecorder recorder;// 录音对象

    /**
     * 开始录音
     */
    public static void startRecorder(String fileName, Runnable startAction) {
        // 建立录音档
        try {
            File recAudioFile = new File(ConfigManager.record, fileName);
            if (recAudioFile.exists()) {
                recAudioFile.delete();
            }
            if (null == recorder) {
                recorder = new MediaRecorder();
                //设定录音来源为麦克风
            }
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(recAudioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            if(null!=startAction){
                startAction.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    public static void startRecorder(String fileName) {
        startRecorder(fileName, null);
    }

    /**
     * 停止录音
     */
    public static void stopRecorder() {
        if (null != recorder) {
            try {
                recorder.stop();
            } catch (IllegalStateException e) {
            }
        }
    }
}
