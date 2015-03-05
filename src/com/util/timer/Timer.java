package com.util.timer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**用来获取时间
 * @author jumper
 *
 */
public class Timer {
	private static SimpleDateFormat formatBuilder;

	public static String getDate(String format) {
		formatBuilder = new SimpleDateFormat(format);
		return formatBuilder.format(new Date());
	}

	public static String getDate() {
		return getDate("MM-dd  hh:mm:ss");
	}
}
