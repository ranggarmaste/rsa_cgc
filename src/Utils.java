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
}
