package edu.uiuc.cs.cs425.gossip;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.Membership.FailureDetector;
import edu.uiuc.cs.cs425.gossip.Membership.Membership;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipID;
import edu.uiuc.cs.cs425.gossip.Membership.MembershipList;

/**
 * Main class for test gossip protocol
 * Program entrance
 * @author wwang84, lexu1
 *
 */
public class Main {

	public static List<NodeID> contactServer;

	public static MembershipID self;

	public static FailureDetector fd;
	
	public static boolean selfContact;

	/**
	 * take argument and default settings
	 * @param args
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NumberFormatException,
			IOException {

		if (args.length < 2) {
			System.out
					.println("Please run the program like: Main server.config localName localPort");
			return;
		}

		int selfPort = 5444;
		try {
			selfPort = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out
					.println("Wrong format for port, please input a number between 0 and 65535");
		}
		if (selfPort < 0 || selfPort > 65535) {
			System.out
					.println("Wrong format for port, please input a number between 0 and 65535");
		}
		// initialize the contact server
		contactServer = LoadServer(args[0]);
		// initialize self server
		self = new MembershipID(new Timestamp(new Date().getTime()), InetAddress.getLocalHost().getHostAddress(), selfPort);
		Membership ms=new Membership(0);
		ms.setSelf(true);
		int sendNodeNum=1;
		int lossRate=0;
		int failThreshold = 3000;
		int heartBeatInterval = 200;
		Message msg=new MembershipList(failThreshold, self, ms, args[0],selfContact,contactServer);
		fd = new FailureDetector(msg, self, contactServer, sendNodeNum, lossRate, heartBeatInterval);
		//fd.start();
		new Thread(fd).start();

		String command = "";
		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			try {
				command = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			handleCommand(fd, command);
		}
	}

	/**
	 * load server using config file
	 * @param fileName
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static  List<NodeID> LoadServer(String fileName)
			throws NumberFormatException, IOException {
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
					if(ip.equals(InetAddress.getLocalHost().getHostAddress())){
						selfContact=true;
					}	
					break;
				case 2:
					port = content[i];
					break;
				default:
					break;
				}
			}
			id = new MembershipID(null, ip, Integer.parseInt(port));
			list.add(id);
		}
		bufferedReader.close();
		
		return list;
	}

	/**
	 * listening to user's command
	 * @param fd
	 * @param command
	 */
	private static void handleCommand(FailureDetector fd, String command) {
		if (command.equals("join")) {
			fd.join();
		} else if (command.equals("leave")) {
			fd.leaveGroup();
		} else {
			System.out.println("Please indicate join or leave");
		}
	}

}
