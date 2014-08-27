package edu.uiuc.cs.cs425.myKV;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * KVstorage class
 * Main storage of key value pair
 * Support insert, delete, update, lookup, show
 * migrate data while leaving
 * @author lexu1, wwang84
 *
 */
public class KVstorage {
	
	private Map<Long, Object> kvMap = new HashMap<Long, Object>();
	
	public synchronized boolean insert(long key, Object value){
		if(this.kvMap.containsKey(key)){
			return false;
		}
		this.kvMap.put(key, value);
		return true;
	}
	
	public synchronized void insert(Map<Long, Object> map){
		this.kvMap.putAll(map);
	}
	
	public synchronized boolean delete(long key){
		if(!this.kvMap.containsKey(key)){
			return false;
		}
		this.kvMap.remove(key);
		return true;
	}
	
	public synchronized void delete(Map<Long, Object> newmap){
		Iterator<Long> it=newmap.keySet().iterator();
		while(it.hasNext()){
			Long k=it.next();
			this.kvMap.remove(k);
		}
	}
	
	/**
	 * migrate data while leaving the group
	 * @param range
	 * @return
	 */
	public synchronized Map<Long, Object> migrate(int[] range){
		Map<Long, Object> ret=new HashMap<Long, Object>();
		Iterator<Long> it = this.kvMap.keySet().iterator();
		while(it.hasNext()){
			Long key=it.next();
			if(range[0]<range[1]){
				if(key>range[0]&&key<=range[1]){
					Object value=this.kvMap.get(key);
					ret.put(key, value);
				}
			}
			else{
				if(key>range[0]||key<=range[1]){
					Object value=this.kvMap.get(key);
					ret.put(key, value);
				}
			}
		}		
		if(ret.size()==0){
			return null;
		}
		return ret;
	}
	
	public synchronized boolean update(long key, Object value){
		if(this.kvMap.containsKey(key)){
			this.kvMap.put(key, value);
			return true;
		}
		return false;
		
	}
	
	public synchronized Object lookup(long key){
		if(!this.kvMap.containsKey(key)){
			return null;
		}
		return this.kvMap.get(key);		
	}

	public synchronized boolean contain(long key){
		return kvMap.containsKey(key);
	}
	
	public synchronized Map<Long, Object> getKVmap(){
		return this.kvMap;
	}
	public synchronized int size(){
		return this.kvMap.size();
	}
}
