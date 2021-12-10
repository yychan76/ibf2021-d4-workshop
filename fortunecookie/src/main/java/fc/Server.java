package fc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private String dataFile;

    public Server(int port, String dataFile) {
        this.port = port;
        this.dataFile = dataFile;
    }

    public void start() throws IOException {
        System.out.println("Starting server...");
        Cookie cookie = new Cookie(dataFile);
        cookie.load();
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            Socket clientSocket = serverSocket.accept();
            // PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))) {

                System.out.println("Listening on port: " + clientSocket.getLocalPort());


                try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()))) {

                    String msg = "";
                    while (!"close".equals(msg.trim())) {
                        msg = inputStream.readUTF();
                        System.out.println("Received: " + msg);

                        if ("get-cookie".equals(msg)) {
                            System.out.println("Sending cookie to client...");
                            String msgToClient = "cookie-text " + cookie.get();
                            System.out.println("└─: " + msgToClient);
                            out.writeUTF(msgToClient);
                            out.flush();
                        }
                    }
                    System.out.println("Client closed the connection");

                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(3000, "cookie_file.txt");
        server.start();
    }
}
