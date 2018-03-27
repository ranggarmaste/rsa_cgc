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
        LongLongInteger prime2 = LongLongInteger.fromString("65537");
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

        ElGamal eg2 = new ElGamal(LongLongInteger.fromString("3"), LongLongInteger.fromString("3"), prime2, LongLongInteger.fromString("7"), LongLongInteger.fromString("3"));
        System.out.println("\n===============\nHOLA");
        eg2.createKey("eg2", LongLongInteger.fromString("11"));
        System.out.println("\nen");
        eg2.encryptFile("hello.txt", "test.txt", "eg2.pub", LongLongInteger.fromString("5"));
        System.out.println("\nde");
        eg2.decryptFile("test.txt", "out.txt","eg2.pri", RSA.LONGLONGINT_TYPE);
    }
}
