package com.weishang.repeater.widget.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class CalendarDay implements Serializable {
	private static final long serialVersionUID = -5456695978688356202L;
	private Calendar calendar;

	public int day;
	public int month;
	public int year;

	public CalendarDay() {
		setTime(System.currentTimeMillis());
	}

	public CalendarDay(int year, int month, int day) {
		setDay(year, month, day);
	}

	public CalendarDay(long timeInMillis) {
		setTime(timeInMillis);
	}

	public CalendarDay(Calendar calendar) {
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}

	private void setTime(long timeInMillis) {
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}
		calendar.setTimeInMillis(timeInMillis);
		month = this.calendar.get(Calendar.MONTH);
		year = this.calendar.get(Calendar.YEAR);
		day = this.calendar.get(Calendar.DAY_OF_MONTH);
	}

	public void set(CalendarDay calendarDay) {
		year = calendarDay.year;
		month = calendarDay.month;
		day = calendarDay.day;
	}

	public void setDay(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public Date getDate() {
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}
		calendar.set(year, month, day,0,0,0);
		return calendar.getTime();
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{ year: ");
		stringBuilder.append(year);
		stringBuilder.append(", month: ");
		stringBuilder.append(month);
		stringBuilder.append(", day: ");
		stringBuilder.append(day);
		stringBuilder.append(" }");

		return stringBuilder.toString();
	}

	public static class SelectedDays<K> implements Serializable {
		private static final long serialVersionUID = 3942549765282708376L;
		private K first;
		private K last;

		public K getFirst() {
			return first;
		}

		public void setFirst(K first) {
			this.first = first;
		}

		public K getLast() {
			return last;
		}

		public void setLast(K last) {
			this.last = last;
		}
	}
}
