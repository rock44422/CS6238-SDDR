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
		System.out.println("Connection Established");
    	}

    	public void readResponse() throws IOException
	{
      		String userInput;
       		BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

       		System.out.print("RESPONSE FROM SERVER:");
       		while ((userInput = stdIn.readLine()) != null)
		{
           		System.out.println(userInput);
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

	public void get(String FILE_TO_RECEIVE) throws IOException
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
			fos = new FileOutputStream(FILE_TO_RECEIVE);
			System.out.println("Waiting");
			bos = new BufferedOutputStream(fos);
			System.out.println("Waiting");
			bytesRead = is.read(mybytearray, 0 , mybytearray.length);
			System.out.println(bytesRead);
			System.out.println("Waiting");
			current = bytesRead;
			System.out.println(current);
			System.out.println("Waiting");
			do
			{
						System.out.println("Waiting");
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				System.out.println(bytesRead);
				if(bytesRead>=0)
				{
					System.out.println("Waiting");
					current += bytesRead;
				}
			}while(bytesRead>-1);
			bos.write(mybytearray,0, current);
			bos.flush();
			System.out.println("File" + FILE_TO_RECEIVE + "Download (" + current + "bytes read)");
		}
		finally
		{
			if(fos != null) fos.close();
			if(bos != null) bos.close();
			if(socketClient != null) socketClient.close();
		}
	}
   	public static void main(String args[])
	{
	        //Creating a SocketClient object
        	SocketClient client = new SocketClient ("localhost",9991);
        	try
		{
		    	//trying to establish connection to the server
		    	client.connect();
		    	//asking server for time
		   	 //client.askForTime();
		    	//waiting to read response from server
		    	//client.readResponse();
			//client.gen_Signature();
			System.out.println("Do you want to receive a file");
			Scanner sc = new Scanner(System.in);
			String S= sc.nextLine();
			client.get(S);
            
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