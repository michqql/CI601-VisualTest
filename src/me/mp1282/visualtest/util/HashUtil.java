package me.mp1282.visualtest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String calculateHash(File file, Algorithm algo) {
        Preconditions.fileExists(file);

        try {
            MessageDigest md = MessageDigest.getInstance(algo.getAlgorithmName());
            byte[] chunk = new byte[1024];
            int count;
            try (FileInputStream fis = new FileInputStream(file)) {
                /* Reads 1024 bytes at a time and updates the hash for each chunk
                 * The FileInputStream::read method returns -1 when there are no
                 * more bytes to read.
                 */
                while((count = fis.read(chunk)) != -1)
                    md.update(chunk, 0, count);
            }

            StringBuilder builder = new StringBuilder();
            for(byte b : md.digest()) {
                builder.append(String.format("%02X", b));
            }

            return builder.toString();

        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Algorithm {
        MD5("MD5"),
        SHA_256("SHA-256")

        ;

        private final String algorithmName;

        Algorithm(String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }
    }
}
