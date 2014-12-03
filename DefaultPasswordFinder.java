import org.bouncycastle.openssl.PasswordFinder;

/**
 *
 * A straight-forward password finder, for use with PEMReader
 *
 * @author Saumitro Dasgupta
 */
public class DefaultPasswordFinder implements PasswordFinder {

    private char[] password;

    public DefaultPasswordFinder(String password) {

        setPassword(password);
    }

    public void setPassword(String password) {

        this.password = password.toCharArray();

    }

    public char[] getPassword() {
        
        return password;
    }

}
