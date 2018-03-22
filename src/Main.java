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
        rsa.create_key("nano", 53, 61);
        rsa.create_key("nano", new BigInteger("53"), new BigInteger("61"));
    }
}
