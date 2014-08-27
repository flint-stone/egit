package edu.uiuc.cs.cs425.fd.Membership;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Membership list entry
 * include heartbeat count, state, local timestamp, self flag
 * @author lexu1, wwang84
 *
 */
public class Membership implements Serializable{
	/**
	 * add serial version id
	 */
	private static final long serialVersionUID = -7934551153587115653L;
	
	public static transient final int ACTIVE=0;
	public static transient final int LEAVING=1;
	public static transient final int FAILURE=2;
	
	private int heartbeat_count;
	private int state=0;
	private transient Timestamp stamp;
	
	private boolean isSelf=false;
	
	public Timestamp getStamp() {
		return stamp;
	}


	public void setStamp(Timestamp stamp) {
		this.stamp = stamp;
	}

	
	/**
	 * membership constructor
	 * construct new membership when machine joins
	 * @param heartbeat_count
	 */
	public Membership(int heartbeat_count){
		//this.setId(id);
		this.heartbeat_count=heartbeat_count;
		this.stamp = new Timestamp(new Date().getTime());
	}
	

	public int getHeartbeat_count() {
		return heartbeat_count;
	}

	public void setHeartbeat_count(int heartbeat_count) {
		this.heartbeat_count = heartbeat_count;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isSelf() {
		return isSelf;
	}


	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}
}
