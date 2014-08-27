package edu.uiuc.cs.cs425.myKV;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.gossip.Membership.FailureDetector;
import edu.uiuc.cs.cs425.gossip.Membership.Membership;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipID;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipList;
import edu.uiuc.cs.cs425.myKV.TCP.CommandReceiver;
import edu.uiuc.cs.cs425.myKV.TCP.QueryReceiver;
import edu.uiuc.cs.cs425.myKV.TCP.QuerySender;
/**
 * Coordinator class, used for deciding if commands are executed locally 
 * or forwarded to other servers
 * @author lexu1, wwang84
 *
 */
public class Coordinator implements Observer{

	private KVstorage storage;
	private NodeID id;
	private Locator locator;
	private QuerySender sender;
	private QueryReceiver receiver;
	private FailureDetector detector;

	public Coordinator(String ip, int gossipPort, int kvServerPort, int commandServerPort, List<NodeID> contactServer, String filename) throws UnknownHostException {
		int range = 1000000;//one million
		this.id=new NodeID(ip,gossipPort);
		this.storage = new KVstorage();
		this.locator=new HashLocator(range);
		this.locator.addNode(this.id);
		
		initialGossip(ip,gossipPort,contactServer, filename);
		//listen to other server's request
		this.receiver= new QueryReceiver(this.id.getPort(),this.storage);
		new Thread(receiver).start();
		//ready to forward queries to other sender
		this.sender = new QuerySender();
		
		CommandReceiver commandReceiver = new CommandReceiver(commandServerPort,this);
		new Thread(commandReceiver).start();
	}
	/**
	 * Initial the gossip - failure detector protocol
	 * @param ip
	 * @param port
	 * @param contactServer
	 * @param filename
	 * @throws UnknownHostException
	 */
	public void initialGossip(String ip, int port, List<NodeID> contactServer, String filename) throws UnknownHostException{
		long thresh = 100000;
		MembershipID selfID = new MembershipID(new Timestamp(new Date().getTime()),ip,port);
		Membership self=new Membership(0);
		boolean selfContact=false;
		if(contactServer.get(0).getIp().equals(InetAddress.getLocalHost().getHostAddress())){
			selfContact=true;
		}
		MembershipList list = new MembershipList(thresh,selfID,self,filename,selfContact,contactServer );
		list.addObserver(this);
		int infectNode=2;
		int msgLossRate=0;
		int interval=500;
		
		this.detector=new FailureDetector(list,selfID, contactServer,infectNode,msgLossRate, interval);
		new Thread(detector).start();
	}

	/**
	 * This method is invoked by CommandClient with command: insert update delete lookup
	 * it is also invoked by Local command line with command: join leave show showmember
	 * @param command
	 * @param key
	 * @param value
	 * @return
	 */
	public Object executeCommand(String command, int key, String value){
		if(command.equals("insert") || command.equals("update") || command.equals("delete")|| command.equals("lookup")){
			// execute in the local node
			NodeID destination = locator.locateKey(key);
			if(destination.equals(this.id)){
				Object res = null;
				if(command.equals("insert")){
					res= storage.insert(key, value);
				}
				else if(command.equals("update")){
					res= storage.update(key, value);
				}
				else if(command.equals("delete")){
					res= storage.delete(key);
				}
				else if(command.equals("lookup")){
					res= storage.lookup(key);
				}
				else{
				}
				String response = "";
				if(res==null){
					response = this.id.getIp()+"@"+this.id.getPort()+": "+"result is null";
				}
				else if(res instanceof Exception){
					response =  this.id.getIp()+"@"+this.id.getPort()+": "+((Exception)res).getMessage();
				}
				else if(res instanceof Boolean && (Boolean)res==false){
					response =  this.id.getIp()+"@"+this.id.getPort()+ ": operation fails";
				}
				else if(res instanceof Boolean && (Boolean)res==true){
					response =  this.id.getIp()+"@"+this.id.getPort()+ ": operation succeeds";
				}
				else{
					response =this.id.getIp()+"@"+this.id.getPort()+ ": "+res.toString();
				}
				return response;
						
			}
			// execute in remote node
			else{
				String[] args= new String[3];
				args[0]=command;
				args[1]=String.valueOf(key);
				args[2]=value;
				//call sender to execute command remotely
				
				return sender.send(args, destination.getIp(), destination.getPort());
				
			}
		}
		else{
			return "There is no such operation. Please check your grammar";
		}
		
	}
	/**
	 * execute local command like show, join and leave
	 * @param command String
	 */
	public Object exeucateLocalCommand(String command){
		if(command.equals("show")){
			return this.storage.getKVmap();
		}
		else if(command.equals("showcount")){
			return String.valueOf(this.storage.size());
		}
		else if(command.equals("showmember")){
			return this.detector.getMessage();
		}
		else if(command.equals("showring")){
			this.locator.printMap();
			return null;
		}
		else if(command.equals("join")){
			this.join();
			return null;
		}
		else if(command.equals("leave")){
			this.selfLeave();
			return null;
		}
		else{
			
		}
		return null;
	}

	/**
	 * update whenever the membership list is changed or there are some data is coming to this node
	 * 
	 * @@param arg = String[] String[0]="join"/"leave" String[1] = ip String[2] = port
	 */
	@Override
	public void update(Observable arg0, Object arg) {
		if (arg instanceof String[]) {
            String[] event = (String[]) arg;
            if(event[0].equals("join")){
            	NodeID newNode= new NodeID(event[1],Integer.parseInt(event[2]));
            	int[] range = locator.addNode(newNode);
            	for(int i=0;i<range.length;i+=2){
            		int[] array={range[i],range[i+1]};
            		Map<Long, Object> movedData = storage.migrate(array);
                	if(movedData != null){
                		
                		sender.send(movedData, newNode.getIp(), newNode.getPort(),this.storage);
						
                	}
            	}	
            	Logger.logKvDebug("Node gets update: somebody is joining" );
            }
            else if(event[0].equals("leave")){
            	NodeID leaveNode= new NodeID(event[1],Integer.parseInt(event[2]));
            	locator.deleteNode(leaveNode);
            	Logger.logKvDebug("Node gets update: somebody is leaving" );
            }
        }
	}
	/**
	 * The method is called when this node is leaving the group.
	 * It will sends the key-value pairs stored in itself to its neighbors
	 */
	private void selfLeave() {
		this.detector.leaveGroup();
		String[] target = locator.leave(this.id);
		for(int i=0;i<target.length;i+=4){
			String ip = target[i];
			int port=Integer.parseInt(target[i+1]);
			int[] range = {Integer.parseInt(target[i+2]),Integer.parseInt(target[i+3])};
			Map<Long, Object> movedData = storage.migrate(range);
        	if(movedData != null){
        		sender.send(movedData, ip, port,this.storage);
				
        	}
        	this.locator.clean();
		}
		
	}
	
	private void join() {
		this.detector.join();
	}


}
