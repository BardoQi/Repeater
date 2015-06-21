package com.weishang.repeater.event;

import com.weishang.repeater.bean.Remark;

/**
 * Created by momo on 2015/4/25.
 * 添加备忘事件
 */
public class AddRemarkEvent {
    private final Remark remark;

    public AddRemarkEvent(Remark remark) {
        this.remark = remark;
    }

    public Remark getRemark() {
        return remark;
    }
}
