package edu.ibf2021.d6;

import java.io.IOException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting fortune cookie app...");
        if (args.length > 0) {
            System.out.println(Arrays.toString(args));
            if (args.length == 3) {
                int port = Integer.parseInt(args[1]);
                String dataFile = args[2];
                String module = args[0];
                if (module.equals("fc.Server")) {
                    Server server = new Server(port, dataFile);
                    server.start();
                }
            } else if (args.length == 2) {
                String[] tokens = args[1].split(":");
                int port = Integer.parseInt(tokens[1]);
                String host = tokens[0];
                String module = args[0];
                if (module.equals("fc.Client")) {
                    Client client = new Client(host, port);
                    client.start();
                }
            }
        }
    }
}
