package fc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private String dataFile;
    private ServerSocket serverSocket;


    public Server(int port, String dataFile) {
        this.port = port;
        this.dataFile = dataFile;
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Starting server...");
        try {
            while (!this.serverSocket.isClosed()) {
                Socket socket = this.serverSocket.accept();
                System.out.println("Listening on port: " + socket.getLocalPort());
                CookieClientHandler cookieClientHandler = new CookieClientHandler(socket, this.dataFile);
                System.out.printf("A new client has connected from %s:%d %n", socket.getInetAddress(), socket.getPort());

                Thread thread = new Thread(cookieClientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(3000, "cookie_file.txt");
        server.start();
    }
}
