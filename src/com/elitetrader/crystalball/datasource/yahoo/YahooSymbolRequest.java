package com.elitetrader.crystalball.datasource.yahoo;

import org.joda.time.DateTime;

/*
 *  Docs: https://code.google.com/p/yahoo-finance-managed/wiki/csvHistQuotesDownload
 * */

final public class YahooSymbolRequest {
	private static String baseURL = "http://ichart.finance.yahoo.com/table.csv?s=";
	private String symbol;
	private DateTime fromDate;
	
	public String toRequestURL() {
		if(fromDate==null) return baseURL + symbol;
		else return baseURL + symbol + 
				"&a=" + (fromDate.getMonthOfYear()-1) + 
				"&b=" + (fromDate.getDayOfMonth()) +
				"&c=" + (fromDate.getYear());
	}
	public YahooSymbolRequest(String symbol) {
		this(symbol, null);
	}
	
	public YahooSymbolRequest(String symbol, DateTime fromDate) {
		this.symbol = symbol;
		this.fromDate = fromDate;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return symbol + (fromDate != null ? " From: " + fromDate.toString() : "");
	}
}
