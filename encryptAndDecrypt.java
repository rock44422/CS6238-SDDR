
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.math.BigInteger;

public class encryptAndDecrypt {
	
	
	/* Encrypting History File using DES. */
	public void File_Encrypt(int c, String File)
	{
		try 
		{
		FileInputStream fis= new FileInputStream(File);
		FileOutputStream fos= new FileOutputStream("Enc_"+File);
		encryptOrDecrypt(Integer.toString(c),Cipher.ENCRYPT_MODE,fis,fos);
		}
		catch(Throwable e)
		{
			System.out.println(e.getMessage());
		}

	}

	/* Decrypting History File using DES. */
	public void File_Decrypt(int c, String File)
	{
		try 
		{
			FileInputStream fis= new FileInputStream("Enc_"+File);
			FileOutputStream fos= new FileOutputStream(File);
			encryptOrDecrypt(Integer.toString(c),Cipher.DECRYPT_MODE,fis,fos);
		}
		catch(Throwable e)
		{
			System.out.println(e.getMessage());
		}	
	}
	
	
	
	
	
	/* Encrypt or Decrypt. */
	public void encryptOrDecrypt(String key, int mode, FileInputStream is, FileOutputStream os) throws Throwable
	{
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE

		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}
	}

	public static void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}


}
