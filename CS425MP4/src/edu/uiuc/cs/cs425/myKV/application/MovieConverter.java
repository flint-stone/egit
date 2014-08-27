package edu.uiuc.cs.cs425.myKV.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import edu.uiuc.cs.cs425.myKV.Command;
import edu.uiuc.cs.cs425.myKV.Record;
import edu.uiuc.cs.cs425.myKV.ReplicationManager;
import edu.uiuc.cs.cs425.myKV.TCP.CommandSender;

public class MovieConverter {
	static Map<Object, List<String>> movies = new HashMap<Object, List<String>>();

	public static void main(String[] args) throws IOException {
		File movieFile = new File("movies.txt");
		BufferedReader br1 = new BufferedReader(new FileReader(movieFile));
		String line;
		int loop=0;
		while ((line = br1.readLine()) != null && loop<10000) {
			StringTokenizer token = new StringTokenizer(line,".?!& ");
			Set<String> words = new HashSet<String>();
			while (token.hasMoreTokens()) {
				String word = (String) token.nextToken();
				if (word != null && !word.equals("")
						&& !words.contains(word)
						&& !word.equalsIgnoreCase("the")
						&& !word.equalsIgnoreCase("a")
						&& !word.equalsIgnoreCase("an")
						&& !word.equalsIgnoreCase("and")
						&& !word.equalsIgnoreCase("or")) {
					addMovie(word.toLowerCase(),line);
					words.add(word);
				}
			}
			loop++;
		}
		br1.close();
		System.out.println(movies.size());
		List<Command> commandList = new LinkedList<Command>();
		
		Set<Object> keys = movies.keySet();
		
		Iterator<Object> it = keys.iterator();

		while(it.hasNext() ){
			Command command = new Command();
			Object key = it.next();
			Object value = movies.get(key);
			command.setCommand("insert");
			command.setKey(key);
			command.setValue(new Record<Object>(new Timestamp(new Date().getTime()),value));
			command.setConsistentLevel(ReplicationManager.ONE);
			commandList.add(command);
		}
		CommandSender.send(commandList,"127.0.0.1", 54441);
	}

	private static void addMovie(String word, String title) {
		//System.out.println(word+" "+title);
		if (!movies.containsKey(word)) {
			List<String> titleList = new LinkedList<String>();
			titleList.add(title);
			movies.put(word, titleList);
		} else {
			movies.get(word).add(title);
		}
	}

}
