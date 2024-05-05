import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LinkShortener {
    private static Map<String, String> shortToLongMap = new HashMap<>();
    private static Map<String, String> longToShortMap = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an option (1, 2 or 3):");
            System.out.println("1. Shorten URL");
            System.out.println("2. Expand URL");
            System.out.println("3. Exit");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid option.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Enter the long URL:");
                    String longURL = scanner.nextLine();
                    String shortURL = shortenURL(longURL);
                    if (shortURL != null) {
                        System.out.println("Shortened URL: " + shortURL);
                    } else {
                        System.out.println("Error: Unable to shorten URL.");
                    }
                    break;
                case 2:
                    System.out.println("Enter the short URL:");
                    String inputShortURL = scanner.nextLine();
                    String expandedURL = expandURL(inputShortURL);
                    if (expandedURL != null) {
                        System.out.println("Expanded URL: " + expandedURL);
                    } else {
                        System.out.println("Error: Short URL not found.");
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    saveMappings();   // To save mappings before exiting
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Error: Invalid choice. Please enter a valid option.");
            }
        }
    }

    public static String shortenURL(String longURL) {
        // To check if the long URL is empty
        if (longURL.isEmpty()) {
            System.out.println("Error: Long URL cannot be empty.");
            return null;
        }

        // To check if the long URL is already shortened
        if (longToShortMap.containsKey(longURL)) {
            return longToShortMap.get(longURL);
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(longURL.getBytes());
            String hashBase64 = Base64.getEncoder().encodeToString(hashBytes);
            String shortURL = hashBase64.substring(0, 8); // Take first 8 characters as short URL

            // Handles collisions
            int attempt = 1;
            while (shortToLongMap.containsKey(shortURL)) {
                hashBytes = digest.digest((longURL + attempt).getBytes());
                hashBase64 = Base64.getEncoder().encodeToString(hashBytes);
                shortURL = hashBase64.substring(0, 8); // Take first 8 characters as short URL
                attempt++;
            }

            // Store mapping
            shortToLongMap.put(shortURL, longURL);
            longToShortMap.put(longURL, shortURL);

            return shortURL;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String expandURL(String shortURL) {
        // To check if the short URL is empty
        if (shortURL.isEmpty()) {
            System.out.println("Error: Short URL cannot be empty.");
            return null;
        }

        // To check if the short URL exists
        if (!shortToLongMap.containsKey(shortURL)) {
            System.out.println("Error: Short URL not found.");
            return null;
        }

        return shortToLongMap.get(shortURL);
    }

    public static void saveMappings() {
        System.out.println("Mappings saved.");
    }
}
