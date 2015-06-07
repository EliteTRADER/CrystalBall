package com.elitetrader.crystalball.datasource.yahoo;

import org.joda.time.DateTime;

/*
 *  Docs: https://code.google.com/p/yahoo-finance-managed/wiki/csvHistQuotesDownload
 * */

final class YahooSymbolRequest {
	private static String baseURL = "http://ichart.finance.yahoo.com/table.csv?s=";
	private String symbol;
	private DateTime fromDate;
	
	public String toRequestURL() {
		return baseURL + symbol + 
				"&a=" + (fromDate.getMonthOfYear()-1) + 
				"&b=" + (fromDate.getDayOfMonth()) +
				"&c=" + (fromDate.getYear());
	}
	
	public YahooSymbolRequest(String symbol, DateTime fromDate) {
		this.symbol = symbol;
		this.fromDate = fromDate;
	}
}
