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
	private static final long serialVersionUID = 4475838070395618696L;
	protected String ip;
	protected int port;
	private NodeID predecessor;
	private NodeID successor;
	
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
	

	public NodeID getPredecessor() {
		return predecessor;
	}
	public void setPredecessor(NodeID predecessor) {
		this.predecessor = predecessor;
	}
	public NodeID getSuccessor() {
		return successor;
	}
	public void setSuccessor(NodeID successor) {
		this.successor = successor;
	}
	public boolean isMyPredecessor(NodeID predecessor) {
		//System.out.println(this.toString()+" "+this.predecessor.getIp()+"@"+this.predecessor.getPort());
		return this.predecessor.equals(predecessor);
	}


	public boolean isMySuccessor(NodeID successor) {
		//System.out.println(this.toString()+" "+this.successor.getIp()+"@"+this.successor.getPort());
		return this.successor.equals(successor);
	}
	public boolean isMyNeighbor( NodeID node) {
		if(this.isMyPredecessor(node) || this.isMySuccessor(node)){
			return true;
		}
		else {
			return false;
		}
	}


}
