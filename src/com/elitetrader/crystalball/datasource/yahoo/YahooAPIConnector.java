package com.elitetrader.crystalball.datasource.yahoo;

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
	private final BlockingQueue<YahooAPIModel> pipline;
	private final List<YahooSymbolRequest> requestList;
	
	private final static Logger logger = Logger.getLogger(YahooAPIConnector.class);
	
	public YahooAPIConnector(BlockingQueue<YahooAPIModel> pipline, List<YahooSymbolRequest> requestList) {
		this.pipline = pipline;
		this.requestList = requestList;
	}

	public void run() {
		logger.info("start processing symbol list:\n" + requestList.toString());
		try {
			for(YahooSymbolRequest symbolRequest : requestList) {
				logger.info("Processing: " + symbolRequest);
				final String url = symbolRequest.toRequestURL();
				BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
				CSVReader reader = new CSVReader(in);
				try {
					String [] title = reader.readNext();
					YahooAPICSVMapper mapper = new YahooAPICSVMapper(title);
					String [] nextLine;
					while((nextLine = reader.readNext())!=null) {
						pipline.put(mapper.map(symbolRequest.getSymbol(), nextLine));
					}
				} finally {
					reader.close();
					in.close();
					// put in poison pill
					pipline.put(YahooAPIModel.getPoisonPill());
				}
				logger.info("End of Processing, " + symbolRequest);
			}
		} catch(Exception e) {
			logger.error("Exception : " + e.getMessage());
		}		
	}
	
}
