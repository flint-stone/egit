package edu.uiuc.cs.cs425.myKV.CommandClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.Command;
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
				command = br.readLine();
				handleCommand(command, ipAddress, port);
			}
		}
		else{ 
			if(args[2].equals("-b")){
				handleBatchCommand(args[3],ipAddress, port);
			}
			else {
				File file = new File(args[2]);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                	handleCommand(line, ipAddress, port);
                }
                br.close();
			}
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
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		List<Command> commandList = new ArrayList<Command>();
		
		while ((line = bufferedReader.readLine()) != null) {
			Command command = CommandParser.parse(line);
			if(command!=null){
				commandList.add(command);
			}
		}
		CommandSender.send(commandList, ipAddress, port);
		bufferedReader.close();
		
	}

	private static void handleCommand(String commandLine,String ip, int port) {
		Command command=CommandParser.parse(commandLine);
		if(command!=null){
			System.out.println("hashed key:"+ command.getKey().hashCode()%1000000);
			CommandSender.send(command, ip, port);
		}
		else{
			System.out.println("Wrong command");
		}
	}

	

}
