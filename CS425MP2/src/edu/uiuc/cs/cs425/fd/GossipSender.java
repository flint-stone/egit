package edu.uiuc.cs.cs425.fd;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.uiuc.cs.cs425.bandwidth.BandwidthMeasure;
import edu.uiuc.cs.cs425.fd.Membership.Membership;
import edu.uiuc.cs.cs425.fd.Membership.MembershipID;
import edu.uiuc.cs.cs425.fd.Membership.MembershipList;
/**
 * GossipSender: thread worker works as a server thread
 * update membership information
 * @author lexu1, wwang84
 *
 */
public class GossipSender{
	
	BandwidthMeasure measure;
	
	/**
	 * gossip sender constructor
	 */
	public GossipSender(){
		this.measure=new BandwidthMeasure("Sender");
		new Thread(measure).start();
	}
	
	
	/**
	 * Sender for current node spawn thread serving specific target node
	 * @param list
	 * @param id_list
	 * @param pktloss
	 */
	public void send(MembershipList list, List<MembershipID> id_list, int pktloss){
		for(int i=0; i<id_list.size();i++){
			new Thread(new SenderWorker(id_list.get(i).getIp(),id_list.get(i).getPort(),list, pktloss, this.measure)).start();
		}
	}
	
}


/**
 * SenderWorker: thread spawned by Sender
 * Send UDP gossip to other nodes
 * @author lexu1, wwang84
 *
 */

class SenderWorker implements Runnable{

	private String ipAddress;
	private int port;
	private DatagramSocket requestSocket;
	private MembershipList list;
	private int packetLoss;
	private BandwidthMeasure measure;
	
	/**
	 * Sender worker constructor
	 * @param ipAddress
	 * @param port
	 * @param list
	 * @param packetLoss
	 * @param measure
	 */
	SenderWorker( String ipAddress, int port, MembershipList list, int packetLoss, BandwidthMeasure measure ){
		//this.name=name;
		this.ipAddress=ipAddress;
		this.port=port;
		this.list=list;
		this.packetLoss=packetLoss;
		this.measure = measure;
	}
	
	
	/**
	 * Sender worker thread sends heart beats to target node
	 * serialize the object and send through udp
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if(packetLoss()){
				requestSocket=new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName(this.ipAddress);
				ByteArrayOutputStream bytearr=new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bytearr);
				
				//String json = JsonWriter.objectToJson(this.list);
				out.writeObject(this.list);
				byte[] sendarr = bytearr.toByteArray();
				int len=sendarr.length;
				DatagramPacket packet=new DatagramPacket(sendarr, len, IPAddress, this.port);
				Logger.logTraceInfo("SEND TO ---"+ IPAddress);
				requestSocket.send(packet);
				Set<MembershipID> remoteKeySet=this.list.getMap().keySet();
				Iterator<MembershipID> it=remoteKeySet.iterator();
				while(it.hasNext()){
					MembershipID remoteID=it.next();
					Membership member = this.list.getMap().get(remoteID);	
						Logger.logTraceInfo("Send Gossip membership is "+remoteID.getIp()+" "+member.getHeartbeat_count()+" "+member.getState());
				
				}
				measure.increment(len);
				out.close();
			}
		} catch (NumberFormatException e) {
			Logger.logError(e.getMessage());
		} catch (UnknownHostException e) {
			Logger.logError(e.getMessage());
		} catch (Exception e) {
			Logger.logError(e.getMessage());
		}
	}
	
	
	/**
	 * random simulation on packet loss by loss rate
	 * benchmark use only
	 * @return boolean
	 */
	private boolean packetLoss() {
		// TODO Auto-generated method stub
		//http://www.cs.geneseo.edu/~baldwin/reference/random.html
		Random generator = new Random();
		int rndnum = generator.nextInt(100)+1;
		Logger.logTraceInfo("RANDOM NUMBER "+ rndnum);
		if(this.packetLoss > rndnum ){
			return false;
		}
		else{
			return true;
		}
			
	}
}