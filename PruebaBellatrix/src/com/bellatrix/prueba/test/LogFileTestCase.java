package com.bellatrix.prueba.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
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
		System.out.println("File valid: " + file.canExecute() + ":" + file.canRead() + ":" + file.exists());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		dBuilder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.contains("logger.dtd")) {
					return new InputSource(new StringReader(""));
				} else {
					return null;
				}
			}
		});
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
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
