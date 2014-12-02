import java.io.*;
import java.util.Scanner;
import java.net.*;
import java.security.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
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
	public void certExchange()
	{
		try
		{
		System.out.println("Server certificate requested and Client certificate sent");	
		String s;
		Process p = Runtime.getRuntime().exec("openssl verify -verbose -CAfile ../CA/ca.crt  ../cert/server.crt");
            	BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            	while ((s = br.readLine()) != null)
		{
			if(s.equals("../cert/server.crt: OK"))
			{
				System.out.println("Server verification successfull");
			}
			else
			{
				System.out.println("Server verification failed");
			}
		}
	        p.destroy();
        } catch (Exception e) {e.printStackTrace();}

	}
    	public void sendSelfInfo() throws IOException
	{
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
      		writer.write("client1\n");
		System.out.println("Client-1\n");
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
		String userInput;
		FileOutputStream fos = null;
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
      			writer.write("GET\n");
			writer.write(FILE_TO_RECEIVE);
	       		writer.newLine();
	       		writer.flush();
      			BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			fos = new FileOutputStream(FILE_TO_RECEIVE);
       			System.out.print("RESPONSE FROM SERVER:");
       			while ((userInput = stdIn.readLine()) != null)
			{
           			System.out.println(userInput);
				fos.write(userInput.getBytes());
				fos.write("\n".getBytes());
       			}
			System.out.println("File " + FILE_TO_RECEIVE + " Downloaded ");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void putFile(String FILE_TO_SEND, String Security_Flag) throws IOException
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try
		{
			/*BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
      			writer.write("PUT\n");
			writer.write(FILE_TO_SEND);
	       		writer.newLine();*/
			File myFile = new File(FILE_TO_SEND);
			byte [] mybytearray = new byte [(int)myFile.length()];
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray,0,mybytearray.length);
			os = socketClient.getOutputStream();
			os.write("PUT\n".getBytes());
			os.write((FILE_TO_SEND+"\r\n").getBytes());
			os.write(Security_Flag.getBytes());
			os.write("\n".getBytes());
			System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + "bytes)");
			os.write(mybytearray,0,mybytearray.length);
			fis.close();
			os.flush();
			System.out.println("Done. ");
		}
		catch (FileNotFoundException e)
		{
		    	byte [] mybytearray = "File Not Found Exception".getBytes();
		    	os.write(mybytearray,0,mybytearray.length);
			os.flush();
		}
		finally
		{
			if (bis != null) bis.close();
			if(os != null) os.close();

		}
	}
	
	
	//ADDED THE DELEGATE METHOD
	public void delegate(String ownerID)
	{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br3 = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br4 = new BufferedReader(new InputStreamReader(System.in));
		
		String OwnerID = ownerID;
		String clientID;   
		String documentName;
		String permissions;
		String allowFurtherDelegationFlag;
		int days;

		
		//FETCHING USER INPUTS
		System.out.println("Enter the name of the document you want to delegate rights to:\n");
		documentName = br.readLine();
		br.close();
		
		System.out.println("Enter the name // ID of the client you want to delegate rights to:\n");
		clientID = br1.readLine();
		br1.close();
		
		System.out.println("Do you want to allow client " + clientID + " to be able to delegate rights to other clients for " + documentName + "? Press Y or N." + "\n");
		allowFurtherDelegationFlag = br2.readLine();
		br2.close();
		
		System.out.println("Enter the permissions you want to give to " + clientID + " over file " + documentName + ":\n");
		clientID = br3.readLine();
		br3.close();
		
		System.out.println("Enter the number of days for which you want this delegation to exist:\n");
		days = Integer.parseInt(br4.readLine());
		br4.close();
		
		//CALLING PUTFILEPERMISSIONS METHOD TO SEND PERMISSIONS TO SERVER
		putFilePermissions(OwnerID, clientID, permissions, allowFurtherDelegationFlag, documentName, days);
		
		
	}
	
	//========================COPIED THE PUTFILE METHOD AND CREATED THE METHOD BELOW====================================================
	public void putFilePermissions(String OwnerID, String ClientID, String permissions, String delegate, String documentName, int days) throws IOException
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try
		{
			
			//WRITING ALL THE ARGUMENTS RECEIVED IN THE METHOD CALL TO THE OUTPUTSTREAM
			os = socketClient.getOutputStream();
			os.write("PUTPERMISSIONS\n".getBytes());
			os.write((OwnerID+"\n").getBytes());
			os.write((ClientID+"\n").getBytes());
			os.write((documentName+"\r\n").getBytes());
			os.write(permissions.getBytes());
			os.write((delegate+"\r\n").getBytes());
			os.write((days+"\r\n").getBytes());
			os.write("\n".getBytes());
			System.out.println("Sending request to server from " + OwnerID + " to delegate rights to " + ClientID + " for permissions " + permissions);
			
			fis.close();
			os.flush();
			System.out.println("Done. ");
		}
		catch (FileNotFoundException e)
		{
		    	byte [] mybytearray = "File Not Found Exception".getBytes();
		    	os.write(mybytearray,0,mybytearray.length);
			os.flush();
		}
		finally
		{
			if (bis != null) bis.close();
			if(os != null) os.close();

		}
	}
	
	//==============================================================================================================================

	public void endSession() throws IOException
	{
		OutputStream os = null;
		System.out.println("Session with Server closed");	
		os = socketClient.getOutputStream();
		os.write("CLOSE\n".getBytes());
		os.flush();
		socketClient.close();
		
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
			String Security_Flag;
			SocketClient client = new SocketClient ("localhost",9991);
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
					client.connect();
					client.certExchange();
					break;
				
				case 2:	//Put a file
					System.out.print("Enter filename: ");
					fileUID = input.readLine();
					System.out.println("Security Flag Menu");
					System.out.println("1. Confidential");
					System.out.println("2. Integrity");
					System.out.println("3. None");
					System.out.println("Enter Option");
					Security_Flag = input.readLine();
					client.putFile(fileUID,Security_Flag);
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
			client.endSession();
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
