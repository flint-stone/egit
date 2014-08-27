package edu.uiuc.cs.cs425.gossip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipID;
/**
 * Logger class records failure detector usage
 * @author wwang84, lexu1
 *
 */
public class Logger {
	static Log gossipLog = LogFactory.getLog("Gossip");
	static Log kvLog = LogFactory.getLog("KV");
	static Log commandLog = LogFactory.getLog("Command");
	

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
		
		logGossipInfo(logMsg);
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
		logGossipInfo(logMsg);
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
		logGossipInfo(logMsg);
	}

	/**
	 * trace log for gossipe
	 * @param message
	 */
	public static void logGossipComm(String message) {
		gossipLog.trace(message);
	}

	/**
	 * info log for gossip
	 * @param message
	 */
	public static void logGossipInfo(String message) {
		gossipLog.info(message);
	}
	/**
	 * debug log for kv operations
	 * @param message
	 */
	public static void logKvDebug(String message){
		kvLog.debug(message);
	}
	/**
	 * trace log for kv communication
	 * @param message
	 */
	public static void logKvComm(String message) {
		kvLog.trace(message);
	}
	/**
	 * info log for kv operations
	 * @param message
	 */
	public static void logKvInfo(String message) {
		kvLog.info(message);
	}
	/**
	 * error log for kv communication
	 * @param message
	 */
	public static void logKvCommError(String message) {
		kvLog.error(message);
		
	}
	/**
	 * trace log for commandClient communication
	 * @param message
	 */
	public static void logCommandComm(String msg){
		commandLog.trace(msg);
	}
	/**
	 * info log for commandClient operation
	 * @param message
	 */
	public static void logCommandInfo(String msg){
		commandLog.info(msg);
	}
	/**
	 * error log for commandClient operation
	 * @param message
	 */
	public static void logCommandError(String msg){
		commandLog.error(msg);
	}
}
