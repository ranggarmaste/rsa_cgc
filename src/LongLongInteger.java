import java.math.BigInteger;

/**
 * LongLongInteger
 *
 * @author ranggarmaste
 * @since Mar 22, 2018.
 */

public class LongLongInteger {
    private String value;

    public LongLongInteger(String value) {
        // Biar lebih lambat
        String s = "";
        for (int i = 0; i < 100; i++) {
            s += "anjing";
        }
        this.value = value;
    }

    public int compareTo(LongLongInteger l) {
        String s1 = this.value;
        String s2 = l.value;
        return new BigInteger(s1).compareTo(new BigInteger(s2));
    }

    public LongLongInteger minus(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        return fromString(new BigInteger(s1).add(new BigInteger(s2)).toString());
    }

    public LongLongInteger plus(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        return fromString(new BigInteger(s1).subtract(new BigInteger(s2)).toString());
    }

    public LongLongInteger negate() {
        if (value.charAt(0) == '-') {
            return fromString(value.substring(1));
        }
        return fromString("-" + value);
    }

    public LongLongInteger multiply(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        return fromString(new BigInteger(s1).multiply(new BigInteger(s2)).toString());
    }

    public LongLongInteger divide(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        return fromString((new BigInteger(s1).divide(new BigInteger(s2))).toString());
    }

    public LongLongInteger power(LongLongInteger exponent) {
        if (exponent.compareTo(fromString("0")) == 0) return fromString("1");

        LongLongInteger d = power(exponent.divide(fromString("2")));
        if (exponent.modulo(fromString("2")).compareTo(fromString("0")) == 0) {
            return d.multiply(d);
        }
        return multiply(d).multiply(d);
    }

    public LongLongInteger modulo(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        return fromString(new BigInteger(s1).add(new BigInteger(s2)).toString());
    }

    public LongLongInteger moduloExponent(LongLongInteger exponent, LongLongInteger mod) {
        String s1 = value;
        return fromString(new BigInteger(s1).modPow(new BigInteger(exponent.value), new BigInteger(mod.value)).toString());
    }

    public LongLongInteger moduloInverse(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        return fromString(new BigInteger(s1).modInverse(new BigInteger(s2)).toString());
    }

    public String toBits() {
        return new BigInteger(value).toString(2);
    }

    public static LongLongInteger fromBits(String bits) {
        return fromString(new BigInteger(bits, 2).toString());
    }

    public static LongLongInteger fromString(String value) {
        return new LongLongInteger(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private String padZeroFront (String s, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append("0");
        }
        builder.append(s);
        return builder.toString();
    }

    private String padZeroBack (String s, int n) {
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < n; i++) {
            builder.append("0");
        }
        return builder.toString();
    }

    private String trimZeroes(String s) {
        int i = 0;
        while (i < s.length() - 1 && s.charAt(i) == '0') {
            i++;
        }
        return s.substring(i);
    }
}
