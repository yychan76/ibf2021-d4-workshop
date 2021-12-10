package fc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CookieTest {
    // testData will go into a new text file for testing
    private List<String> testData = new ArrayList<String>(Arrays.asList("first cookie", "second cookie", "third cookie"));
    private String validDataFileName;
    private String missingDataFileName;

    static Class typeof(Object a) {
        return a.getClass();
    }

    @BeforeEach
    public void setUp() throws Exception {
        validDataFileName = "new_test_cookie.txt";
        missingDataFileName = "missing.txt";
        Path validDataFile = Paths.get(validDataFileName);
        Path missingDataFile = Paths.get(missingDataFileName);
        if (validDataFile.toFile().exists()) {
            validDataFile.toFile().delete();
        }
        if (missingDataFile.toFile().exists()) {
            missingDataFile.toFile().delete();
        }
        Files.write(validDataFile, testData);
    }

    @AfterEach
    public void cleanup() {
        Path validDataFile = Paths.get(validDataFileName);
        Path missingDataFile = Paths.get(missingDataFileName);
        if (validDataFile.toFile().exists()) {
            validDataFile.toFile().delete();
        }
        if (missingDataFile.toFile().exists()) {
            missingDataFile.toFile().delete();
        }
    }

    @Test
    public void instantiateCookie_CreateWithValidFile_ShouldCreateInstance() {
        assumeTrue(Paths.get(validDataFileName).toFile().exists());
        Cookie cookie = new Cookie(validDataFileName);
        assertNotNull(cookie);
        assertEquals(0, cookie.size());
    }

    @Test
    public void instantiateCookie_CreateWithMissingFile_ShouldCreateInstance() {
        Path newDataFile = Paths.get(missingDataFileName);
        if (newDataFile.toFile().exists()) {
            newDataFile.toFile().delete();
        }
        Cookie cookie = new Cookie(missingDataFileName);
        assertNotNull(cookie);
        assertEquals(0, cookie.size());
    }

    @Test
    public void instantiateCookie_CreateWithFileWithData_ShouldCreateInstance() throws IOException {
        Cookie cookie = new Cookie(validDataFileName);
        assertNotNull(cookie);
        assertEquals(0, cookie.size());
        cookie.load();
        assertEquals(testData.size(), cookie.size());
    }

    @Test
    public void getCookie_CreateWithValidFile_ShouldGetCookieString() throws IOException {
        Cookie cookie = new Cookie(validDataFileName);
        cookie.load();
        String text = cookie.get();
        assertTrue(typeof(text).equals(String.class));
        assertTrue(testData.contains(text));
    }

    @Test
    public void getCookie_CreateWithMissingFile_ShouldGetString() throws IOException {
        Cookie cookie = new Cookie(missingDataFileName);
        cookie.load();
        String text = cookie.get();
        assertTrue(typeof(text).equals(String.class));
        // the default cookie values should not match the test data
        assertFalse(testData.contains(text));
    }

}
