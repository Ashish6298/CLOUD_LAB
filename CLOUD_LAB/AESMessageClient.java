import java.io.*;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESMessageClient {

    private static final String ALGORITHM = "AES";

    public static void main(String[] args) {
        try {
            String keyString;
            String encryptedMessage;

            // Connect to server and receive key
            try (Socket socket = new Socket("localhost", 12345);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Receive AES key from server
                keyString = in.readLine();
                System.out.println("Received AES Key: " + keyString);

                // Message to send
                String originalMessage = "Hello, server!";
                System.out.println("Original message: " + originalMessage);

                // Encrypt the message
                encryptedMessage = encryptMessage(originalMessage, keyString);
                System.out.println("Encrypted message: " + encryptedMessage);

                // Send encrypted message to server
                out.println(encryptedMessage);

                // Read response from server
                String response = in.readLine();
                System.out.println("Server response: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encryptMessage(String message, String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }
}
