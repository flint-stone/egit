package edu.uiuc.cs.cs425.xgrep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class is used as thread called by each server
 * Each thread is responsible for handling one request received by server  
 * @author lexu1
 * @version 1.3
 */
public class ServerWorker implements Runnable {
	private Socket connection;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ServerWorker( Socket s) {
		this.connection=s;
	}

	/**
	 * Receiving and handling requests from connection
	 * exit 0 on success
	 */
	@Override
	public void run() {
		System.out.println("grep in run....");
		try {
			in=new ObjectInputStream(connection.getInputStream());
			out=new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			String[] command= (String[])(in.readObject());
			for(int i=0;i<command.length;i++){
				System.out.println(command[i]);
			}			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				out.writeObject(line);
	            out.flush();
            }
			
			out.writeObject("Done");
            out.flush();
			process.waitFor(); 
			in.close();
			out.close();
			br.close();
			System.out.println("Exit Code: " + process.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		

	}
}

