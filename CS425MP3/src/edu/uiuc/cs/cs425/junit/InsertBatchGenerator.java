package edu.uiuc.cs.cs425.junit;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class InsertBatchGenerator {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		String fileName = "insertBatch1.bh";
		String fileName1 = "deleteBatch1.bh";
		String fileName2 = "lookupBatch1.bh";
		String[] words = {" \"hello world!\""," Nihao"," 123456789", " \"CS425 rocks! We love MP!\""," \"Everybody gets A!\""};
		int lineCount = 1000;
		int range = 1000000;
		Random random = new Random(System.currentTimeMillis());
		PrintWriter writer1 = new PrintWriter(fileName, "UTF-8");
		PrintWriter writer2 = new PrintWriter(fileName1, "UTF-8");
		PrintWriter writer3 = new PrintWriter(fileName2, "UTF-8");
		for(int i=0;i<lineCount;i++){
			int ran = random.nextInt(range);
			String line1 = "insert ";
			line1 += ran;
			line1 += words[random.nextInt(words.length)];
			writer1.println(line1);
			
			String line2 = "delete ";
			line2 += ran;
			line2 += words[random.nextInt(words.length)];
			writer2.println(line2);
			
			String line3 = "lookup ";
			line3 += random.nextInt(range);
			line3 += words[random.nextInt(words.length)];
			writer3.println(line3);
			
		}
		writer3.close();
		writer2.close();
		writer1.close();
	}
}
