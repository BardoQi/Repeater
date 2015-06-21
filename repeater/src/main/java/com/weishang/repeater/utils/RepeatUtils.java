package com.weishang.repeater.utils;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import com.weishang.repeater.App;
import com.weishang.repeater.PlayActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.event.FavoriteEvent;
import com.weishang.repeater.provider.BusProvider;

import java.util.ArrayList;

/**
 * 复读列表工具
 *
 * @author momo
 * @Date 2015/3/1
 */
public class RepeatUtils {

    /**
     * 播放音乐
     *
     * @param context
     * @param position
     * @param datas
     */
    public static void playMusic(Context context, int position, ArrayList<Music> datas) {
        if (null != datas && !datas.isEmpty()) {
            Music music = datas.get(position);
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra(PlayActivity.PLAY_LIST_ID, music.listId);
            intent.putExtra(PlayActivity.PLAY_POSITION, position);
            intent.putExtra(PlayActivity.PLAY_MUSIC, music);
            context.startActivity(intent);
            setMusicPlay(music);
        }
    }

    /**
     * 设置音乐己音乐
     *
     * @param music
     */
    public static void setMusicPlay(final Music music) {
        if (null == music) return;
        RunnableUtils.runWithExecutor(new Runnable() {
            @Override
            public void run() {
                //更新:列表名,时间
                music.listId = MyDb.LAST_VISIBLE;
                music.ut = System.currentTimeMillis();
                MyDb.repleceData(music, DbTable.FILE_URI, "id=? and list_id=?", String.valueOf(music.id), String.valueOf(music.listId));
            }
        });
    }

    /**
     * 检索是否收藏
     *
     * @param music
     * @return
     */
    public static boolean isFavoriteMusic(Music music) {
        boolean result = false;
        if (null != music) {
            ContentResolver resolver = App.getResolver();
            Cursor c = resolver.query(DbTable.FILE_URI, null, "id=? and list_id=?", new String[]{String.valueOf(music.id), String.valueOf(MyDb.MY_FAVOURITE)}, null);
            if (null != c) {
                result = c.moveToFirst();
                c.close();
            }
        }
        return result;
    }

    /**
     * 更新收藏音乐状态,收藏音乐分两个位置存储,一个为原来music对象,一个为单独的listid
     *
     * @param music
     */
    public static void favoriteMusic(View v, Music music, boolean isFavorite) {
        if (null == music) return;
        v.setSelected(music.isFavourite = isFavorite);
        ContentResolver resolver = App.getResolver();
        if (isFavorite) {
            ContentValues contentValues = music.getContentValues();
            resolver.update(DbTable.FILE_URI, music.getContentValues(), "id=? and list_id=?", new String[]{String.valueOf(music.id), String.valueOf(music.listId)});
            contentValues.put("list_id", MyDb.MY_FAVOURITE);
            resolver.insert(DbTable.FILE_URI, contentValues);
            App.toast(R.string.collect_success);
        } else {
            //更新以前表的favourite
            resolver.update(DbTable.FILE_URI, music.getContentValues(), "id=? and list_id=?", new String[]{String.valueOf(music.id), String.valueOf(music.listId)});
            //删除收藏分列
            resolver.delete(DbTable.FILE_URI, "id=? and list_id=?", new String[]{String.valueOf(music.id), String.valueOf(MyDb.MY_FAVOURITE)});
            App.toast(R.string.collect_cancel);
        }
        BusProvider.getInstance().post(new FavoriteEvent(music, isFavorite));
    }

    /**
     * 插入音乐
     *
     * @param music
     * @param listId
     * @param runnable
     */
    public static void insertMusic(final Music music, final int listId, final Runnable runnable) {
        if (null == music) return;
        HandleTask.run(new HandleTask.TaskAction<Object>() {
            @Override
            public Object run() {
                music.listId = listId;
                MyDb.insertWithNotFound(music, DbTable.FILE_URI, "id=? and list_id=?", String.valueOf(music.id), String.valueOf(listId));
                return null;
            }

            @Override
            public void postRun(Object o) {
                if (null != runnable) {
                    runnable.run();
                }
            }
        });
    }
}
