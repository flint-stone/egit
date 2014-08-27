package edu.uiuc.cs.cs425.junit;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class InsertBatchGenerator {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
		String insertName = "insertBatch.bh";
		
		String updateOneName = "updateBatchOne.bh";
		String updateQuorumName = "updateBatchQuorum.bh";
		String updateAllName = "updateBatchAll.bh";
		String updateIncreasingName = "updateBatchIncreasing.bh";
		
		String lookupOneName = "lookupBatchOne.bh";
		String lookupQuorumName = "lookupBatchQuorum.bh";
		String lookupAllName = "lookupBatchAll.bh";
		
		String[] words = {"\"hello world!\"","Nihao","123456789", "\"CS425 rocks! We love MP!\"","\"Everybody gets A!\""};
		int lineCount = 1000;
		int range = 1000000;
		
		Random random = new Random(System.currentTimeMillis());
		
		PrintWriter writer1 = new PrintWriter(insertName, "UTF-8");
		PrintWriter writer2 = new PrintWriter(updateOneName, "UTF-8");
		PrintWriter writer3 = new PrintWriter(updateQuorumName, "UTF-8");
		PrintWriter writer4 = new PrintWriter(updateAllName, "UTF-8");
		PrintWriter writer5 = new PrintWriter(updateIncreasingName, "UTF-8");
		PrintWriter writer6 = new PrintWriter(lookupOneName, "UTF-8");
		PrintWriter writer7 = new PrintWriter(lookupQuorumName, "UTF-8");
		PrintWriter writer8 = new PrintWriter(lookupAllName, "UTF-8");
		
		for(int i=0;i<lineCount;i++){
			int ran = random.nextInt(range);
			
			writer1.println(getLine("insert",ran,words[random.nextInt(words.length)],"one"));
			
			writer2.println(getLine("update",ran,words[random.nextInt(words.length)],"one"));
			writer3.println(getLine("update",ran,words[random.nextInt(words.length)],"quorum"));
			writer4.println(getLine("update",ran,words[random.nextInt(words.length)],"all"));
			writer5.println(getLine("update",ran,String.valueOf(i),"one"));
			
			writer6.println(getLine("lookup",ran,words[random.nextInt(words.length)],"one"));
			writer7.println(getLine("lookup",ran,words[random.nextInt(words.length)],"quorum"));
			writer8.println(getLine("lookup",ran,words[random.nextInt(words.length)],"all"));
			
			
		}
		writer8.close();
		writer7.close();
		writer6.close();
		writer5.close();
		writer4.close();
		writer3.close();
		writer2.close();
		writer1.close();
	}
	
	private static String getLine(String operation, int key, String value, String level){
		return operation+" "+String.valueOf(key)+" "+value+" "+level;
	}
}
