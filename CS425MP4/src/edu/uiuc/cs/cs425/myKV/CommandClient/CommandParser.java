package edu.uiuc.cs.cs425.myKV.CommandClient;

import java.sql.Timestamp;
import java.util.Date;

import edu.uiuc.cs.cs425.myKV.Command;
import edu.uiuc.cs.cs425.myKV.Record;
import edu.uiuc.cs.cs425.myKV.ReplicationManager;

/**
 * CommandParser class
 * Parse Command to each server's format
 * @author wwang84, lexu1
 *
 */
public class CommandParser {
	
	public static Command parse(String command) {
		String[] commandArray=command.split("\\s");
		Command result = new Command();
		Timestamp ts = new Timestamp(new Date().getTime());
		
		if(commandArray[0].startsWith("show")){
			result.setCommand(commandArray[0].trim());
		}
		else if(commandArray[0].equals("delete")|| commandArray[0].equals("lookup")){
			result.setCommand(commandArray[0].trim());
			result.setKey(commandArray[1].trim());
			result.setValue(new Record<Object>(ts,null));
			result.setConsistentLevel(parseConsistencyLevel(commandArray[commandArray.length-1].trim()));
		}
		else if(commandArray[0].equals("insert") || commandArray[0].equals("update")){
			result.setCommand(commandArray[0].trim());
			result.setKey(commandArray[1].trim());
			result.setValue(new Record<Object>(ts,constructSentence(commandArray)));
			result.setConsistentLevel(parseConsistencyLevel(commandArray[commandArray.length-1].trim()));
			
		}
		else{
			System.out.println("Wrong command. No such command");
			return null;
		}
		return result;
	}
	

	private static int parseConsistencyLevel(String level) {
		if(level.equalsIgnoreCase("one")){
			return ReplicationManager.ONE;
		} else if (level.equalsIgnoreCase("quorum")){
			return ReplicationManager.QUORUM;
		} else if (level.equalsIgnoreCase("all")){
			return ReplicationManager.ALL;
		}
		 return ReplicationManager.ALL;
	}


	private static String constructSentence(String[] commandArray) {
		String sentence ="";
		String tempSen = "";
		boolean hasQuote=false;
		for(int i=2;i<commandArray.length;i++){
			if(commandArray[i].startsWith("\"")){
				hasQuote = true;
				if(commandArray[i].endsWith("\"")){
					return commandArray[i].substring(1,commandArray[i].length()-1);
				}
				else{
					sentence+=commandArray[i].substring(1,commandArray[i].length());
				}
			}
			else if(commandArray[i].endsWith("\"")){
				tempSen+=" "+commandArray[i].substring(0,commandArray[i].length()-1);
				sentence+=tempSen;
				return sentence;
			}
			else{
				if(!hasQuote){
					return commandArray[i];
				}
				else{
					tempSen+=" "+commandArray[i];
				}
			}
		}
		System.out.println("wrong command input. Watch out \" usage");
		return null;
	}

}
