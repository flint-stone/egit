package edu.uiuc.cs.cs425.myKV;

import java.io.Serializable;
/**
 * The class use for transferring command between client and storage server
 * @author lexu1, wwang84
 *
 */
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
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
	public int getConsistentLevel() {
		return consistentLevel;
	}
	public void setConsistentLevel(int consistentLevel) {
		this.consistentLevel = consistentLevel;
	}
	private String command;
	private Object key;
	private Record<Object> value;
	private int consistentLevel;
	
}
