package edu.uiuc.cs.cs425.junit;

import org.junit.Assert;
import org.junit.Test;

import edu.uiuc.cs.cs425.myKV.CommandClient.CommandParser;

public class CommandInputTest {
	
	public void setUp(){
		
	}

	@Test
	public void testShow() {
		String command = "show";
		String[] array={"show",null,null};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}

	@Test
	public void testShowMembership() {
		String command = "showMembership";
		String[] array={"showMembership",null,null};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testLookup() {
		String command = "lookup 1000";
		String[] array={"lookup","1000",null};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testDelete() {
		String command = "delete abc";
		String[] array={"delete","abc",null};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testInsert() {
		String command = "insert 123 abc";
		String[] array={"insert","123","abc"};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testUpdate() {
		String command = "update 932 ass";
		String[] array={"update","932","ass"};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testSentence() {
		String command = "update 123 \"nihao hello world\"";
		String[] array={"update","123","nihao hello world"};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}

	@Test
	public void testWrongSentence() {
		String command = "update 123 \"nihao hello world";
		String[] array={"update","123",null};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testHalfSentence() {
		String command = "update 123 \"nihao hello\" world\"";
		String[] array={"update","123","nihao hello"};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}
	
	@Test
	public void testOneWordSentence() {
		String command = "update 123 \"nihao\"";
		String[] array={"update","123","nihao"};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}

	@Test
	public void testNoQuoteSentence() {
		String command = "update 123 nihao hello world";
		String[] array={"update","123","nihao"};
		Assert.assertArrayEquals(CommandParser.parse(command),array);
	}


	
}
