package com.weishang.repeater.event;

import com.weishang.repeater.bean.Music;

/**
 * Created by momo on 2015/3/29.
 * 音乐播放事件
 */
public class MusicPlayEvent {
    private final Music music;
    private final boolean isPlaying;

    public MusicPlayEvent(Music music, boolean isPlaying) {
        this.music = music;
        this.isPlaying = isPlaying;
    }

    public Music getMusic() {
        return music;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
