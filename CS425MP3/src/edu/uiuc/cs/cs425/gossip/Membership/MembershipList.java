package edu.uiuc.cs.cs425.gossip.Membership;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.gossip.Message;
import edu.uiuc.cs.cs425.gossip.NodeID;

/**
 * membershiplist class
 * Hashmap structure
 * @author lexu1, wwang84
 *
 */
public class MembershipList extends Observable implements Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4114028097093225910L;

	private Map<MembershipID, Membership> map=new HashMap<MembershipID,Membership>();
	
	private transient long threshFail;
	private MembershipID selfID;
	private transient Boolean selfContact;
	private transient String filename;
	private transient Hashtable contactSeverUseOnly;
	
	
	
	/**
	 * set up membership list
	 * @param thresh
	 * @param selfID
	 * @param self
	 * @throws UnknownHostException 
	 */
	public MembershipList(long thresh, MembershipID selfID, Membership self,String filename,boolean selfContact, List<NodeID> contactServer){
		this.map.put(selfID, self);
		this.threshFail=thresh;
		this.selfID=selfID;
		this.selfContact=selfContact;
		this.filename=filename;
		this.contactSeverUseOnly=new Hashtable();
		if(selfContact){
			for(int i=0;i<contactServer.size();i++){
				this.contactSeverUseOnly.put(contactServer.get(i).getIp(), 1);
			}
		}
	}
	
	
	
	public MembershipList(MembershipID selfID, Membership self) throws UnknownHostException{
		this(1000 ,selfID,self,"server.config",false,null);
	}
	
	
	
	public void add(MembershipID selfID, Membership self){
		this.map.put(selfID, self);
	}
	

	/**
	 * called by receiver worker by customized while receiving membershiplist from other node
	 * update local membership list
	 * @param list
	 * @throws IOException 
	 */
	public void merge(Message msg){
		
		MembershipList list=(MembershipList)msg;
		
		Set<MembershipID> rmKeySet=list.getMap().keySet();
		Iterator<MembershipID> item=rmKeySet.iterator();
		while(item.hasNext()){
			MembershipID remoteID=item.next();
			Membership member = list.getMap().get(remoteID);
			Logger.logGossipComm("Received Gossip membership is "+remoteID.getIp()+" "+member.getHeartbeat_count()+" "+member.getState());

		}
		
		synchronized(this.map){
			Set<MembershipID> remoteKeySet=list.getMap().keySet();
			Iterator<MembershipID> it=remoteKeySet.iterator();
			while(it.hasNext()){
				MembershipID remoteID=it.next();
				Membership remoteMember=list.getMap().get(remoteID);
				if(map.containsKey(remoteID)){
					Membership localMember=map.get(remoteID);
					//If someone is leaving
					if(remoteMember.getState() == Membership.LEAVING && localMember.getState()==Membership.ACTIVE){
						localMember.setState(Membership.LEAVING);
						localMember.setHeartbeat_count(remoteMember.getHeartbeat_count());
						Logger.logLeavingMember(remoteID, "");
						this.notifyLeaveEvent(remoteID);
						continue;
					}
					//If someone has failed
					else if(remoteMember.getState() == Membership.FAILURE){
						continue;
					}
					//If someone is alive
					else if(remoteMember.getState()==Membership.ACTIVE &&(localMember.getState()!=Membership.LEAVING)&& localMember.getHeartbeat_count()<remoteMember.getHeartbeat_count()){
						//need to update heartbeat and time and reset the timeout of that member
	                    localMember.setHeartbeat_count(remoteMember.getHeartbeat_count());
	                    localMember.setStamp(new Timestamp(new Date().getTime()));
	                    localMember.setState(Membership.ACTIVE);
	                    Logger.logGossipComm("Update member: "+remoteID.getIp()+" "+localMember.getHeartbeat_count()+" "+localMember.getState());
					}
				}
				else{
					//If someone is new and alive
					if(remoteMember.getState()==Membership.ACTIVE){
						Membership newMember = new Membership(remoteMember.getHeartbeat_count());
						map.put(remoteID,newMember);
						newMember.setStamp(new Timestamp(new Date().getTime()));
						Logger.logNewMember(remoteID, "");
						this.notifyJoinEvent(remoteID);
						//update the server.config if true
						if(selfContact&&(!this.contactSeverUseOnly.containsKey(remoteID.getIp()))){
							this.contactSeverUseOnly.put(remoteID.getIp(), 1);
							FileWriter fw;
							try {
								fw = new FileWriter(filename,true);
								BufferedWriter w=new BufferedWriter(fw);
								String app="contactServer "+remoteID.getIp()+" "+remoteID.getPort();
								System.out.println(app);
								w.newLine();
								w.write(app);
								w.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
						}
					}
					
				}
			}
		}
	}
	
	
	/**
	 * tag certain member as left
	 * @param ID
	 */
	public void setMembershipLeave(MembershipID ID){
		Membership local = map.get(ID);
		if(local!=null){
			local.setState(Membership.LEAVING);
		}
	}

	
	/**
	 * update entry in the membershiplist
	 * increase the heartbeat of the node and update the current local time
	 * @param id
	 */
	public void increaseHeartBeat(){
		synchronized(this.map){
			Membership member=map.get(selfID);
			Timestamp current = new Timestamp(new Date().getTime());
			member.setHeartbeat_count(member.getHeartbeat_count()+1);
			member.setStamp(current);
		}
	}
	
	
	
	public Map<MembershipID, Membership> getMap() {
		return map;
	}

	
	
	public void setMap(Map<MembershipID, Membership> map) {
		this.map = map;
	}
	
	
	
	public int size(){
		return map.size();
	}

	

	
	/**
	 * called every second by failure detector
	 * tag nodes who haven't been updated for a certain amount of time (customized threshold) as failure
	 * remove failed nodes who haven't been updated for a certain amount of time (customized threshold) 
	 * remove nodes who voluntarily left
	 */
	public void selfcheck(){
		synchronized(this.map){
			Set<MembershipID> keySet=this.map.keySet();
			for(int i=0;i<keySet.size();i++){
				MembershipID id = (MembershipID) keySet.toArray()[i];
				Membership member = this.map.get(id);
				Logger.logGossipComm("SELF Refresh --- "+id.getIp()+" "+member.getState()+" "+member.getHeartbeat_count());
				if(!member.isSelf()){
					if((member.getState()==Membership.LEAVING||member.getState()==Membership.FAILURE )&& (new Timestamp(new Date().getTime()).getTime()-member.getStamp().getTime()) >2*this.threshFail){
						this.deleteMembership(id);
						i--;
					}
					else if( member.getState()==Membership.ACTIVE && ( new Timestamp(new Date().getTime())).getTime()-member.getStamp().getTime()>this.threshFail){
						member.setState(Membership.FAILURE);
						Logger.logCrashMember(id,"");
					}
				}
			}
		}		
	}

	
	/**
	 * delete membership from list by its ID
	 * @param id
	 */
	private void deleteMembership(MembershipID id) {
            this.map.remove(id);
		Logger.logGossipInfo("Deleted  "+id.getIp() +"from Membership List");
	}



	@Override
	public void printMsg() {
		Set<MembershipID> remoteKeySet=this.map.keySet();
		Iterator<MembershipID> it=remoteKeySet.iterator();
		while(it.hasNext()){
			MembershipID remoteID=it.next();
			Membership member = this.map.get(remoteID);	
			Logger.logGossipComm("Local membership is "+remoteID.getIp()+" "+member.getHeartbeat_count()+" "+member.getState());
		
		}
	}

	private void notifyJoinEvent(NodeID node){
		String[] event={"join",node.getIp(),String.valueOf(node.getPort())};
		setChanged();
		notifyObservers(event);
	}
	
	private void notifyLeaveEvent(NodeID node){
		String[] event={"leave",node.getIp(),String.valueOf(node.getPort())};
		setChanged();
		notifyObservers(event);
	}
}
