import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainClass {
    public static void main(String args[]) {
        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec("openssl verify -verbose -CAfile ../CA/ca.crt  Client1.crt");
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}
    }
}
