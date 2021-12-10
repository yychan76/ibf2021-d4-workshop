package fc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
    private String host;
    private int port;
    private final String COOKIE_PREFIX = "cookie-text ";
    private final int RETRY_LIMIT = 100;
    private final long CONNECT_RETRY_WAIT_MS = 1000;
    private final long RESPONSE_RETRY_WAIT_MS = 10;
    private Socket socket;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException, InterruptedException {
        System.out.println("Starting client...");
        System.out.printf("Connecting to %s at port %d%n", this.host, this.port);
        do {
            try {
                this.socket = new Socket(this.host, this.port);
            } catch (ConnectException e) {
                System.err.printf("Failed to connect to %s:%d. Retrying in %d ms%n", this.host, this.port, CONNECT_RETRY_WAIT_MS);
                try {
                    Thread.sleep(CONNECT_RETRY_WAIT_MS);
                } catch (InterruptedException interruptedException) {

                }
            }
        } while (this.socket == null);

        System.out.printf("Connected to %s:%d from local port %d%n", this.socket.getInetAddress(), this.socket.getPort(), this.socket.getLocalPort());
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()))) {

            try (DataInputStream serverResponse = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()))) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                String msg = "";
                String serverMsg = "";
                while (!"close".equals(msg.trim())) {
                    int retries = 0;
                    System.out.print("> ");
                    msg = reader.readLine();
                    dos.writeUTF(msg);
                    dos.flush();
                    // wait for server response
                    while (serverResponse.available() == 0 && retries < RETRY_LIMIT) {
                        retries++;
                        Thread.sleep(RESPONSE_RETRY_WAIT_MS);
                    }
                    while (serverResponse.available() > 0) {
                        serverMsg = serverResponse.readUTF();
                        // System.out.println("Server response: " + serverMsg);
                        if (serverMsg.startsWith(COOKIE_PREFIX)) {
                            String cookieText = serverMsg.substring(COOKIE_PREFIX.length());
                            System.out.println("╘═{*} " + cookieText);
                        }
                    }
                }

                System.out.println("Closing client connection");
                this.socket.close();
            }
        }


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client("localhost", 3000);
        client.start();
    }
}
