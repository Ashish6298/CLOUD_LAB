import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESMessageServer {

    private static final String ALGORITHM = "AES";
    private static final String STATIC_KEY_STRING = "QvZJafkl7MLEePlcRK7lCA=="; // Static key for both server and client

    public static void main(String[] args) {
        try {
            System.out.println("Server AES Key: " + STATIC_KEY_STRING);

            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server listening on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Read encrypted message from client
                String encryptedMessage = in.readLine();
                System.out.println("Received encrypted message: " + encryptedMessage);

                String response;
                try {
                    // Decrypt message
                    String originalMessage = decryptMessage(encryptedMessage, STATIC_KEY_STRING);
                    response = "Message received: " + originalMessage;
                } catch (Exception e) {
                    response = "Error decrypting message: " + e.getMessage();
                    e.printStackTrace();
                }

                // Send response
                out.println(response);

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String decryptMessage(String encryptedMessage, String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
        byte[] originalMessageBytes = cipher.doFinal(decodedMessage);
        return new String(originalMessageBytes);
    }
}
