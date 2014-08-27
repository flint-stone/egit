package edu.uiuc.cs.cs425.fd.Membership;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * Membership ID class
 * Membership identifier composed by IP, join time and port used
 * @author lexu1, wwang84
 *
 */
public class MembershipID implements Serializable{

	/**
	 * add serial version id
	 */
	private static final long serialVersionUID = 8378217395284473568L;
	
	private Timestamp id_time;
	private String ip;
	private int port;
	
	/**
	 * 
	 * @param id_time
	 * @param ip
	 * @param port
	 */
	public MembershipID(Timestamp id_time, String ip, int port){
		this.setId_time(id_time);
		this.setIp(ip);
		this.setPort(port);
	}
	
	public Timestamp getId_time() {
		return id_time;
	}

	public void setId_time(Timestamp id_time) {
		this.id_time = id_time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id_time == null) ? 0 : id_time.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MembershipID other = (MembershipID) obj;
		if (id_time == null) {
			if (other.id_time != null)
				return false;
		} else if (!id_time.equals(other.id_time))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	
}
