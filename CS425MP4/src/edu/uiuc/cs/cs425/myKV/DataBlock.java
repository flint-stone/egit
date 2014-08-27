package edu.uiuc.cs.cs425.myKV;

import java.io.Serializable;
import java.util.Map;

import edu.uiuc.cs.cs425.gossip.NodeID;
/**
 * the wrap class to use transfer replica among nodes
 * @author lexu1, wwang1
 */
public class DataBlock implements Serializable{
	private static final long serialVersionUID = 1L;

	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public transient static int MIGRATION_ADD=0;
	public transient static int MIGRATION_LEAVE=1;
	public transient static int REPLICATION=2;
	
	private Map map;
	private NodeID self;
	
	public NodeID getSelf() {
		return self;
	}

	public void setSelf(NodeID self) {
		this.self = self;
	}

	public DataBlock(Map map){
		this.map=map;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
}
