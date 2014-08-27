package edu.uiuc.cs.cs425.myKV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uiuc.cs.cs425.gossip.NodeID;
/**
 * HashLocator, one implementation of Locator, using hash function and organize servers as a ring like chord
 * @author lexu1, wwang84
 *
 */
public class HashLocator extends Locator {

	private Map<Integer, NodeID> virtualMap=new HashMap<Integer, NodeID>();
	
	public HashLocator(int range) {
		super(range);
	}

	@Override
	public int locateID(NodeID self) {
		String hashstring=self.getIp()+Integer.toString(self.getPort());
		int scratch=hashstring.hashCode();
		return Math.abs(scratch%range);
	}

	private int locateVirtualID(NodeID self){
		int scratch=self.getPort() +self.getIp().hashCode()*10000;
		return Math.abs(scratch%range);
	}
	/**
	 * Add a node/server into the ring
	 * @param node which is added to the ring
	 * @return a integer array which contains the range of keys need to move
	 */
	@Override
	public int[] addNode(NodeID node){
		List<Integer> list = new ArrayList<Integer>();
		int id1=locateID(node);
		int id2=locateVirtualID(node);
		this.virtualMap.put(id1, node);
		this.virtualMap.put(id2, node);
		
		List<Integer> keylist=new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);
		list.addAll(getMoveRange(id1,keylist).toLongList());
		list.addAll(getMoveRange(id2,keylist).toLongList());
		int[] range = new int[4];
		for(int i=0;i<list.size();i++){
			range[i] = list.get(i);
		}
		return range;
	}
	/**
	 * delete a node/server from the ring
	 * @param node which is added to the ring
	 * @return null
	 */
	public void deleteNode(NodeID node){
		int id1=locateID(node);
		int id2=locateVirtualID(node);
		this.virtualMap.remove(id1);
		this.virtualMap.remove(id2);
	}
	/**
	 * locate a key 
	 * @param key
	 * @return a server which holds this key
	 */
	@Override
	public NodeID locateKey(long key) {

		List<Integer> keylist=new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);
		Iterator<Integer> it=keylist.iterator();
		while(it.hasNext()){
			int cur=(Integer) it.next();
			if(cur>=key){
				return this.virtualMap.get(cur);
			}
		}
		return this.virtualMap.get(keylist.get(0));
	}

	@Override
	public void printMap(){
		List<Integer> keylist=new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);
		Iterator<Integer> it=keylist.iterator();
		int mark=0;
		while(it.hasNext()){
			int cur=(Integer) it.next();
			System.out.println("ring#:"+cur+" node:"+this.virtualMap.get(cur).getIp());
			mark=cur;
		}
	}
	/**
	 * 
	 * @param node which is voluntarily leaving
	 * @return a string array in which string[0]=ipadress, string[1]=port, string[3][4]= the range of keys need to move
	 */
	@Override
	public String[] leave(NodeID node) {
		List<String> target = new ArrayList<String>();
		int id1=locateID(node);
		int id2=locateVirtualID(node);
		
		List<Integer> keylist=new ArrayList<Integer>(this.virtualMap.keySet());
		Collections.sort(keylist);
		
		target.addAll(this.getMoveRangeAndTarget(id1, node, keylist).toStringList());
		target.addAll(this.getMoveRangeAndTarget(id2, node, keylist).toStringList());
		
		for(int i=0;i<target.size();i++){
			System.out.print(target.get(i)+" ");
		}
		System.out.println();
		
		return target.toArray(new String[target.size()]);
	}
	
	private Range getMoveRange(int id, List<Integer> keyList){
		Range range = new Range();
		range.setEnd(id);
		Iterator<Integer> it = keyList.iterator();
		while(it.hasNext()){
			int cur=(Integer) it.next();
			if(cur<id){
				range.setBegin(cur);
			}
		}
		if(range.getBegin()==0){
			range.setBegin(keyList.get(keyList.size()-1));
		}
		
		return range;
	}
	
	private Range getMoveRangeAndTarget(int id,NodeID node, List<Integer> keyList){
		Range range = new Range();
		range.setEnd(id);
		Iterator<Integer> it=keyList.iterator();
		while(it.hasNext()){
			int cur=(Integer) it.next();
			if(cur<id){
				range.setBegin(cur);
			}
			else if(cur>id  && !this.virtualMap.get(cur).equals(node)){
				if(range.getIpAddress()==null){
					range.setIpAddress(this.virtualMap.get(cur).getIp());
					range.setPort(String.valueOf(this.virtualMap.get(cur).getPort()));
				}
			}
			
		}
		if(range.getBegin()==0){
			range.setBegin(keyList.get(keyList.size()-1));
		}
		
		
		if(range.getIpAddress()==null){
			int position = 0;
			if(this.virtualMap.get(keyList.get(0)).equals(node)){
				position = 1;
			}
			range.setIpAddress(this.virtualMap.get(keyList.get(position)).getIp());
			range.setPort(String.valueOf(this.virtualMap.get(keyList.get(position)).getPort()));
		}
	
		return range;
	}

	@Override
	public void clean() {
		this.virtualMap.clear();
	}
}
/**
 * Internal class for range which is used for data migration
 * @author wwang84 lexu1
 */
class Range{
	private int begin;
	private int end;
	private String ipAddress;
	private String port;
	
	public List<String> toStringList(){
		List<String> convertResult = new ArrayList<String>();
		convertResult.add(this.ipAddress);
		convertResult.add(this.port);
		convertResult.add(String.valueOf(this.begin));
		convertResult.add(String.valueOf(this.end));
		return convertResult;
	}
	
	public List<Integer> toLongList(){
		List<Integer> range = new ArrayList<Integer>();
		range.add(this.begin);
		range.add(this.end);
		return range;
	}
	
	public void setBegin(int begin) {
		this.begin = begin;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public long getBegin() {
		return begin;
	}

	public long getEnd() {
		return end;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getPort() {
		return port;
	}
}
