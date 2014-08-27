package edu.uiuc.cs.cs425.myKV.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.Command;
import edu.uiuc.cs.cs425.myKV.TCP.CommandSender;

public class MovieSearch {
	public static void main(String[] args) {
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
			System.out
					.println("Wrong format for port, please input a number between 0 and 65535");
		}

		if (args.length < 3) {
			String command = "";
			while (true) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				try {
					command = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handleCommand(command, ipAddress, port);
			}
		} else {
			if (args[2].equals("-b")) {
				try {
					handleBatchCommand(args[3], ipAddress, port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				File file = new File(args[2]);
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(file));
					String line;
					while ((line = br.readLine()) != null) {
						handleCommand(line, ipAddress, port);
					}
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}

	/**
	 * Batch command processing
	 * 
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
		List<Command> commandList = new ArrayList<Command>();

		while ((line = bufferedReader.readLine()) != null) {
			Command command = SearchParser.parse(line);
			if (command != null) {
				commandList.add(command);
			}
		}
		CommandSender.send(commandList, ipAddress, port);

	}

	private static void handleCommand(String command, String ip, int port) {
		Command commandArray = SearchParser.parse(command);
		if (commandArray != null) {
			CommandSender.send(commandArray, ip, port);
		} else {
			System.out.println("Wrong command");
		}
	}

}
