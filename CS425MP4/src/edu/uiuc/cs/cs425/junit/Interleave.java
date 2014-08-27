package edu.uiuc.cs.cs425.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Interleave {
	
	public static void main(String[] args) throws IOException{
		File updatefile = new File("updateBatchIncreasing.bh");
		File readfile = new File("lookupBatchOne.bh");
        BufferedReader br1 = new BufferedReader(new FileReader(updatefile));
        BufferedReader br2 = new BufferedReader(new FileReader(readfile));
        
        PrintWriter writer = new PrintWriter("updateAndRead.bh", "UTF-8");
        String line1;
        String line2;
        while ((line1 = br1.readLine()) != null &&(line2 = br2.readLine()) != null) {
        	writer.println(line1);
        	writer.println(line2);
        }
        writer.close();
        br1.close();
        br2.close();
	}

}
