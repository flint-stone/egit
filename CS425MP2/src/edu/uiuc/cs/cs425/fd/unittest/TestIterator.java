package edu.uiuc.cs.cs425.fd.unittest;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TestIterator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Iterator<String> it = list.iterator();
		
		list.add("a");
		list.add("b");
		list.add("c");
		
		for(int i=0;i<list.size();i++){
			System.out.println(list.size());
			list.remove(list.get(i));
		}
	}

}
