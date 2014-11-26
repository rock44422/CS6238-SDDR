import java.io.*;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.KeyStore;

public class MainClass {

  public static void main(String args[]) throws Exception {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    List mylist = new ArrayList();
    InputStream in = new FileInputStream(args[0]);
    KeyStore ks = KeyStore.getInstance("PKCS12");
    ks.load(in,"chawla".toCharArray());
    Certificate c = ks.getCertificate("Harsh CA");
    mylist.add(c);

    CertPath cp = cf.generateCertPath(mylist);
    //System.out.println(cp);	
	InputStream in1= new FileInputStream(args[1]);
    Certificate trust = cf.generateCertificate(in1);
    TrustAnchor anchor = new TrustAnchor((X509Certificate) trust, null);
    PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
    params.setRevocationEnabled(false);
    CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
    PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
    System.out.println(result);
  }
}
