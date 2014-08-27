package edu.uiuc.cs.cs425.unittest;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Log unit test for test generator
 * @author lexu1
 *
 */
public class LogTest {
	String filename;
	Scanner input;
	
	@Before
	public void setUp(){
		return;
	}
	
	@After
	public void tearDown(){
		input.close();
	}
	@Test
	public void testHelloS() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.1.log"));
		int count= 0;
		String test=null;
		while(input.hasNextLine()){
			test=input.nextLine();
			if(test.contains("Hello!")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 1497);
	}
	
	@Test
	public void testHelloM() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.2.log"));
		int count= 0;
		String test=null;
		while(input.hasNextLine()){
			test=input.nextLine();
			if(test.contains("Hello!")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 0);
	}
	@Test
	public void testHaveAGoodDayS() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.1.log"));
		int count= 0;
		String test=null;
		while(input.hasNextLine()){
			test=input.nextLine();
			if(test.contains("Have a good day")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 0);
	}
	
	@Test
	public void testHaveAGoodDayM() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.2.log"));
		int count= 0;
		String test=null;
		while(input.hasNextLine()){
			test=input.nextLine();
			if(test.contains("Have a good day")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 0);
	}
	
	@Test
	public void testSOSS() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.1.log"));
		int count= 0;
		String test=null;
		while(input.hasNextLine()){
			test=input.nextLine();
			if(test.contains("SOS")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 5);
	}
	
	@Test
	public void testSOSM() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.2.log"));
		int count= 0;
		String test=null;
		while(input.hasNextLine()){
			test=input.nextLine();
			if(test.contains("SOS")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 5 );
	}

	@Test
	public void testPleaseS() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.1.log"));
		int count= 0;
		String test=null;
		int total=0;
		while(input.hasNextLine()){
			total++;
			test=input.nextLine();
			if(test.contains("Please")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 144117 );
	}
	
	@Test
	public void testPleaseM() throws FileNotFoundException {
		//fail("Not yet implemented");
		input = new Scanner(new File("10M.2.log"));
		int count= 0;
		String test=null;
		int total=0;
		while(input.hasNextLine()){
			total++;
			test=input.nextLine();
			if(test.contains("Please")){
				count++;
			}
		}
		System.out.println(count);
		assertEquals(count, 144117);
	}
}
