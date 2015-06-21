package com.weishang.repeater.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

/**
 * 局部字体帮助类
 * 
 * @author Administrator
 * 
 */
public class TextFontUtils {
	/**
	 * 改变包含此字符串的字符,可选设置背景高度
	 */
	public static void setSearchFontColor(TextView view, String text, String word, boolean showBG) {
		if (!TextUtils.isEmpty(text)) {
			if (!TextUtils.isEmpty(word)) {
				int index = text.indexOf(word);
				if (-1 != index) {
					SpannableStringBuilder span = new SpannableStringBuilder(text);
					span.setSpan(new ForegroundColorSpan(Color.BLACK), index, index + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (showBG) {
						span.setSpan(new BackgroundColorSpan(Color.RED), index, index + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					view.setText(span);
				}
			} else {
				view.setText(text);
			}
		}
	}

	/**
	 * 改变包含此字符串的字符,不设置背景高度
	 */
	public static void setSearchFontColor(TextView view, String text, String word) {
		setSearchFontColor(view, text, word, true);
	}

	/**
	 * 设置局部文本颜色
	 * 
	 * @param view
	 * @param color
	 * @param words
	 */
	public static void setWordColor(TextView view, int color, Object... words) {
		CharSequence info = view.getText();
		if (!TextUtils.isEmpty(info) && null != words) {
			SpannableStringBuilder span = new SpannableStringBuilder(info);
			for (int i = 0; i < words.length; i++) {
				if (null != words[i]) {
					int start = info.toString().indexOf(words[i].toString());
					if (-1 != start) {
						span.setSpan(new ForegroundColorSpan(color), start, start + words[i].toString().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					}
				}
			}
			view.setText(span);
		}
	}

	/**
	 * 设置局部粗体/斜体颜色
	 * 
	 * @param view
	 * @param style
	 *            {@link android.graphics.Typeface}
	 * @param words
	 */
	public static void setWordTypedFace(TextView view, int style, Object... words) {
		CharSequence info = view.getText();
		if (!TextUtils.isEmpty(info) && null != words) {
			SpannableStringBuilder span = new SpannableStringBuilder(info);
			for (int i = 0; i < words.length; i++) {
				if (null != words[i]) {
					int start = info.toString().indexOf(words[i].toString());
					if (-1 != start) {
						span.setSpan(new StyleSpan(style), start, start + words[i].toString().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					}
				}
			}
			view.setText(span);
		}
	}

	/**
	 * 设置局部粗体/斜体颜色
	 * 
	 * @param view
	 * @param style
	 *            {@link android.graphics.Typeface}
	 * @param words
	 */
	public static void setWordColorAndTypedFace(TextView view, int color, int style, Object... words) {
		setWordColor(view, color, words);
		setWordTypedFace(view, style, words);
	}
}
