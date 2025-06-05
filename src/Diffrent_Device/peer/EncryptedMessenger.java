package Different_Device.peer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class EncryptedMessenger {

    private static final String KEY = "1234567812345678"; // Must match receiver
    private static final int PORT = 5050; // Must match receiver

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter receiver IP address (e.g., 127.0.0.1): ");
        String ip = scanner.nextLine();

        while (true) {
            System.out.print("\nEnter message (or type 'exit' to quit): ");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("exit")) {
                System.out.println("Exiting messenger...");
                break;
            }

            try (Socket socket = new Socket(ip, PORT);
                 PrintWriter writer = new PrintWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                String encrypted = encryptMessage(message);
                writer.println(encrypted);

                System.out.println("[Encrypted Sent]: " + encrypted);
            } catch (Exception e) {
                System.err.println("❌ Could not send message: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static String encryptMessage(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("❌ Encryption error: " + e.getMessage());
            return "[Encryption Failed]";
        }
    }
}
