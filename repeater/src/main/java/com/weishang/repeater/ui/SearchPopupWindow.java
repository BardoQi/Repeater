package com.weishang.repeater.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.PlayActivity;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Music;
import com.weishang.repeater.bean.PlayList;
import com.weishang.repeater.db.DbTable;
import com.weishang.repeater.db.MyDb;
import com.weishang.repeater.utils.RepeatUtils;
import com.weishang.repeater.utils.UnitUtils;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by cz on 15/5/24.
 * 添加音乐弹窗
 */
public class SearchPopupWindow extends PopupWindow {
    private static final int MIN_ITEM_COUNT=3;

    @ID(id=R.id.ll_container)
    private LinearLayout mLayout;
    public SearchPopupWindow(final Context context,final Music music) {
        super(LayoutInflater.from(context).inflate(R.layout.search_popup, null), UnitUtils.dip2px(context, 200), ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = getContentView();
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setFocusable(true);
        setOutsideTouchable(true);
        ViewInject.init(this,view);
        //更改背景颜色
        Drawable background = mLayout.getBackground();
        if(background instanceof LayerDrawable){
            LayerDrawable layerDrawable = (LayerDrawable) background;
            Drawable drawable = layerDrawable.findDrawableByLayerId(R.id.shape_bacground);
            if(drawable instanceof GradientDrawable){
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(context.getResources().getColor(R.color.yellow));
            }
        }
        //查找列表
        final ArrayList<PlayList> playLists = MyDb.getDatas(DbTable.LIST_URI, new PlayList(), DbTable.LIST_SELECTION, null);
        if(null!=playLists&&!playLists.isEmpty()){
            final int size=playLists.size();
            if(MIN_ITEM_COUNT<size){
                addItems(context,0,MIN_ITEM_COUNT,playLists,music);
                addTextView(context, App.getResString(R.string.more), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLayout.removeView(v);
                        addItems(context, MIN_ITEM_COUNT, size, playLists, music);
                    }
                });
            } else {
                addItems(context,0,size,playLists,music);
            }

        }
        mLayout.findViewById(R.id.tv_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra(PlayActivity.PLAY_LIST_ID, MyDb.SINGLE_ITEM);
                music.listId=MyDb.SINGLE_ITEM;
                intent.putExtra(PlayActivity.PLAY_MUSIC, music);
                context.startActivity(intent);
                RepeatUtils.setMusicPlay(music);
            }
        });
    }

    public void addItems(Context context,int start,int count,ArrayList<PlayList> playLists,final Music music){
        for(int i=start;i<count;i++){
            final PlayList playList = playLists.get(i);
            addTextView(context, playList.name, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加音乐到列表
                    dismiss();
                    music.listId = playList.id;
                    MyDb.insertData(music, DbTable.FILE_URI);
                    App.toast(R.string.add_complate);
                }
            });
        }
    }

    public void addTextView(Context context, String text, View.OnClickListener listener){
        int padding= UnitUtils.dip2px(context, 12);
        TextView textView=new TextView(context);
        textView.setTextColor(App.getResourcesColor(R.color.text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textView.setPadding(padding, padding, padding, padding);
        textView.setOnClickListener(listener);
        textView.setText(text);
        textView.setBackgroundResource(R.drawable.item_selector);
        mLayout.addView(textView);
    }




}
