package edu.uiuc.cs.cs425.xgrep;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This class send the grep command to the server side and print the result to
 * end users.
 * @author wwang84
 * @version 1.3
 */
public class ClientWorker implements Runnable {

	private String name;
	private String ipAddress;
	private String port;
	private String logFilePath;
	private String[] commandLine;
	private Socket requestSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int maxConnectionTime=1024;
	/**
	 * Class constructor initialize the object with machine name, ipAddress,
	 * port and log file fath
	 */
	public ClientWorker(String line) {
		String[] content = line.split("\\s");
		for (int i = 0; i < content.length; i++) {
			switch (i) {
			case 0:
				this.name = content[i];
				break;
			case 1:
				this.ipAddress = content[i];
				break;
			case 2:
				this.port = content[i];
				break;
			case 3:
				this.logFilePath = content[i];
				break;
			case 4:
				this.maxConnectionTime = Integer.parseInt(content[i]);
				break;
			default:
				break;
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String[] getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String[] commandLine) {
		this.commandLine = commandLine.clone();
		this.commandLine[2] = this.logFilePath;
	}

	@Override
	public void run() {
		try {
			tryConnect();

		} catch (UnknownHostException e) {
			System.out.println("Host is unknown " + name + " at " + ipAddress
					+ ". Please check your server.config file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Machine " + name + " in " + ipAddress
					+ " port " + port + " is down");
			//When the server is down, reconnect it
			reconnect();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (requestSocket != null) {
					requestSocket.close();
				}
			} catch (IOException e) {
				System.out
						.println("Error occurs when trying to close Socket, ObjectOutputStream or ObjectInputStream");
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method reconnects to server within a upper limit time. The method
	 * tries to connect to server in "interval" time. If it fails, it will try
	 * in next 2*interval time. If the trying time exceed maxTrytime, it stops
	 * trying and print timeout.
	 * 
	 * @param void.
	 * @return void.
	 */
	private void reconnect() {
		int interval = 1;
		while (interval<=this.maxConnectionTime) {
			try {
				Thread.sleep(interval * 1000);
				tryConnect();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {// mainly handle
										// ConnectionException(Connection
										// refused) and EOFException()
				System.out.println("Machine " + name + " in " + ipAddress
						+ " port " + port + " is still down.");
				System.out.println("Connecting machine in " + 2 * interval
						+ " socends..");
				interval *= 2;
				continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		if(interval>this.maxConnectionTime){
			System.out.println("Time out in connecting machine"+this.name+" ip in "+this.ipAddress);
		}
	}

	/**
	 * This method tries to connect to the server, sends grep command and print
	 * the returned results. If the connection fails(refused or reset), it will
	 * throw UnknownHostException, IOExceptio.
	 * 
	 * @param void
	 * @return void.
	 * @exception NumberFormatException
	 *                : port number cast exception UnknownHostException: :ip
	 *                error IOException : sending grep or getting result error
	 *                ClassNotFoundException : result casting error
	 * @see NumberFormatException
	 * @see UnknownHostException
	 * @see IOException
	 * @see ClassNotFoundException
	 */
	private void tryConnect() throws NumberFormatException,
			UnknownHostException, IOException, ClassNotFoundException {
		requestSocket = new Socket(ipAddress, Integer.parseInt(port));
		System.out.println("Connected to " + ipAddress + " in port " + port);

		// Send grep command to the socket
		out = new ObjectOutputStream(requestSocket.getOutputStream());
		out.flush();
		out.writeObject(this.commandLine);
		out.flush();
		// Read the grep result from the socket
		in = new ObjectInputStream(requestSocket.getInputStream());
		//ArrayList<String> s = new ArrayList<String>();
		//System.out.println("Xgrep result from Machine(" + name + ") in "
		//		+ ipAddress + " in port " + port);
		String s="";
		do {
			s = (String) in.readObject();
			System.out.println("--"+ipAddress+"--"+s);
		} while (!s.equals("Done"));
	}
}
