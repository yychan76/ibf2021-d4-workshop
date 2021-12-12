package fc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
    private final String COOKIE_PREFIX = "cookie-text ";
    private final String PROMPT = "> ";
    private final long CONNECT_RETRY_WAIT_MS = 1000;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        System.out.println("Starting client...");
        System.out.printf("Connecting to %s at port %d%n", this.host, this.port);
        do {
            try {
                this.socket = new Socket(this.host, this.port);
                this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            } catch (ConnectException connectionException) {
                System.err.printf("Failed to connect to %s:%d. Retrying in %d ms%n", this.host, this.port, CONNECT_RETRY_WAIT_MS);
                try {
                    Thread.sleep(CONNECT_RETRY_WAIT_MS);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (IOException ioException) {
                closeAll(this.socket, this.bufferedReader, this.bufferedWriter, ioException);
            }
        } while (this.socket == null);
        System.out.printf("Connected to %s:%d from local port %d%n", this.socket.getInetAddress(), this.socket.getPort(), this.socket.getLocalPort());
    }

    public void sendCommands(){
        Scanner scanner = new Scanner(System.in);
        String command = "";
        try {
            while (!"close".equals(command.trim()) && this.socket.isConnected()) {
                if (!"get-cookie".equals(command.trim())) {
                    // if user typed get-cookie command, wait to display the cookie text
                    System.out.print(PROMPT);
                }
                command = scanner.nextLine();
                this.bufferedWriter.write(command);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(this.socket, this.bufferedReader, this.bufferedWriter, e);
        }
        scanner.close();
    }

    public void listenForResponse() {
        new Thread(() -> {
            String serverResponse;
            while (this.socket.isConnected()) {
                try {
                    serverResponse = this.bufferedReader.readLine();
                    if (serverResponse.startsWith(COOKIE_PREFIX)) {
                        String cookieText = serverResponse.substring(COOKIE_PREFIX.length());
                        System.out.println("╘═{*} " + cookieText);
                        // show the prompt for the next command
                        System.out.print(PROMPT);
                    }
                } catch (IOException e) {
                    closeAll(this.socket, this.bufferedReader, this.bufferedWriter, e);
                }
            }
        }).start();
    }

    private void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter, Exception exception) {
        exception.printStackTrace();
        System.out.println("Closing client connection");
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            String[] tokens = args[0].split(":");
            String host = tokens[0];
            int port = Integer.parseInt(tokens[1]);

            Client client = new Client(host, port);
            client.listenForResponse();
            client.sendCommands();
        }
    }
}
