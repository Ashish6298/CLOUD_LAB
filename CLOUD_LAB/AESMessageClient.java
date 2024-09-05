import java.io.*;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESMessageClient {

    private static final String ALGORITHM = "AES";
    private static final String STATIC_KEY_STRING = "QvZJafkl7MLEePlcRK7lCA=="; // Static key for both server and client

    public static void main(String[] args) {
        try {
            // Message to send
            String originalMessage = "Hello, server!";
            System.out.println("Original message: " + originalMessage);

            // Encrypt the message
            String encryptedMessage = encryptMessage(originalMessage, STATIC_KEY_STRING);
            System.out.println("Encrypted message: " + encryptedMessage);

            // Send encrypted message to server
            String response = sendTcpRequest("localhost", 12345, encryptedMessage);
            System.out.println("Server response: " + response);

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

    private static String sendTcpRequest(String serverAddress, int port, String encryptedMessage) throws IOException {
        StringBuilder response = new StringBuilder();
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send encrypted message to server
            out.println(encryptedMessage);

            // Read response from server
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }
}
