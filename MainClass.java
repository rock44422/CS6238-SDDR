import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class MainClass {

  public static void main(String[] args) throws Exception {

    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

    FileInputStream fis = new FileInputStream(args[0]);

    Certificate cert = certFactory.generateCertificate(fis);
    fis.close();

    System.out.println(cert);
  }
}
