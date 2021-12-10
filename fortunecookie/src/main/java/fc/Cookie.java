package fc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Cookie {
    private Path dataFile;
    private List<String> cookies;
    private List<Integer> seenIdx;

    public Cookie(String dataFile) {
        this.dataFile = Paths.get(dataFile);
        this.seenIdx = new ArrayList<>();
    }

    public void load() throws IOException {
        if (Files.isRegularFile(this.dataFile)) {
            System.out.println("Reading cookies from " + this.dataFile.toString());
            this.cookies = Files.readAllLines(this.dataFile);
        } else {
            System.err.println(this.dataFile.toString() + " cannot be found!\n>> Using default cookies store...");
            this.cookies = new ArrayList<>(Arrays.asList("Sometimes cookie jars cannot be found", "Live your life as you wish, and don't worry about fortune from a cookie"));
        }
    }

    public String get() {
        if (this.seenIdx.size() == this.cookies.size()) {
            // we have already exhausted the list, reset it
            this.seenIdx.clear();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(this.cookies.size());
        while (seenIdx.contains(randomIndex) && seenIdx.size() != this.cookies.size()) {
            // don't get the same cookie twice without exhausting the entire cookies list
            randomIndex = ThreadLocalRandom.current().nextInt(this.cookies.size());
        }

        String cookie = this.cookies.get(randomIndex);
        seenIdx.add(randomIndex);
        return cookie;
    }

    public int size() {
        if (this.cookies != null) {
            try {
                return this.cookies.size();
            } catch (NullPointerException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public boolean hasNext() {
        return this.seenIdx.size() < this.cookies.size();
    }

    public static void main(String[] args) throws IOException {
        Cookie cookie = new Cookie("cookie_file.txt");
        cookie.load();
        while(cookie.hasNext()) {
            System.out.println(cookie.get());
        }
        for (int i = 0; i < 40; i++) {
            System.out.println(i+1 + ". " + cookie.get());
        }
    }
}
