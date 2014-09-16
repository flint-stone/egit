import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Master {
	
	public static void main(String[] args) throws IOException{
		int port = 6789;
		ServerSocket socket = new ServerSocket(port, 10);
		Socket connection;
		while(true){
			connection=socket.accept();
			System.out.println("Waiting for connection...");
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			ServerWorker worker=new ServerWorker(connection);
			worker.run();			
		}
	}

}

class ServerWorker implements Runnable{
	
	private Socket connection;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ServerWorker(Socket connection) throws IOException {
		// TODO Auto-generated constructor stub
		this.connection=connection;
		this.in=new ObjectInputStream(this.connection.getInputStream());
		this.out=new ObjectOutputStream(this.connection.getOutputStream());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("master in run...");
		try {
			this.out.flush();
			
			//receive profile
			Object obj=this.in.readObject();
			Profile prf=(Profile)obj;
			
			//print out information
			System.out.println("host IP address: "+prf.ip);
			System.out.println(prf.ip+"-Bandwidth_in: "+prf.getBandwidth_in());
			System.out.println(prf.ip+"-Bandwidth_out: "+prf.getBandwidth_out());
			System.out.println(prf.ip+"-cpu_usage: "+prf.getCpu_usage());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
