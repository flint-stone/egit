package edu.uiuc.cs.cs425.fd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.Set;

import edu.uiuc.cs.cs425.bandwidth.BandwidthMeasure;
import edu.uiuc.cs.cs425.fd.Membership.Membership;
import edu.uiuc.cs.cs425.fd.Membership.MembershipID;
import edu.uiuc.cs.cs425.fd.Membership.MembershipList;

/**
 * multi threaded gossip receiver (UDP) class
 * @author lexu1, wwang84
 *
 */
public class GossipReceiver implements Runnable{
	//ServerSocket socket;
	//http://www.coderpanda.com/java-socket-programming-transferring-java-object-through-socket-using-udp/
	private DatagramSocket socket=null;
	//Socket connection;
	int port;
	private BandwidthMeasure measure;
	MembershipList globalList;
	private boolean isLive;
	
	/**
	 * getter of isLive
	 * @return
	 */
	public boolean isLive() {
		return isLive;
	}

	/**
	 * setter of isLive
	 * @param isLive
	 */
	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	/**
	 * receiver constructor
	 * @param port
	 * @param globalList
	 * @param isLive
	 */
	public GossipReceiver(String port, MembershipList globalList, boolean isLive){
		this.port=Integer.parseInt(port);
		this.globalList = globalList;
		this.isLive=isLive;
		this.measure=new BandwidthMeasure("Receiver");
		new Thread(measure).start();
	}
	
	/**
	 * Gossip Receiver starter, not really a thread context
	 */
	@Override
	public void run() {
		byte[] buffer=null;
		try {
			socket = new DatagramSocket(this.port);
			while(isLive){		
				buffer = new byte[4096];
				DatagramPacket packet=new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				new Thread(new ReceiverWorker( packet, this.globalList, this.measure)).start(); 
			}
		} catch (IOException e1) {
			Logger.logError(e1.getMessage());
		}
		finally{
			socket.close();
		}
		System.out.println("Receivor work done");
	}
	
}

/**
 * Receiver thread spawned by Receiver
 * Receive heartbeat(gossip) message through UDP
 * @author lexu1, wwang84
 *
 */
class ReceiverWorker implements Runnable{
	private DatagramPacket packet;
	private MembershipList list;
	private BandwidthMeasure measure;
	/**
	 * ReceiverWorker thread constructor
	 * @param socket
	 * @param packet
	 * @param globalList
	 */
	public ReceiverWorker(  DatagramPacket packet, MembershipList globalList, BandwidthMeasure measure) {
		this.packet=packet;
		this.list=globalList;
		this.measure=measure;
	}
	
	/**
	 * ReceiverWorker thread context
	 * get bit stream through UDP and de-serialize the object  
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			byte[] indata = this.packet.getData();
			this.measure.increment(indata.length);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(indata));
			MembershipList newlist=(MembershipList)in.readObject();       
			this.list.update(newlist);
			Set<MembershipID> remoteKeySet=newlist.getMap().keySet();
			Iterator<MembershipID> it=remoteKeySet.iterator();
			while(it.hasNext()){
				MembershipID remoteID=it.next();
				Membership member = newlist.getMap().get(remoteID);
				if(member.isSelf()){
					Logger.logTraceInfo("REC FROM--- "+ remoteID.getIp());
					Logger.logTraceInfo("Received Gossip membership is "+remoteID.getIp()+" "+member.getHeartbeat_count()+" "+member.getState());
				}
				else{
					Logger.logTraceInfo("Received Gossip membership is "+remoteID.getIp()+" "+member.getHeartbeat_count()+" "+member.getState());
				}
			}
			in.close();
		} catch (IOException e) {
			Logger.logError(e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.logError(e.getMessage());
		} 
		
	}
}