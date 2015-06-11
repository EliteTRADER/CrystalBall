package com.elitetrader.crystalball.dataprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.elitetrader.crystalball.database.influxdb.YahooDataWriter;
import com.elitetrader.crystalball.datasource.yahoo.YahooAPIConnector;
import com.elitetrader.crystalball.datasource.yahoo.YahooAPIModel;
import com.elitetrader.crystalball.datasource.yahoo.YahooSymbolRequest;

/**
 *  Glue all stuff together
 * 
 */

public class YahooDataProcessor implements Runnable{
	private final static int QUEUESIZE = 32768;
	private final static int TOTALTHREAD = 6;
	private final static int TIMEOUTHOUR = 1;
	
	private final static Logger logger = Logger.getLogger(YahooDataProcessor.class);
	
	private List<String> symbols;
	private Configuration configuration;
	BlockingQueue<YahooAPIModel> queue;
	
	public YahooDataProcessor(List<String> symbolList, Configuration configuration) {
		this.symbols = symbolList;
		this.configuration = configuration;
		queue = new ArrayBlockingQueue<YahooAPIModel>(QUEUESIZE);
	}

	public void run() {
		// One reader multiple writer
		logger.info("Translate to YahooRequest format for symbols: " + symbols.toString());
		List<YahooSymbolRequest> requests = new ArrayList<YahooSymbolRequest>();
		for(String symbol : symbols)
			requests.add(new YahooSymbolRequest(symbol));
		
		YahooAPIConnector urlConn = new YahooAPIConnector(queue, requests);
		logger.info("Created Yahoo API connector");
		
		List<YahooDataWriter> writers = new ArrayList<YahooDataWriter>();
		for(int i=0; i<TOTALTHREAD-1; i++)
			writers.add(new YahooDataWriter(queue, configuration));
		
		logger.info("Created Yahoo Data Writers");
		
		// If database is not there, create one
		if(!writers.get(0).hasThisDatabase(configuration.getString("databasename"))) 
			writers.get(0).createDB(configuration.getString("databasename"));
		
		// Dispatch to executor
		ExecutorService executor = Executors.newFixedThreadPool(TOTALTHREAD);
		executor.submit(urlConn);
		for(YahooDataWriter writer: writers)
			executor.submit(writer);
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(TIMEOUTHOUR, TimeUnit.HOURS);
			logger.info("Processor successfully shutdown.");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			executor.shutdownNow();
		}
	}
}
