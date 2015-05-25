package com.elitetrader.crystalball.database.influxdb;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Database;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Serie;

public abstract class InfluxDBBase {
	protected final String localDBUrl = "http://localhost:8086";
	protected final String localUser = "root";
	protected final String localPassword = "root";
	
	protected InfluxDB db;
	
	public InfluxDB getNewLocalConnection() {
		return InfluxDBFactory.connect(localDBUrl, localUser, localPassword);
	}
	
	public abstract InfluxDB getNewRemoteConnection();
	
	protected boolean isDBUp() {
		Pong result = db.ping();
		return "ok".equalsIgnoreCase(result.getStatus());
	}
	
	protected boolean hasThisDatabase(String databaseName) {
		List<Database> result = db.describeDatabases();
		for(Database table: result)
			if(databaseName.equals(table.getName()))
				return true;
		return false;
	}
	
	protected void write(String databaseName, String[] columns, Object[] values) {
		Serie serie = new Serie.Builder(databaseName).columns(columns).values(values).build();
		db.write(databaseName, TimeUnit.MILLISECONDS, serie);
	}
	
	protected void createDB(String databaseName) {
		db.createDatabase(databaseName);
	}
}
