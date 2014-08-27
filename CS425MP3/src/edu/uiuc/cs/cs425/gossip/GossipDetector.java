package edu.uiuc.cs.cs425.gossip;

import java.util.List;

/**
 * The frame of gossip protocol 
 * @author lexu1, wwang84
 *
 */
public abstract class GossipDetector implements Runnable {
	
	protected GossipReceiver gossipReceiver;
	protected GossipSender gossipSender;
	protected Message localMsg;
	protected boolean isAlive;
	private int heartbeatInterval;
	private List<NodeID> contactServer;
	protected int infectnumber;
	
	public GossipDetector(Message msg, int interval, List<NodeID> contactServer, String port, int infectnumber){
		this.localMsg = msg;
		this.heartbeatInterval = interval;
		this.contactServer = contactServer;
		this.isAlive = true;
		this.gossipReceiver=new GossipReceiver(port, msg, isAlive);
		this.gossipSender = new GossipSender();
		this.infectnumber = infectnumber;
	}

	@Override
	public void run() {
		new Thread(gossipReceiver).start();
		while(true){
			
			try {	
			while (isAlive) {
					Thread.sleep(this.heartbeatInterval);
					heartbeating();
			}
			//System.out.println("I'm leaving...");
			} catch (InterruptedException e) {
				Logger.logGossipComm(e.getMessage());
			}
		}
	}
	/**
	 * Send heartbeat to other nodes
	 * @param null
	 * @return null
	 */
	protected void heartbeating() {
		this.localMsg.selfcheck();
		List<NodeID> randomNodes = getInfectNode(this.infectnumber);

		if (!randomNodes.isEmpty()) {
			this.gossipSender.send(this.localMsg, (List<NodeID>)(List<?>)randomNodes, 0);
			for (NodeID node : randomNodes) {
				Logger.logGossipComm(node.getIp()
						+ " is heartbeating to " + node.getIp());
			}
		} else {
			Logger.logGossipComm("Local membership list only has oneself membership. It sends heartbeat to no one");
		}		
	}
	/**
	 * get the contact nodes, used for sending heartbeat
	 * @param randomNodeNum the number of Nodes need to be notified
	 * @return a list of nodes need to be notified
	 */
	protected abstract List<NodeID> getInfectNode(int randomNodeNum);
	
	public void join(){
		for(int i=0;i<2;i++){
			this.gossipSender.send(this.localMsg, this.contactServer, 0);
			}
		Logger.logGossipInfo("local server wants to join the global group");
	}
	/**
	 * return the gossip message
	 * @return Message in the gossip protocol
	 */
	public Message getMessage(){
		return this.localMsg;
	}

}
