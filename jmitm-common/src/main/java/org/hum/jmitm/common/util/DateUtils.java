package org.hum.jmitm.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	public static String formatTime(Date date) {
		return sdf.format(date);
	}
}
