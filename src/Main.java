import java.math.BigInteger;

/**
 * Main
 *
 * @author ranggarmaste
 * @since Mar 22, 2018.
 */
public class Main {

    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.createKey("nano", 53, 61);
        rsa.createKey("nano", new BigInteger("53"), new BigInteger("61"));
        rsa.encrypt("hello.txt", "hello.out", "nano.pub", RSA.LONG_TYPE);
        rsa.decrypt("hello.out", "hello-new.txt", "nano.pri", RSA.LONG_TYPE);
        rsa.encrypt("venus.txt", "venus.out", "nano.pub", RSA.LONG_TYPE);
        rsa.decrypt("venus.out", "venus-new.txt", "nano.pri", RSA.LONG_TYPE);
        // rsa.encrypt("aa.jpg", "aa.out", "nano.pub", RSA.LONG_TYPE);
        // rsa.decrypt("aa.out", "aa-new.jpg", "nano.pri", RSA.LONG_TYPE);
    }
}
