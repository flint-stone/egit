package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.myKV.Coordinator;
/**
 * CommandReceiver class
 * TCP connection for command sending
 * @author lexu1, wwang84
 *
 */
public class CommandReceiver implements Runnable{

	private Coordinator owner;
	private int port;
	
	public CommandReceiver(int port, Coordinator owner){
		this.port=port;
		this.owner=owner;
	}
	@Override
	public void run() {
		if(this.port>65535){
			Logger.logCommandError("port out of range");
			return;
		}
		try {
			ServerSocket socket = new ServerSocket(this.port, 10);
			while(true){
				Logger.logCommandComm("Waiting for connection in port "+ this.port+" ...");
				try {
					Socket connection;
					connection=socket.accept();
					CommandReceiverWorker worker= new CommandReceiverWorker(connection, this.owner);
					Logger.logCommandComm("Connection received from " + connection.getInetAddress().getHostName());
					new Thread(worker).start();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	


}

/**
 * CommandReceiverWorker
 * CommandReceiver worker thread
 * @author lexu1, wwang84
 *
 */
class CommandReceiverWorker implements Runnable{
	
	Socket connection;
	Coordinator owner;
	
	public CommandReceiverWorker(Socket connection, Coordinator owner){
		this.connection=connection;
		this.owner=owner;
	}

	/**
	 * thread execution function
	 */
	@Override
	public void run() {
		Logger.logCommandComm("Command Receiver worker started....");
		try {
			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			Object res = null;
			Object obj=in.readObject();
			if(obj instanceof String[]){
				String[] command = (String[])obj;
				for(int i=0;i<command.length;i++){
					Logger.logCommandComm(command[i]);
				}
				
				if(command[0].equals("insert")||command[0].equals("update")){
					res=this.owner.executeCommand(command[0], Integer.parseInt(command[1]), command[2]);
				}
				else{
					res=this.owner.executeCommand(command[0], Integer.parseInt(command[1]), null);
				}
			}
			else if(obj instanceof List){
				List<String[]> list = (List<String[]>)obj;
				res = new ArrayList<Object>();
				for(int i = 0;i< list.size();i++){
					String[] command = list.get(i);
					if(command[0].equals("insert")||command[0].equals("update")){
						((List) res).add(this.owner.executeCommand(command[0], Integer.parseInt(command[1]), command[2]));
					}
					else{
						((List) res).add(this.owner.executeCommand(command[0], Integer.parseInt(command[1]), null));
					}
				}
			}
			else{
				
			}
			out.writeObject(res);
			out.flush();
			in.close();
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}