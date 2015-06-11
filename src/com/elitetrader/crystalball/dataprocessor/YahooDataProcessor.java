package com.elitetrader.crystalball.dataprocessor;

import java.util.List;

import org.apache.commons.configuration.Configuration;

/**
 *  Glue all stuff together
 * 
 */

public class YahooDataProcessor {
	private List<String> symbols;
	private Configuration configuration;
	
	public YahooDataProcessor(List<String> symbolList, Configuration configuration) {
		this.symbols = symbolList;
		this.configuration = configuration;
	}
	
	public void startProcess() {
		// Go through all symbols and put together the list of YahooSymbolRequest
		for(String symbol : symbols) {
			
		}
	}
}
