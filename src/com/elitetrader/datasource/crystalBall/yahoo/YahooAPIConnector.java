package com.elitetrader.datasource.crystalBall.yahoo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.opencsv.CSVReader;

/**
 * @author alexander
 * Yahoo API for downloading tick info
 * 
 */
public class YahooAPIConnector implements Runnable{
	private final String baseUrl = "http://ichart.finance.yahoo.com/table.csv?s=";
	private final BlockingQueue<YahooAPIModel> pipline;
	private final List<String> symbolList;
	
	private final static Logger logger = Logger.getLogger(YahooAPIConnector.class);
	
	public YahooAPIConnector(BlockingQueue<YahooAPIModel> pipline, List<String> symbolList) {
		this.pipline = pipline;
		this.symbolList = symbolList;
	}

	public void run() {
		logger.info("start processing symbol list:\n" + symbolList.toString());
		try {
			for(String symbol : symbolList) {
				logger.info("Processing: " + symbol);
				final String url = baseUrl + symbol;
				BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
				CSVReader reader = new CSVReader(in);
				try {
					String [] title = reader.readNext();
					YahooAPICSVMapper mapper = new YahooAPICSVMapper(title);
					String [] nextLine;
					while((nextLine = reader.readNext())!=null) {
						pipline.put(mapper.map(symbol, nextLine));
					}
				} finally {
					reader.close();
					in.close();
				}
				logger.info("End of Processing, " + symbol);
			}
		} catch(Exception e) {
			logger.error("Exception : " + e.getMessage());
		}		
	}
	
}
