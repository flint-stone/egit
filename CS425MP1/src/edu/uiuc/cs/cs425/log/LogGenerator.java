package edu.uiuc.cs.cs425.log;

import java.io.File;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class generate logs for test.
 * 
 * @version 1.2
 */

public class LogGenerator {

	// commons-logging-lib
	static Log log = LogFactory.getLog(LogGenerator.class);

	// Fixed lines
	static String fixCentent0 = "Hello! This is a test line. You can ingore it.";

	Random ranInt = new Random();

	// Random lines
	String[][] candidate = { { "The ", "This ", "The " },
			{ "process ", "thread ", "device " }, { "is ", "is ", "is " },
			{ "dead. ", "wrong. ", "shutdown. " },
			{ "Please ", "Please ", "Please " },
			{ "repair. ", "contact administrator. ", "kill youself. " } };

	/**
	 * This method generate a log file with some random lines and some fixed
	 * lines.
	 * 
	 * @param maxFileSize
	 *            : The size of log file need to be generated
	 * @param filePath
	 *            : The file path of the log file.
	 * @return void.
	 */
	public void log(double maxFileSize, String filePath) {

		int k = 0;
		File file = new File(filePath);
		double fileSize = Math.floor((file.length() / 1048576) * 100) / 100;

		// generate a file with limited file size
		;
		while (fileSize < maxFileSize) {
			log.trace(randomLogLine());
			log.debug(randomLogLine());
			log.info(randomLogLine());
			log.warn(randomLogLine());
			log.error(randomLogLine());
			log.fatal(randomLogLine());

			k++;

			fileSize = Math.floor((file.length() / 1048576) * 100) / 100;
			double finishPercent = file.length() / 1048576 / maxFileSize * 100;

			// Showing log generating progress
			if (k % 1000 == 0)
				System.out.println("Generating files....." + finishPercent
						+ "% finished...");

		}
		System.out.println("Generating files.....100.0% finished...");
	}

	/**
	 * This method randomly return a fixed line or a random line.
	 * 
	 * @param void
	 * @return a log info sentence.
	 */
	private String randomLogLine() {
		String randomSen = "";
		int ran = ranInt.nextInt(Integer.MAX_VALUE);

		// randomly log a fixed line or a random line

		if (ran % 100 == 0) {
			randomSen = fixCentent0;
			return randomSen;
		} else {
			return randomSen();
		}
	}

	/**
	 * This method constructs a random sentence from some candidate words.
	 * 
	 * @param void
	 * @return a log info sentence.
	 */
	private String randomSen() {
		String randomSen = "";
		for (int i = 0; i < 6; i++) {
			randomSen += candidate[i][(int) (Math.random() * 3)];
		}
		return randomSen;
	}

	/**
	 * This main method is the entry of the log generating program.
	 * 
	 * @param void
	 * @return a log info sentence.
	 */
	public static void main(String[] args) {

		LogGenerator test = new LogGenerator();
		String filepath = "logs/TRACE.log";
		test.log(100.0, filepath);

	}
}
