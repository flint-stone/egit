package edu.uiuc.cs.cs425.fd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uiuc.cs.cs425.fd.Membership.Membership;
import edu.uiuc.cs.cs425.fd.Membership.MembershipID;

/**
 * Main class
 * Program entrance
 * @author wwang84, lexu1
 *
 */
public class Main {

	public static List<MembershipID> contactServer;

	public static MembershipID self;

	public static FailureDetector fd;

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
		self = new MembershipID(new Timestamp(new Date().getTime()),
				InetAddress.getLocalHost().getHostAddress(), selfPort);

		int sendNodeNum=1;
		int lossRate=0;
		int failThreshold = 3000;
		int heartBeatInterval = 200;
		fd = new FailureDetector(self, contactServer, sendNodeNum, lossRate, heartBeatInterval, failThreshold);
		fd.start();
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
	private static  List<MembershipID> LoadServer(String fileName)
			throws NumberFormatException, IOException {
		FileReader fileReader = new FileReader(fileName);
		List<MembershipID> list = new ArrayList<MembershipID>();
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		MembershipID id = null;
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
			fd.joinGroup();
		} else if (command.equals("leave")) {
			fd.leaveGroup();
		} else if (command.equals("rejoin")) {
			fd.rejoinGroup();
		} else {
			System.out.println("Please indicate join or leave");
		}
	}

}
