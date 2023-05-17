package org.example;
import java.security.MessageDigest;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PageHash {
    public static List<String> hashes = new ArrayList<>();

    public static boolean HashIsNew(String page) throws IOException {
        String pageHash = generateHash(page);
        if (hashes.contains(pageHash)) {
            return false;
        }
        hashes.add(pageHash);
        return true;
    }

    private static String generateHash(String input) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}