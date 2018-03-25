import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utils
 * {Default file description, change this}
 * Copyright (c) 2018 GDP Labs. All rights reserved.
 *
 * @author ranggarmaste
 * @since Mar 22, 2018.
 */
public class Utils {
    public static String bytesToBits(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            String s = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            builder.append(s);
        }
        return builder.toString();
    }

    public static byte[] readFile(String filename) {
        try {
            Path path = Paths.get(filename);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void writeBits(String output, String encrypted) {
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

    public static String padFront(String bit, int count) {
        String newBit = bit;
        while (newBit.length() < count) {
            newBit = "0" + newBit;
        }
        return newBit;
    }

    public static String padBack(String bit, int count) {
        String newBit = bit;
        while (newBit.length() < count) {
            newBit = newBit + "0";
        }
        return newBit;
    }
}
