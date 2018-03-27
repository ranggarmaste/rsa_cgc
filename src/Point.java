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

public class Point {
    public static String LONG_TYPE = "LONG_TYPE";
    public static String BIGINTEGER_TYPE = "BIGINTEGER_TYPE";
    public static String LONGLONGINT_TYPE = "LONGLONGINT_TYPE";

    public long x_l;
    public BigInteger x_bl;
    public LongLongInteger x_ll;

    public long y_l;
    public BigInteger y_bl;
    public LongLongInteger y_ll;

    public Point(long x, long y) {
        x_l = x;
        y_l = y;
    }

    public Point(BigInteger x, BigInteger y) {
        x_bl = x;
        y_bl = y;
    }

    public Point(LongLongInteger x, LongLongInteger y) {
        x_ll = x;
        y_ll = y;
    }

    public void setX(long x) {x_l = x;}
    public void setX(BigInteger x) {x_bl = x;}
    public void setX(LongLongInteger x) {x_ll = x;}
    public void setY(long y) {y_l = y;}
    public void setY(BigInteger y) {y_bl = y;}
    public void setY(LongLongInteger y) {y_ll = y;}


    public boolean compareTo(Point p, String type) {
        if (type.equals(LONG_TYPE)) {
            return ((p.x_l==x_l) && (p.y_l==y_l));
        } else if (type.equals(BIGINTEGER_TYPE)) {
            return ((p.x_bl.equals(x_bl)) && (p.y_bl.equals(y_bl)));
        } else {
            return (((p.x_ll.compareTo(x_ll))==1) && ((p.y_ll.compareTo(y_ll))==1));
        }
    }

    public String toString(String type) {
        if (type.equals(LONG_TYPE)) {
            return Long.toString(x_l) + ' ' + Long.toString(y_l);
        } else if (type.equals(BIGINTEGER_TYPE)) {
            return x_bl.toString() + ' ' + y_bl.toString();
        } else {
            return x_ll.toString() + ' ' + y_ll.toString();
        }
    }
}
