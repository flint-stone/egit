package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.Command;
import edu.uiuc.cs.cs425.myKV.DataBlock;

/**
 * QuerySender class
 * TCP connection between servers
 * @author lexu1, wwang84
 *
 */
public class QuerySender {
	
	public Object send(Command command, NodeID nodeID){
		
		QuerySenderWorker worker=new QuerySenderWorker(command, nodeID.getIp(), nodeID.getPort());
		try {
			return worker.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object sendDataBlock(DataBlock dataBlock, NodeID nodeID) throws Exception {
		QuerySenderWorker worker=new QuerySenderWorker(dataBlock, nodeID.getIp(), nodeID.getPort());
		return worker.call();
	}

}
/**
 * QuerySender worker thread
 * @author lexu1, wwang84
 *
 */
class QuerySenderWorker implements Callable{
	private Command command;
	private DataBlock block;
	private String ip;
	private int port;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public QuerySenderWorker(Command command, String ip, int port){
		this.command=command;
		this.ip=ip;
		this.port=port;
		this.block=null;
	}
	public QuerySenderWorker(DataBlock block, String ip, int port) {
		this.block=block;
		this.ip=ip;
		this.port=port;
		this.command=null;
	}

	/**
	 * main execution thread context
	 */
	@Override
	public Object call() {
		try {
			this.socket=new Socket(this.ip, this.port);
			Logger.logKvComm("QuerySender connected to " + this.ip + " in port " + this.port);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.out.flush();
			if(this.command!=null){
				this.out.writeObject(this.command);
				this.out.flush();
				this.in = new ObjectInputStream(this.socket.getInputStream());
				Object res=null;
				res = in.readObject();
				this.in.close();
				this.out.close();
				Logger.logKvDebug(res.toString());
				//System.out.println(res.toString());
				return res;
			}	
			else{
				this.out.writeObject(this.block);
				this.out.flush();
				this.in = new ObjectInputStream(this.socket.getInputStream());
				Object res=null;
				res = in.readObject();
				if(res instanceof Boolean){
					Boolean b=(Boolean)res;
					return b;
				}
				this.in.close();
				this.out.close();
				this.socket.close();
				return null;
			}
			
		} catch (UnknownHostException e) {
			Logger.logKvCommError(this.getClass().getCanonicalName()+" "+e.getMessage());
		} catch (IOException e) {
			Logger.logKvCommError(this.getClass().getCanonicalName()+" "+e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.logKvCommError(this.getClass().getCanonicalName()+" "+e.getMessage());
		}		
		return null;
	}
}
