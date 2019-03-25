package com.sds.ocp.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * �궇吏� �떆媛� 愿��젴 �쑀�떥 �겢�옒�뒪. <br/> 
 * �쁽�옱 �겢�옒�뒪 �삉�뒗 long, Date ���엯 媛앹껜瑜� �궇吏� �삎�깭濡� �룷留ㅽ똿 �븯�뒗 硫붿꽌�뱶 �쐞二� 
 * 
 * <h4>Date and Time Patterns</h4>
 * 
 * <blockquote>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart shows pattern letters, date/time component, presentation, and examples.">
 *     <tr bgcolor="#ccccff">
 *         <th align=left>Letter
 *         <th align=left>Date or Time Component
 *         <th align=left>Presentation
 *         <th align=left>Examples
 *     <tr>
 *         <td><code>G</code>
 *         <td>Era designator
 *         <td><a href="#text">Text</a>
 *         <td><code>AD</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>y</code>
 *         <td>Year
 *         <td><a href="#year">Year</a>
 *         <td><code>1996</code>; <code>96</code>
 *     <tr>
 *         <td><code>M</code>
 *         <td>Month in year
 *         <td><a href="#month">Month</a>
 *         <td><code>July</code>; <code>Jul</code>; <code>07</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>w</code>
 *         <td>Week in year
 *         <td><a href="#number">Number</a>
 *         <td><code>27</code>
 *     <tr>
 *         <td><code>W</code>
 *         <td>Week in month
 *         <td><a href="#number">Number</a>
 *         <td><code>2</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>D</code>
 *         <td>Day in year
 *         <td><a href="#number">Number</a>
 *         <td><code>189</code>
 *     <tr>
 *         <td><code>d</code>
 *         <td>Day in month
 *         <td><a href="#number">Number</a>
 *         <td><code>10</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>F</code>
 *         <td>Day of week in month
 *         <td><a href="#number">Number</a>
 *         <td><code>2</code>
 *     <tr>
 *         <td><code>E</code>
 *         <td>Day in week
 *         <td><a href="#text">Text</a>
 *         <td><code>Tuesday</code>; <code>Tue</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>a</code>
 *         <td>Am/pm marker
 *         <td><a href="#text">Text</a>
 *         <td><code>PM</code>
 *     <tr>
 *         <td><code>H</code>
 *         <td>Hour in day (0-23)
 *         <td><a href="#number">Number</a>
 *         <td><code>0</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>k</code>
 *         <td>Hour in day (1-24)
 *         <td><a href="#number">Number</a>
 *         <td><code>24</code>
 *     <tr>
 *         <td><code>K</code>
 *         <td>Hour in am/pm (0-11)
 *         <td><a href="#number">Number</a>
 *         <td><code>0</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>h</code>
 *         <td>Hour in am/pm (1-12)
 *         <td><a href="#number">Number</a>
 *         <td><code>12</code>
 *     <tr>
 *         <td><code>m</code>
 *         <td>Minute in hour
 *         <td><a href="#number">Number</a>
 *         <td><code>30</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>s</code>
 *         <td>Second in minute
 *         <td><a href="#number">Number</a>
 *         <td><code>55</code>
 *     <tr>
 *         <td><code>S</code>
 *         <td>Millisecond
 *         <td><a href="#number">Number</a>
 *         <td><code>978</code>
 *     <tr bgcolor="#eeeeff">
 *         <td><code>z</code>
 *         <td>Time zone
 *         <td><a href="#timezone">General time zone</a>
 *         <td><code>Pacific Standard Time</code>; <code>PST</code>; <code>GMT-08:00</code>
 *     <tr>
 *         <td><code>Z</code>
 *         <td>Time zone
 *         <td><a href="#rfc822timezone">RFC 822 time zone</a>
 *         <td><code>-0800</code>
 * </table>
 * </blockquote>
 * 
 * <h4>Examples</h4>
 *
 * <blockquote>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Examples of date and time patterns interpreted in the U.S. locale">
 *     <tr style="background-color: rgb(204, 204, 255);">
 *         <th align=left>Date and Time Pattern
 *         <th align=left>Result
 *     <tr>
 *         <td><code>"yyyy.MM.dd G 'at' HH:mm:ss z"</code>
 *         <td><code>2001.07.04 AD at 12:08:56 PDT</code>
 *     <tr style="background-color: rgb(238, 238, 255);">
 *         <td><code>"EEE, MMM d, ''yy"</code>
 *         <td><code>Wed, Jul 4, '01</code>
 *     <tr>
 *         <td><code>"h:mm a"</code>
 *         <td><code>12:08 PM</code>
 *     <tr style="background-color: rgb(238, 238, 255);">
 *         <td><code>"hh 'o''clock' a, zzzz"</code>
 *         <td><code>12 o'clock PM, Pacific Daylight Time</code>
 *     <tr>
 *         <td><code>"K:mm a, z"</code>
 *         <td><code>0:08 PM, PDT</code>
 *     <tr style="background-color: rgb(238, 238, 255);">
 *         <td><code>"yyyyy.MMMMM.dd GGG hh:mm aaa"</code>
 *         <td><code>02001.July.04 AD 12:08 PM</code>
 *     <tr>
 *         <td><code>"EEE, d MMM yyyy HH:mm:ss Z"</code>
 *         <td><code>Wed, 4 Jul 2001 12:08:56 -0700</code>
 *     <tr style="background-color: rgb(238, 238, 255);">
 *         <td><code>"yyMMddHHmmssZ"</code>
 *         <td><code>010704120856-0700</code>
 *     <tr>
 *         <td><code>"yyyy-MM-dd'T'HH:mm:ss.SSSZ"</code>
 *         <td><code>2001-07-04T12:08:56.235-0700</code>
 *     <tr style="background-color: rgb(238, 238, 255);">
 *         <td><code>"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"</code>
 *         <td><code>2001-07-04T12:08:56.235-07:00</code>
 *     <tr>
 *         <td><code>"YYYY-'W'ww-u"</code>
 *         <td><code>2001-W27-3</code>
 * </table>
 * </blockquote>
 *
 * @see SimpleDateFormat
 * @author 源��깭�샇 <th71.kim@samsung.com>
 * @author 源��꽦�삙
 * 
 */
public final class DateUtil {

	private static final Locale LOCALE = Locale.getDefault();

	private DateUtil() {
		throw new AssertionError();
	}

	/**
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @param millis
	 * @return
	 */
	public static String format(String pattern, long millis, TimeZone timezone) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.forTimeZone(timezone));
		return formatter.print(millis);

		// org.apache.commons.lang.time.FastDateFormat df = org.apache.commons.lang.time.FastDateFormat.getInstance(
		// pattern, timezone);
		// return df.format(millis);
	}

	/**
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @param millis
	 * @return
	 */
	public static String format(String pattern, long millis, Locale locale) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern).withLocale(locale);
		return formatter.print(millis);

		// org.apache.commons.lang.time.FastDateFormat df = org.apache.commons.lang.time.FastDateFormat.getInstance(
		// pattern, locale);
		// return df.format(millis);
	}

	/**
	 * formatting with Default Locale
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @param millis
	 * @return
	 */
	public static String formatDefaultLocale(String pattern, long millis) {
		return format(pattern, millis, LOCALE);
	}

	/**
	 * formatting Current DateTime
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @return
	 */
	public static String getCurDateTime(String pattern, TimeZone timeZone) {
		return format(pattern, System.currentTimeMillis(), timeZone);
	}

	/**
	 * formatting Current DateTime
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @return
	 */
	public static String getCurDateTime(String pattern, Locale locale) {
		return format(pattern, System.currentTimeMillis(), locale);
	}

	/**
	 * formatting Current DateTime with Default Locale
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @return
	 */
	public static String getCurDateTimeDefaultLocale(String pattern) {
		return formatDefaultLocale(pattern, System.currentTimeMillis());
	}

	/**
	 * 
	 * @param pattern
	 *            e.g. yyyyMMddHHmmss
	 * @param dateTimeStr
	 *            e.g. 20131209084700
	 * @param seconds
	 *            e.g. 60
	 * @return
	 */
	public static boolean isDateTimeExpired(String pattern, String dateTimeStr, int seconds) {
		String expiredDateTimeStr = formatDefaultLocale(pattern, System.currentTimeMillis() - (seconds * 1000));
		if (dateTimeStr.substring(0, 14).compareTo(expiredDateTimeStr) <= 0) {
			return true; // expired !!
		}
		return false;
	}

	/**
	 * 
	 * @param pattern
	 * @param addDays
	 * @return
	 */
	public static String addDays(String pattern, int addDays) {
		// 1�씪: (millis*1000*60珥�*60遺�*24�떆)
		return formatDefaultLocale(pattern, System.currentTimeMillis() + ((long) addDays * 1000 * 60 * 60 * 24));
	}

	/**
	 * �쁽�옱 湲곗� month�쟾 �궇吏� timestamp濡� return
	 *
	 * @param month
	 * @return
	 */
	public static Timestamp getNowBeforeMonth(int month) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -month);
		return new Timestamp(cal.toInstant().toEpochMilli());
	}

	/**
	 * �엯�젰諛쏆� �씪�옄 �씠�썑 �씪�옄瑜� 諛섑솚�븳�떎.
	 * 
	 * @param instant
	 *        湲곗��씪�옄
	 * @param days
	 *        �뜑�궇 �씪�옄
	 * @return �뜑�빐吏� �씪�옄
	 * @author �넀�꽦�썕
	 */
	public static long addDays(long instant, int days) {
		if (days == 0) {
			return instant;
		}

		DateTime dt = new DateTime(instant);

		DateTime changed = dt.withFieldAdded(DurationFieldType.days(), days);
		return changed.getMillis();
	}

	public static long addMins(long instant, int min) {
		if (min == 0) {
			return instant;
		}

		DateTime dt = new DateTime(instant);

		DateTime changed = dt.withFieldAdded(DurationFieldType.minutes(), min);
		return changed.getMillis();
	}

	/**
	 * 
	 * @param instant
	 * @param seconds
	 * @return
	 */
	public static long addSeconds(long instant, int seconds) {
		if (seconds == 0) {
			return instant;
		}

		DateTime dt = new DateTime(instant);

		DateTime changed = dt.withFieldAdded(DurationFieldType.seconds(), seconds);
		return changed.getMillis();
	}
	
	/**
	 * 
	 * @param instant
	 * @param seconds
	 * @return
	 */
	public static long minusSeconds(long instant, int seconds) {
		
		if (seconds == 0) {
			return instant;
		}

		DateTime dt = new DateTime(instant);
		DateTime changed = dt.minusSeconds(seconds);

		return changed.getMillis();			
		
	}
	
	/*
	 */
	public static long getTime( String timeStr , String format ) {

		SimpleDateFormat f = new SimpleDateFormat(format);
		try {
		    Date d = f.parse(timeStr);
		    long milliseconds = d.getTime();
		    return milliseconds;
		} catch (Throwable e) {
		    e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * format : "yyyy-mm-dd HH:mm:ss.SSS"
	 */
	public static long getTime( String timeStr ) {		
		return getTime( timeStr , "yyyy-MM-dd HH:mm:ss.SSS" );
	}
	
	public static String getTimeString( long time ) {

		Date date = new Date(time);
		
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String timeStr = df2.format(date);
		
		return timeStr;
	}
	
	public static long getToTime( long from , int gap ) {
		long to  = addSeconds( from, gap );
		long cur = System.currentTimeMillis(); 
		to = to < cur ? cur : to ;
		return to ;
	}
	
	public static boolean isDataIssue(long lastTime, TimeZone timeZone) {
		String hour = getCurDateTime("hh", timeZone );
		Integer iHour = Integer.parseInt(hour);
		if( iHour >= 8 && iHour <= 18 
				&& lastTime < System.currentTimeMillis() - 3600000 * 2 ) {
			return true;
		}
		return false;
	}
	
	
}
