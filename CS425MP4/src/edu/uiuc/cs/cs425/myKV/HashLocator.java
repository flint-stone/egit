package edu.uiuc.cs.cs425.myKV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uiuc.cs.cs425.gossip.NodeID;

/**
 * HashLocator, one implementation of Locator, using hash function and organize
 * servers as a ring like chord
 * 
 * @author lexu1, wwang84
 * 
 */
public class HashLocator {

	private Map<Integer, NodeID> virtualMap = new HashMap<Integer, NodeID>();

	public static int range;

	private NodeID self;

	public HashLocator(int range, NodeID self) {
		HashLocator.range = range;
		this.self = self;
		this.addNode(self);
	}

	public static int hashObject(Object key) {
		return key.hashCode() % range;
	}

	public void resetSuccessorAndProdecessor(NodeID node) {
		node.setSuccessor(null);
		node.setPredecessor(null);
		List<Integer> keyList = new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keyList);
		int selfID = this.locateID(node);

		Iterator<Integer> it = keyList.iterator();
		while (it.hasNext()) {
			int cur = it.next();
			if (cur > selfID && node.getSuccessor() == null) {
				node.setSuccessor(this.virtualMap.get(cur));
			}
			if (cur < selfID) {
				node.setPredecessor(this.virtualMap.get(cur));
			}
		}
		if (node.getSuccessor() == null) {
			node.setSuccessor(this.virtualMap.get(keyList.get(0)));
		}
		if (node.getPredecessor() == null) {
			node.setPredecessor(this.virtualMap.get(keyList.get(keyList.size() - 1)));
		}

	}

	public synchronized int locateID(NodeID node) {
		String hashstring = node.getIp() + Integer.toString(node.getPort());
		int scratch = hashstring.hashCode();
		return Math.abs(scratch % range);
	}

	/**
	 * Add a node/server into the ring
	 * 
	 * @param node
	 *            which is added to the ring
	 * @return a integer array which contains the range of keys need to move
	 */

	public synchronized int[] addNode(NodeID node) {
		int id = locateID(node);
		this.virtualMap.put(id, node);
		List<NodeID> nodes = new ArrayList<NodeID>(this.virtualMap.values());
		for (NodeID n : nodes) {
			this.resetSuccessorAndProdecessor(n);
		}
		return getSelfRange(node);
	}

	/**
	 * delete a node/server from the ring
	 * 
	 * @param node
	 *            which is added to the ring
	 * @return null
	 */
	public synchronized void deleteNode(NodeID node) {
		int id = locateID(node);
		if (this.virtualMap.containsKey(id)) {
			this.virtualMap.remove(id);
			List<NodeID> nodes = new ArrayList<NodeID>(this.virtualMap.values());
			for (NodeID n : nodes) {
				this.resetSuccessorAndProdecessor(n);
			}
		}
	}

	/**
	 * locate a key
	 * 
	 * @param key
	 * @return a server which holds this key
	 */

	public NodeID locateKey(Object key) {

		List<Integer> keylist = new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);
		Iterator<Integer> it = keylist.iterator();
		while (it.hasNext()) {
			int cur = (Integer) it.next();
			if (cur >= HashLocator.hashObject(key)) {
				return this.virtualMap.get(cur);
			}
		}
		return this.virtualMap.get(keylist.get(0));
	}

	public void printMap() {

		System.out.println("I am\t\t" + this.self.getIp() + "@"
				+ this.self.getPort());
		System.out.println("Predecessor is \t"
				+ this.self.getPredecessor().getIp() + "@"
				+ this.self.getPredecessor().getPort());
		System.out.println("Successor is \t" + this.self.getSuccessor().getIp()
				+ "@" + this.self.getSuccessor().getPort());
		System.out.println("--------------");
		List<Integer> keylist = new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);
		Iterator<Integer> it = keylist.iterator();
		while (it.hasNext()) {
			int cur = (Integer) it.next();
			System.out.println("ring#:" + cur + " node:"
					+ this.virtualMap.get(cur).getIp() + "@"
					+ this.virtualMap.get(cur).getPort());
		}
		System.out.println("--------------");

	}

	/**
	 * 
	 * @param node
	 *            which is voluntarily leaving
	 * @return a string array in which string[0]=ipadress, string[1]=port,
	 *         string[3][4]= the range of keys need to move
	 */
	public synchronized String[] leave(NodeID node) {

		String[] target = new String[4];
		target[0] = String.valueOf(this.getSelfRange(node)[0]);
		target[1] = String.valueOf(this.getSelfRange(node)[1]);
		target[2] = node.getSuccessor().getIp();
		target[3] = String.valueOf(node.getSuccessor().getPort());

		return target;
	}

	public void clean() {
		this.virtualMap.clear();
	}

	public synchronized boolean hasNode(NodeID leavingNode) {
		return this.virtualMap.containsKey(this.locateID(leavingNode));
	}

	public NodeID[] getMyNeighbor() {
		NodeID[] neighbors = new NodeID[2];
		neighbors[0] = this.self.getPredecessor();
		neighbors[1] = this.self.getSuccessor();
		return neighbors;
	}

	public int[] getSelfRange(NodeID node) {
		int id = locateID(node);
		List<Integer> keylist = new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);

		int[] range = new int[2];
		range[1] = id;
		for (int i = 0; i < keylist.size(); i++) {
			if (keylist.get(i) < id) {
				range[0] = keylist.get(i);
			}
		}
		if (range[0] == 0) {
			if (keylist.size() == 0) {
				range[0] = id;
			} else {
				range[0] = keylist.get(keylist.size() - 1);
			}
		}
		return range;
	}

	public synchronized int[] getSelfRange() {
		return this.getSelfRange(this.self);
	}

	public synchronized int[] getPredecessorRange() {
		return this.getSelfRange(this.self.getPredecessor());
	}

	public synchronized int[] getSuccessorRange() {
		return this.getSelfRange(this.self.getSuccessor());
	}

}
