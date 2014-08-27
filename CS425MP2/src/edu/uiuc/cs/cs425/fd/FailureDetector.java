package edu.uiuc.cs.cs425.fd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.uiuc.cs.cs425.fd.Membership.Membership;
import edu.uiuc.cs.cs425.fd.Membership.MembershipID;
import edu.uiuc.cs.cs425.fd.Membership.MembershipList;

/**
 * Failure Detector main control process
 * 
 * @author wwang84, lexu1
 * 
 */
public class FailureDetector implements Runnable {

	private MembershipList list;
	private MembershipID selfID;
	private Membership self;
	private List<MembershipID> contactServer;

	private int msgLossRate;
	private int randomSelectNode;

	private int heartbeatInterval = 100;

	private GossipReceiver receiver;
	private Thread receThread;
	private GossipSender sender;
	private int faultThreshold;
	private boolean isLive;

	/**
	 * initialized in main thread
	 * @param self
	 * @param contactServer
	 * @param randomSelectNode
	 * @param msgLossRate
	 * @param heartbeatRate
	 * @param faultThres
	 */
	public FailureDetector(MembershipID self, List<MembershipID> contactServer,
			int randomSelectNode, int msgLossRate, int heartbeatRate, int faultThres) {
		this.selfID = self;
		this.contactServer = contactServer;
		this.randomSelectNode = randomSelectNode;
		this.msgLossRate = msgLossRate;
		this.faultThreshold=faultThres;
		if (heartbeatRate > 0) {
			this.heartbeatInterval = heartbeatRate;
		}
		this.isLive = true;
	}
	
	/**
	 * start main thread
	 */
	public void start() {
		self = new Membership(0);
		self.setSelf(true);
		list = new MembershipList(this.faultThreshold, selfID, self);
		receiver = new GossipReceiver(String.valueOf(selfID.getPort()),
				this.list, this.isLive);
		sender = new GossipSender();
	}

	/**
	 * send out leaving message through membership list
	 */
	public void leaveGroup() {
		this.list.setMembershipLeave(selfID);
		// send list to random nodes
		sender.send(list, getRandomNode(this.randomSelectNode),
				this.msgLossRate);
		Logger.logInfo(this.selfID.getIp() + " wants to leave");
		this.isLive = false;
		this.receiver.setLive(false);
	}
	/**
	 * set membership to active and flag to be true 
	 * we are back in the game
	 */
	public void rejoinGroup() {
		this.isLive = true;
		this.self.setState(Membership.ACTIVE);
		this.joinGroup();
	}

	/**
	 * contact certain member to join the group
	 */
	// send gossip to a certain contact node
	public void joinGroup() {
		for(int i=0;i<3;i++){
		sender.send(list, contactServer, this.msgLossRate);
		}
		Logger.logInfo(this.selfID.getIp() + " wants to join");
	}

	/**
	 * send out heart beat by customized period
	 */
	public void heartbeating() {
		this.list.increaseHeartBeat(selfID);
		this.list.selfRefresh();
		List<MembershipID> randomNodes = getRandomNode(this.randomSelectNode);

		if (!randomNodes.isEmpty()) {
			sender.send(list, randomNodes, this.msgLossRate);
			for (MembershipID node : randomNodes) {
				Logger.logTraceInfo(node.getIp()
						+ " is heartbeating to " + node.getIp());
			}
		} else {
			Logger.logTraceInfo("Local membership list only has oneself membership. It sends heartbeat to no one");
		}
	}

	/**
	 * start a receiver main thread and listen to the incoming packets
	 */
	public void listenAndUpdate() {
		receThread = new Thread(receiver);
		receThread.start();
	}

	/**
	 * generate random nodes to send my heart beat to
	 * there's no duplicate nodes in the result
	 * @param randomNodeNum
	 * @return
	 */
	private List<MembershipID> getRandomNode(int randomNodeNum) {
		List<MembershipID> randomNodes = new ArrayList<MembershipID>();
		Random ranInt = new Random();
		List<MembershipID> activeNode = new ArrayList<MembershipID>();

		synchronized (this.list.getMap()) {
			Set<MembershipID> idList = this.list.getMap().keySet();
			// if the list is only self, then don't do gossip
			if (idList.size() == 1) {
				//System.out.println("1");
				return randomNodes;
			} else {
				// record all active nonself nodes
				Iterator<MembershipID> it = idList.iterator();
				while (it.hasNext()) {
					MembershipID id = it.next();
					Membership member = this.list.getMap().get(id);
					if (member.getState() == Membership.ACTIVE
							&& !member.equals(self)) {
						activeNode.add(id);
					}
				}
				// if there is no other active nodes, then don't gossip
				if (activeNode.isEmpty()) {
					//System.out.println("2");
					return randomNodes;
				} else {
					if(randomNodeNum > activeNode.size()){
						//System.out.println("3");
						return activeNode;
					}
					boolean[] count_arr= new boolean[activeNode.size()];
					int i=0;
					while(i<randomNodeNum) {
						int test=Math.abs(ranInt.nextInt()) % activeNode.size();
						if(count_arr[test]==false){
							count_arr[test]=true;
							i++;
							randomNodes.add(activeNode.get(test));
						}
					}
				}
			}

		}
		//System.out.println("random Node list size= "+randomNodes.size());
		return randomNodes;
	}

	/**
	 * failure detector thread context
	 */
	@Override
	public void run() {
		listenAndUpdate();
		while(true){
			
			try {
				
			while (isLive) {
					Thread.sleep(this.heartbeatInterval);
					heartbeating();
			}
			//System.out.println("I'm leaving...");
			} catch (InterruptedException e) {
				Logger.logError(e.getMessage());
			}
		}
	}
}
