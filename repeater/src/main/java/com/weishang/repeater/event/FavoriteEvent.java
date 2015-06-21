package com.weishang.repeater.event;

import com.weishang.repeater.bean.Music;

/**
 * Created by momo on 2015/3/30.
 * 收藏音乐事件
 */
public class FavoriteEvent {
    private final Music music;
    private final boolean isFavorite;

    public FavoriteEvent(Music music, boolean isFavorite) {
        this.music = music;
        this.isFavorite = isFavorite;
    }

    public Music getMusic() {
        return music;
    }


    public boolean isFavorite() {
        return isFavorite;
    }
}
