package fc;

import java.io.IOException;
import java.util.Arrays;

public class App {

    private static final int NUM_ARGS_FOR_SERVER = 3;
    private static final int NUM_ARGS_FOR_CLIENT = 2;
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean isValidArgs = true;

        System.out.println("Starting fortune cookie app...");
        if (args.length > 0) {
            System.out.println(Arrays.toString(args));
            int port;
            String module;
            switch (args.length) {
                case NUM_ARGS_FOR_SERVER:
                    port = Integer.parseInt(args[1]);
                    String dataFile = args[2];
                    module = args[0];
                    if (module.equals("fc.Server")) {
                        Server server = new Server(port, dataFile);
                        server.start();
                    }
                    break;
                case NUM_ARGS_FOR_CLIENT:
                    String[] tokens = args[1].split(":");
                    port = Integer.parseInt(tokens[1]);
                    String host = tokens[0];
                    module = args[0];
                    if (module.equals("fc.Client")) {
                        Client client = new Client(host, port);
                        client.start();
                    }
                    break;
                default:
                    isValidArgs = false;
                    break;

            }
        } else {
            isValidArgs = false;
        }

        if (!isValidArgs) {
            System.out.println("""
                Wrong number of arguments supplied
                To launch server, please run this with java -jar fortunecookie.jar fc.Server <port> <data_file>
                To launch client, please run this with java -jar fortunecookie.jar fc.Client <hostname>:<port>
            """);
            throw new IllegalArgumentException();
        }
    }
}
