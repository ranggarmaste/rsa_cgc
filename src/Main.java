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

        long prime1 = Long.valueOf("65537");
        BigInteger prime = BigInteger.valueOf(Long.valueOf("65537"));
        System.out.println(BigInteger.valueOf(Long.valueOf("-462215806")).mod(prime));
        ElGamal eg = new ElGamal(BigInteger.valueOf(3), BigInteger.valueOf(3), prime, BigInteger.valueOf(7), BigInteger.valueOf(3));
        System.out.println("HOLA");
        eg.createKey("eg", BigInteger.valueOf(11));
        System.out.println("en");
        eg.encryptFile("hello.txt", "test.txt", "eg.pub", BigInteger.valueOf(5));
        System.out.println("de");
        eg.decryptFile("test.txt", "out.txt","eg.pri", RSA.BIGINTEGER_TYPE);
    }
}
