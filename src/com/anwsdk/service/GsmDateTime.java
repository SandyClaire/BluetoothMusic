package com.anwsdk.service;

public class GsmDateTime {
	// The difference between local time and GMT in hours
	public int Timezone;
	public int Second;
	public int Minute;
	public int Hour;
	public int Day;
	// January = 1, February = 2, etc.
	public int Month;
	// Complete year number. Not 03, but 2003
	public int Year;

	public void setTimezone(int value) {
		Timezone = value;
	}

	public int getTimezone() {
		return Timezone;
	}

	public void setSecond(int value) {
		Second = value;
	}

	public int getSecond() {
		return Second;
	}

	public void setMinute(int value) {
		Minute = value;
	}

	public int getMinute() {
		return Minute;
	}

	public void setHour(int value) {
		Hour = value;
	}

	public int getHour() {
		return Hour;
	}

	public void setDay(int value) {
		Day = value;
	}

	public int getDay() {
		return Day;
	}

	public void setMonth(int value) {
		Month = value;
	}

	public int getMonth() {
		return Month;
	}

	public void setYear(int value) {
		Year = value;
	}

	public int getYear() {
		return Year;
	}
	
	public GsmDateTime(int timezone, int second, int minute, int hour, int day, int month, int year) {
		Timezone = timezone;
		Second = second;
		Minute = minute;
		Hour = hour;
		Day = day;
		Month = month;
		Year = year;
	}
	
	public GsmDateTime() {
		Timezone = 0;
		Second = 0;
		Minute = 0;
		Hour = 0;
		Day = 0;
		Month = 0;
		Year = 0;
	}
}
