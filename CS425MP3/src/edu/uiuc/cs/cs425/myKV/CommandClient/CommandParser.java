package edu.uiuc.cs.cs425.myKV.CommandClient;

/**
 * CommandParser class
 * Parse Command to each server's format
 * @author wwang84, lexu1
 *
 */
public class CommandParser {
	
	public static String[] parse(String command) {
		String[] commandArray=command.split("\\s");
		String[] result = new String[3];
		
		if(commandArray[0].startsWith("show")){
			result[0]=commandArray[0];
		}
		else if(commandArray[0].equals("delete")|| commandArray[0].equals("lookup")){
			result[0]=commandArray[0];
			result[1]=commandArray[1];
		}
		else if(commandArray[0].equals("insert") || commandArray[0].equals("update")){
			result[0]=commandArray[0];
			result[1]=commandArray[1];
			result[2]=constructSentence(commandArray);
		}
		else{
			System.out.println("Wrong command. No such command");
			return null;
		}
		return result;
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
