package fc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    void testApp() {
        assertEquals(1, 1);
    }

    @Test
    void launchApp_LaunchWithInvalidArguments_ShouldThrowIllegalArgumentsException() throws IOException, InterruptedException {
        String[] args = new String[] {""};
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            App.main(args);
        });
        System.out.println(exception.getMessage());
    }
}
