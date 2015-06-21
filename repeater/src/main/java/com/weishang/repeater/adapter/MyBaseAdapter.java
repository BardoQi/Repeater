package com.weishang.repeater.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.weishang.repeater.utils.ViewInject;

import java.util.ArrayList;

/**
 * 简易的数据适配器
 *
 * @param <T>
 * @author Administrator
 */
public abstract class MyBaseAdapter<T, H> extends BaseAdapter implements Filterable {
    protected LayoutInflater mInflater;
    protected ArrayList<T> ts;

    public MyBaseAdapter(Context context, ArrayList<T> ts) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.ts = new ArrayList<T>();
        if (null != ts && !ts.isEmpty()) {
            this.ts.addAll(ts);
        }
    }

    @Override
    public int getCount() {
        return ts.size();
    }

    @Override
    public T getItem(int position) {
        T t = null;
        try {
            t = ts.get(position);
        } catch (Exception e) {
        }
        return t;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (null == convertView) {
            convertView = newView(itemViewType, position, convertView, parent);
        }
        H holder = (H) convertView.getTag();
        initView(itemViewType, position, convertView, holder);
        return convertView;
    }

    public void remove(int position) {
        this.ts.remove(position);
        notifyDataSetChanged();
    }

    public void addHeaderData(T t) {
        if (null != t) {
            this.ts.add(t);
            notifyDataSetChanged();
        }
    }

    public void addHeaderDatas(ArrayList<T> ts) {
        if (null != ts && !ts.isEmpty()) {
            ArrayList<T> temp = new ArrayList<T>(ts);
            temp.addAll(this.ts);
            this.ts = temp;
            notifyDataSetChanged();
        }
    }

    public void addFootData(T t) {
        if (null != t) {
            this.ts.add(t);
            notifyDataSetChanged();
        }
    }

    public void addFootDatas(ArrayList<T> ts) {
        if (null != ts && !ts.isEmpty()) {
            this.ts.addAll(ts);
            notifyDataSetChanged();
        }
    }

    public void swrpDatas(ArrayList<T> datas) {
        if (null != ts) {
            ts.clear();
        }
        ts.addAll(datas);
        notifyDataSetChanged();
    }

    public ArrayList<T> getDatas() {
        return ts;
    }

    public void clear() {
        if (null != ts) {
            ts.clear();
            ts = null;
        }
    }

    /**
     * 过滤关键字
     */
    public void filterItem(CharSequence charSequence) {
        Filter filter = getFilter();
        if (null == filter) {
            throw new NullPointerException("过滤器对象不能为空!请复写getFilter()方法~");
        }
        filter.filter(charSequence);
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public ArrayList<T> getItems() {
        return ts;
    }

    protected View initConvertView(ViewGroup parent, int layout, H ViewHolder) {
        View convertView = mInflater.inflate(layout, parent, false);
        ViewInject.init(ViewHolder, convertView);
        convertView.setTag(ViewHolder);
        return convertView;
    }

    /**
     * 初始化view
     *
     * @param position
     * @param convertView
     * @param parent
     */
    public abstract View newView(int type, int position, View convertView, ViewGroup parent);

    public abstract void initView(int type, int position, View convertView, H holder);

}
