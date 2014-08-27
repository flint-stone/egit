package edu.uiuc.cs.cs425.xgrep;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Server class run on each server, waiting for requests sent from client
 * Server spawns a thread to handle each request
 * @author lexu1
 *
 */
public class Server {
	static ServerSocket socket;
	static Socket connection;
	/**
     * The entry point on server side. It listens to its port and build connection to the client
     *
     * @param String[] args: the first of args is the port need to be listenned to
     * @return void.
     */
	public static void main(String[] args){
		//1. get client connection
		try {
			int port=Integer.parseInt(args[0]);
			if(port>65535){
				System.out.println("port out of range");
				return;
			}	
			socket = new ServerSocket(port, 10);
		} catch (IOException e) {
			System.out.println("Please input the port number(<65535).");
			e.printStackTrace();
			return;
		}
		while(true){
			//2. spawn thread to run
			ServerWorker executor;
			System.out.println("Waiting for connection...");
			try {
				connection=socket.accept();
				executor= new ServerWorker(connection);
				System.out.println("Connection received from " + connection.getInetAddress().getHostName());
				executor.run();
			} catch (IOException e) {
				e.printStackTrace();
			} 		
		}
	}

}
