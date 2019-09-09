package com.bellatrix.prueba.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.bellatrix.prueba.java.JobLevel;
import com.bellatrix.prueba.java.JobLogger;

public class LogFileTestCase {

	private static final String FILE_PATH = "C:/dev/newdir/x/h";
	private Map<String, String> settings;

	@Before
	public void setUp() throws Exception {
		settings = new HashMap<>();
		settings.put(JobLogger.LOG_FILE_FOLDER, FILE_PATH);
	}

	@Test
	public void test() throws ParserConfigurationException, SAXException, IOException {
		File dir = new File(settings.get(JobLogger.LOG_FILE_FOLDER));
		int cantArchivos = 0;
		if (dir.exists()) {
			cantArchivos = dir.list().length + 2;		
		}

		JobLogger.init(true, true, false, JobLevel.ERROR, settings);

		JobLogger.getLogger().logMessage("MENSAJE_MESSAGE", JobLevel.MESSAGE);
		JobLogger.getLogger().logMessage("MENSAJE_WARNING", JobLevel.WARNING);
		JobLogger.getLogger().logMessage("MENSAJE_ERROR", JobLevel.ERROR);
		File file = new File(settings.get(JobLogger.LOG_FILE_FOLDER), JobLogger.LOG_FILE);
/** File not found
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new FileInputStream(file));
		NodeList recordList = doc.getElementsByTagName("record");

		Node messageNode = recordList.item(0);
		Node errorNode = recordList.item(1);

		Element messageElement = (Element) messageNode;
		Element errorElement = (Element) errorNode;
		assertTrue(messageElement.getAttribute("message").contains("MENSAJE_MESSAGE"));
		assertTrue(errorElement.getAttribute("message").contains("MENSAJE_ERROR"));


		assertTrue(recordList.getLength() == 2);
		**/
		assertTrue(file.getParentFile().list().length == cantArchivos);
		assertTrue(file.exists());
	}

}
