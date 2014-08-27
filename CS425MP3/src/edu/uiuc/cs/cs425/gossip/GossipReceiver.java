package edu.uiuc.cs.cs425.gossip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
	Message msg;
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
	public GossipReceiver(String port, Message msg, boolean isLive){
		this.port=Integer.parseInt(port);
		this.msg=msg;
		this.isLive=isLive;
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
				Logger.logGossipComm("RECE FROM ---"+ packet.getAddress()+" "+this.port);
				new Thread(new ReceiverWorker(packet, this.msg)).start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			Logger.logGossipComm(e1.getMessage());
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
	private Message msg;
	
	/**
	 * ReceiverWorker thread constructor
	 * @param socket
	 * @param packet
	 * @param globalList
	 */
	public ReceiverWorker(  DatagramPacket packet, Message msg) {
		this.packet=packet;
		this.msg=msg;
	}
	
	/**
	 * ReceiverWorker thread context
	 * get bit stream through UDP and de-serialize the object  
	 */
	@Override
	public void run() {
		try {
			byte[] indata = this.packet.getData();
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(indata));
			Message message=(Message)in.readObject();       
			this.msg.merge(message);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.logGossipComm(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Logger.logGossipComm(e.getMessage());
		} 
		
	}
}