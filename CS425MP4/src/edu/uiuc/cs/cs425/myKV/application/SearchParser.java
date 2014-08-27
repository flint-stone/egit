package edu.uiuc.cs.cs425.myKV.application;

import edu.uiuc.cs.cs425.myKV.Command;

public class SearchParser {
	public static Command parse(String command) {
		String[] commandArray=command.split("\\s");
		Command result = new Command();
		
		if(commandArray[0].startsWith("search")){
			result.setCommand("lookup");
			result.setKey(commandArray[1]);
		}
		else{
			System.out.println("Wrong command. No such command");
			return null;
		}
		return result;
	}
}
