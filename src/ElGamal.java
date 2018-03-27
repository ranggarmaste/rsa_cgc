import javax.rmi.CORBA.Util;
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

public class ElGamal {
    private ECC polynom;
    private Point base;

    public ElGamal(long a, long b, long p, long x, long y) {
        polynom = new ECC(a, b, p);
        base = new Point(x, y);
    }

    public ElGamal(BigInteger a, BigInteger b, BigInteger p, BigInteger x, BigInteger y) {
        polynom = new ECC(a, b, p);
        base = new Point(x, y);
    }

    public ElGamal(LongLongInteger a, LongLongInteger b, LongLongInteger p, LongLongInteger x, LongLongInteger y) {
        polynom = new ECC(a, b, p);
        base = new Point(x, y);
    }

    public ECC getPolynom() {return polynom;}
    public Point getBase() {return  base;}

    public void createKey(String filename, long sk) {
        Point pk = new Point(base.x_l, base.y_l);
        for (long i=0; i < sk; i++) {
            pk = polynom.addPoint(pk, base, ECC.LONG_TYPE);
        }

        writePublicKey(filename + ".pub", pk.toString(ECC.LONG_TYPE));
        writePrivateKey(filename + ".pri", Long.toString(sk));
    }

    public void createKey(String filename, BigInteger sk) {
        Point pk = new Point(base.x_bl, base.y_bl);
        BigInteger i = BigInteger.valueOf(sk.longValue());
        while (i.compareTo(BigInteger.ZERO) > 0) {
            pk = polynom.addPoint(pk, base, ECC.BIGINTEGER_TYPE);
            i = i.subtract(BigInteger.ONE);
        }

        writePublicKey(filename + ".pub", pk.toString(ECC.BIGINTEGER_TYPE));
        writePrivateKey(filename + ".pri", sk.toString());
    }

    public void createKey(String filename, LongLongInteger sk) {
        Point pk = new Point(base.x_ll, base.y_ll);
        LongLongInteger i = new LongLongInteger(sk.toString());
        while (i.compareTo(LongLongInteger.fromString("0")) > 0) {
            pk = polynom.addPoint(pk, base, ECC.LONGLONGINT_TYPE);
            i = i.minus(LongLongInteger.fromString("1"));
        }

        writePublicKey(filename + ".pub", pk.toString(ECC.LONGLONGINT_TYPE));
        writePrivateKey(filename + ".pri", sk.toString());
    }

    public void encryptFile(String input, String output, String publicKeyFile, long k) {
        StringBuilder encrypted = new StringBuilder();

        // kPb
        String[] pks = readPublicKey(publicKeyFile);
        Point pk = new Point(Long.valueOf(pks[0]), Long.valueOf(pks[1]));
        Point _pk = new Point(pk.x_l, pk.y_l);
        for (long i=0; i < k; i++) {
            _pk = polynom.addPoint(_pk, pk, ECC.LONG_TYPE);
        }

        // Pm
        int size = 32;
        byte[] fileBytes = Utils.readFile(input);
        String bits = Utils.bytesToBits(fileBytes);
        int pad = bits.length() % size;
        encrypted.append(String.valueOf(pad) + " ");
        while (bits.length() % size > 0) {
            bits += "0";
        }
        for (int i=0; i<(bits.length()/size); i++) {
            long mx = Long.parseLong(bits.substring((i*size), ((i*size) + 16)), 2);
            long my = Long.parseLong(bits.substring(((i*size) + 16), ((i*size) + 32)), 2);
            Point pm = new Point(mx, my);
            Point[] cipher = encrypt(_pk, pm, k);
            encrypted.append(Long.toString(cipher[0].x_l) + " " + Long.toString(cipher[0].y_l) + " " +
                    Long.toString(cipher[1].x_l) + " " + Long.toString(cipher[1].y_l) + " ");
        }
        writeBits(output, encrypted.toString());
    }

    public void encryptFile(String input, String output, String publicKeyFile, BigInteger k) {
        StringBuilder encrypted = new StringBuilder();

        // kPb
        String[] pks = readPublicKey(publicKeyFile);
        Point pk = new Point(BigInteger.valueOf(Long.valueOf(pks[0])), BigInteger.valueOf(Long.valueOf(pks[1])));
        Point _pk = new Point(pk.x_bl, pk.y_bl);
        BigInteger j = BigInteger.valueOf(k.longValue());
        while (j.compareTo(BigInteger.ZERO) > 0) {
            _pk = polynom.addPoint(_pk, pk, ECC.BIGINTEGER_TYPE);
            j = j.subtract(BigInteger.ONE);
        }

        // Pm
        int size = 32;
        byte[] fileBytes = Utils.readFile(input);
        String bits = Utils.bytesToBits(fileBytes);
        int pad = bits.length() % size;
        encrypted.append(String.valueOf(pad) + " ");
        while (bits.length() % size > 0) {
            bits += "0";
        }
        for (int i=0; i<(bits.length()/size); i++) {
            BigInteger mx = new BigInteger(bits.substring((i*size), ((i*size) + 16)), 2);
            BigInteger my = new BigInteger(bits.substring(((i*size) + 16), ((i*size) + 32)), 2);
            Point pm = new Point(mx, my);
//            System.out.println(mx.toString() + " " + my.toString());
            Point[] cipher = encrypt(_pk, pm, k);
            encrypted.append(Long.toString(cipher[0].x_bl.longValue()) + " " + Long.toString(cipher[0].y_bl.longValue()) + " " +
                    Long.toString(cipher[1].x_bl.longValue()) + " " + Long.toString(cipher[1].y_bl.longValue()) + " ");
        }
        writeBits(output, encrypted.toString());
    }

    public void encryptFile(String input, String output, String publicKeyFile, LongLongInteger k) {
        StringBuilder encrypted = new StringBuilder();

        // kPb
        String[] pks = readPublicKey(publicKeyFile);
        Point pk = new Point(LongLongInteger.fromString(pks[0]), LongLongInteger.fromString(pks[1]));
        Point _pk = new Point(pk.x_ll, pk.y_ll);
        LongLongInteger j = new LongLongInteger(k.toString());
        while (j.compareTo(LongLongInteger.fromString("0")) > 0) {
            _pk = polynom.addPoint(_pk, pk, ECC.LONGLONGINT_TYPE);
            j = j.minus(LongLongInteger.fromString("1"));
        }

        // Pm
        int size = 32;
        byte[] fileBytes = Utils.readFile(input);
        String bits = Utils.bytesToBits(fileBytes);
        int pad = bits.length() % size;
        encrypted.append(String.valueOf(pad) + " ");
        while (bits.length() % size > 0) {
            bits += "0";
        }
        for (int i=0; i<(bits.length()/size); i++) {
            LongLongInteger mx = LongLongInteger.fromBits(bits.substring((i*size), ((i*size) + 16)));
            LongLongInteger my = LongLongInteger.fromBits(bits.substring(((i*size) + 16), ((i*size) + 32)));
//            System.out.println(mx.toString() + " " + my.toString());
            Point pm = new Point(mx, my);
            Point[] cipher = encrypt(_pk, pm, k);
            encrypted.append((cipher[0].x_ll.toString()) + " " + (cipher[0].y_ll.toString()) + " " +
                    (cipher[1].x_ll.toString()) + " " + (cipher[1].y_ll.toString()) + " ");
        }
        writeBits(output, encrypted.toString());
    }

    public Point[] encrypt(Point kpb, Point pm, long k) {
        // kB
        Point kb = new Point(base.x_l, base.y_l);
        for (long i=0; i<k; i++) {
            kb = polynom.addPoint(kb, base, ECC.LONG_TYPE);
        }

        // Pm + kPb
        Point pr;
        pr = polynom.addPoint(kpb, pm, ECC.LONG_TYPE);

        return new Point[]{kb, pr};
    }

    public Point[] encrypt(Point kpb, Point pm, BigInteger k) {
        // kB
        Point kb = new Point(base.x_bl, base.y_bl);
        BigInteger i = BigInteger.valueOf(k.longValue());
        while (i.compareTo(BigInteger.ZERO) > 0) {
            kb = polynom.addPoint(kb, base, ECC.BIGINTEGER_TYPE);
            i = i.subtract(BigInteger.ONE);
        }

        // Pm + kPb
        Point pr;
        pr = polynom.addPoint(kpb, pm, ECC.BIGINTEGER_TYPE);

        return new Point[]{kb, pr};
    }

    public Point[] encrypt(Point kpb, Point pm, LongLongInteger k) {
        // kB
        Point kb = new Point(base.x_ll, base.y_ll);
        LongLongInteger i = new LongLongInteger(k.toString());
        while (i.compareTo(LongLongInteger.fromString("0")) > 0) {
            kb = polynom.addPoint(kb, base, ECC.LONGLONGINT_TYPE);
            i = i.minus(LongLongInteger.fromString("1"));
        }

        // Pm + kPb
        Point pr;
        pr = polynom.addPoint(kpb, pm, ECC.LONGLONGINT_TYPE);

        return new Point[]{kb, pr};
    }

    public void decryptFile(String input, String output, String privateKeyFile, String type) {
        String[] fileNums = readFile(input);
        int pad = Integer.parseInt(fileNums[0]);
        fileNums = Arrays.copyOfRange(fileNums, 1, fileNums.length);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            if (type.equals(ECC.LONG_TYPE)) {
                for (int i=0; i<(fileNums.length/4); i++) {
                    long x1 = Long.valueOf(fileNums[(i*4)]);
                    long y1 = Long.valueOf(fileNums[(i*4)+1]);
                    long x2 = Long.valueOf(fileNums[(i*4)+2]);
                    long y2 = Long.valueOf(fileNums[(i*4)+3]);

                    Point kb = new Point(x1,y1);
                    Point pr = new Point(x2,y2);
                    long sk = Long.valueOf(readPrivateKey(privateKeyFile)[0]);
                    Point pm = decrypt(kb, pr, sk);
                    String mx = Long.toBinaryString(pm.x_l);
                    String my = Long.toBinaryString(pm.y_l);
                    while (mx.length() < 16) {
                        mx = "0" + mx;
                    }
                    while (my.length() < 16) {
                        my = "0" + my;
                    }

                    if (i==((fileNums.length/4)-1)) {
                        while ((pad > 0) && (my.length() > 0)) {
                            my = my.substring(0, (my.length()-1));
                            pad -= 1;
                        }
                        if (pad > 0) {
                            while (pad > 0) {
                                mx = mx.substring(0, (mx.length()-1));
                                pad -= 1;
                            }
                            writer.write(Utils.bitsToString(mx));
                        } else {
                            writer.write(Utils.bitsToString(mx));
                            writer.write(Utils.bitsToString(my));
                        }
                    } else {
                        writer.write(Utils.bitsToString(mx));
                        writer.write(Utils.bitsToString(my));
                    }
                }
            } else if (type.equals(ECC.BIGINTEGER_TYPE)) {
                for (int i=0; i<(fileNums.length/4); i++) {
                    BigInteger x1 = new BigInteger(fileNums[(i*4)]);
                    BigInteger y1 = new BigInteger(fileNums[(i*4)+1]);
                    BigInteger x2 = new BigInteger(fileNums[(i*4)+2]);
                    BigInteger y2 = new BigInteger(fileNums[(i*4)+3]);

                    Point kb = new Point(x1,y1);
                    Point pr = new Point(x2,y2);
                    BigInteger sk = new BigInteger(readPrivateKey(privateKeyFile)[0]);
                    Point pm = decrypt(kb, pr, sk);
                    String mx = pm.x_bl.toString(2);
                    String my = pm.y_bl.toString(2);
                    while (mx.length() < 16) {
                        mx = "0" + mx;
                    }
                    while (my.length() < 16) {
                        my = "0" + my;
                    }

                    if (i==((fileNums.length/4)-1)) {
                        while ((pad > 0) && (my.length() > 0)) {
                            my = my.substring(0, (my.length()-1));
                            pad -= 1;
                        }
                        if (pad > 0) {
                            while (pad > 0) {
                                mx = mx.substring(0, (mx.length()-1));
                                pad -= 1;
                            }
                            writer.write(Utils.bitsToString(mx));
                        } else {
                            writer.write(Utils.bitsToString(mx));
                            writer.write(Utils.bitsToString(my));
                        }
                    } else {
                        writer.write(Utils.bitsToString(mx));
                        writer.write(Utils.bitsToString(my));
                    }
                }
            } else {
                for (int i=0; i<(fileNums.length/4); i++) {
                    LongLongInteger x1 = LongLongInteger.fromString(fileNums[(i*4)]);
                    LongLongInteger y1 = LongLongInteger.fromString(fileNums[(i*4)+1]);
                    LongLongInteger x2 = LongLongInteger.fromString(fileNums[(i*4)+2]);
                    LongLongInteger y2 = LongLongInteger.fromString(fileNums[(i*4)+3]);

                    Point kb = new Point(x1,y1);
                    Point pr = new Point(x2,y2);
                    LongLongInteger sk = LongLongInteger.fromString(readPrivateKey(privateKeyFile)[0]);
                    Point pm = decrypt(kb, pr, sk);
                    String mx = pm.x_ll.toBits();
                    String my = pm.y_ll.toBits();
                    while (mx.length() < 16) {
                        mx = "0" + mx;
                    }
                    while (my.length() < 16) {
                        my = "0" + my;
                    }

                    if (i==((fileNums.length/4)-1)) {
                        while ((pad > 0) && (my.length() > 0)) {
                            my = my.substring(0, (my.length()-1));
                            pad -= 1;
                        }
                        if (pad > 0) {
                            while (pad > 0) {
                                mx = mx.substring(0, (mx.length()-1));
                                pad -= 1;
                            }
                            writer.write(Utils.bitsToString(mx));
                        } else {
                            writer.write(Utils.bitsToString(mx));
                            writer.write(Utils.bitsToString(my));
                        }
                    } else {
                        writer.write(Utils.bitsToString(mx));
                        writer.write(Utils.bitsToString(my));
                    }
                }
            }
            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Point decrypt(Point kb, Point pr, long sk) {
        Point _kb = new Point(kb.x_l, kb.y_l);
        for (long i=0; i<sk; i++) {
            _kb = polynom.addPoint(_kb, kb, ECC.LONG_TYPE);
        }
        _kb.setY(_kb.y_l * -1);
        return polynom.addPoint(pr, _kb, ECC.LONG_TYPE);
    }

    private Point decrypt(Point kb, Point pr, BigInteger sk) {
        Point _kb = new Point(kb.x_bl, kb.y_bl);
        BigInteger i = BigInteger.valueOf(sk.longValue());
        while (i.compareTo(BigInteger.ZERO) > 0) {
            _kb = polynom.addPoint(_kb, kb, ECC.BIGINTEGER_TYPE);
            i = i.subtract(BigInteger.ONE);
        }
        _kb.setY(_kb.y_bl.negate());
        return polynom.addPoint(pr, _kb, ECC.BIGINTEGER_TYPE);
    }

    private Point decrypt(Point kb, Point pr, LongLongInteger sk) {
        Point _kb = new Point(kb.x_ll, kb.y_ll);
        LongLongInteger i = new LongLongInteger(sk.toString());
        while (i.compareTo(LongLongInteger.fromString("0")) > 0) {
            _kb = polynom.addPoint(_kb, kb, ECC.LONGLONGINT_TYPE);
            i = i.minus(LongLongInteger.fromString("1"));
        }
        _kb.setY(_kb.y_ll.negate());
        return polynom.addPoint(pr, _kb, ECC.LONGLONGINT_TYPE);
    }

    private String[] readPublicKey(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String s = reader.readLine();
            reader.close();
            return s.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private String[] readPrivateKey(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String s = reader.readLine();
            reader.close();
            return s.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private String[] readFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String s = reader.readLine();
            reader.close();
            return s.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private void writePublicKey(String filename, String pk) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(pk + "\n");
            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void writePrivateKey(String filename, String sk) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(sk + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeBits(String output, String encrypted) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            writer.write(encrypted + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
