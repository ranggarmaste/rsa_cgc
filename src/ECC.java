import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ECC {
    public static String LONG_TYPE = "LONG_TYPE";
    public static String BIGINTEGER_TYPE = "BIGINTEGER_TYPE";
    public static String LONGLONGINT_TYPE = "LONGLONGINT_TYPE";

    // y^2 = x^3 + a.x + b mod p
    private long a_l;
    private BigInteger a_bl;
    private LongLongInteger a_ll;

    private long b_l;
    private BigInteger b_bl;
    private LongLongInteger b_ll;

    private long p_l;
    private BigInteger p_bl;
    private LongLongInteger p_ll;

    public ECC(long a, long b, long p) {
        a_l = a;
        b_l = b;
        p_l = p;
    }

    public ECC(BigInteger a, BigInteger b, BigInteger p) {
        a_bl = a;
        b_bl = b;
        p_bl = p;
    }

    public ECC(LongLongInteger a, LongLongInteger b, LongLongInteger p) {
        a_ll = a;
        b_ll = b;
        p_ll = p;
    }

    public long getLongA() {return a_l;}
    public long getLongB() {return b_l;}
    public long getLongP() {return p_l;}

    public BigInteger getBigIntegerA() {return a_bl;}
    public BigInteger getBigIntegerB() {return b_bl;}
    public BigInteger getBigIntegerP() {return p_bl;}

    public LongLongInteger getLongLongIntegerA() {return a_ll;}
    public LongLongInteger getLongLongIntegerB() {return b_ll;}
    public LongLongInteger getLongLongIntegerP() {return p_ll;}

    public Point addPoint(Point a, Point b, String type) {
        if (a.compareTo(b, type)) return addDoublePoint(a, type);
        if (type.equals(ECC.LONG_TYPE)) {
            long dy = b.y_l - a.y_l;
            long dx = b.x_l - a.x_l;
            dy = dy % p_l;
            dx = BigInteger.valueOf(dx).modInverse(BigInteger.valueOf(p_l)).longValue();
            long grad = (dy * dx) % p_l;

            long xr = grad*grad - a.x_l - b.x_l;
            long yr = grad * (a.x_l - xr) - a.y_l;
            xr = BigInteger.valueOf(xr).mod(BigInteger.valueOf(p_l)).longValue();
            yr = BigInteger.valueOf(yr).mod(BigInteger.valueOf(p_l)).longValue();
            return new Point(xr, yr);
        } else if (type.equals(ECC.BIGINTEGER_TYPE)) {
            BigInteger dy = b.y_bl.subtract(a.y_bl);
            BigInteger dx = b.x_bl.subtract(a.x_bl);
            dy = dy.mod(p_bl);
            dx = dx.modInverse(p_bl);
            BigInteger grad = dy.multiply(dx).mod(p_bl);

            BigInteger xr = grad.multiply(grad).subtract(a.x_bl).subtract(b.x_bl);
            BigInteger yr = a.x_bl.subtract(xr).multiply(grad).subtract(a.y_bl);
            xr = xr.mod(p_bl);
            yr = yr.mod(p_bl);
            return new Point(xr, yr);
        } else {
            LongLongInteger dy = b.y_ll.minus(a.y_ll);
            LongLongInteger dx = b.x_ll.minus(a.x_ll);
            dy = dy.modulo(p_ll);
            dx = dx.moduloInverse(p_ll);
            LongLongInteger grad = dy.multiply(dx).modulo(p_ll);

            LongLongInteger xr = grad.multiply(grad).minus(a.x_ll).minus(b.x_ll);
            LongLongInteger yr = a.x_ll.minus(xr).multiply(grad).minus(a.y_ll);
            xr = xr.modulo(p_ll);
            yr = yr.modulo(p_ll);
            return new Point(xr, yr);
        }
    }

    public Point addDoublePoint(Point a, String type) {
        if (type.equals(ECC.LONG_TYPE)) {
            long num = 3 * a.x_l * a.x_l + a_l;
            long den = 2 * a.y_l;
            num = num % p_l;
            den = BigInteger.valueOf(den).modInverse(BigInteger.valueOf(p_l)).longValue();
            long grad = (num * den) % p_l;

            long xr = grad*grad - 2 * a.x_l;
            long yr = grad * (a.x_l - xr) - a.y_l;
            xr = BigInteger.valueOf(xr).mod(BigInteger.valueOf(p_l)).longValue();
            yr = BigInteger.valueOf(yr).mod(BigInteger.valueOf(p_l)).longValue();
            return new Point(xr, yr);
        } else if (type.equals(ECC.BIGINTEGER_TYPE)) {
            BigInteger num = BigInteger.valueOf(3).multiply(a.x_bl).multiply(a.x_bl).add(a_bl);
            BigInteger den = BigInteger.valueOf(2).multiply(a.y_bl);
            num = num.mod(p_bl);
            den = den.modInverse(p_bl);
            BigInteger grad = num.multiply(den).mod(p_bl);

            BigInteger xr = grad.multiply(grad).subtract(a.x_bl).subtract(a.x_bl);
            BigInteger yr = a.x_bl.subtract(xr).multiply(grad).subtract(a.y_bl);
            xr = xr.mod(p_bl);
            yr = yr.mod(p_bl);
            return new Point(xr, yr);
        } else {
            LongLongInteger num = LongLongInteger.fromString("3").multiply(a.x_ll).multiply(a.x_ll).plus(a_ll);
            LongLongInteger den = LongLongInteger.fromString("2").multiply(a.y_ll);
            num = num.modulo(p_ll);
            den = den.moduloInverse(p_ll);
            LongLongInteger grad = num.multiply(den).modulo(p_ll);

            LongLongInteger xr = grad.multiply(grad).minus(a.x_ll).minus(a.x_ll);
            LongLongInteger yr = a.x_ll.minus(xr).multiply(grad).minus(a.y_ll);
            xr = xr.modulo(p_ll);
            yr = yr.modulo(p_ll);
            return new Point(xr, yr);
        }
    }
}
