package com.mitec.business.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Service;

@Service
public class CustomFormatDate {

	private CustomFormatDate() {}
	
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd";
	private static final String INPUT_DATE_TIME_PATTERN = "dd/MM/yyyy";
	
	public static String formatLocalDate(LocalDateTime localDate, String pattern) {
		if (localDate == null) return null;
		@SuppressWarnings("deprecation")
		Date newDate = new Date(localDate.getYear() - 1900, localDate.getMonthValue() - 1, localDate.getDayOfMonth());
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(newDate);
	}
	
	public static LocalDateTime formatStartTime(String startTimeStr) {
		LocalDateTime startTime = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(startTimeStr)) {
			startTime = LocalDate.parse(startTimeStr, formatter).atTime(LocalTime.MIN);
		}
		
		return startTime;
	}
	
	public static LocalDateTime formartEndTime(String endTimeStr) {
		LocalDateTime endTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(endTimeStr)) {
			endTime = LocalDate.parse(endTimeStr, formatter).atTime(LocalTime.MAX);
		}
		
		return endTime;
	}
	
	public static LocalDateTime formatStartTimeForInput(String startTimeStr) {
		LocalDateTime startTime = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INPUT_DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(startTimeStr)) {
			startTime = LocalDate.parse(startTimeStr, formatter).atTime(LocalTime.MIN);
		}
		
		return startTime;
	}
	
	public static LocalDateTime formartEndTimeForInput(String endTimeStr) {
		LocalDateTime endTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(INPUT_DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(endTimeStr)) {
			endTime = LocalDate.parse(endTimeStr, formatter).atTime(LocalTime.MAX);
		}
		
		return endTime;
	}
	
	public static String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {
	    Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
	    SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
	    
	    return formatter.format(initDate);
	}
	
	public static String todayString() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(INPUT_DATE_TIME_PATTERN);
		
		return formatter.format(today);
	}
	
	public static String todayMinusMonths(int inMonth) {
		LocalDate localDate = LocalDate.now().minusMonths(inMonth);
		
		@SuppressWarnings("deprecation")
		Date newDate = new Date(localDate.getYear() - 1900, localDate.getMonthValue() - 1, 1);
		SimpleDateFormat formatter = new SimpleDateFormat(INPUT_DATE_TIME_PATTERN);
		
		return formatter.format(newDate);
	}
	
	public static LocalDateTime parseInputDateTimeLocal(String dateStr) {
		if (StringUtils.isBlank(dateStr)) return null;
		
		dateStr = dateStr.replace("T", " ");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return LocalDateTime.parse(dateStr, formatter);
	}
	
	public static String formatToInputDateTimeLocal(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateStr = formatter.format(date);

        dateStr = dateStr.replace(" ", "T");

        return dateStr;
    }
	
	public static String getTimeInLocalDateTime(LocalDateTime date) {
		if (date == null) return null;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		return dateTimeFormatter.format(date);
	}
}
