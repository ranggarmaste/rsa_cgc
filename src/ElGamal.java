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
    public static String LONG_TYPE = "LONG_TYPE";
    public static String BIGINTEGER_TYPE = "BIGINTEGER_TYPE";
    public static String LONGLONGINT_TYPE = "LONGLONGINT_TYPE";

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

    public void createKey(String filename, long sk) {
        Point pk = new Point(base.x_l, base.y_l);
        for (long i=0; i < sk; i++) {
            pk = polynom.addPoint(pk, base, ECC.LONG_TYPE);
        }

        writePublicKey(filename + ".pub", pk.toString());
        writePrivateKey(filename + ".pri", Long.toString(sk));
    }

    public void createKey(String filename, BigInteger sk) {
        Point pk = new Point(base.x_bl, base.y_bl);
        while (sk.compareTo(BigInteger.ZERO) > 0) {
            pk = polynom.addPoint(pk, base, ECC.BIGINTEGER_TYPE);
            sk.subtract(BigInteger.ONE);
        }

        writePublicKey(filename + ".pub", pk.toString());
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
        for (int i=0; i<=(bits.length()/size); i++) {
            long mx = Long.parseLong(bits.substring((i*size), ((i*size) + 16)), 2);
            long my = Long.parseLong(bits.substring(((i*size) + 16), ((i*size) + 32)), 2);
            Point pm = new Point(mx, my);
            Point[] cipher = encrypt(_pk, pm, k);
            encrypted.append(Long.toBinaryString(cipher[0].x_l) + " " + Long.toBinaryString(cipher[0].y_l) + " " +
                    Long.toBinaryString(cipher[1].x_l) + " " + Long.toBinaryString(cipher[1].y_l));
        }
        writeBits(output, encrypted.toString());
    }

    public void encryptFile(String input, String output, String publicKeyFile, BigInteger k) {
        StringBuilder encrypted = new StringBuilder();

        // kPb
        String[] pks = readPublicKey(publicKeyFile);
        Point pk = new Point(BigInteger.valueOf(Long.valueOf(pks[0])), BigInteger.valueOf(Long.valueOf(pks[1])));
        Point _pk = new Point(pk.x_bl, pk.y_bl);
        while (k.compareTo(BigInteger.ZERO) > 0) {
            _pk = polynom.addPoint(_pk, pk, ECC.BIGINTEGER_TYPE);
            k.subtract(BigInteger.ONE);
        }

        // Pm
        int size = 32;
        byte[] fileBytes = Utils.readFile(input);
        String bits = Utils.bytesToBits(fileBytes);
        for (int i=0; i<=(bits.length()/size); i++) {
            BigInteger mx = BigInteger.valueOf(Long.parseLong(bits.substring((i*size), ((i*size) + 16)), 2));
            BigInteger my = BigInteger.valueOf(Long.parseLong(bits.substring(((i*size) + 16), ((i*size) + 32)), 2));
            Point pm = new Point(mx, my);
            Point[] cipher = encrypt(_pk, pm, k);
            encrypted.append(Long.toString(cipher[0].x_bl.longValue()) + " " + Long.toString(cipher[0].y_bl.longValue()) + " " +
                    Long.toString(cipher[1].x_bl.longValue()) + " " + Long.toString(cipher[1].y_bl.longValue()) + " ");
            System.out.println(encrypted.toString());
        }
        writeBits(output, encrypted.toString());
    }

    public void decrypt(String input, String output, String privateKeyFile, String type) {
        byte[] fileBytes = Utils.readFile(input);
        byte[] paddingSizeBytes = Arrays.copyOfRange(fileBytes, 0, 4);
        byte[] textBytes = Arrays.copyOfRange(fileBytes, 4, fileBytes.length);

        String bits = Utils.bytesToBits(textBytes);
        int paddingSize = Integer.valueOf(Utils.bytesToBits(paddingSizeBytes), 2);
        String[] splits = readPrivateKey(privateKeyFile);
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
        while (k.compareTo(BigInteger.ZERO) > 0) {
            kb = polynom.addPoint(kb, base, ECC.BIGINTEGER_TYPE);
            k.subtract(BigInteger.ONE);
        }

        // Pm + kPb
        Point pr;
        pr = polynom.addPoint(kpb, pm, ECC.BIGINTEGER_TYPE);

        return new Point[]{kb, pr};
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
