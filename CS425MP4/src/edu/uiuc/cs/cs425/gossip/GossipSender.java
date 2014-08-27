package edu.uiuc.cs.cs425.gossip;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

/**
 * GossipSender: thread worker works as a server thread
 * update membership information
 * @author lexu1, wwang84
 *
 */
public class GossipSender{

	
	/**
	 * gossip sender constructor
	 */
	public GossipSender(){

	}
	
	
	/**
	 * Sender for current node spawn thread serving specific target node
	 * @param list
	 * @param id_list
	 * @param pktloss
	 */
	public void send(Message message, List<NodeID> id_list, int pktloss){
		for(int i=0; i<id_list.size();i++){
			new Thread(new SenderWorker(id_list.get(i).getIp(),id_list.get(i).getPort(),message, pktloss)).start();
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
	private Message message;
	private int packetLoss;
	
	/**
	 * Sender worker constructor
	 * @param ipAddress
	 * @param port
	 * @param list
	 * @param packetLoss
	 * @param measure
	 */
	SenderWorker( String ipAddress, int port, Message message, int packetLoss){
		//this.name=name;
		this.ipAddress=ipAddress;
		this.port=port;
		this.message=message;
		this.packetLoss=packetLoss;
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
				out.writeObject(this.message);
				byte[] sendarr = bytearr.toByteArray();
				int len=sendarr.length;
				DatagramPacket packet=new DatagramPacket(sendarr, len, IPAddress, this.port);
				Logger.logGossipComm("SEND TO ---"+ IPAddress+" "+this.port);
				requestSocket.send(packet);
				this.message.printMsg();
				out.close();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Logger.logGossipComm(e.getMessage());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Logger.logGossipComm(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.logGossipComm(e.getMessage());
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
		if(this.packetLoss > rndnum ){
			return false;
		}
		else{
			return true;
		}
			
	}
}