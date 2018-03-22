import java.math.BigInteger;

/**
 * RSA
 *
 * @author ranggarmaste
 * @since Mar 22, 2018.
 */
public class RSA {
    private static BigInteger ZERO = new BigInteger("0");
    private static BigInteger ONE = new BigInteger("1");

    public void create_key(String filename, long p, long q) {
        long n = p * q;
        long phi = (p - 1) * (q - 1);
        long e = 2;

        while (e < phi) {
            if (gcd(e, phi) == 1) break;
            e++;
        }

        long k = 1;
        while ((1 + (k * phi)) % e != 0) k++;
        long d = (1 + (k * phi)) / e;
        System.out.println(n + " " + e + " " + d);
    }

    public void create_key(String name, BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(ONE)).multiply(q.subtract(ONE));
        BigInteger e = new BigInteger("2");

        while (e.compareTo(phi) == -1) {
            if (gcd(e, phi).equals(ONE)) break;
            e = e.add(ONE);
        }

        BigInteger k = ONE;
        while (!(ONE.add(k.multiply(phi))).mod(e).equals(ZERO)) {
            k = k.add(ONE);
        };
        BigInteger d = (ONE.add(k.multiply(phi))).divide(e);
        System.out.println(n + " " + e + " " + d);
    }

    private long gcd(long x, long y) {
        if (y == 0) return x;
        return gcd (y, x % y);
    }

    private BigInteger gcd(BigInteger x, BigInteger y) {
        if (y.equals(ZERO)) return x;
        return gcd(y, x.mod(y));
    }
}
