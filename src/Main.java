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
        // rsa.createKey("nano", 8191, 6700417);
        rsa.createKey("nano", new BigInteger("2147483647"), new BigInteger("6700417"));
        rsa.encrypt("hello.txt", "hello.out", "nano.pub", RSA.BIGINTEGER_TYPE);
        rsa.decrypt("hello.out", "hello-new.txt", "nano.pri", RSA.BIGINTEGER_TYPE);
        rsa.encrypt("venus.txt", "venus.out", "nano.pub", RSA.BIGINTEGER_TYPE);
        rsa.decrypt("venus.out", "venus-new.txt", "nano.pri", RSA.BIGINTEGER_TYPE);
    }
}
