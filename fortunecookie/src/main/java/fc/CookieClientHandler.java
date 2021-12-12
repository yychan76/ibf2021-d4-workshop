package fc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class CookieClientHandler implements Runnable {
    private String dataFile;
    private Socket socket;
    private Cookie cookie;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public CookieClientHandler(Socket socket, String dataFile) {
        try {
            this.socket = socket;
            this.dataFile = dataFile;
            this.cookie = new Cookie(this.dataFile);
            cookie.load();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeAll(this.socket, this.bufferedReader, this.bufferedWriter, e);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (this.socket.isConnected()) {
            try {
                messageFromClient = this.bufferedReader.readLine();
                System.out.printf("Received from [%s:%d]: %s%n", this.socket.getInetAddress(), this.socket.getPort(), messageFromClient);

                if (messageFromClient != null && "get-cookie".equals(messageFromClient.trim())) {
                    sendCookie();
                }

                if (messageFromClient != null && "close".equals(messageFromClient.trim())) {
                    closeAll(this.socket, this.bufferedReader, this.bufferedWriter, null);
                    break;
                }
            } catch (IOException e) {
                closeAll(this.socket, this.bufferedReader, this.bufferedWriter, e);
                break;
            }
        }
    }

    private void sendCookie() {
        System.out.printf("Sending cookie to client [%s:%d]...%n", this.socket.getInetAddress(), this.socket.getPort());
        String msgToClient = "cookie-text " + this.cookie.get();
        System.out.println("└─: " + msgToClient);
        try {
            this.bufferedWriter.write(msgToClient);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException e) {
            closeAll(this.socket, this.bufferedReader, this.bufferedWriter, e);
        }
    }

    private void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter, Exception exception) {
        if (exception != null) {
            exception.printStackTrace();
        }
        System.out.println("Closing server client handler");
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

}
