package com.elitetrader.crystalball.database.influxdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elitetrader.crystalball.datasource.yahoo.YahooAPIConnector;
import com.elitetrader.crystalball.datasource.yahoo.YahooAPIModel;
import com.elitetrader.crystalball.datasource.yahoo.YahooSymbolRequest;

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
		newconfig.addProperty("databasename", "crystalball-test");
		config = newconfig;
		
		yhooWriter = new YahooDataWriter(queue, config);
		List<YahooSymbolRequest> symbolList = new ArrayList<YahooSymbolRequest>();
		symbolList.add(new YahooSymbolRequest("aapl", new DateTime(2000,1,1,0,0)));
		symbolList.add(new YahooSymbolRequest("euo", new DateTime(2015,1,1,0,0)));
		symbolList.add(new YahooSymbolRequest("hedj", new DateTime(2015,2,1,0,0)));
		symbolList.add(new YahooSymbolRequest("gs", new DateTime(2015,3,1,0,0)));
		
		// Check if I need to create new database
		if(!yhooWriter.hasThisDatabase(newconfig.getString("databasename"))) 
			yhooWriter.createDB(newconfig.getString("databasename"));
		
		urlConn = new YahooAPIConnector(queue, symbolList);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		Thread dbWriter = new Thread(yhooWriter);
		Thread urlReader = new Thread(urlConn);
		new Thread(new YahooDataWriter(queue, config)).start();
		new Thread(new YahooDataWriter(queue, config)).start();
		urlReader.start();
		dbWriter.start();
		urlReader.join();
		dbWriter.join();
	}

}
