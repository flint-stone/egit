package edu.uiuc.cs.cs425.gossip.Membership;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.uiuc.cs.cs425.gossip.GossipDetector;
import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.gossip.Message;
import edu.uiuc.cs.cs425.gossip.NodeID;

/**
 * Failure Detector main control process
 * 
 * @author wwang84, lexu1
 * 
 */
public class FailureDetector extends GossipDetector {

	private MembershipID self;
	
	/**
	 * initialized in main thread
	 * @param self
	 * @param contactServer
	 * @param randomSelectNode
	 * @param msgLossRate
	 * @param heartbeatRate
	 * @param faultThres
	 */
	public FailureDetector(Message msg, MembershipID selfID, List<NodeID> contactServer,
			int randomSelectNode, int msgLossRate, int interval) {
		super(msg, interval, contactServer, String.valueOf(selfID.getPort()), randomSelectNode);
		this.self = selfID;
	}
	

	/**
	 * send out leaving message through membership list
	 */
	public void leaveGroup() {
		MembershipList list=(MembershipList)this.localMsg;
		list.setMembershipLeave(self);
		// send list to random nodes
		this.gossipSender.send(this.localMsg, getInfectNode(this.infectnumber), 0);
		Logger.logGossipInfo("I want to leave the global group!");
		this.isAlive = false;
		this.gossipReceiver.setLive(false);
	}

	
	
	@Override
	protected List<NodeID> getInfectNode(int randomNodeNum) {
		MembershipList list=(MembershipList)this.localMsg;
		List<NodeID> randomNodes = new ArrayList<NodeID>();
		Random ranInt = new Random();
		List<NodeID> activeNode = new ArrayList<NodeID>();

		synchronized (list.getMap()) {
			Set<MembershipID> idList = list.getMap().keySet();
			// if the list is only self, then don't do gossip
			if (idList.size() == 1) {
				return randomNodes;
			} else {
				// record all active nonself nodes
				Iterator<MembershipID> it = idList.iterator();
				while (it.hasNext()) {
					MembershipID id = it.next();
					Membership member = list.getMap().get(id);
					if (member.getState() == Membership.ACTIVE && !member.equals(self)) {
						activeNode.add(id);
					}
				}
				// if there is no other active nodes, then don't gossip
				if (activeNode.isEmpty()) {
					return randomNodes;
				} else {
					if(randomNodeNum > activeNode.size()){
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
		return randomNodes;
	}
	@Override
	public void heartbeating(){
		MembershipList list=(MembershipList)this.localMsg;
		list.increaseHeartBeat();
		super.heartbeating();
	}
}
