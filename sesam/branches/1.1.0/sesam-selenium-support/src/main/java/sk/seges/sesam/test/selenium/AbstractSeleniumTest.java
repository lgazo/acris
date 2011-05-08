package sk.seges.sesam.test.selenium;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;

import sk.seges.sesam.core.test.selenium.configuration.DefaultSeleniumConfigurator;
import sk.seges.sesam.core.test.selenium.configuration.api.MailSettings;
import sk.seges.sesam.core.test.selenium.configuration.api.ReportingSettings;
import sk.seges.sesam.core.test.selenium.configuration.api.SeleniumConfigurator;
import sk.seges.sesam.core.test.selenium.configuration.api.TestEnvironment;
import sk.seges.sesam.core.test.selenium.configuration.api.properties.ConfigurationValue;
import sk.seges.sesam.test.selenium.support.DefaultSeleniumMailSupport;
import sk.seges.sesam.test.selenium.support.SeleniumMailSupport;
import bromine.brunit.BRUnit;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.unitedinternet.portal.selenium.utils.logging.HtmlResultFormatter;
import com.unitedinternet.portal.selenium.utils.logging.LoggingCommandProcessor;
import com.unitedinternet.portal.selenium.utils.logging.LoggingDefaultSelenium;
import com.unitedinternet.portal.selenium.utils.logging.LoggingResultsFormatter;
import com.unitedinternet.portal.selenium.utils.logging.LoggingUtils;

public abstract class AbstractSeleniumTest extends BRUnit implements SeleniumConfigurator, SeleniumMailSupport {

	private static final String RESULT_FILE_ENCODING = "UTF-8";

	protected TestEnvironment testEnvironment;
	protected MailSettings mailEnvironment;
	protected ReportingSettings reportingSettings;

	private SeleniumConfigurator seleniumConfigurator;
	private SeleniumMailSupport seleniumMailSupport;

	private static final char[] symbols = new char[36];

	private static final String RESULT_PATH_PREFIX = "target" + File.separator;

	private LoggingCommandProcessor loggingProcessor;
	private BufferedWriter loggingWriter;

	protected AbstractSeleniumTest() {
		seleniumConfigurator = getSeleniumConfigurator();
	}

	protected String getResultDirectory() {
		String result = RESULT_PATH_PREFIX
				+ (reportingSettings.getResultDirectory() == null ? "report" : reportingSettings.getResultDirectory());

		result = new File(result).getAbsolutePath();
		result += File.separator;

		return result;
	}

	protected String getScreenshotDirectory() {

		String screenshotsDirectory = (reportingSettings.getScreenshotsDirectory() == null ? "screenshot"
				: reportingSettings.getScreenshotsDirectory());

		return getResultDirectory() + screenshotsDirectory;
	}

	@Override
	public void start(String host, int port, String browser, String sitetotest, String test_id) {
		if (reportingSettings != null && reportingSettings.isProduceScreenshots() == true) {

			if (!new File(getScreenshotDirectory()).exists()) {
				new File(getScreenshotDirectory()).mkdirs();
			}

			String resultHtmlFileName = null;

			resultHtmlFileName = getResultDirectory() + "result" + LoggingUtils.timeStampForFileName() + ".html";

			loggingWriter = LoggingUtils.createWriter(resultHtmlFileName, RESULT_FILE_ENCODING, true);
			LoggingResultsFormatter htmlFormatter = new HtmlResultFormatter(loggingWriter, RESULT_FILE_ENCODING) {
				
			};

			htmlFormatter.setScreenShotBaseUri("file:///" + getScreenshotDirectory());
			htmlFormatter.setAutomaticScreenshotPath("file:///" + getScreenshotDirectory() + File.separator);

			loggingProcessor = new LoggingCommandProcessor(new HttpCommandProcessor(host, port, browser, sitetotest),
					htmlFormatter);
			loggingProcessor.setExcludedCommands(new String[] {});
		}

		super.start(host, port, browser, sitetotest, test_id);
	}

	@Override
	protected DefaultSelenium createSelenium(String host, int port, String browser, String sitetotest) {
		if (loggingProcessor != null) {
			return new LoggingDefaultSelenium(loggingProcessor);
		}
		return super.createSelenium(host, port, browser, sitetotest);
	}

	@Override
	public void setUp(String bromineHost, int brominePort) {
		super.setUp(bromineHost, brominePort);
		seleniumMailSupport = getMailSupport();
	}

	public SeleniumMailSupport getMailSupport() {
		return new DefaultSeleniumMailSupport(mailEnvironment);
	}

	public SeleniumConfigurator getSeleniumConfigurator() {
		return new DefaultSeleniumConfigurator();
	}

	private final Random random = new Random();

	public void setTestEnvironment(TestEnvironment testEnvironment) {
		this.testEnvironment = testEnvironment;
	}

	public void setMailEnvironment(MailSettings mailEnvironment) {
		this.mailEnvironment = mailEnvironment;
	}

	public void setReportingSettings(ReportingSettings reportingSettings) {
		this.reportingSettings = reportingSettings;
	}

	@Override
	public ConfigurationValue[] collectSystemProperties() {
		return seleniumConfigurator.collectSystemProperties();
	}

	@Override
	public TestEnvironment mergeTestConfiguration(TestEnvironment environment) {
		return seleniumConfigurator.mergeTestConfiguration(environment);
	}

	@Override
	public MailSettings mergeMailConfiguration(MailSettings mailEnvironment) {
		return seleniumConfigurator.mergeMailConfiguration(mailEnvironment);
	}

	public void runTests() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp(testEnvironment.getBromineEnvironment().getBromineHost(), testEnvironment.getBromineEnvironment()
				.getBrominePort());
		start(testEnvironment.getSeleniumEnvironment().getSeleniumHost(), testEnvironment.getSeleniumEnvironment()
				.getSeleniumPort(), testEnvironment.getBrowser(), testEnvironment.getHost(), getClass().getSimpleName());
	}

	@After
	public void tearDown() throws Exception {
		if (loggingProcessor != null) {
//			selenium.captureScreenshot(getScreenshotDirectory() + File.separator + "result_" + LoggingUtils.timeStampForFileName() + ".png");
			selenium.captureEntirePageScreenshot(getScreenshotDirectory() + File.separator + "result_" + LoggingUtils.timeStampForFileName() + ".png", "background=#FFFFFF");
		}

		super.tearDown();

		try {
			if (null != loggingWriter) {
				loggingWriter.close();
			}
		} catch (IOException e) {
			// do nothing
		}
	}

	@Override
	public void verifyTrue(Boolean statement1) throws Exception {
		if (!statement1) {
			throw new RuntimeException("Test failed");
		}
	}

	static {
		for (int idx = 0; idx < 10; ++idx)
			symbols[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < 36; ++idx)
			symbols[idx] = (char) ('a' + idx - 10);
	}

	protected String getRandomString(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("length < 1: " + length);
		}

		char[] buf = new char[length];
		for (int idx = 0; idx < buf.length; ++idx)
			buf[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buf);
	}

	protected void waitAndClick(String xpath) throws Exception {
		waitForElementPresent(xpath);
		selenium.click(xpath);
	}

	protected void waitForTextPresent(String xpath) throws Exception {
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium.isTextPresent(xpath))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}
	}

	protected void waitForElementPresent(String xpath) throws Exception {
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium.isElementPresent(xpath))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}
	}

	public void waitForMailNotPresent(String subject) throws InterruptedException {
		seleniumMailSupport.waitForMailNotPresent(subject);
	}

	public String waitForMailPresent(String subject) throws InterruptedException {
		return seleniumMailSupport.waitForMailPresent(subject);
	}
}