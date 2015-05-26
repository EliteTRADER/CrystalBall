package com.elitetrader.crystalball.datasource.yahoo;

import org.joda.time.DateTime;

/*
 * This Yahoo models
 * */

public class YahooAPIModel {
	public static final int NUMOFCOL = 7;
	
	public static final String POISONPILL = "yahooPoisonPill";
	
	public static final String DATE = "Date";
	public static final String OPEN = "Open";
	public static final String HIGH = "High";
	public static final String LOW = "Low";
	public static final String CLOSE = "Close";
	public static final String VOLUME = "Volume";
	public static final String ADJCLOSE = "Adj Close";
	
	private final String ticker;
	private final DateTime time;
	private final double  open;
	private final double high;
	private final double low;
	private final double close;
	private final long volume;
	private final double adjustedClose;
	
	public YahooAPIModel( String ticker, DateTime time, double open, double high,
						  double low, double close, long volume, double adjustedClose) {
		this.ticker = ticker;
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.adjustedClose = adjustedClose;
	}
	
	public static YahooAPIModel getPoisonPill() {
		return new YahooAPIModel(POISONPILL,null,0,0,0,0,0,0);
	}

	public String getTicker() {
		return ticker;
	}

	public DateTime getTime() {
		return time;
	}

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public long getVolume() {
		return volume;
	}

	public double getAdjustedClose() {
		return adjustedClose;
	}

	@Override
	public String toString() {
		return "YahooAPIModel [ticker=" + ticker + ", time=" + time + ", open="
				+ open + ", high=" + high + ", low=" + low + ", close=" + close
				+ ", volume=" + volume + ", adjustedClose=" + adjustedClose
				+ "]";
	}	
}
