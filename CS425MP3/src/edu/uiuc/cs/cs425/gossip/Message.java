package edu.uiuc.cs.cs425.gossip;

import java.io.Serializable;

/**
 * For gossip framework, the message used in gossip protocol
 * Every gossip implementation must have a class implements this interface
 * @author wwang84, lexu1
 *
 */
public interface Message extends Serializable{

	void printMsg();
	public void merge(Message msg);
	public void selfcheck();

}
