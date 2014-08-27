package edu.uiuc.cs.cs425.fd.unittest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uiuc.cs.cs425.fd.FailureDetector;
import edu.uiuc.cs.cs425.fd.GossipReceiver;
import edu.uiuc.cs.cs425.fd.GossipSender;
import edu.uiuc.cs.cs425.fd.Membership.Membership;
import edu.uiuc.cs.cs425.fd.Membership.MembershipID;
import edu.uiuc.cs.cs425.fd.Membership.MembershipList;

public class TestNETWORK {

	public static void main(String[] args){
		Membership member1;
		Membership member2;
		Membership member3;
		Timestamp machineJoinTime1= new Timestamp(new Date().getTime());
		Timestamp machineJoinTime2= new Timestamp(new Date().getTime());
		Timestamp machineJoinTime3= new Timestamp(new Date().getTime());
		Timestamp machineJoinTime4= new Timestamp(new Date().getTime());
		MembershipID id1 = new MembershipID("1", machineJoinTime1, "127.0.0.1", 54444);
		MembershipID id2 = new MembershipID("2", machineJoinTime2, "127.0.0.2", 54444);
		MembershipID id3 = new MembershipID("3", machineJoinTime3, "127.0.0.3", 54444);
		MembershipID id4 = new MembershipID("4", machineJoinTime4, "127.0.0.4", 54444);
		FailureDetector fd= new FailureDetector();
		MembershipList localList = new MembershipList(fd,2000);
		member1 = new Membership(id1, 1, localList);
		member2 = new Membership(id2, 2, localList);
		member3 = new Membership(id3, 3, localList);
		localList.add(id1, member1);
		localList.add(id2, member2);
		localList.add(id3, member3);
		
		GossipReceiver r = new GossipReceiver("54444", null);
		new Thread(r).start();
		//Thread.sleep(1000);
		List<MembershipID> id_list=new ArrayList<MembershipID>();
		id_list.add(id1);
		GossipSender s=new GossipSender();
		s.send(localList, id_list, 0);
	}
}
