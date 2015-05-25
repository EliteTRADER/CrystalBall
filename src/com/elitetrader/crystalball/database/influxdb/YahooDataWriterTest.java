package com.elitetrader.crystalball.database.influxdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elitetrader.crystalball.datasource.yahoo.YahooAPIConnector;
import com.elitetrader.crystalball.datasource.yahoo.YahooAPIModel;

public class YahooDataWriterTest {
	
	private Configuration config;
	
	private final static int QUEUESIZE = 16384;
	BlockingQueue<YahooAPIModel> queue;
	
	private YahooDataWriter yhooWriter;
	private YahooAPIConnector urlConn;

	@Before
	public void setUp() throws Exception {
		queue = new ArrayBlockingQueue<YahooAPIModel>(QUEUESIZE);
		Configuration newconfig = new BaseConfiguration();
		newconfig.addProperty("env", "local");
		config = newconfig;
		
		yhooWriter = new YahooDataWriter(queue, config);
		List<String> symbolList = new ArrayList<String>();
		symbolList.add("aapl");
		
		for(String symbol : symbolList)
			if(!yhooWriter.hasThisDatabase(symbol)) yhooWriter.createDB(symbol);
		
		urlConn = new YahooAPIConnector(queue, symbolList);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		Thread dbWriter = new Thread(yhooWriter);
		Thread urlReader = new Thread(urlConn);
		urlReader.start();
		dbWriter.start();
		urlReader.join();
	}

}
