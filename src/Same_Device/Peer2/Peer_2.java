package Same_Device.Peer2;




import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

    public class Peer_2 {

        private static final int LISTEN_PORT = 5000;
        private static final String SECRET_KEY = "1234567812345678"; // 16-char AES key

        public static void main(String[] args) {
            System.out.println("Receiver is starting...");

            try (ServerSocket serverSocket = new ServerSocket(LISTEN_PORT)) {
                System.out.println("Receiver is listening on port " + LISTEN_PORT);

                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(
                                 new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {

                        System.out.println("Connected to: " + clientSocket.getInetAddress());

                        String encryptedMessage = in.readLine();

                        if (encryptedMessage == null || encryptedMessage.isEmpty()) {
                            System.out.println("Received empty message.");
                        } else {
                            String decryptedMessage = decrypt(encryptedMessage);
                            System.out.println("\nReceived (decrypted): " + decryptedMessage);
                        }

                    } catch (Exception e) {
                        System.err.println("Error handling client: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error starting server: " + e.getMessage());
            }
        }

        private static String decrypt(String encryptedText) {
            try {
                SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return new String(decryptedBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return "[Decryption Error]";
            }
        }
    }


