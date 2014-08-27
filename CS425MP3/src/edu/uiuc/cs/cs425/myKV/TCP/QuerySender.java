package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.myKV.KVstorage;

/**
 * QuerySender class
 * TCP connection between servers
 * @author lexu1, wwang84
 *
 */
public class QuerySender {
	
	public Object send(String[] command, String ip, int port){
		//new Thread(new QuerySenderWorker(command, ip, port)).start();
		/*ExecutorService pool = Executors.newFixedThreadPool(1);
		Callable<Object> worker = new QuerySenderWorker(command, ip, port);
		Future<Object> res=pool.submit(worker);
		return res.get();*/
		QuerySenderWorker worker=new QuerySenderWorker(command, ip, port);
		try {
			return worker.call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Object send(Map<Long, Object> map, String ip, int port, KVstorage store) {
		//new Thread(new QuerySenderWorker(map, ip, port, store)).start();
		/*ExecutorService pool = Executors.newFixedThreadPool(1);
		Callable<Object> worker = new QuerySenderWorker(map, ip, port, store);
		Future<Object> res=pool.submit(worker);
		return res.get();*/
		QuerySenderWorker worker=new QuerySenderWorker(map, ip, port, store);
		try {
			return worker.call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
/**
 * QuerySender worker thread
 * @author lexu1, wwang84
 *
 */
class QuerySenderWorker implements Callable{
	private String[] command;
	private Map<Long, Object> map;
	private String ip;
	private int port;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private KVstorage store;
	
	public QuerySenderWorker(String[] command, String ip, int port){
		this.command=command;
		this.ip=ip;
		this.port=port;
		this.map=null;
	}
	public QuerySenderWorker(Map<Long, Object> map, String ip2, int port2, KVstorage store) {
		this.map=map;
		this.ip=ip2;
		this.port=port2;
		this.command=null;
		this.store=store;
	}

	/**
	 * main execution thread context
	 */
	@Override
	public Object call() throws Exception {
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
				String response = "";
				if(res==null){
					response = this.ip+"@"+this.port+": "+"result is null";
				}
				else if(res instanceof Exception){
					response =  this.ip+"@"+this.port+": "+((Exception)res).getMessage();
				}
				else if(res instanceof Boolean && (Boolean)res==false){
					response =  this.ip+"@"+this.port+ ": operation fails";
				}
				else if(res instanceof Boolean && (Boolean)res==true){
					response =  this.ip+"@"+this.port+ ": operation succeeds";
				}
				else{
					response =this.ip+"@"+this.port+ ": "+res.toString();
				}
				this.in.close();
				this.out.close();
				Logger.logKvDebug(response);
				return response;
			}	
			else{
				this.out.writeObject(this.map);
				this.out.flush();
				this.in = new ObjectInputStream(this.socket.getInputStream());
				Object res=null;
				res = in.readObject();
				if(res instanceof Boolean){
					//TODO delete print
					System.out.println("Migration data response:"+res.toString());
					Boolean b=(Boolean)res;
					if(b){
						this.store.delete(this.map);
					}
				}
				this.in.close();
				this.out.close();
				return null;
			}
			
		} catch (UnknownHostException e) {
			Logger.logKvCommError(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Logger.logKvCommError(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Logger.logKvCommError(e.getMessage());
			e.printStackTrace();
		}		
		return null;
	}
}
