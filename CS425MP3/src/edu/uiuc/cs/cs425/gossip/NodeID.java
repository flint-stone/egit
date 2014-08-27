package edu.uiuc.cs.cs425.gossip;

import java.io.Serializable;

/**
 * The identifier for each node including ip and port
 * @author wwang84 lexu1
 *
 */
public class NodeID implements Serializable{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		NodeID other = (NodeID) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4475838070395618696L;
	protected String ip;
	protected int port;
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

	public NodeID(String ip, int port){
		this.ip=ip;
		this.port=port;
	}
}
