package Same_Device.Peer1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Peer_1 {

    private static final String SECRET_KEY = "1234567812345678"; // 16-char AES key
    private static final String RECEIVER_IP = "localhost";
    private static final int RECEIVER_PORT = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println("Peer1 (Sender) started. Messages will be sent to " + RECEIVER_IP + ":" + RECEIVER_PORT);

        while (true) {
            System.out.print("\nEnter message to send (type 'exit' to quit): ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                break;
            }

            try (Socket socket = new Socket(RECEIVER_IP, RECEIVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

                String encryptedMessage = encrypt(userInput);
                out.println(encryptedMessage);
                System.out.println("✅ Message encrypted and sent.");

            } catch (IOException e) {
                System.err.println("❌ Error sending message: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("❌ Unexpected error: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Peer1 stopped.");
    }

    private static String encrypt(String plainText) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
