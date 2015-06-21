package com.weishang.repeater.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weishang.repeater.App;
import com.weishang.repeater.R;
import com.weishang.repeater.annotation.ID;
import com.weishang.repeater.bean.Remark;
import com.weishang.repeater.utils.DateUtils;
import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * Created by momo on 2015/4/25.
 * 备忘列表数据适配器
 */
public class RemarkAdapter extends MyRecyclerAdapter<Remark, RemarkAdapter.ViewHolder> {
    private int[] mColors;
    private int[] mFontColor;

    public RemarkAdapter(Context context, ArrayList<Remark> items, RecyclerView recyclerView) {
        super(context, items, recyclerView);
        mColors = new int[]{0xFFFFFAE6, 0xFFF7EAD0, 0xFFE7D3AA, 0xFFDECFB2, 0xFFA5A395, 0xFFE7E6BA, 0xFFCEEABA, 0xFF3E4F43, 0xFF293B2C, 0xFF293343};
        mFontColor = new int[]{0xFF312F2C, 0xFF393325, 0xFF393340, 0xFF393365, 0xFF2D2C29, 0xFF424433, 0xFF46573F, 0xFF909F8D, 0xFF90A487, 0xFFADB6B2};
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.remark_item, parent, false));
    }

    @Override
    public void onBind(ViewHolder holder, Remark remark, int position) {
        holder.title.setText(remark.title);
        holder.content.setText(remark.content);
        //字体颜色
        holder.title.setTextColor(mFontColor[remark.color]);
        holder.content.setTextColor(mFontColor[remark.color]);
        //设置背景颜色
        holder.container.setCardBackgroundColor(mColors[remark.color]);
        long yearStartMillis = DateUtils.getYearStartMillis();
        String formatValue = null;
        if (yearStartMillis > remark.ut) {
            formatValue = App.getStr(R.string.year_format);
        } else {
            formatValue = App.getStr(R.string.month_format);
        }
        holder.date.setText(DateUtils.getFromat(formatValue, remark.ut));
        holder.date.setTextColor(getDarkColor(mColors[remark.color]));
        //分隔线颜色
        holder.divier.setBackgroundColor(getDarkColor(mColors[remark.color]));
    }

    private int getDarkColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.rgb(r + 30 > 0xFF ? r - 30 : r + 30, g + 30 > 0xFF ? g - 30 : g + 30, b + 30 > 0xFF ? b - 30 : b + 30);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @ID(id = R.id.cv_remark_card)
        CardView container;
        @ID(id = R.id.tv_remark_title)
        public TextView title;
        @ID(id = R.id.tv_remark_content)
        public TextView content;
        @ID(id = R.id.tv_remark_date)
        public TextView date;
        @ID(id = R.id.view_divier)
        View divier;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewInject.init(this, itemView);
        }
    }
}
