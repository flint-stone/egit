package edu.uiuc.cs.cs425.fd.Membership;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.Notification;
import javax.management.NotificationListener;

import edu.uiuc.cs.cs425.fd.Logger;

/**
 * membershiplist class
 * Hashmap structure
 * @author lexu1, wwang84
 *
 */
public class MembershipList implements Serializable, NotificationListener{

	/**
	 * add serial version id
	 */
	private static final long serialVersionUID = -6652842257856157048L;
	
	private Map<MembershipID, Membership> map=new HashMap<MembershipID,Membership>();
	
	private transient long threshFail;
	
	
	/**
	 * set up membership list
	 * @param thresh
	 * @param selfID
	 * @param self
	 */
	public MembershipList(long thresh, MembershipID selfID, Membership self){
		this.map.put(selfID, self);
		this.threshFail=thresh;
	}
	
	
	
	public MembershipList(MembershipID selfID, Membership self){
		this(1000 ,selfID,self);
	}
	
	
	
	public void add(MembershipID selfID, Membership self){
		this.map.put(selfID, self);
	}
	

	/**
	 * called by receiver worker by customized while receiving membershiplist from other node
	 * update local membership list
	 * @param list
	 */
	public void update(MembershipList list){
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
	                    Logger.logTraceInfo("Update member: "+remoteID.getIp()+" "+localMember.getHeartbeat_count()+" "+localMember.getState());
					}
				}
				else{
					//If someone is new and alive
					if(remoteMember.getState()==Membership.ACTIVE){
						Membership newMember = new Membership(remoteMember.getHeartbeat_count());
						map.put(remoteID,newMember);
						newMember.setStamp(new Timestamp(new Date().getTime()));
						Logger.logNewMember(remoteID, "");
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
	public void increaseHeartBeat(MembershipID id){
		synchronized(this.map){
			Membership member=map.get(id);
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

	
	
	@Override
	public void handleNotification(Notification notification, Object handback) {
		Logger.logTraceInfo("TIMEOUT-timer");
		selfRefresh();
	}
	
	
	/**
	 * called every second by failure detector
	 * tag nodes who haven't been updated for a certain amount of time (customized threshold) as failure
	 * remove failed nodes who haven't been updated for a certain amount of time (customized threshold) 
	 * remove nodes who voluntarily left
	 */
	public void selfRefresh(){
		synchronized(this.map){
			Set<MembershipID> keySet=this.map.keySet();
			for(int i=0;i<keySet.size();i++){
				MembershipID id = (MembershipID) keySet.toArray()[i];
				Membership member = this.map.get(id);
				Logger.logTraceInfo("SELF Refresh --- "+id.getIp()+" "+member.getState()+" "+member.getHeartbeat_count());
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
		Logger.logInfo("Deleted  "+id.getIp() +"from Membership List");
	}	
}
