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
            encrypted.append(padFront(Integer.toBinaryString(paddingSize), 32));

            for (int i = 0; i < splitBits.length; i++) {
                long val = Long.valueOf(splitBits[i], 2);
                long ans = modularPower(val, e, n);
                encrypted.append(padFront(Long.toBinaryString(ans), splitBits[0].length()+1));
            }
            writeBits(output, encrypted.toString());
        } else if (type.equals(RSA.BIGINTEGER_TYPE)) {
            BigInteger n = new BigInteger(splits[0]);
            BigInteger e = new BigInteger(splits[1]);
            String[] splitBits = splitToBits(bits, n, true);
            StringBuilder encrypted = new StringBuilder();

            // Append last bit-block padding length
            int paddingSize = (splitBits.length * splitBits[0].length()) %  bits.length();
            encrypted.append(padFront(Integer.toBinaryString(paddingSize), 32));

            for (int i = 0; i < splitBits.length; i++) {
                BigInteger val = new BigInteger(splitBits[i], 2);
                BigInteger ans = val.modPow(e, n);
                encrypted.append(padFront(ans.toString(2), splitBits[0].length()+1));
            }
            writeBits(output, encrypted.toString());
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
                decrypted.append(padFront(Long.toBinaryString(ans), splitBits[0].length()-1));
            }

            // Clean form padding
            int cleanLength = decrypted.toString().length() - splitBits[0].length();
            String cleanedEnd = decrypted.toString().substring(cleanLength).substring(paddingSize);
            String cleaned = decrypted.toString().substring(0, cleanLength) + cleanedEnd;
            writeBits(output, cleaned);
        } else if (type.equals(RSA.BIGINTEGER_TYPE)) {
            BigInteger n = new BigInteger(splits[0]);
            BigInteger d = new BigInteger(splits[1]);
            String[] splitBits = splitToBits(bits, n, false);
            StringBuilder decrypted = new StringBuilder();

            for (int i = 0; i < splitBits.length; i++) {
                BigInteger val = new BigInteger(splitBits[i], 2);
                BigInteger ans = val.modPow(d, n);
                decrypted.append(padFront(ans.toString(2), splitBits[0].length()-1));
            }

            // Clean form padding
            int cleanLength = decrypted.toString().length() - splitBits[0].length();
            String cleanedEnd = decrypted.toString().substring(cleanLength).substring(paddingSize);
            String cleaned = decrypted.toString().substring(0, cleanLength) + cleanedEnd;
            writeBits(output, cleaned);
        }
    }

    private void writeBits(String output, String encrypted) {
        int len = (int) Math.ceil((double) encrypted.length() / 8.0);
        byte[] bytes = new byte[len];

        for (int i = 0; i < encrypted.length(); i += 8) {
            int limit = i + 8 > encrypted.length() ? encrypted.length() : i + 8;
            String bit = padBack(encrypted.substring(i, limit), 8);
            byte result = (byte) (int) Integer.valueOf(bit, 2);
            bytes[i / 8] = result;
        }
        Path path = Paths.get(output);
        try {
            Files.write(path, bytes);
        } catch (Exception e) {
            e.printStackTrace();
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
                String splitBit = padFront(bits.substring(i, limit), count);
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
                String splitBit = padFront(bits.substring(i, limit), count);
                splitBits.add(splitBit);
            }
        }
        return splitBits.toArray(new String[splitBits.size()]);
    }

    private String padFront(String bit, int count) {
        String newBit = bit;
        while (newBit.length() < count) {
            newBit = "0" + newBit;
        }
        return newBit;
    }

    private String padBack(String bit, int count) {
        String newBit = bit;
        while (newBit.length() < count) {
            newBit = newBit + "0";
        }
        return newBit;
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
}
