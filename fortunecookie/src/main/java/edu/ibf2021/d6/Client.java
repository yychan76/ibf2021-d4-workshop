package edu.ibf2021.d6;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    private String host;
    private int port;
    private final String COOKIE_PREFIX = "cookie-text ";
    private final int RETRY_LIMIT = 100;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException, InterruptedException {
        System.out.println("Starting client...");
        System.out.printf("Connecting to %s at port %d%n", this.host, this.port);
        try (Socket socket = new Socket(this.host, this.port)) {
            System.out.printf("Connected to %s:%d from local port %d%n", socket.getInetAddress(), socket.getPort(), socket.getLocalPort());
            try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

                try (DataInputStream serverResponse = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                    String msg = "";
                    String serverMsg = "";
                    while (!msg.trim().equals("close")) {
                        int retries = 0;
                        System.out.print("> ");
                        msg = reader.readLine();
                        dos.writeUTF(msg);
                        dos.flush();
                        // wait for server response
                        while (serverResponse.available() == 0 && retries < RETRY_LIMIT) {
                            retries++;
                            Thread.sleep(10);
                        }
                        while (serverResponse.available() > 0) {
                            serverMsg = serverResponse.readUTF();
                            // System.out.println("Server response: " + serverMsg);
                            if (serverMsg.startsWith(COOKIE_PREFIX)) {
                                String cookieText = serverMsg.substring(COOKIE_PREFIX.length());
                                System.out.println(cookieText);
                            }
                        }
                    }

                    System.out.println("Closing client connection");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client("localhost", 3000);
        client.start();
    }
}
