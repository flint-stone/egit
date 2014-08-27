package edu.uiuc.cs.cs425.junit;


import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.HashLocator;

public class HashLocatorTest {

	public static void main(String[] args) {
		int range = 1000000;
		
		String ip1="172.16.194.245";
		String ip2="192.17.11.7";
		String ip3="192.17.11.8";
		String ip4="172.16.133.35";
		
		int port1=54445;
		int port2=54445;
		int port3=54445;
		int port4=54445;
		
		NodeID node1 = new NodeID(ip1,port1);
		NodeID node2 = new NodeID(ip2,port2);
		NodeID node3 = new NodeID(ip3,port3);
		NodeID node4 = new NodeID(ip4,port4);
		
		HashLocator locator = new HashLocator(range);
		int[] migrateRange= new int[4];
		
		
		migrateRange = locator.addNode(node1);
		locator.printMap();
		printRange(migrateRange);

		migrateRange = locator.addNode(node2);
		locator.printMap();
		printRange(migrateRange);
		
		migrateRange = locator.addNode(node3);
		locator.printMap();
		printRange(migrateRange);
	
		migrateRange = locator.addNode(node4);
		locator.printMap();
		printRange(migrateRange);
		
		String[] array = locator.leave(node3);
		printStringArray(array);
	
		System.out.println(locator.locateKey(500000).getIp());
	}
	
	public static void printRange(int[] migrateRange){
		for(int i=0;i<migrateRange.length;i++){
			System.out.print(migrateRange[i]+" ");
		}
		System.out.println();
	}
	
	public static void printStringArray(String[] args){
		for(int i=0;i<args.length;i++){
			System.out.print(args[i]+" ");
		}
		System.out.println();
	}

}
