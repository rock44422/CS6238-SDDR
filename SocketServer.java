import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.bouncycastle.*;

public class SocketServer {
    
    private ServerSocket serverSocket;
    private int port;
    
    public SocketServer(int port) {
        this.port = port;
    }
    
    public void start() throws IOException, InterruptedException {
        System.out.println("Starting the socket server at port:" + port);
        serverSocket = new ServerSocket(port);

        Socket client = null;
        
        while(true){
        	System.out.println("Waiting for clients...");
        	client = serverSocket.accept();
        	System.out.println("The following client has connected:"+client.getInetAddress().getCanonicalHostName());
        	//A client has connected to this server. Send welcome message
		askClientInfo(client);
            Thread thread = new Thread(new SocketClientHandler(client,readResponse(client)));
            thread.start();
        }     
    }
    
        private void askClientInfo(Socket client) throws IOException, InterruptedException {
	//ufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
	//writer.write("Enter your CN");
	//writer.flush();
	//writer.close();
	OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
	writer.write("Enter your CN\n",0, "Enter your CN\n".length());
	writer.flush();
    }
    	public String readResponse(Socket client) throws IOException, InterruptedException
	{
      		String userInput;
       		BufferedReader stdIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

       		System.out.print("RESPONSE FROM CLIENT:");
       		while ((userInput = stdIn.readLine()) != null)
		{
           		System.out.println(userInput);
			break;
       		}
		return userInput;
   	} 
    /**
    * Creates a SocketServer object and starts the server.
    *
    * @param args
    */
    public static void main(String[] args) throws IOException, InterruptedException {
        // Setting a default port number.
        int portNumber = 9991;
        
        try {
            // initializing the Socket Server
            SocketServer socketServer = new SocketServer(portNumber);
            socketServer.start();
            
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
