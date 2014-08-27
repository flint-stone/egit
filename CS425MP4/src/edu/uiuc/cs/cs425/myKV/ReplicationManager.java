package edu.uiuc.cs.cs425.myKV;

import java.util.Map;
import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.TCP.QuerySender;

/**
 * Replication Manager class, used for maintaining three replicas of each key
 * value pair
 * 
 * @author lexu1, wwang84
 * 
 */
public class ReplicationManager {
	private KVstorage storage;
	private QuerySender sender;
	private NodeID self;
	private HashLocator locator;

	public static int ALL = 0;
	public static int QUORUM = 1;
	public static int ONE = 2;

	public ReplicationManager(KVstorage storage, QuerySender sender,
			NodeID self, HashLocator locator) {
		this.storage = storage;
		this.sender = sender;
		this.self = self;
		this.locator = locator;
	}

	/**
	 * send data of this node to two other neighbors： predecessor and successor
	 * 
	 * @return void
	 * 
	 */
	public void sendMyDataToMyNeighbour() {
		NodeID[] neighbours = locator.getMyNeighbor();
		int[] range = locator.getSelfRange();
		for (int i = 0; i < neighbours.length; i++) {
			if (!neighbours[i].equals(self)) {
				sendMyDataToOneNeighbour(range, DataBlock.REPLICATION,
						neighbours[i]);
			}
		}
	}

	/**
	 * send data in a range to one node
	 * 
	 * @param int[] range
	 * @param status
	 *            indicates what kind of data it is
	 * @param NodeID
	 *            neigbor one node in the virtual ring
	 * @return void
	 * 
	 */
	public void sendMyDataToOneNeighbour(int[] range, int status,
			NodeID neighbour) {
		Map map = storage.migrate(range);
		if (map != null) {
			DataBlock dataBlock = new DataBlock(map);
			dataBlock.setStatus(status);
			dataBlock.setSelf(self);
			System.out.println("sending my data as replica" + map.size()
					+ " to " + neighbour.getIp() + "@" + neighbour.getPort());
			try {
				sender.sendDataBlock(dataBlock, neighbour);
			} catch (Exception e) {
				System.out
						.println("Sending DataBlock error: " + e.getMessage());
			}
		}
	}

	/**
	 * handle received data when a node receives data/replication data from its
	 * neighbors
	 * 
	 * @param block
	 *            the data block this node received
	 * @return void
	 */
	public void handleDataBlock(DataBlock block) {
		int status = block.getStatus();
		NodeID node = block.getSelf();
		if (status == DataBlock.MIGRATION_LEAVE) {
			System.out.println("Updating my primary data"
					+ block.getMap().size() + " from " + node.getIp() + "@"
					+ node.getPort());
			if (locator.hasNode(node)) {
				locator.deleteNode(node);
			}
			this.storage.insert(block.getMap());
			sendMyDataToMyNeighbour();
		} else if (status == DataBlock.MIGRATION_ADD) {
			System.out.println("Updating my primary data "
					+ block.getMap().size() + " from " + node.getIp() + "@"
					+ node.getPort());
			if (!locator.hasNode(node)) {
				locator.addNode(node);
			}
			this.storage.insert(block.getMap());
			sendMyDataToMyNeighbour();
		} else if (status == DataBlock.REPLICATION) {
			if (!locator.hasNode(node)) {
				locator.addNode(node);
			}
			this.storage.insert(block.getMap());
		}
	}
}
