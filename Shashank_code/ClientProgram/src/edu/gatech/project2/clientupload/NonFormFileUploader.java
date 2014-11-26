package edu.gatech.project2.clientupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
 
/**
 * This program demonstrates how to upload files to a web server
 * using HTTP POST request without any HTML form.
 * @author www.codejava.net
 * Slightly modified by Shashank Jagirdar to suit this project's needs.
 */
public class NonFormFileUploader {
    static final String UPLOAD_URL = "http://localhost:8080//ServletTestUpload//UploadServlet";
    static final int BUFFER_SIZE = 4096;
 
    public static void main(String[] args) throws IOException {
        // takes file path from first program's argument
       // String filePath = args[0];
    	String filePath = "C:\\Users\\shash_000\\Desktop\\clemsongameticket.pdf";
        File uploadFile = new File(filePath);
        
        
        //File metadata i.e. permissions. For each client for which a given client wants to set permissions, we can
        //make strings like below and concatenate with \n to make a csv file that we can store on the server
        String permissions = "";
        permissions = "c1,r,w,d,1"; 
        //c1 is same delegated client name. "r" is read permission. "w" is write permission. "d" is delegate permission.
        // "1" indicates the file should be encrypted before storing.
        
        //we need to encrypt the above permissions string with the server's public key before sending it to the server. encryption
        //method below:
        
        
        
        //Continue after encryption call
        System.out.println("File to upload: " + filePath);
 
        // creates a HTTP connection
        URL url = new URL(UPLOAD_URL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        
        // sets file name as a HTTP header
        httpConn.setRequestProperty("fileName", uploadFile.getName());
        
        //Sending permissions as another key-value propoerty to the server.
        httpConn.addRequestProperty("filePermissions", permissions);
        
 
        // opens output stream of the HTTP connection for writing data
        OutputStream outputStream = httpConn.getOutputStream();
 
        // Opens input stream of the file for reading data
        FileInputStream inputStream = new FileInputStream(uploadFile);
 
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
 
        System.out.println("Start writing data...");
 
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
 
        System.out.println("Data was written.");
        outputStream.close();
        inputStream.close();
 
        // always check HTTP response code from server
        int responseCode = httpConn.getResponseCode();
        System.out.println(responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // reads server's response
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String response = reader.readLine();
            System.out.println("Server's response: " + response);
        } else {
            System.out.println("Server returned non-OK code: " + responseCode);
        }
    }
}