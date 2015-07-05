package com.elitetrader.crystalball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.elitetrader.crystalball.dataprocessor.YahooDataProcessor;

public class ServerMain {
	
	private final static Logger logger = Logger.getLogger(ServerMain.class);
	
	private final static String CONFIGLOCATION = "/local/config/CrystalBall/main.properties";
	
	public static void main(String[] args) throws ConfigurationException {
		logger.info("Starting Crystal Ball Server.");
		// Need to change it to load from file
		Configuration appConfig = new PropertiesConfiguration(CONFIGLOCATION);
		
		// Need to load from file as well
		@SuppressWarnings("unchecked")
		List<String> yahooSymbols = appConfig.getList("Yahoo.symbol");
		try {
			Thread yProcess = new Thread(new YahooDataProcessor(yahooSymbols, appConfig));
			yProcess.start();
			yProcess.join();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
