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

/**
 * RSA
 *
 * @author ranggarmaste
 * @since Mar 22, 2018.
 */
public class RSA {
    public static String LONG_TYPE = "LONG_TYPE";
    public static String BIGINTEGER_TYPE = "BIGINTEGER_TYPE";
    public static String LONGLONGINT_TYPE = "LONGLONGINT_TYPE";

    private static BigInteger ZERO = new BigInteger("0");
    private static BigInteger ONE = new BigInteger("1");
    private static BigInteger TWO = new BigInteger("2");

    private static LongLongInteger ZERO_L = new LongLongInteger("0");
    private static LongLongInteger ONE_L = new LongLongInteger("1");
    private static LongLongInteger TWO_L = new LongLongInteger("2");

    public void createKey(String filename, long p, long q) {
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

        writePublicKey(filename + ".pub", Long.toString(n), Long.toString(e));
        writePrivateKey(filename + ".pri", Long.toString(n), Long.toString(d));
    }

    public void createKey(String filename, BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(ONE)).multiply(q.subtract(ONE));
        BigInteger e = TWO;

        while (e.compareTo(phi) == -1) {
            if (gcd(e, phi).equals(ONE)) break;
            e = e.add(ONE);
        }

        BigInteger k = ONE;
        while (!(ONE.add(k.multiply(phi))).mod(e).equals(ZERO)) {
            k = k.add(ONE);
        };
        BigInteger d = (ONE.add(k.multiply(phi))).divide(e);

        writePublicKey(filename + ".pub", n.toString(), e.toString());
        writePrivateKey(filename + ".pri", n.toString(), d.toString());
    }

    public void createKey(String filename, LongLongInteger p, LongLongInteger q) {
        LongLongInteger n = p.multiply(q);
        LongLongInteger phi = (p.minus(ONE_L)).multiply(q.minus(ONE_L));
        LongLongInteger e = TWO_L;

        while (e.compareTo(phi) == -1) {
            if (gcd(e, phi).equals(ONE_L)) break;
            e = e.plus(ONE_L);
        }

        LongLongInteger k = ONE_L;
        while (!(ONE_L.plus(k.multiply(phi))).modulo(e).equals(ZERO_L)) {
            k = k.plus(ONE_L);
        };
        LongLongInteger d = (ONE_L.plus(k.multiply(phi))).divide(e);

        writePublicKey(filename + ".pub", n.toString(), e.toString());
        writePrivateKey(filename + ".pri", n.toString(), d.toString());
    }

    public void encrypt(String input, String output, String publicKeyFile, String type) {
        byte[] fileBytes = Utils.readFile(input);
        String bits = Utils.bytesToBits(fileBytes);
        String[] splits = readPublicKey(publicKeyFile);

        if (type.equals(RSA.LONG_TYPE)) {
            long n = Long.parseLong(splits[0]);
            long e = Long.parseLong(splits[1]);
            String[] splitBits = splitToBits(bits, n, true);
            StringBuilder encrypted = new StringBuilder();

            // Append last bit-block padding length
            int paddingSize = (splitBits.length * splitBits[0].length()) %  bits.length();
            encrypted.append(Utils.padFront(Integer.toBinaryString(paddingSize), 32));

            for (int i = 0; i < splitBits.length; i++) {
                long val = Long.valueOf(splitBits[i], 2);
                long ans = modularPower(val, e, n);
                encrypted.append(Utils.padFront(Long.toBinaryString(ans), splitBits[0].length()+1));
            }
            Utils.writeBits(output, encrypted.toString());
        } else if (type.equals(RSA.BIGINTEGER_TYPE)) {
            BigInteger n = new BigInteger(splits[0]);
            BigInteger e = new BigInteger(splits[1]);
            String[] splitBits = splitToBits(bits, n, true);
            StringBuilder encrypted = new StringBuilder();

            // Append last bit-block padding length
            int paddingSize = (splitBits.length * splitBits[0].length()) %  bits.length();
            encrypted.append(Utils.padFront(Integer.toBinaryString(paddingSize), 32));

            for (int i = 0; i < splitBits.length; i++) {
                BigInteger val = new BigInteger(splitBits[i], 2);
                BigInteger ans = val.modPow(e, n);
                encrypted.append(Utils.padFront(ans.toString(2), splitBits[0].length()+1));
            }
            Utils.writeBits(output, encrypted.toString());
        } else if (type.equals(RSA.LONGLONGINT_TYPE)) {
            LongLongInteger n = new LongLongInteger(splits[0]);
            LongLongInteger e = new LongLongInteger(splits[1]);
            String[] splitBits = splitToBits(bits, n, true);
            StringBuilder encrypted = new StringBuilder();

            // Append last bit-block padding length
            int paddingSize = (splitBits.length * splitBits[0].length()) %  bits.length();
            encrypted.append(Utils.padFront(Integer.toBinaryString(paddingSize), 32));

            for (int i = 0; i < splitBits.length; i++) {
                LongLongInteger val = LongLongInteger.fromBits(splitBits[i]);
                LongLongInteger ans = val.moduloExponent(e, n);
                encrypted.append(Utils.padFront(ans.toBits(), splitBits[0].length()+1));
            }
            Utils.writeBits(output, encrypted.toString());
        }
    }

    public void decrypt(String input, String output, String privateKeyFile, String type) {
        byte[] fileBytes = Utils.readFile(input);
        byte[] paddingSizeBytes = Arrays.copyOfRange(fileBytes, 0, 4);
        byte[] textBytes = Arrays.copyOfRange(fileBytes, 4, fileBytes.length);

        String bits = Utils.bytesToBits(textBytes);
        int paddingSize = Integer.valueOf(Utils.bytesToBits(paddingSizeBytes), 2);
        String[] splits = readPrivateKey(privateKeyFile);

        if (type.equals(RSA.LONG_TYPE)) {
            long n = Long.parseLong(splits[0]);
            long d = Long.parseLong(splits[1]);
            String[] splitBits = splitToBits(bits, n, false);
            StringBuilder decrypted = new StringBuilder();

            for (int i = 0; i < splitBits.length; i++) {
                long val = Long.valueOf(splitBits[i], 2);
                long ans = modularPower(val, d, n);
                decrypted.append(Utils.padFront(Long.toBinaryString(ans), splitBits[0].length()-1));
            }

            // Clean form padding
            int cleanLength = decrypted.toString().length() - splitBits[0].length();
            String cleanedEnd = decrypted.toString().substring(cleanLength).substring(paddingSize);
            String cleaned = decrypted.toString().substring(0, cleanLength) + cleanedEnd;
            Utils.writeBits(output, cleaned);
        } else if (type.equals(RSA.BIGINTEGER_TYPE)) {
            BigInteger n = new BigInteger(splits[0]);
            BigInteger d = new BigInteger(splits[1]);
            String[] splitBits = splitToBits(bits, n, false);
            StringBuilder decrypted = new StringBuilder();

            for (int i = 0; i < splitBits.length; i++) {
                BigInteger val = new BigInteger(splitBits[i], 2);
                BigInteger ans = val.modPow(d, n);
                decrypted.append(Utils.padFront(ans.toString(2), splitBits[0].length()-1));
            }

            // Clean form padding
            int cleanLength = decrypted.toString().length() - splitBits[0].length();
            String cleanedEnd = decrypted.toString().substring(cleanLength).substring(paddingSize);
            String cleaned = decrypted.toString().substring(0, cleanLength) + cleanedEnd;
            Utils.writeBits(output, cleaned);
        } else if (type.equals(RSA.LONGLONGINT_TYPE)) {
            LongLongInteger n = new LongLongInteger(splits[0]);
            LongLongInteger d = new LongLongInteger(splits[1]);
            String[] splitBits = splitToBits(bits, n, false);
            StringBuilder decrypted = new StringBuilder();

            for (int i = 0; i < splitBits.length; i++) {
                LongLongInteger val = LongLongInteger.fromBits(splitBits[i]);
                LongLongInteger ans = val.moduloExponent(d, n);
                decrypted.append(Utils.padFront(ans.toBits(),splitBits[0].length()-1));
            }

            // Clean form padding
            int cleanLength = decrypted.toString().length() - splitBits[0].length();
            String cleanedEnd = decrypted.toString().substring(cleanLength).substring(paddingSize);
            String cleaned = decrypted.toString().substring(0, cleanLength) + cleanedEnd;
            Utils.writeBits(output, cleaned);
        }
    }

    private String[] splitToBits(String bits, long n, boolean isEncrypt) {
        int count = 0;
        if (!isEncrypt) count = 1;

        long start = n;
        while (start > 1) {
            count += 1;
            start /= 2;
        }

        ArrayList<String> splitBits = new ArrayList<>();
        for (int i = 0; i < bits.length(); i += count) {
            int limit = i + count > bits.length() ? bits.length() : i + count;
            if ((limit - i == count) || isEncrypt) {
                String splitBit = Utils.padFront(bits.substring(i, limit), count);
                splitBits.add(splitBit);
            }
        }
        return splitBits.toArray(new String[splitBits.size()]);
    }

    private String[] splitToBits(String bits, BigInteger n, boolean isEncrypt) {
        int count = 0;
        if (!isEncrypt) count = 1;

        BigInteger start = n;
        while (start.compareTo(ONE) == 1) {
            count += 1;
            start = start.divide(TWO);
        }

        ArrayList<String> splitBits = new ArrayList<>();
        for (int i = 0; i < bits.length(); i += count) {
            int limit = i + count > bits.length() ? bits.length() : i + count;
            if ((limit - i == count) || isEncrypt) {
                String splitBit = Utils.padFront(bits.substring(i, limit), count);
                splitBits.add(splitBit);
            }
        }
        return splitBits.toArray(new String[splitBits.size()]);
    }

    private String[] splitToBits(String bits, LongLongInteger n, boolean isEncrypt) {
        int count = 0;
        if (!isEncrypt) count = 1;

        LongLongInteger start = n;
        while (start.compareTo(ONE_L) == 1) {
            count += 1;
            start = start.divide(TWO_L);
        }

        ArrayList<String> splitBits = new ArrayList<>();
        for (int i = 0; i < bits.length(); i += count) {
            int limit = i + count > bits.length() ? bits.length() : i + count;
            if ((limit - i == count) || isEncrypt) {
                String splitBit = Utils.padFront(bits.substring(i, limit), count);
                splitBits.add(splitBit);
            }
        }
        return splitBits.toArray(new String[splitBits.size()]);
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

    private void writePublicKey(String filename, String n, String e) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(n + " " + e + "\n");
            writer.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void writePrivateKey(String filename, String n, String d) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(n + " " + d + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long modularPower(long base, long exponent, long modulus) {
        long res = 1;
        base = base % modulus;

        while (exponent > 0)
        {
            if ((exponent & 1) == 1)
                res = (res * base) % modulus;
            exponent = exponent >> 1;
            base = (base * base) % modulus;
        }
        return res;
    }

    private long gcd(long x, long y) {
        if (y == 0) return x;
        return gcd (y, x % y);
    }

    private BigInteger gcd(BigInteger x, BigInteger y) {
        if (y.equals(ZERO)) return x;
        return gcd(y, x.mod(y));
    }

    private LongLongInteger gcd(LongLongInteger x, LongLongInteger y) {
        if (y.equals(ZERO_L)) return x;
        return gcd(y, x.modulo(y));
    }
}
