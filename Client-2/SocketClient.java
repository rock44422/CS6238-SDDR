import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.security.*;
/**
 * A Simple Socket client that connects to our socket server
 *
 */
public class SocketClient
{

	private String hostname;
    	private int port;
    	Socket socketClient;
	public final static int FILE_SIZE = 6022386;
    	public SocketClient(String hostname, int port)
	{
        this.hostname = hostname;
        this.port = port;
	}

    	public void connect() throws UnknownHostException, IOException
	{
		System.out.println("Attempting to connect to "+hostname+":"+port);
		socketClient = new Socket(hostname,port);
		readResponse();
		System.out.println("Connection Established");
		sendSelfInfo();
    	}
    	public void sendSelfInfo() throws IOException
	{
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
      		writer.write("Client-2\n");
		System.out.println("Client-2\n");
       		writer.newLine();
       		writer.flush();
    	}
    	public void readResponse() throws IOException
	{
      		String userInput;
       		BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

       		System.out.print("RESPONSE FROM SERVER:");
       		while ((userInput = stdIn.readLine()) != null)
		{
           		System.out.println(userInput);
			break;
       		}
   	}
    
    	public void askForTime() throws IOException
	{
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
      		writer.write("TIME?");
       		writer.newLine();
       		writer.flush();
    	}
	//Reference from https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
	/*public void gen_Signature()
	{
		try
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");	
			keyGen.initialize(1024, random);
			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();
			System.out.println("Your Public Key is "+pub);
			System.out.println("Your Private Key is "+priv);
		}
		catch (Exception e)
		{
            		System.err.println("Caught exception " + e.toString());
			e.printStackTrace();
        	}
	}
	public void gen_Certificate()
	{
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		FileInputStream fis = new FileInputStream(args[0]);
		Certificate cert = certFactory.generateCertificate(fis);
		fis.close();
		System.out.println(cert);
	}*/

	public void getFile(String FILE_TO_RECEIVE) throws IOException
	{
		int bytesRead;
		
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try
		{
			byte [] mybytearray = new byte [FILE_SIZE];
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
      			writer.write(FILE_TO_RECEIVE);
	       		writer.newLine();
	       		writer.flush();
			InputStream is = socketClient.getInputStream();
			bytesRead = is.read(mybytearray, 0 , mybytearray.length);
			current = bytesRead;
			do
			{
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead>=0)
				{
					current += bytesRead;
				}
			}while(bytesRead>-1);
			fos = new FileOutputStream(FILE_TO_RECEIVE);
			bos = new BufferedOutputStream(fos);
			bos.write(mybytearray,0, current);
			bos.flush();
			System.out.println("File " + FILE_TO_RECEIVE + " Downloaded (" + current + " bytes read)");
		}
		finally
		{
			if(fos != null) fos.close();
			if(bos != null) bos.close();
		}
	}
	public void putFile(String fileUID)
	{
	}
	public void delegate(String clientID)
	{
	}
	public void endSession()
	{
	}
   	public static void main(String args[])
	{
	        //Creating a SocketClient object

        	try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			int option;
			String choice;
			String fileUID;
			String clientID;
			SocketClient client = new SocketClient ("localhost",9991);
			client.connect();
			do
			{

				System.out.println("Menu");
				System.out.println("1. Connect to server");
				System.out.println("2. Put a file");
				System.out.println("3. Get a file");
				System.out.println("4. Delegate role");
				System.out.println("5. Terminate session");
				System.out.println("6. Ask for Time");
				System.out.println("Enter option");
				option = Integer.parseInt(input.readLine());
			
				switch(option)
				{
				case 1:	//Connect to server

					break;
				
				case 2:	//Put a file
					System.out.print("Enter filename: ");
					fileUID = input.readLine();
					client.putFile(fileUID);
					break;
				
				case 3:	//Get a file
					System.out.print("Enter filename: ");
					fileUID = input.readLine();
					client.getFile(fileUID);
					break;
				
				case 4:	//Delegate role
					System.out.print("Enter client ID: ");
					clientID = input.readLine();
					client.delegate(clientID);
					break;
				
				case 5:	//Terminate session
					client.endSession();
					break;
				case 6: //Ask for Time
					client.askForTime();
					client.readResponse();
					break;
				
				default: System.out.println("Invalid option. Retry.");
				}
			
				System.out.print("Do you wish to continue (Y/n)?");
				choice = input.readLine();
			}
			while(choice.equalsIgnoreCase("y"));
		    	//trying to establish connection to the server

		    	//asking server for time
		   	 //client.askForTime();
		    	//waiting to read response from server
		    	//client.readResponse();
			//client.gen_Signature();
            
        	} 
		catch (UnknownHostException e)
		{
            		System.err.println("Host unknown. Cannot establish connection");
        	}
		catch (IOException e)
		{
	            	System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        	}
    	}
}
