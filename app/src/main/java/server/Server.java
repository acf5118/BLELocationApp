package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Server class that maintains a server time that can be
 * changed with correct username and password.
 * @author Bryan Passino, Adam Fowles
 */
public class Server
{
	// private state
	private ServerSocket socket;
	private int port = 5000;
	private String[] deviceNames = {"Bean", "elementary-0", "Nicks Phone", "Adam's iPhone"};
	private HashMap<String, String> paintingInfo = new HashMap<String, String>();
	private String file = "app/src/main/assets/info.txt";

	/**
	 * Server constructor, creates
	 * the socket and starts up the TCP Handlers
	 */
	public Server()
	{
		try
		{
			socket = new ServerSocket(port);
			String ip = getCurrentIp();
			System.out.println("Server Address: " + ip);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		importPaintingInfo(file);
		new HandleTCP().start();
	}

	/**
	 * Main method creates a new server object
	 * @param args - unused
     */
	public static void main(String[] args){
		new Server();
	}

	/**
	 * Gets the current IP address of the host
	 * @return - the IP as a string
	 * @throws UnknownHostException
     */
	public String getCurrentIp() throws UnknownHostException
	{
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while(nias.hasMoreElements())
				{
                    InetAddress ia= nias.nextElement();
                    if (!ia.isLinkLocalAddress() 
                     && !ia.isLoopbackAddress()
                     && ia instanceof Inet4Address) {
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("unable to get current IP ");
        }
        return null;
    }

	/**
	 * Get the painting info from a file
	 * @param file - the file to get into from
     */
	public void importPaintingInfo(String file){
		try {
			Scanner in = new Scanner(new FileReader(file));
			int i = 0;
			while(in.hasNextLine()){
				String value = in.nextLine();
				paintingInfo.put(deviceNames[i], value);
				i++;
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A class to handle the TCP connections
	 */
	private class HandleTCP extends Thread
	{
		
		/**
		 * Accept a socket connection and starts
		 * an inner thread to handle the connection.
		 */
		public void run(){
			while(true){
				try {
					System.out.println("listening");

					Socket connectedSocket = socket.accept();
					System.out.println("accepted");
					new Thread(new TCP_Connection(connectedSocket)).start();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}//end while
		}
		
		/**
		* Thread to handle individual TCP connection
		*/
		private class TCP_Connection implements Runnable
		{
			private Socket connectedSocket;
			
			/**
			* Constructor
			*/
			public TCP_Connection(Socket connectedSocket)
			{
				this.connectedSocket = connectedSocket;
			}
			
			/**
			 * Accepts an incoming request to return time or to set time.
			 * The response is sent back along
			 * with hop info at the end of return message.
			 */
			public void run(){
				String clientIp = "";
				try{ 
					InetAddress inetAddress = connectedSocket.getInetAddress();
					clientIp = inetAddress.getHostAddress();
					System.out.println("Connected to " + clientIp);
					//create data input stream
					DataInputStream fromClient = new DataInputStream(connectedSocket.getInputStream());
					
					//create data output stream
					DataOutputStream toClient = new DataOutputStream(connectedSocket.getOutputStream());


					toClient.writeUTF(getDevices());

					while(true){
						
						String request = fromClient.readUTF();
						System.out.println(request);
						toClient.writeUTF(paintingInfo.get(request.trim()));
					}
				}
				catch(IOException ex){
					System.err.println(ex);
				}//end catch 
				
			}

			public String getDevices(){
				String devices = "";
				for(String d: deviceNames){
					devices += d + ",";
				}
				return devices;
			}
		}
	}//end inner class
	
}
