package edu.uiuc.cs.cs425.myKV;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

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
	private NodeID selfID;
	private HashLocator locator;
	private QuerySender sender;
	private QueryReceiver receiver;
	private FailureDetector detector;
	private ReplicationManager rm;

	public Coordinator(String ip, int gossipPort, int commandServerPort, List<NodeID> contactServer, String filename) throws UnknownHostException {
		int range = 1000000;//one million
		this.selfID=new NodeID(ip,gossipPort);
		this.storage = new KVstorage();
		this.locator=new HashLocator(range,selfID);
	
		initialGossip(ip,gossipPort,contactServer, filename);

		//listen to other server's request
		this.sender = new QuerySender();
		CommandReceiver commandReceiver = new CommandReceiver(commandServerPort,this);
		new Thread(commandReceiver).start();
		
		this.rm=new ReplicationManager(this.storage,this.sender,this.selfID,this.locator);
		
		this.receiver= new QueryReceiver(this.selfID.getPort(),this.storage,this.rm);
		new Thread(receiver).start();
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
		long thresh = 4000;
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
		int interval=250;
		
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
	public Object execute(Command command){
		String commandline = command.getCommand();
		if(commandline.equals("insert") || commandline.equals("update") || commandline.equals("delete")|| commandline.equals("lookup")){
			// execute in the local node
			NodeID[] replicas=new NodeID[3];
			NodeID destination = locator.locateKey(command.getKey());
			NodeID predecessor = destination.getPredecessor();
			NodeID sucessor = destination.getSuccessor();
			
			replicas[0]=destination;
			replicas[1]=predecessor;
			replicas[2]=sucessor;
			try {
				return new Execute(command,replicas,sender).executeWriteRead();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		else if(commandline.startsWith("hidden")){
			return this.storage.insert(command.getKey(),command.getValue()); 
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
			return this.storage.getKvMap();
		}
		else if(command.equals("showrecentread")){
			return this.storage.getRecentReadOperation();
		}
		else if(command.equals("showrecentwrite")){
			return this.storage.getRecentWriteOperation();
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
		else if (command.equals("showpcount")){
			Map data = this.storage.migrate(locator.getPredecessorRange());
			if(data != null){
				System.out.println(data.size());
			}
			else {
				System.out.println(0);
			}
		}
		else if(command.equals("showscount")){
			Map data = this.storage.migrate(locator.getSuccessorRange());
			if(data != null){
				System.out.println(data.size());
			}
			else {
				System.out.println(0);
			}
		}
		else if(command.equals("showselfcount")){
			Map data = this.storage.migrate(locator.getSelfRange());
			if(data != null){
				System.out.println(data.size());
			}
			else {
				System.out.println(0);
			}
			
		}
		else if(command.equals("clean")){
			this.storage.clean(locator.getPredecessorRange(),locator.getSelfRange(),locator.getSuccessorRange());
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
	 * @param arg = String[] String[0]="join"/"leave" String[1] = ip String[2] = port
	 */
	@Override
	public void update(Observable arg0, Object arg) {
		if (arg instanceof String[]) {
            String[] event = (String[]) arg;
            if(event[0].equals("join") ){
            	NodeID newNode= new NodeID(event[1],Integer.parseInt(event[2]));
            	if(!locator.hasNode(newNode) ){
	            	int[] range = locator.addNode(newNode);
	            	if(selfID.isMyPredecessor(newNode)){
		        		int[] array={range[0],range[1]};
		        		System.out.println(range[0]+" "+range[1]);
		        		rm.sendMyDataToOneNeighbour(array, DataBlock.MIGRATION_ADD, newNode);		
	            	}
            	}	
            	if(selfID.isMyNeighbor(newNode)){
            		rm.sendMyDataToMyNeighbour();
            	}
            	Logger.logKvDebug("Node gets update: somebody is joining" );
            }
            else if(event[0].equals("leave")){
            	System.out.println("Someone is leaving "+event[1]+"@"+event[2]);
            	NodeID leaveNode= new NodeID(event[1],Integer.parseInt(event[2]));
            	boolean myNeighbor = selfID.isMyNeighbor(leaveNode);
            	if(locator.hasNode(leaveNode)){
            		locator.deleteNode(leaveNode);
            	}
            	if(myNeighbor){
            		rm.sendMyDataToMyNeighbour();
            	}
            	// the successor of the leaving node will send its data to its neighbor after receiving the leaving node data
            	Logger.logKvDebug("Node gets update: somebody is leaving" );
            }
            else if(event[0].equals("fail")){
            	NodeID failedNode= new NodeID(event[1],Integer.parseInt(event[2]));
            	boolean myNeighbor = selfID.isMyNeighbor(failedNode);
            	int[] range = {0,locator.range};
            	if(locator.hasNode(failedNode)){
            		locator.deleteNode(failedNode);
            	}
            	if(myNeighbor){
            		rm.sendMyDataToOneNeighbour(range, DataBlock.REPLICATION,selfID.getPredecessor());
            		rm.sendMyDataToOneNeighbour(range, DataBlock.REPLICATION,selfID.getSuccessor());
            		rm.sendMyDataToMyNeighbour();
            	}
            }
        }
	}
	/**
	 * The method is called when this node is leaving the group.
	 * It will sends the key-value pairs stored in itself to its neighbors
	 */
	private void selfLeave() {
		this.detector.leaveGroup();
		String[] target = locator.leave(this.selfID);
		
		String ip = target[2];
		int port=Integer.parseInt(target[3]);
		int[] range = {Integer.parseInt(target[0]),Integer.parseInt(target[1])};
		rm.sendMyDataToOneNeighbour(range, DataBlock.MIGRATION_LEAVE, new NodeID(ip, port));
		this.locator.clean();
	}
	
	private void join() {
		this.detector.join();
	}


}
