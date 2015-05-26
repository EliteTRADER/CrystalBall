package com.elitetrader.crystalball.database.influxdb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.joda.time.DateTime;

import com.elitetrader.crystalball.datasource.yahoo.YahooAPIModel;

public class YahooDataWriter extends InfluxDBBase implements Runnable {
	private final BlockingQueue<YahooAPIModel> pipline;
	private final Configuration config;
	
	private final String databaseName;

	private final static Logger logger = Logger.getLogger(YahooDataWriter.class);
	
	// This is the same across all Yahoo API
	private final String[] TITLE = {"time", YahooAPIModel.OPEN,YahooAPIModel.CLOSE,YahooAPIModel.HIGH,
									YahooAPIModel.LOW, YahooAPIModel.VOLUME, YahooAPIModel.ADJCLOSE};
	
	private boolean isTheEnd = false;
	
	public YahooDataWriter(BlockingQueue<YahooAPIModel> queue, Configuration configuration) {
		this.pipline = queue;
		this.config = configuration;
		this.databaseName = config.getString("databasename", "crystalball");
		
		if(config.getString("env").equals("local")) this.db = getNewLocalConnection();
		else this.db = getNewRemoteConnection();
	}

	@Override
	public InfluxDB getNewRemoteConnection() {
		// This need real centralized database
		return null;
	}
	
	private String serialToDBFormat(List<Object[]> value, List<YahooAPIModel> models) {
		// translate time to epoch time
		for(int i=0; i<models.size(); i++) {
			YahooAPIModel model = models.get(i);
			if(model.getTicker().equals(YahooAPIModel.POISONPILL)) {
				isTheEnd = true;
				continue;
			}
			DateTime datetime = model.getTime();
			long epochTime = datetime.getMillis();
			Object[] elem = new Object[TITLE.length];
			elem[0] = epochTime; 		elem[1] = model.getOpen();
			elem[2] = model.getClose(); elem[3] = model.getHigh();
			elem[4] = model.getLow(); 	elem[5] = model.getVolume();
			elem[6] = model.getAdjustedClose();
			value.add(elem);
		}
		return models.get(0).getTicker();
	}
	
	public void run() {
		try {
			while (!isTheEnd) {
				List<YahooAPIModel> models = new ArrayList<YahooAPIModel>();
				YahooAPIModel model = pipline.take();
				// This would prevent the first item is poison pill
				if (model.getTicker().equals(YahooAPIModel.POISONPILL))
					break;
				else
					models.add(model);
				pipline.drainTo(models);
				// translate into column and values
				List<Object[]> value = new ArrayList<Object[]>();
				String ticker = serialToDBFormat(value, models);
				this.write(databaseName, ticker, TITLE, value);
			}
			// Put back poison pill to pipline in order to shut down other writer
			pipline.put(YahooAPIModel.getPoisonPill());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		logger.info("Completed writing Yahoo data to database.");
	}
}
