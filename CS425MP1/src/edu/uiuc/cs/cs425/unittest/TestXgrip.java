package edu.uiuc.cs.cs425.unittest;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.uiuc.cs.cs425.CommandParser;

/**
 * Test Xgrip parse function
 * @author lexu1
 *
 */
public class TestXgrip {

	String command;
	String path;
	
	/**
	 * set up function: set up testing command and path
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		command="";
		path="/logs/error";
	}
	
	/**
	 * tear down function: tear down testing path
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		path="";
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores garbage input
	 */
	@Test
	public void testGarbage() {
		//fail("Not yet implemented");
		command="this is garbage and it is supposed to be ignored";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test function's response to regular key value pair request
	 */
	@Test
	public void testRegKVpair() {
		command="Xgrep -key testkey -value testvalue";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		String[] assumption = {"grep", "testkey.*:.*testvalue",""};
		assertArrayEquals(ret, assumption);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test function's response to key-only request
	 */
	@Test
	public void testKonly() {
		command="Xgrep -key testkey";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		String[] assumption = {"grep", "testkey.*:",""};
		assertArrayEquals(ret, assumption);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test function's response to key-only request
	 */
	@Test
	public void testVonly() {
		command="Xgrep -key testkey";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		String[] assumption = {"grep","testkey.*:",""};
		assertArrayEquals(ret, assumption);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores key option with no input
	 */
	@Test
	public void testKonlyNoInput() {
		//fail("Not yet implemented");
		command="Xgrep -key ";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores value option with no input
	 */
	@Test
	public void testVonlyNoInput() {
		command="Xgrep -value ";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores key-value option with no input
	 */
	@Test
	public void testKVNoInput() {
		command="Xgrep -key -value ";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test function's response to regular key-value and count
	 */
	@Test
	public void testKVC() {
		command="Xgrep -key testkey -value testvalue -c";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		String[] assumption = {"grep", "testkey.*:.*testvalue","", "-c"};
		assertArrayEquals(ret, assumption);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test function's response to regular key-count-value pattern
	 */
	@Test
	public void testKCV() {
		command="Xgrep -key testkey -c -value testvalue";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		String[] assumption = {"grep","testkey.*:.*testvalue","","-c"};
		assertArrayEquals(ret, assumption);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores key-count-value pattern with no input
	 */
	@Test
	public void testKCVInput() {
		command="Xgrep -key -c -value";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
	
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores count option with no input
	 */
	@Test
	public void testConly() {
		command="Xgrep -c";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}

	/**
	 * Test on parseXgrepToGrep
	 * test function's response to regular key-value pattern with quote
	 */
	@Test
	public void testQuote() {
		command="Xgrep -key 'testkey1 testkey2 testkey3' -c -value testvalue";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		String[] assumption = {"grep","testkey1 testkey2 testkey3.*:.*testvalue","","-c"};
		assertArrayEquals(ret, assumption);
	}
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores key-value pattern with only one quote
	 */
	@Test
	public void testHalfQuote1() {
		command="Xgrep -key \"testkey1 testkey2 testkey3 -c -value testvalue";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
	/**
	 * Test on parseXgrepToGrep
	 * test if function ignores key-value pattern with only one quote
	 */
	@Test
	public void testHalfQuote2() {
		command="Xgrep -key testkey1 testkey2 testkey3 -c -value testvalue";
		String[] ret = CommandParser.parseXgrepToGrep(command);
		assertNull(ret);
	}
}
