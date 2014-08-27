package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.myKV.Command;
import edu.uiuc.cs.cs425.myKV.DataBlock;
import edu.uiuc.cs.cs425.myKV.KVstorage;
import edu.uiuc.cs.cs425.myKV.Record;
import edu.uiuc.cs.cs425.myKV.ReplicationManager;

/**
 * Query Receiver class
 * TCP connection between Servers
 * @author lexu1, wwang84
 *
 */
public class QueryReceiver implements Runnable{
	private ServerSocket socket;
	private Socket connection;
	private int port;
	private KVstorage storage;
	private ReplicationManager rm;
	
	public QueryReceiver(int port,KVstorage storage,ReplicationManager rm){
		this.port=port;
		this.storage=storage;
		this.rm=rm;
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
				QueryReceiverWorker worker= new QueryReceiverWorker(connection, this.storage,this.rm, this.port);
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
	private KVstorage storage;
	private int port;
	private ReplicationManager rm;
	
	public QueryReceiverWorker(Socket connection, KVstorage storage,ReplicationManager rm,int port){
		this.connection = connection;
		this.storage=storage;
		this.port=port;
		this.rm=rm;
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

			if(obj instanceof DataBlock){//migrate data due to node joining and leaving
				this.rm.handleDataBlock((DataBlock)obj);
				out.writeObject(true);
				out.flush();
			}	
			
			else{
				Command command= (Command)(obj);
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
	private Object processRequest(Command command) {
		//System.out.println("Recieved Command:"+command.getCommand()+" "+ command.getKey()+" "+ command.getValue().getContent()+" "+command.getValue().getTimeStamp());
		String commandLine = command.getCommand();
		Object key =  command.getKey();
		Record<Object> value = command.getValue();
		if(commandLine.equals("insert")){
			return this.storage.insert(key,value);
		}
		else if(commandLine.equals("update")){
			return this.storage.update(key,value);
		}
		else if(commandLine.equals("delete")){
			return this.storage.delete(key,value);
		}
		else if(commandLine.equals("lookup")){
			return this.storage.lookup(key);
		}
		else{
			return null;
		}
		//return rm.executeCommand(command[0], command[1], command[2], Integer.parseInt(command[3]));
	}
	
}
