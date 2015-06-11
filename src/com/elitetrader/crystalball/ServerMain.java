package com.elitetrader.crystalball;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.elitetrader.crystalball.dataprocessor.YahooDataProcessor;

public class ServerMain {
	
	private final static Logger logger = Logger.getLogger(ServerMain.class);
	
	public static void main(String[] args) {
		logger.info("Starting Crystal Ball Server.");
		// Need to change it to load from file
		Configuration newconfig = new BaseConfiguration();
		newconfig.addProperty("env", "local");
		newconfig.addProperty("databasename", "crystalball");
		
		// Need to load from file as well
		List<String> yahooSymbols = new ArrayList<String>();
		yahooSymbols.add("ms");
		yahooSymbols.add("jpm");
		yahooSymbols.add("gs");
		try {
			Thread yProcess = new Thread(new YahooDataProcessor(yahooSymbols, newconfig));
			yProcess.start();
			yProcess.join();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
