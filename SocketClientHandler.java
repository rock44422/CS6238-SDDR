import java.io.*;
import java.net.Socket;
import java.util.Date;

public class SocketClientHandler implements Runnable {

	private Socket client;
	private String client_Name;
  public SocketClientHandler(Socket client, String client_Name) {
	this.client = client;
	this.client_Name = client_Name;
	System.out.println("Client_Name: "+client_Name);
  }

  @Override
  public void run() {
	System.out.println("Thread started with name:"+Thread.currentThread().getName());
	while(!client.isClosed())
     try {

	readResponse();
       } catch (IOException e) {
	 e.printStackTrace();
       } catch (InterruptedException e) {
         e.printStackTrace();
       }
	}
   private void readResponse() throws IOException, InterruptedException {
	String userInput,fileUID;
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
	while ((userInput = stdIn.readLine()) != null) {
		if(userInput.equals("TIME?")){
			System.out.println("REQUEST TO SEND TIME RECEIVED. SENDING CURRENT TIME");
			sendTime();
			break;
		}
		else if(userInput.equals("GET"))
		{
			fileUID = stdIn.readLine();
			System.out.println("File name requested " + fileUID);
			get(fileUID);
			break;
		}
		else if(userInput.equals("PUT"))
		{
			fileUID = stdIn.readLine();
			System.out.println("File name put " + fileUID);
			put(fileUID,stdIn);
			break;
		}
		else if(userInput.equals("CLOSE"))
		{
			System.out.println("Session close request from:  " + client_Name);
			client.close();
			break;
		}
	}
	}
	public void put(String FILE_TO_RECEIVE,BufferedReader stdIn) throws IOException
	{
		String userInput;
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(FILE_TO_RECEIVE);
       			System.out.print("RESPONSE FROM CLIENT:");
       			while ((userInput = stdIn.readLine()) != null && stdIn.read()!=-1)
			{
           			System.out.println(userInput);
				fos.write(userInput.getBytes());
				break;
       			}
			System.out.println("File " + FILE_TO_RECEIVE + " Downloaded ");
		}
		finally
		{
			if(fos != null) fos.close();
		}
	}
    private void sendTime() throws IOException, InterruptedException {
	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
	writer.write(new Date().toString()+"\n");
	writer.flush();
	
    }
	public void get(String FILE_TO_SEND) throws IOException, InterruptedException, FileNotFoundException
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try
		{
			File myFile = new File(FILE_TO_SEND);
			byte [] mybytearray = new byte [(int)myFile.length()];
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray,0,mybytearray.length);
			os = client.getOutputStream();
			System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + "bytes)");
			os.write(mybytearray,0,mybytearray.length);
			os.write("\n".getBytes(),0,"\n".getBytes().length);
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
			//if(os != null) os.close();

		}
	}

}
