package com.elitetrader.datasource.crystalBall.yahoo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class YahooAPICSVMapper {
	private final String DATE = "Date";
	private final String OPEN = "Open";
	private final String HIGH = "High";
	private final String LOW = "Low";
	private final String CLOSE = "Close";
	private final String VOLUME = "Volume";
	private final String ADJCLOSE = "Adj Close";
	
	
	private final Map<String, Integer> titleToPos;
	
	public YahooAPICSVMapper(String[] firstline) {
		Map<String, Integer> titleMap = new HashMap<String, Integer>();
		for(int i=0; i<firstline.length; i++)
			titleMap.put(firstline[i],i);
		titleToPos = Collections.unmodifiableMap(titleMap);
	}
	
	public YahooAPIModel map(String ticker, String[] rawCsv) {
		String timeStr = rawCsv[titleToPos.get(DATE)];
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime datetime = formatter.parseDateTime(timeStr);
		double open = Double.parseDouble(rawCsv[titleToPos.get(OPEN)]);
		double high = Double.parseDouble(rawCsv[titleToPos.get(HIGH)]);
		double low = Double.parseDouble(rawCsv[titleToPos.get(LOW)]);
		double close = Double.parseDouble(rawCsv[titleToPos.get(CLOSE)]);
		long volume = Long.parseLong(rawCsv[titleToPos.get(VOLUME)]);
		double adjclose = Double.parseDouble(rawCsv[titleToPos.get(ADJCLOSE)]);
		return new YahooAPIModel(ticker, datetime, open, high, low, close, volume, adjclose);
	}
}
