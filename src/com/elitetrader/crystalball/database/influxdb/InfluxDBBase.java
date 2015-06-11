package com.elitetrader.crystalball.database.influxdb;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Database;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Serie;
import org.influxdb.dto.Serie.Builder;

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
	
	public boolean hasThisDatabase(String databaseName) {
		List<Database> result = db.describeDatabases();
		for(Database table: result)
			if(databaseName.equals(table.getName()))
				return true;
		return false;
	}
	
	// Single data point write
	protected void write(String databaseName, String tablename, String[] columns, Object[] values) {
		Serie serie = new Serie.Builder(tablename).columns(columns).values(values).build();
		db.write(databaseName, TimeUnit.MILLISECONDS, serie);
	}
	
	// Write multiple values
	protected void write(String databaseName, String tablename, String[] columns, List<Object[]>values) {
		Builder builder = new Serie.Builder(tablename).columns(columns);
		for(int i=0; i<values.size(); i++) builder.values(values.get(i));
		db.write(databaseName, TimeUnit.MILLISECONDS, builder.build());
	}
	
	// Read SQL
	protected List<Serie> executeQuery(String databaseName, String query) {
		return db.query(databaseName, query, TimeUnit.MILLISECONDS);
	}
	
	public void createDB(String databaseName) {
		db.createDatabase(databaseName);
	}
}
