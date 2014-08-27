package edu.uiuc.cs.cs425.myKV;

import java.util.List;

import edu.uiuc.cs.cs425.gossip.NodeID;
/**
 * Locator abstract class
 * @author lexu1, wwang84
 *
 */
public abstract class Locator {
	protected int range;
	public Locator(int range){
		this.range=range;
	}
	public abstract int locateID(NodeID self);
	public abstract NodeID locateKey(long key );
	public List<NodeID> locateKeyList(int key, List<NodeID> list){
		return null;
	}
	public abstract int[] addNode(NodeID id);
	public abstract void deleteNode(NodeID id);
	public abstract String[] leave(NodeID id);
	public abstract void printMap();
	public abstract void clean();
}
