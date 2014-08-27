package edu.uiuc.cs.cs425.gossip.Membership;

import java.sql.Timestamp;

import edu.uiuc.cs.cs425.gossip.NodeID;
/**
 * Membership ID class
 * Membership identifier composed by IP, join time and port used
 * @author lexu1, wwang84
 *
 */
public class MembershipID extends NodeID{
	
	private static final long serialVersionUID = -6617561869444704828L;
	private Timestamp id_time;

	
	/**
	 * 
	 * @param id_time
	 * @param ip
	 * @param port
	 */
	public MembershipID(Timestamp id_time, String ip, int port){
		super(ip, port);
		this.setId_time(id_time);
	}
	
	public Timestamp getId_time() {
		return id_time;
	}

	public void setId_time(Timestamp id_time) {
		this.id_time = id_time;
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
