package edu.uiuc.cs.cs425.myKV.CommandClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.TCP.CommandSender;
/**
 * CommandClient class
 * Client end of the project
 * @author lexu1, wwang84
 *
 */
public class CommandClient {
	
	public static void main(String[] args) throws IOException{
		if (args.length < 2) {
			System.out.println("Please run program with IP address and port.");
			return;
		}
		String ipAddress = args[0];
		int port = 54446;
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out
					.println("Wrong format for port, please input a number between 0 and 65535");
		}
		if (port < 0 || port > 65535) {
			System.out.println("Wrong format for port, please input a number between 0 and 65535");
		}
		
		if(args.length <3){
			String command = "";
			while (true) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					try {
						command = br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					handleCommand(command, ipAddress, port);
			}
		}
		else{
			handleBatchCommand(args[2],ipAddress, port);
		}

	}

	/**
	 * Batch command processing
	 * @param fileName
	 * @param ipAddress
	 * @param port
	 * @throws IOException
	 */
	private static void handleBatchCommand(String fileName, String ipAddress,
			int port) throws IOException {
		FileReader fileReader = new FileReader(fileName);
		List<NodeID> list = new ArrayList<NodeID>();
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		NodeID id = null;
		List<String[]> commandList = new ArrayList<String[]>();
		
		while ((line = bufferedReader.readLine()) != null) {
			String[] commandArray = CommandParser.parse(line);
			if(commandArray!=null){
				commandList.add(commandArray);
			}
		}
		CommandSender.send(commandList, ipAddress, port);
		
	}

	private static void handleCommand(String command,String ip, int port) {
		String[] commandArray=CommandParser.parse(command);
		if(commandArray!=null){
			CommandSender.send(commandArray, ip, port);
		}
		else{
			System.out.println("Wrong command");
		}
	}

	

}
