package com.bellatrix.prueba.test;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.bellatrix.prueba.java.JobLevel;
import com.bellatrix.prueba.java.JobLogger;

public class LogDBTestCase {
	private HashMap<String, String> settings;

	private static final String FILE_PATH = "C:/dev/newdir/x/h";


	@Before
	public void setUp() throws Exception {
		settings = new HashMap<>();
		settings.put(JobLogger.DBMS, "mariadb");
		settings.put(JobLogger.PASSWORD, "");
		settings.put(JobLogger.PORT_NUMBER, "3306");
		settings.put(JobLogger.SERVER_NAME, "localhost");
		settings.put(JobLogger.USER_NAME, "root");
		settings.put(JobLogger.SCHEMA, "test");
	}

	@Test
	public void test() throws SQLException {
		JobLogger.init(false, false, true, JobLevel.ERROR, settings);
		Connection connection = DriverManager.getConnection(
				String.format("jdbc:%s://%s:%s/%s", settings.get(JobLogger.DBMS), settings.get(JobLogger.SERVER_NAME),
						settings.get(JobLogger.PORT_NUMBER), settings.get(JobLogger.SCHEMA)),
				settings.get(JobLogger.USER_NAME), settings.get(JobLogger.PASSWORD));

		int[] cantidades = new int[3];
		int[] cantidadesNuevas = new int[3];
		String sql = "SELECT COUNT(LEVEL) c, LEVEL l FROM log_values GROUP BY LEVEL";
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			cantidades[rs.getInt("l") - 1] = rs.getInt("c");

		}
		rs.close();
		statement.close();

		JobLogger.getLogger().logMessage("MENSAJE_MESSAGE", JobLevel.MESSAGE);
		JobLogger.getLogger().logMessage("MENSAJE_WARNING", JobLevel.WARNING);
		JobLogger.getLogger().logMessage("MENSAJE_ERROR", JobLevel.ERROR);
		statement = connection.createStatement();
		rs = statement.executeQuery(sql);
		while (rs.next()) {
			cantidadesNuevas[rs.getInt("l") - 1] = rs.getInt("c");

		}
		rs.close();
		statement.close();

		for (int i = 0; i < cantidadesNuevas.length; i++) {
			if (i <= 1) {
				assertTrue(cantidadesNuevas[i] - cantidades[i] == 1);
			} else {
				assertTrue(cantidadesNuevas[i] - cantidades[i] == 0);
			}
		}

	}

}
