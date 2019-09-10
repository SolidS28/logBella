package com.bellatrix.prueba.java;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	public static final String LOG_FILE = "logFile.txt";

	public static final String LOG_FILE_FOLDER = "logFileFolder";

	public static final String PASSWORD = "password";

	public static final String USER_NAME = "userName";

	public static final String PORT_NUMBER = "portNumber";

	public static final String SERVER_NAME = "serverName";

	public static final String DBMS = "dbms";

	public static final String SCHEMA = "schema";

	private static JobLogger instance;

	private static boolean logToFile;

	private static boolean logToConsole;

	private static boolean logToDatabase;

	private static JobLevel minLogLevel;

	private static Connection connection;

	private static Map<String, String> settings;

	private static Logger logger;

	private JobLogger() {
		// Singleton constructor
	}

	/**
	 * Log a message into the logging system used
	 * 
	 * @param messageText - Message to log
	 * @param logLevel    - Log level to use
	 */
	public void logMessage(String messageText, JobLevel logLevel) {
		if (messageText != null && !messageText.trim().isEmpty()
				&& logLevel.getPriority() <= minLogLevel.getPriority()) {

			String date = DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
			String loggedMessage = String.format("%s - %s - %s", logLevel.getText(), date, messageText.trim());

			if (logToFile || logToConsole) {
				logger.log(Level.INFO, loggedMessage);
			}

			if (logToDatabase) {
				logIntoDatabase(loggedMessage, logLevel);
			}
		}

	}

	/**
	 * Logs into the table 'Log_Values' the messages with the logging level
	 * 
	 * @param loggedMessage
	 * @param logLevel
	 */
	private void logIntoDatabase(String loggedMessage, JobLevel logLevel) {
		// Separar responsabilidades
		setupConnection();

		try (Statement stmt = connection.createStatement();) {
			// Separar para imprimir?, se crean aún antes de saber si va a haber conexión
			stmt.executeUpdate(String.format("INSERT INTO Log_Values(message, level) VALUES ('%s',  %d)", loggedMessage,
					logLevel.getPriority()));
		} catch (SQLException e) {
			throw new IllegalStateException(
					"Could not insert the info in the database, please check if the database has the correct table settings for logging",
					e);
		}

	}

	/**
	 * Initilize the instance of JobLogger with the settings specified At least one
	 * logging method should be used
	 * 
	 * @param logToFileParam     - if true the class will log the messages in a file
	 * @param logToConsoleParam  - if true the class will log the messages in the
	 *                           console
	 * @param logToDatabaseParam - if true the class will log the messages in
	 *                           database
	 * @param minLogLevelParam   - minimum logging level to use
	 * @param settingParamsMap   - settings to use the class
	 */
	public static synchronized void init(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			JobLevel minLogLevelParam, Map<String, String> settingParamsMap) {
		if (instance != null) {
			throw new IllegalStateException("One instance of JobLogger is already initialized");
		} else if (!logToConsoleParam && !logToFileParam && !logToDatabaseParam) {
			throw new IllegalArgumentException("At least one logging output should be used");
		} else if (minLogLevelParam == null) {
			throw new IllegalArgumentException("A minimum logging level should be specified");
		}

		Handler[] handlers;

		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		settings = settingParamsMap;
		minLogLevel = minLogLevelParam;
		settingParamsMap.get(DBMS);
		settingParamsMap.get(SERVER_NAME);
		instance = new JobLogger();
		logger = Logger.getLogger("MyLog");
		logger.setUseParentHandlers(false);
		handlers = logger.getHandlers();

		for (Handler handler : handlers) {
			if (handler.getClass() == ConsoleHandler.class && handler.getLevel().equals(Level.INFO)) {
				logger.removeHandler(handler);
			}
		}

		if (logToConsoleParam) {
			logger.addHandler(new ConsoleHandler());
		}

		if (logToFileParam) {
			FileHandler fh;
			File logFile = createFile();
			try {
				fh = new FileHandler(logFile.getAbsolutePath());
				logger.addHandler(fh);
			} catch (SecurityException | IOException e) {
				throw new IllegalStateException("Cannot use the file as file handler", e);
			}
		}
		if (logToDatabaseParam) {

			try {
				DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
			} catch (SQLException e) {
				throw new IllegalStateException("Cannot register DB Driver");
			}
		}

	}

	/**
	 * Creates a new file if it is necessary
	 * 
	 * @return file created
	 */
	private static File createFile() {
		File dir = new File(settings.get(LOG_FILE_FOLDER));
		File logFile;
		dir.mkdirs();
		logFile = new File(dir, LOG_FILE);

		if (logFile.exists()) {
			logFile.renameTo(new File(logFile.getParent(), String.format("BackupLog %d.txt", new Date().getTime())));
			logFile = new File(dir, LOG_FILE);
		}
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create log file", e);
		}

		return logFile;
	}

	/**
	 * If it's necessary starts a new connection with the database
	 */
	private static void setupConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(
						String.format("jdbc:%s://%s:%s/%s", settings.get(DBMS), settings.get(SERVER_NAME),
								settings.get(PORT_NUMBER), settings.get(SCHEMA)),
						settings.get(USER_NAME), settings.get(PASSWORD));
			}
		} catch (SQLException e) {
			throw new IllegalStateException("It could not connect to the Database", e);
		}
	}

	/**
	 * Indicates if the instance is initialized
	 * 
	 * @return true if the instance is initialized
	 */
	public static boolean isInitialized() {
		return instance != null;
	}

	/**
	 * Returns the instance of JobLogger
	 * 
	 * @return if exists the instance of JobLogger
	 */
	public static JobLogger getLogger() {
		if (instance == null) {
			throw new IllegalStateException("There is no instance of JobLogger initialized");
		}
		return instance;
	}

}
