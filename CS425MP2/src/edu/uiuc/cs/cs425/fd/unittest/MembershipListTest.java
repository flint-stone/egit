package edu.uiuc.cs.cs425.fd.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import edu.uiuc.cs.cs425.fd.Logger;
import edu.uiuc.cs.cs425.fd.Membership.Membership;
import edu.uiuc.cs.cs425.fd.Membership.MembershipID;
import edu.uiuc.cs.cs425.fd.Membership.MembershipList;

public class MembershipListTest {

	Timestamp machineJoinTime1= new Timestamp(new Date().getTime());
	Timestamp machineJoinTime2= new Timestamp(new Date().getTime());
	Timestamp machineJoinTime3= new Timestamp(new Date().getTime());
	Timestamp machineJoinTime4= new Timestamp(new Date().getTime());

	MembershipID id1 = new MembershipID("1", machineJoinTime1, "127.0.0.1", 5444);
	MembershipID id2 = new MembershipID("2", machineJoinTime2, "127.0.0.2", 5444);
	MembershipID id3 = new MembershipID("3", machineJoinTime3, "127.0.0.3", 5444);
	MembershipID id4 = new MembershipID("4", machineJoinTime4, "127.0.0.4", 5444);

	// generate Member
	Timestamp heartbeatTime1 = new Timestamp(new Date().getTime());
	Timestamp heartbeatTime2 = new Timestamp(new Date().getTime());
	Timestamp heartbeatTime3 = new Timestamp(new Date().getTime());
	Timestamp heartbeatTime4 = new Timestamp(new Date().getTime());

	Membership member1;
	Membership member2;
	Membership member3;
	Membership member4;
	
	Membership member5;
	Membership member6;
	Membership member7;
	Membership member8;

	MembershipList localList;
	MembershipList remoteList1;
	MembershipList remoteList2;


	/**
	 * set up function: set up testing command and path
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() {
		
		Logger log = new Logger();
		localList = new MembershipList(log,2000);
		
		member1 = new Membership(id1, 1, localList);
		member2 = new Membership(id2, 2, localList);
		member3 = new Membership(id3, 3, localList);
		//member4 = new Membership(id4, 4, fd, localList);

		// genrate Membership	
		localList.add(member1.getId(), member1);
		localList.add(member2.getId(), member2);
		localList.add(member3.getId(), member3);
		
		remoteList1 = new MembershipList(log,2000);
		member5 = new Membership(id1, 2, remoteList1);
		member6 = new Membership(id2, 2, remoteList1);
		member7 = new Membership(id4, 3, remoteList1);
		member8 = new Membership(id3, 4, remoteList1);
		member8.setState(Membership.LEAVING);
		
		remoteList1.add(member5.getId(), member5);
		remoteList1.add(member6.getId(), member6);
		remoteList1.add(member7.getId(), member7);
		
		remoteList2 = new MembershipList(log,2000);
		remoteList2.add(member8.getId(), member8);
		
		
		
		
	}
	@Test
	public void testHEartBeatUpdate() throws FileNotFoundException {
		setUp();
		localList.update(remoteList1);
		Membership member = localList.getMap().get(id1);
		assertEquals(member.getHeartbeat_count(),2);
	}
	
	@Test
	public void testNew() throws FileNotFoundException {
		setUp();
		localList.update(remoteList1);
		assertEquals(localList.size(), 4);
	}
	
	@Test
	public void testFailure() throws FileNotFoundException, InterruptedException {
		setUp();
		member1.startTimer();
		member2.startTimer();
		member3.startTimer();
		member5.startTimer();
		member6.startTimer();
		member7.startTimer();
		Thread.sleep(1000);
		localList.update(remoteList1);
		Thread.sleep(1100);
		assertEquals(localList.size(), 2);
	}
	
	@Test
	public void testLeaving() throws FileNotFoundException, InterruptedException {
		setUp();
		localList.update(remoteList2);
		Membership member = localList.getMap().get(id3);
		assertNull(member);
	}
	
	@Test
	public void testSingleMember1() throws FileNotFoundException, InterruptedException {
		setUp();
		member1.startTimer();
		Thread.sleep(2100);
		assertEquals(localList.size(), 2);
	}
	
	@Test
	public void testSingleMember2() throws FileNotFoundException, InterruptedException {
		setUp();
		member1.startTimer();
		member2.startTimer();
		member3.startTimer();
		Thread.sleep(2100);
		assertEquals(localList.size(), 0);
	}
}
