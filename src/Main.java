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
//        rsa.createKey("nano", new BigInteger("2147483647"), new BigInteger("6700417"));
//        rsa.encrypt("hello.txt", "hello.out", "nano.pub", RSA.BIGINTEGER_TYPE);
//        rsa.decrypt("hello.out", "hello-new.txt", "nano.pri", RSA.BIGINTEGER_TYPE);
//        rsa.encrypt("venus.txt", "venus.out", "nano.pub", RSA.BIGINTEGER_TYPE);
//        rsa.decrypt("venus.out", "venus-new.txt", "nano.pri", RSA.BIGINTEGER_TYPE);
//
//        System.out.println(new BigInteger("356").modInverse(new BigInteger("23")));
        long prime1 = Long.valueOf("65537");
        BigInteger prime = BigInteger.valueOf(Long.valueOf("65537"));
        System.out.println(Long.valueOf("-462215806") % prime1);
        System.out.println(BigInteger.valueOf(Long.valueOf("-462215806")).mod(prime));
        ElGamal eg = new ElGamal(BigInteger.valueOf(3), BigInteger.valueOf(3), prime, BigInteger.valueOf(7), BigInteger.valueOf(3));
        eg.createKey("eg", BigInteger.valueOf(11));
        System.out.println("en");
        eg.encryptFile("hello.txt", "test.txt", "eg.pub", BigInteger.valueOf(5));
        System.out.println("de");
        eg.decryptFile("test.txt", "out.txt","eg.pri", RSA.BIGINTEGER_TYPE);
    }
}
