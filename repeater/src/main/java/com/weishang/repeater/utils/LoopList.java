package com.weishang.repeater.utils;

/**
 * 循环列表
 *
 * @author momo
 */
public class LoopList<E> {
    private Entry mFirst;
    private Entry mLast;
    private Entry mCurrentEntry;
    private int size;

    public LoopList() {
        super();
    }

    public void offer(E e) {
        Entry entry = null;
        if (null == mFirst) {
            entry = new Entry(e, null);
            mCurrentEntry = mFirst = mLast = entry;
        } else {
            entry = new Entry(e, mFirst);
            mLast.next = entry;
            mLast = entry;
        }
        size++;
    }

    public E peek() {
        return mCurrentEntry.e;
    }

    /**
     * 跳到指定位置
     *
     * @param e
     */
    public void seekTo(E e) {
        if(null==e|| mFirst.e==e) return;
        Entry first = mFirst;
        System.out.println("start:"+first.e);
        while (e != first.e) {
            first=first.next;
            System.out.println(first.e);
        }
        mCurrentEntry=first;
    }

    public E next() {
        mCurrentEntry = mCurrentEntry.next;
        E e = mCurrentEntry.e;
        return e;
    }

    public class Entry {
        private E e;
        public Entry next;

        public Entry(E e, Entry next) {
            super();
            this.e = e;
            this.next = next;
        }

    }

}
