package com.elitetrader.crystalball.datasource.yahoo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class YahooAPICSVMapper {

	private final Map<String, Integer> titleToPos;
	
	public YahooAPICSVMapper(String[] firstline) {
		Map<String, Integer> titleMap = new HashMap<String, Integer>();
		for(int i=0; i<firstline.length; i++)
			titleMap.put(firstline[i],i);
		titleToPos = Collections.unmodifiableMap(titleMap);
	}
	
	public YahooAPIModel map(String ticker, String[] rawCsv) {
		String timeStr = rawCsv[titleToPos.get(YahooAPIModel.DATE)];
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime datetime = formatter.parseDateTime(timeStr);
		double open = Double.parseDouble(rawCsv[titleToPos.get(YahooAPIModel.OPEN)]);
		double high = Double.parseDouble(rawCsv[titleToPos.get(YahooAPIModel.HIGH)]);
		double low = Double.parseDouble(rawCsv[titleToPos.get(YahooAPIModel.LOW)]);
		double close = Double.parseDouble(rawCsv[titleToPos.get(YahooAPIModel.CLOSE)]);
		long volume = Long.parseLong(rawCsv[titleToPos.get(YahooAPIModel.VOLUME)]);
		double adjclose = Double.parseDouble(rawCsv[titleToPos.get(YahooAPIModel.ADJCLOSE)]);
		return new YahooAPIModel(ticker, datetime, open, high, low, close, volume, adjclose);
	}
}
