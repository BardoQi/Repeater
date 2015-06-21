package com.weishang.repeater.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weishang.repeater.bean.RepeatInfo;

import java.util.ArrayList;

/**
 * Created by momo on 2015/4/19.
 * recyclerView 简易数据适配器
 */
public abstract class MyRecyclerAdapter<E, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    protected final ArrayList<E> mItems;
    protected final LayoutInflater mInflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener mListener;

    public MyRecyclerAdapter(Context context, ArrayList<E> items, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.mRecyclerView = recyclerView;
        this.mItems = new ArrayList<E>();
        if (null != items && !items.isEmpty()) {
            this.mItems.addAll(items);
        }
    }

    public MyRecyclerAdapter(Context context, ArrayList<E> items) {
        this(context, items, null);
    }

    @Override
    public abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(H holder, final int position) {
        final E item = getItem(position);
        onBind(holder, item, position);
        //设置点击
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.itemClick(view, position);
                }
            }
        });
    }

    public abstract void onBind(H holder, E e, int position);

    public void insert(E e) {
        if (null != e) {
            this.mItems.add(e);
            int insertPosition = getItemCount() - 1;
            notifyItemInserted(insertPosition);
            if (null != mRecyclerView) {
                mRecyclerView.scrollToPosition(insertPosition);
            }
        }
    }

    public void removeOnAnim(int index) {
        this.mItems.remove(index);
        notifyItemRemoved(index);
        //通知刷新数据,延持600毫秒,300毫秒消失,300移动
        if (null != mRecyclerView) {
            RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
            long duration = itemAnimator.getRemoveDuration() + itemAnimator.getMoveDuration();
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            }, duration);
        }
    }

    public void removeOnAnim(RepeatInfo info) {
        int index = this.mItems.indexOf(info);
        if (-1 != index) {
            removeOnAnim(index);
        }
    }

    /**
     * 更新条目
     *
     * @param e
     */
    public void updateItem(E e) {
        if (null != e) {
            int i = mItems.indexOf(e);
            if (-1 != i) {
                mItems.set(i, e);
                notifyItemChanged(i);
            }
        }
    }

    public void swapItems(final ArrayList<E> items) {
        notifyItemRangeRemoved(0, getItemCount());
        if (null != mRecyclerView) {
            RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
            long moveDuration = itemAnimator.getMoveDuration();
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mItems.clear();
                    notifyDataSetChanged();
                }
            }, moveDuration);
            if (null != items && !items.isEmpty()) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int count = getItemCount();
                        mItems.addAll(items);
                        notifyItemRangeInserted(count, getItemCount());
                    }
                }, moveDuration * 2);
            }
        } else {
            mItems.clear();
            if (null != items) {
                mItems.addAll(items);
            }
            notifyDataSetChanged();
        }
    }

    public E getItem(int position) {
        E e = null;
        if (0 <= position && position < getItemCount()) {
            e = this.mItems.get(position);
        }
        return e;
    }

    public E getLastItem() {
        return getItem(getItemCount() - 1);
    }

    public void clear() {
        this.mItems.clear();
        notifyItemMoved(0, getItemCount());
    }

    public boolean isEmpty() {
        return 0 == getItemCount();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public interface OnItemClickListener {
        void itemClick(View v, int position);
    }
}
