package edu.uiuc.cs.cs425.myKV;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The "value" in key-value pair storage,including timestamp and object(real
 * value)
 * 
 * @author lexu1, wwang84
 * 
 */
public class Record<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Timestamp timeStamp;
	private T content;

	public Record(Timestamp ts, T content) {
		this.timeStamp = ts;
		this.content = content;
	}

	public Record() {
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}
}
