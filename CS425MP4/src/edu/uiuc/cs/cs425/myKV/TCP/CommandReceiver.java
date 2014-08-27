package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.myKV.Command;
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
			if(obj instanceof Command){
				Command command = (Command)obj;
				
				Logger.logCommandComm(command.getCommand());
				Logger.logCommandComm(command.getKey().toString());
				//Logger.logCommandComm(command.getValue().getTimeStamp().toString()+" "+command.getValue().getContent().toString());
				Logger.logCommandComm(String.valueOf(command.getConsistentLevel()));
				
				res=this.owner.execute(command);
				
			}
			else if(obj instanceof List){
				List<Command> list = (List<Command>)obj;
				res = new ArrayList<Object>();
				for(int i = 0;i< list.size();i++){
					Command command = list.get(i);
					((List) res).add(this.owner.execute(command));
					Thread.sleep(10);
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}