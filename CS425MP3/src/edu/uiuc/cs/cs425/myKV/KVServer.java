package edu.uiuc.cs.cs425.myKV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.gossip.Message;
import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.gossip.Membership.Membership;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipID;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipList;
/**
 * KVServer class
 * Server main entrance
 * @author wwang84, lexu1
 *
 */
public class KVServer {

	/**
	 * Server initialization
	 * @param args
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		if (args.length < 2) {
			Logger.logKvInfo("Please run the program like: Main server.config GossiptPort ServerPort");
			return;
		}

		int gossipPort = 54444;
		int kvServerPort = 54445;
		int commandServerPort = 54446;
		try {
			if(args.length>1){
				gossipPort = Integer.parseInt(args[1]);
			}
			if(args.length>2){
				kvServerPort = Integer.parseInt(args[2]);
			}
			if(args.length>3){
				commandServerPort = Integer.parseInt(args[3]);
			}	
		} catch (NumberFormatException e) {
			Logger.logKvInfo("Wrong format for port, please input a number between 0 and 65535");
		}
		if (gossipPort < 0 || gossipPort > 65535 || kvServerPort < 0 || kvServerPort > 65535) {
			Logger.logKvInfo("Wrong format for port, please input a number between 0 and 65535");
		}
		List<NodeID> contactServer = loadContactServer(args[0]);
		
		Coordinator localNode = new Coordinator(InetAddress.getLocalHost().getHostAddress(), gossipPort, kvServerPort,commandServerPort, contactServer,args[0]);
		
		String command = "";
		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				try {
					command = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				handleCommand(localNode, command);
		}
	}

	
	private static List<NodeID> loadContactServer(String fileName) throws NumberFormatException, IOException {
		FileReader fileReader = new FileReader(fileName);
		List<NodeID> list = new ArrayList<NodeID>();
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		NodeID id = null;
		while ((line = bufferedReader.readLine()) != null) {
			String[] content = line.split("\\s");
			String ip = "", port = "";
			for (int i = 0; i < content.length; i++) {
				switch (i) {
				case 0:
					break;
				case 1:
					ip = content[i];
					break;
				case 2:
					port = content[i];
					break;
				default:
					break;
				}
			}
			id = new NodeID( ip, Integer.parseInt(port));
			list.add(id);
		}
		bufferedReader.close();
		
		return list;
	}

	/**
	 * 
	 * @param localNode
	 * @param command that only can be executed locally: show showmember join leave
	 */
	private static void handleCommand(Coordinator localNode, String command){
		if (command.startsWith("show")) {
			Object result = localNode.exeucateLocalCommand(command);
			if(result instanceof Map){
				Map<Integer,Object> map = (Map<Integer,Object>)result;
				List<Integer> keys = new ArrayList<Integer>(map.keySet());
				for(int i=0;i<keys.size();i++){
					Logger.logKvInfo(keys.get(i)+" "+map.get(keys.get(i)).toString());
				}
				
			}
			if(result instanceof String){
				Logger.logKvInfo(result.toString());
			}
			else if(result instanceof Message){
				MembershipList list = (MembershipList) result;
				Set<MembershipID> remoteKeySet=list.getMap().keySet();
				Iterator<MembershipID> it=remoteKeySet.iterator();
				while(it.hasNext()){
					MembershipID remoteID=it.next();
					Membership member = list.getMap().get(remoteID);	
					Logger.logKvInfo("Local membership is "+remoteID.getIp()+" "+member.getHeartbeat_count()+" "+member.getState());
					
				}
			}
			
		}else if(command.equals("join")||command.equals("leave")){
			localNode.exeucateLocalCommand(command);
		}
		else{
			Logger.logKvInfo("Please indicate join or leave");
		}
		
	}

}