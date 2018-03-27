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
        BigInteger prime = BigInteger.valueOf(prime1);
        System.out.println(BigInteger.valueOf(Long.valueOf("-462215806")).mod(prime));

        ElGamal eg1 = new ElGamal(3, 3, prime1, 7, 3);
        System.out.println("\n===============\nHOLA");
        eg1.createKey("eg", 11);
        System.out.println("\nen");
        eg1.encryptFile("hello.txt", "test.txt", "eg.pub", 5);
        System.out.println("\nde");
        eg1.decryptFile("test.txt", "out.txt","eg.pri", RSA.LONG_TYPE);

        ElGamal eg = new ElGamal(BigInteger.valueOf(3), BigInteger.valueOf(3), prime, BigInteger.valueOf(7), BigInteger.valueOf(3));
        System.out.println("\n===============\nHOLA");
        eg.createKey("eg", BigInteger.valueOf(11));
        System.out.println("\nen");
        eg.encryptFile("hello.txt", "test.txt", "eg.pub", BigInteger.valueOf(5));
        System.out.println("\nde");
        eg.decryptFile("test.txt", "out.txt","eg.pri", RSA.BIGINTEGER_TYPE);

        System.out.println("\n\n\n");
        Point a = new Point(611, 50434);
        Point a1 = new Point(7, 3);
        Point b = new Point(BigInteger.valueOf(611), BigInteger.valueOf(50434));
        Point b1 = new Point(BigInteger.valueOf(7), BigInteger.valueOf(3));
        System.out.println(eg1.getPolynom().addPoint(a, a1, ECC.LONG_TYPE).toString(ECC.LONG_TYPE));
        System.out.println(eg.getPolynom().addPoint(b, b1, ECC.BIGINTEGER_TYPE).toString(ECC.BIGINTEGER_TYPE));
    }
}
