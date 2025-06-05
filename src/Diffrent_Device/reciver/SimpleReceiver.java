package Different_Device.receiver;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class SimpleReceiver {

    private static final int PORT = 5050;
    private static final String KEY = "1234567812345678"; // 16 bytes key

    public static void main(String[] args) {
        System.out.println("🔐 Receiver started. Waiting for encrypted messages on port " + PORT + "...\n");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                    String encrypted = reader.readLine();
                    if (encrypted == null) {
                        System.err.println("⚠️ Received null message");
                        continue;
                    }
                    String decrypted = decryptMessage(encrypted);

                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    System.out.println("[" + timestamp + "] Received → " + decrypted);

                } catch (IOException e) {
                    System.err.println("⚠️ Client handling error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Server could not start on port " + PORT + ": " + e.getMessage());
        }
    }

    private static String decryptMessage(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            byte[] decryptedBytes = cipher.doFinal(decoded);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("❌ Decryption error: " + e.getMessage());
            return "[Decryption Failed]";
        }
    }
}
