import java.io.*;
import java.net.Socket;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.spec.*;
import java.security.interfaces.*;

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
				fos.write("\n".getBytes());
       			}
			System.out.println("File " + FILE_TO_RECEIVE + " Downloaded ");
			fos.close();
			Path path = Paths.get("",FILE_TO_RECEIVE);
    			UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
			UserPrincipal clientN = lookupService.lookupPrincipalByName(client_Name);
			UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
    			System.out.println("Security Flag requested is "+ Security_Flag);
			String Flag = "Security_Flag";
			view.write(Flag, Charset.defaultCharset().encode(Security_Flag));
			ByteBuffer buffer = ByteBuffer.allocate(view.size(Flag));
			view.read(Flag, buffer);
			buffer.flip();
	 		String value = Charset.defaultCharset().decode(buffer).toString();
			if(Security_Flag.equals("Integrity"))
			{
				try
				{
					String MD5 = MD5Checksum.getMD5Checksum(FILE_TO_RECEIVE);
					view.write("MD5", Charset.defaultCharset().encode(MD5));
					ByteBuffer Key = ByteBuffer.allocate(view.size("MD5"));
					view.read("MD5", Key);
					Key.flip();
		 			value = Charset.defaultCharset().decode(Key).toString();
		  			System.out.println(value);
				}
				catch (Exception e)
				{
				        e.printStackTrace();
       				}
			}
			if(Security_Flag.equals("Confidentiality"))
			{
				Random rnd = new Random();
				int randomNum = rnd.nextInt(100000)+1000000000;
				new encryptAndDecrypt().File_Encrypt(randomNum,FILE_TO_RECEIVE);
				File inFile = new File(FILE_TO_RECEIVE);
				if (!inFile.delete())
				{
					System.out.println("Could not delete file");
				}
				File file = new File("Enc_"+FILE_TO_RECEIVE);

				// File (or directory) with new name
				File file2 = new File(FILE_TO_RECEIVE);
				if(file2.exists()) throw new java.io.IOException("File with the same name already exists");
				// Rename file (or directory)
				boolean success = file.renameTo(file2);
				if (!success)
				{
					// File was not successfully renamed
				}
				view.write(Flag, Charset.defaultCharset().encode(Security_Flag));
				buffer = ByteBuffer.allocate(view.size(Flag));
				view.read(Flag, buffer);
				buffer.flip();
		 		value = Charset.defaultCharset().decode(buffer).toString();
		  		System.out.println(value);
				view.write("DES_Key", Charset.defaultCharset().encode(Integer.toString(randomNum)));
				ByteBuffer Key = ByteBuffer.allocate(view.size("DES_Key"));
				view.read("DES_Key", Key);
				Key.flip();
	 			value = Charset.defaultCharset().decode(Key).toString();

				
			}
			Files.setOwner(path,clientN);
			GroupPrincipal group = lookupService.lookupPrincipalByGroupName(client_Name);
			File targetFile = new File(FILE_TO_RECEIVE);
			Files.getFileAttributeView(targetFile.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setGroup(group);
			System.out.println("Owner,Group name set to: "+client_Name);
		}
		catch(IOException e)
			{
				System.err.println(e);
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
		int allow_send=0;
		try
		{
			Path path = Paths.get("",FILE_TO_SEND);
			UserDefinedFileAttributeView view = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
			String Flag= "Security_Flag";
			ByteBuffer buffer = ByteBuffer.allocate(view.size(Flag));
			view.read(Flag, buffer);
			buffer.flip();
	 		String value = Charset.defaultCharset().decode(buffer).toString();
	  		System.out.println(value);
			if(value.equals("Integrity"))
			{
				System.out.println("Integrity Flag is set on this File.");
				Flag= "MD5";
				buffer = ByteBuffer.allocate(view.size(Flag));
				view.read(Flag, buffer);
				buffer.flip();
		 		value = Charset.defaultCharset().decode(buffer).toString();
		  		System.out.println(value);
				try
				{
					String MD5 = MD5Checksum.getMD5Checksum(FILE_TO_SEND);
					if(MD5.equals(value))
					{
						allow_send=1;
					}
				}
				catch (Exception e)
				{
				        e.printStackTrace();
       				}
			}
			else if(value.equals("Confidentiality"))
			{
				System.out.println("Confidentiality Flag is set on this File.");
				Flag= "DES_Key";
				buffer = ByteBuffer.allocate(view.size(Flag));
				view.read(Flag, buffer);
				buffer.flip();
		 		value = Charset.defaultCharset().decode(buffer).toString();
		  		System.out.println(value);
				new encryptAndDecrypt().File_Decrypt(Integer.parseInt(value),FILE_TO_SEND);
				File file = new File(FILE_TO_SEND);
				File file2 = new File(FILE_TO_SEND+"_Dec");
				if(file2.exists()) throw new java.io.IOException("File with the same name already exists");
				// Rename file (or directory)
				boolean success = file.renameTo(file2);
				if (!success)
				{
					// File was not successfully renamed
				}
				if (!file.delete())
				{
					System.out.println("Could not delete file");
				}
				File file3 = new File("Dec_"+FILE_TO_SEND);
				success = file3.renameTo(file);
				if (!success)
				{
					// File was not successfully renamed
				}
				//view.write(Flag, Charset.defaultCharset().encode(Security_Flag));
				allow_send=1;
				
				
			}
			else if(value.equals("None"))
			{
				System.out.println("No Flag is set on this File.");
				allow_send=1;
				
			}
			File myFile = new File(FILE_TO_SEND);
			 if (!myFile.exists()) {
			      System.out.println("File not found.");
			      return;
			    }
			os = client.getOutputStream();
			if(allow_send==1)
			{
				byte [] mybytearray = new byte [(int)myFile.length()];
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
				bis.read(mybytearray,0,mybytearray.length);
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
					os.write("Permission Denied:Not the Owner".getBytes(),0,"Permission Denied:Not the Owner".getBytes().length);
					os.flush();
					System.out.println("Permission Denied:Not the Owner");
				}
			}
			else
			{
					os.write("File MD5 Check Problem.".getBytes(),0,"File MD5 Check Problem.".getBytes().length);
					os.flush();
					System.out.println("File MD5 Check Problem.");
			}

		}
		catch (FileNotFoundException e)
		{
		    	byte [] mybytearray = "File Not Found Exception".getBytes();
		    	os.write(mybytearray,0,mybytearray.length);
			os.flush();
		}
		catch(FileSystemException e)
		{
			System.out.println("Exception");
			File myFile = new File(FILE_TO_SEND);
			File myDecFile = new File(FILE_TO_SEND+"_Dec");
			 if (!myFile.exists()) {
			      System.out.println("File not found.");
			      return;
			    }
				byte [] mybytearray = new byte [(int)myFile.length()];
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
				bis.read(mybytearray,0,mybytearray.length);
				os = client.getOutputStream();
				if(getOwner(myDecFile).equals(client_Name))
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
					os.write("Permission Denied:Not the Owner".getBytes(),0,"Permission Denied:Not the Owner".getBytes().length);
					os.flush();
					System.out.println("Permission Denied:Not the Owner");
				}
		}
		finally
		{
			if (bis != null) bis.close();
			if(os != null) os.close();

		}
	}

}
