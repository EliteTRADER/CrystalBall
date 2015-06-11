package com.elitetrader.crystalball.database.influxdb;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InfluxDBBasicTest {

	private InfluxDB influxdb = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		final String dbName = "TestDB";
	
		this.influxdb.createDatabase(dbName);
		
		Serie serie = new Serie.Builder("customTime").columns("value1", "value2", "time")
						.values(System.currentTimeMillis(), 5, System.currentTimeMillis()).build();
		this.influxdb.write(dbName, TimeUnit.MILLISECONDS, serie);
		
		List<Serie> data = influxdb.query(dbName, "select * from customTime", TimeUnit.MILLISECONDS);
		assertEquals(data.isEmpty(), false);
		
		this.influxdb.deleteDatabase(dbName);
		
	}

}
