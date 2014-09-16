import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Slave {
	public static void main (String[] args) throws UnknownHostException, InterruptedException{
		
		while(true){
			Thread.sleep(1000);
			SlaveWorker worker = new SlaveWorker();
			worker.run();
		}
	}

}

class SlaveWorker implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			//GAIN service info
			Profile prf=new Profile(InetAddress.getLocalHost().getHostAddress());
			prf.examine();
			//send service info
			Socket socket = new Socket("127.0.0.1",6789);
			ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			out.writeObject(prf);
			out.flush();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}