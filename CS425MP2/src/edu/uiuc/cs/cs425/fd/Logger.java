package edu.uiuc.cs.cs425.fd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiuc.cs.cs425.fd.Membership.MembershipID;
/**
 * Logger class records failure detector usage
 * @author wwang84, lexu1
 *
 */
public class Logger {
	static Log log = LogFactory.getLog(FailureDetector.class);

	/**
	 * log when failure detected
	 * @param deadmember
	 * @param message
	 */
	public static void logCrashMember(MembershipID deadmember, String message) {
		String logMsg = "Failure Member detected:"
						+ " IP: " + deadmember.getIp() 
						+ " Port: "	+ deadmember.getPort() 
						+ " Timestamp:"	+ deadmember.getId_time().toString() + message;
		
		System.out.println(logMsg);
		log.info(logMsg);
	}

	/**
	 * log when new member join the group
	 * @param newMember
	 * @param message
	 */
	public static void logNewMember(MembershipID newMember, String message) {
		String logMsg = "New Member detected:"
						+ " IP: " + newMember.getIp() 
						+ " Port: " + newMember.getPort() 
						+ " Timestamp: " + newMember.getId_time().toString() + message;
		System.out.println(logMsg);
		log.info(logMsg);
	}


	/**
	 * log when member voluntarily leave the group
	 * @param leavingMember
	 * @param message
	 */
	public static void logLeavingMember(MembershipID leavingMember, String message) {
		String logMsg = "Leaving Member detected:" 
				+ " IP: " + leavingMember.getIp()
				+ " Port: "	+ leavingMember.getPort() + " Timestamp: "
				+ leavingMember.getId_time().toString() + message;
		System.out.println(logMsg);
		log.info(logMsg);
	}

	/**
	 * trace log
	 * @param message
	 */
	public static void logTraceInfo(String message) {
		log.trace(message);
	}

	/**
	 * info log
	 * @param message
	 */
	public static void logInfo(String message) {
		log.info(message);
	}

	/**
	 * error log
	 * @param message
	 */
	public static void logError(String message) {
		log.error(message);
	}
	
	/**
	 * bandwidth log
	 * @param message
	 */
	public static void logBandwidth(String message){
		log.debug(message);
	}
}
