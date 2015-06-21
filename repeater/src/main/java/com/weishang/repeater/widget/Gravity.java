package com.weishang.repeater.widget;

/**
 * 绘制方向
 * 
 * @author momo
 * @Date 2015/2/28
 */
public interface Gravity {

	int NO = 0x0000;
	int TOP = 0x0001;
	int BOTTOM = 0x0002;
	int LEFT = 0x0004;
	int RIGHT = 0x0008;

	int TOP_LEFT = TOP | LEFT;// 5 [0x5]
	int TOP_RIGHT = TOP | RIGHT;// 9 [0x9]

	int BOTTOM_LEFT = BOTTOM | LEFT;// 6 [0x6]
	int BOTTOM_RIGHT = BOTTOM | RIGHT;// 10 [0xa]

	int TOP_BOTTOM = TOP | BOTTOM;// 3[0x3];
	int LEFT_RIGTH = LEFT | RIGHT;// 12 [0xc]

	int LACK_LEFT = RIGHT | TOP | BOTTOM;// 11 [0xb]
	int LACK_RIGHT = LEFT | TOP | BOTTOM;// 7 [0x7]
	int LACK_TOP = LEFT | RIGHT | BOTTOM;// 14 [0xe]
	int LACK_BOTTOM = LEFT | RIGHT | TOP;// 13 [0xd]
	int ALL = LEFT | RIGHT | TOP | BOTTOM;// 15
}
