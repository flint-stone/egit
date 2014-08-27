package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.myKV.KVstorage;

/**
 * Query Receiver class
 * TCP connection between Servers
 * @author lexu1, wwang84
 *
 */
public class QueryReceiver implements Runnable{
	ServerSocket socket;
	Socket connection;
	int port;
	KVstorage store;
	
	public QueryReceiver(int port, KVstorage store){
		this.port=port;
		this.store=store;
	}
	
	@Override
	public void run() {
		if(this.port>65535){
			Logger.logKvCommError("port out of range");
			return;
		}
		try {
			socket = new ServerSocket(this.port, 10);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true){
			Logger.logKvComm("Waiting for connection...");
			try {
				this.connection=this.socket.accept();
				QueryReceiverWorker worker= new QueryReceiverWorker(connection, this.store);
				Logger.logKvComm("Connection received from " + connection.getInetAddress().getHostName());
				new Thread(worker).start();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

}

/**
 * QueryReceiverWorker class
 * worker thread for Query receiver
 * @author lexu
 *
 */
class QueryReceiverWorker implements Runnable{

	private Socket connection;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private KVstorage kvstore;
	
	public QueryReceiverWorker(Socket connection, KVstorage kvstore){
		this.connection = connection;
		this.kvstore=kvstore;
	}
	/**
	 * main execution thread context
	 */
	@Override
	public void run() {
		Logger.logKvComm("Query Receiver worker started....");
		try {
			in=new ObjectInputStream(connection.getInputStream());
			out=new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			Object obj=in.readObject();
			if(obj instanceof Map){//migrate data due to node joining and leaving
				this.kvstore.insert((Map<Long, Object>)obj);
				out.writeObject(true);
				out.flush();
			}	
			else{
				String[] command= (String[])(obj);
				for(int i=0;i<command.length;i++){
					System.out.println(command[i]);
				}
				Object ret=processRequest(command);
				out.writeObject(ret);
				out.flush();
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * process request and call kv storage
	 * @param command
	 * @return
	 */
	private Object processRequest(String[] command) {
		if(command[0].equals("insert")){
			return this.kvstore.insert(Long.parseLong(command[1]), command[2]);
		}
		else if(command[0].equals("update")){
			return this.kvstore.update(Long.parseLong(command[1]), command[2]);
		}
		else if(command[0].equals("delete")){
			return this.kvstore.delete(Long.parseLong(command[1]));
		}
		else if(command[0].equals("lookup")){
			return this.kvstore.lookup(Long.parseLong(command[1]));
		}
		else{
			return "no such operation!";
		}
	}
	
}
