package com.ortega.vk.chatmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Utils {
	private static Scanner consoleScanner;
	
	public static Scanner getConsoleScanner() {
		if (consoleScanner == null)
			consoleScanner = new Scanner(System.in);
		return consoleScanner;
	}
	
	public static String dateTime(int sec) {
		long millis = secToMillis(sec);
		Date date = new Date(millis);
		return dateTime(date);
	}
	
	public static String dateTimeForFileName(Date date) {
		return dateTime(date).replace(':', '.');
	}
	
	public static String dateTime(Date date) {
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}
	
	public static long secToMillis(int sec) {
		long millis = sec;
		millis *= 1000;
		return millis;
	}
	
	public static Date daysAgo(int days) {
		final long MS_PER_DAY = 86400 * 1000;
		Date today = new Date();
		Date ago = new Date(today.getTime() - days * MS_PER_DAY);
		return ago;
	}
}
