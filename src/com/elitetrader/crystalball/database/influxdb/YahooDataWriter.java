package com.elitetrader.crystalball.database.influxdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.joda.time.DateTime;

import com.elitetrader.crystalball.datasource.yahoo.YahooAPIModel;

public class YahooDataWriter extends InfluxDBBase implements Runnable {
	private final BlockingQueue<YahooAPIModel> pipline;
	private final Configuration config;
	
	private final static Logger logger = Logger.getLogger(YahooDataWriter.class);
	
	public YahooDataWriter(BlockingQueue<YahooAPIModel> queue, Configuration configuration) {
		this.pipline = queue;
		this.config = configuration;
		if(config.getString("env").equals("local")) this.db = getNewLocalConnection();
		else this.db = getNewRemoteConnection();
	}

	@Override
	public InfluxDB getNewRemoteConnection() {
		// This need real centralized database
		return null;
	}
	
	private String serialToDBFormat(List<String> title, List<Object> value, YahooAPIModel model) {
		// translate time to epoch time
		DateTime datetime = model.getTime();
		long epochTime = datetime.getMillis();
		
		title.add("time"); 					value.add(epochTime);
		title.add(YahooAPIModel.OPEN);  	value.add(model.getOpen());
		title.add(YahooAPIModel.CLOSE); 	value.add(model.getClose());
		title.add(YahooAPIModel.HIGH); 		value.add(model.getHigh());
		title.add(YahooAPIModel.LOW);		value.add(model.getLow());
		title.add(YahooAPIModel.VOLUME);	value.add(model.getVolume());
		title.add(YahooAPIModel.ADJCLOSE);	value.add(model.getAdjustedClose());
		
		return model.getTicker();
	}
	
	public void run() {
		for(;;) {
			try {
				YahooAPIModel model = pipline.take();
				// translate into column and values
				List<String> title = new ArrayList<String>();
				List<Object> value = new ArrayList<Object>();
				String ticker = serialToDBFormat(title, value, model);
				this.write(ticker, title.toArray(new String[title.size()]), value.toArray());
				
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
