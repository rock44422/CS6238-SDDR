import java.io.*;
import java.net.Socket;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.*;

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
	String Security_Flag;
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
			Security_Flag = stdIn.readLine();
			put(fileUID,stdIn,Security_Flag);
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
	public void put(String FILE_TO_RECEIVE,BufferedReader stdIn, String Security_Flag) throws IOException
	{
		String userInput;
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(""+FILE_TO_RECEIVE);
       			System.out.print("RESPONSE FROM CLIENT:");
       			while ((userInput = stdIn.readLine()) != null)
			{
           			//System.out.println(userInput);
				fos.write(userInput.getBytes());
				break;
       			}
			System.out.println("File " + FILE_TO_RECEIVE + " Downloaded ");
			Path path = Paths.get("",FILE_TO_RECEIVE);
    			UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
			UserPrincipal clientN = lookupService.lookupPrincipalByName(client_Name);
			Files.setOwner(path,clientN);
			UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
			view.write(Security_Flag, Charset.defaultCharset().encode("True"));
    			System.out.println("Security Flag requested is "+ Security_Flag);
			ByteBuffer buffer = ByteBuffer.allocate(view.size(Security_Flag));
			view.read(Security_Flag, buffer);
			buffer.flip();
 			String value = Charset.defaultCharset().decode(buffer).toString();
  			System.out.println(value);
			System.out.println("Owner name set to: "+client_Name);
		}
		catch(IOException e)
			{
				System.err.println(e);
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
	private String getOwner(File f) throws IOException
	{
	Path p = Paths.get(f.getAbsolutePath());
   	UserPrincipal owner = Files.getOwner(p);
    	return owner.getName();
	}

	public void get(String FILE_TO_SEND) throws IOException, InterruptedException, FileNotFoundException
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try
		{
			File myFile = new File(FILE_TO_SEND);
			 if (!myFile.exists()) {
			      System.out.println("File not found.");
			      return;
			    }
			    if (myFile.canRead())
			      System.out.println("  Readable");
			    else
			      System.out.println("  Not Readable");

			    if (myFile.canWrite())
			      System.out.println("  Writable");
			    else
			      System.out.println("  Not Writable");
			    System.out.println("  Last modified on " + new Date(myFile.lastModified()));

			    long t = Calendar.getInstance().getTimeInMillis();
			    if (!myFile.setLastModified(t))
			      System.out.println("Can't set time.");

			    if (!myFile.setReadOnly())
			      System.out.println("Can't set to read-only.");

			    if (myFile.canRead())
			      System.out.println("  Readable");
			    else
			      System.out.println("  Not Readable");

			    if (myFile.canWrite())
			      System.out.println("  Writable");
			    else
			      System.out.println("  Not Writable");
			    System.out.println("  Last modified on " + new Date(myFile.lastModified()));

			    if (!myFile.setWritable(false, true))
			      System.out.println("Can't return to read/write.");

			    if (myFile.canRead())
			      System.out.println("  Readable");
			    else
			      System.out.println("  Not Readable");

			    if (myFile.canWrite())
			      System.out.println("  Writable");
			    else
			      System.out.println("  Not Writable");
			byte [] mybytearray = new byte [(int)myFile.length()];
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray,0,mybytearray.length);
			os = client.getOutputStream();
			if(getOwner(myFile).equals(client_Name))
			{
			System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + "bytes)");
			os.write(mybytearray,0,mybytearray.length);
			os.write("\n".getBytes(),0,"\n".getBytes().length);

			fis.close();
			os.flush();
			System.out.println("Done. ");
			}
			else
			{
				os.write("Permission Denied".getBytes(),0,"Permission Denied".getBytes().length);
				os.flush();
				System.out.println("Permssion Denied. ");
			}

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

}
