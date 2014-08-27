package edu.uiuc.cs.cs425.myKV.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.uiuc.cs.cs425.gossip.Logger;
import edu.uiuc.cs.cs425.myKV.Command;
/**
 * CommandSender Class
 * TCP connection for command sending
 * @author lexu1, wwang84
 *
 */
public class CommandSender {
	
	public static Object send(Command command, String ip, int port){
		ObjectInputStream in;
		ObjectOutputStream out;
		Socket socket;
		try {
			socket=new Socket(ip, port);
			Logger.logCommandComm("Connected to " + ip + " in port " + port);
			out=new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			long startTime = System.currentTimeMillis();
			out.writeObject(command);
			out.flush();
			Object res=null;
			in = new ObjectInputStream(socket.getInputStream());
			res = in.readObject();
			long endTime   = System.currentTimeMillis();
			if(res != null){
				Logger.logCommandInfo(res.toString());
				if(res instanceof Collection){
					Iterator it = ((Collection) res).iterator();
					while(it.hasNext()){
						Logger.logCommandInfo(it.next().toString());
					}
				}
			}
			else{
				Logger.logCommandInfo("result is null");
			}
			long totalTime = endTime - startTime;
			Logger.logCommandInfo("Query time: "+totalTime+" milliseconds");
			in.close();
			out.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	/**
	 * Main function send
	 * @param commandList
	 * @param ip
	 * @param port
	 */
	public static void send(List<Command> commandList, String ip,
			int port) {
		ObjectInputStream in;
		ObjectOutputStream out;
		Socket socket;
		try {
			socket=new Socket(ip, port);
			Logger.logCommandComm("Connected to " + ip + " in port " + port);
			out=new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			long startTime = System.currentTimeMillis();
			out.writeObject(commandList);
			out.flush();
			Object res=null;
			in = new ObjectInputStream(socket.getInputStream());
			res = in.readObject();
			long endTime   = System.currentTimeMillis();
			if(res instanceof List){
				List<Object> list = (List<Object>)res;
				System.out.println(list.size());
				for(int i=0;i<list.size();i++){
					if(list.get(i)!=null && (list.get(i).toString().contains("null") || list.get(i).toString().contains("fail"))){
						Logger.logCommandInfo(list.get(i).toString());
					}
				}
			}
			else{
				System.out.println(res.toString());
			}
			long totalTime = endTime - startTime;
			Logger.logCommandInfo("Query time: "+totalTime+" milliseconds");
			in.close();
			out.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
