package com.elitetrader.datasource.crystalBall.yahoo;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Before;
import org.junit.Test;

public class YahooAPIConnectorTest {
	
	private YahooAPIConnector conn;
	private final static int QUEUESIZE = 4096;
	BlockingQueue<YahooAPIModel> queue = new ArrayBlockingQueue<YahooAPIModel>(QUEUESIZE);
	
	@Before
	public void setUp() {
		queue.clear();
		List<String> symbolList = new ArrayList<String>();
		symbolList.add("aapl");
		conn = new YahooAPIConnector(queue, symbolList);
	}

	@Test
	public void test() throws InterruptedException {
		Thread runner = new Thread(conn);
		runner.start();
		while(queue.isEmpty());
		runner.interrupt();
		runner.join();
		assertNotNull(queue.peek());
	}

}
