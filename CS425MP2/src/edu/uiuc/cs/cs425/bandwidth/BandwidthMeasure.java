package edu.uiuc.cs.cs425.bandwidth;

import edu.uiuc.cs.cs425.fd.Logger;

/**
 * Benchmark use only
 * Testing bandwidth use every second
 * initialized in main failure detector thread
 * @author lexu1, wwang84
 *
 */
public class BandwidthMeasure implements Runnable {

	public 
 int bytecount;
	
	private String name;
	
	public BandwidthMeasure(String name){
		bytecount=0;
		this.name=name;
	}
	
	/**
	 * thread context
	 */
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(1000);
				Logger.logBandwidth(name+": Current bandwidth "+bytecount+" Bytes/sec");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			clear();
		}
	}

	/**
	 * used in sender/worker thread to increase bandwidth count
	 * @param newcount
	 */
	public synchronized void increment(int newcount){
		bytecount+=newcount;
	}
	
	/**
	 * clear bandwidth count within one second
	 */
	public synchronized void clear(){
		bytecount=0;
	}
}
