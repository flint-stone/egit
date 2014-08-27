package edu.uiuc.cs.cs425.myKV;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * KVstorage class Main storage of key value pair Support insert, delete,
 * update, lookup, show migrate data while leaving
 * 
 * @author lexu1, wwang84
 * 
 */
public class KVstorage {

	private Map<Object, Record<Object>> kvMap = new HashMap<Object, Record<Object>>();
	private Queue<OperationRecord> readOp = new LinkedList<OperationRecord>();
	private Queue<OperationRecord> writeOp = new LinkedList<OperationRecord>();

	public Map<Object, Record<Object>> getKvMap() {
		return kvMap;
	}

	/**
	 * insert method for storage level
	 * 
	 * @param key
	 * @param value
	 * @return Record including true/false and timestamp
	 */
	public synchronized Record<Boolean> insert(Object key, Record<Object> value) {
		Record<Boolean> result = new Record<Boolean>();
		Record<Object> oldValue = kvMap.get(key);
		if (this.kvMap.containsKey(key)) {
			if (oldValue.getTimeStamp().after(value.getTimeStamp())) {
				result.setTimeStamp(oldValue.getTimeStamp());
				result.setContent(false);
				return result;
			}
		}
		this.kvMap.put(key, value);
		result.setTimeStamp(value.getTimeStamp());
		result.setContent(true);
		this.recordOperation(key, value, false);
		return result;
	}

	/**
	 * insert a batch of data,used for replication management
	 * 
	 * @param a
	 *            map of <key,value>
	 * @return void
	 */
	public synchronized void insert(Map<Object, Record<Object>> map) {
		Set<Object> keySet = map.keySet();
		Iterator i = keySet.iterator();
		while (i.hasNext()) {
			Object newKey = i.next();
			Record<Object> value = map.get(newKey);
			Record<Object> oldValue = kvMap.get(newKey);
			if (this.kvMap.containsKey(newKey)) {
				if (oldValue.getTimeStamp().after(value.getTimeStamp())) {
					continue;
				}
			}
			this.kvMap.put(newKey, value);
			this.recordOperation(newKey, value, false);
		}
	}

	/**
	 * delete method for storage level
	 * 
	 * @param key
	 * @param value
	 *            including timestamp
	 * @return Record including true/false and timestamp
	 */
	public synchronized Record<Boolean> delete(Object key, Record<Object> value) {
		Record<Boolean> result = new Record<Boolean>();
		Record<Object> oldValue = kvMap.get(key);
		if (this.kvMap.containsKey(key)) {
			if (oldValue.getTimeStamp().after(value.getTimeStamp())) {
				result.setTimeStamp(oldValue.getTimeStamp());
				result.setContent(false);
				return result;
			}
		}
		this.kvMap.put(key, value);
		result.setTimeStamp(value.getTimeStamp());
		result.setContent(true);
		this.recordOperation(key, value, false);
		return result;
	}

	/**
	 * update method for storage level
	 * 
	 * @param key
	 * @param value
	 * @return Record including true/false and timestamp
	 */
	public synchronized Record<Boolean> update(Object key, Record<Object> value) {
		Record<Boolean> result = new Record<Boolean>();
		Record<Object> oldValue = kvMap.get(key);
		if (this.kvMap.containsKey(key)) {
			if (oldValue.getTimeStamp().before(value.getTimeStamp())) {
				this.kvMap.put(key, value);
				result.setContent(true);
				result.setTimeStamp(value.getTimeStamp());
				this.recordOperation(key, value, false);
				return result;
			} else {// key exist but already has newer timestamp
				result.setContent(false);
				result.setTimeStamp(oldValue.getTimeStamp());
				this.recordOperation(key, oldValue, false);
				return result;
			}
		} else {// this key-value has not exist(never been inserted or deleted)
			result.setContent(false);
			result.setTimeStamp(null);
			this.recordOperation(key, oldValue, false);
			return result;
		}
	}

	/**
	 * read method for storage level
	 * 
	 * @param key
	 * @return Record including value and timestamp
	 */
	public synchronized Record<Object> lookup(Object key) {
		Record<Object> result = new Record<Object>();
		if (this.kvMap.containsKey(key)) {
			Record<Object> value = this.kvMap.get(key);
			this.recordOperation(key, value, true);
			return value;
		} else {
			this.recordOperation(key, new Record(), true);
			return result;
		}

	}

	/**
	 * migrate data while leaving the group
	 * 
	 * @param range
	 * @return
	 */
	public synchronized Map<Object, Object> migrate(int[] range) {
		Map<Object, Object> ret = new HashMap<Object, Object>();
		Iterator<Object> it = this.kvMap.keySet().iterator();

		while (it.hasNext()) {
			Object key = it.next();
			int hashedKey = HashLocator.hashObject(key);
			if (range[0] < range[1]) {
				if (hashedKey > range[0] && hashedKey <= range[1]) {
					Object value = this.kvMap.get(key);
					ret.put(key, value);
				}
			} else {
				if (hashedKey > range[0] || hashedKey <= range[1]) {
					Object value = this.kvMap.get(key);
					ret.put(key, value);
				}
			}

		}
		if (ret.size() == 0) {
			return null;
		}
		return ret;
	}

	public synchronized int size() {
		return this.kvMap.size();
	}

	/**
	 * clean the data which does not belong to this node's range (self
	 * predecessor and successor)
	 * 
	 * @param int[] self range
	 * @param int[] predecessor range
	 * @param int[] sucessor range
	 * @return void
	 */
	public void clean(int[] predecessorRange, int[] selfRange,
			int[] successorRange) {
		// System.out.println(keyRange[0]+" "+keyRange[1]);
		Set<Object> keySet = this.kvMap.keySet();
		Object[] array = keySet.toArray();
		for (int i = 0; i < array.length; i++) {
			Object key = array[i];
			int hashedKey = HashLocator.hashObject(key);
			if (!BelongTo(hashedKey, predecessorRange)
					&& !BelongTo(hashedKey, selfRange)
					&& !BelongTo(hashedKey, successorRange)) {
				this.kvMap.remove(key);
			}
		}

	}

	private boolean BelongTo(int hashedKey, int[] keyRange) {
		if (keyRange[0] < keyRange[1]) {
			if (keyRange[0] < hashedKey && hashedKey <= keyRange[1]) {
				return true;
			}
		} else {
			if (keyRange[0] < hashedKey || hashedKey <= keyRange[1]) {
				return true;
			}
		}
		return false;
	}

	private void recordOperation(Object key, Record<Object> value,
			boolean isRead) {
		if (isRead) {
			if (this.readOp.size() > 10) {
				this.readOp.poll();
			}
			this.readOp.add(new OperationRecord(key, value));
		} else {
			if (this.writeOp.size() > 10) {
				this.writeOp.poll();
			}
			this.writeOp.add(new OperationRecord(key, value));
		}

	}

	public Queue<OperationRecord> getRecentReadOperation() {
		return this.readOp;
	}

	public Queue<OperationRecord> getRecentWriteOperation() {
		return this.writeOp;
	}
}

class OperationRecord {
	private Object key;
	private Record<Object> value;

	public OperationRecord(Object key, Record<Object> value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Record<Object> getValue() {
		return value;
	}

	public void setValue(Record<Object> value) {
		this.value = value;
	}

}
