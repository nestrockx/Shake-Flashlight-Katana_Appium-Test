import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;

public class BaseTest {

    protected AndroidDriver driver;

    @BeforeEach
    void setUp() throws Exception {

        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName("Android Emulator");

        // OPTION 1 — Use APK file
        options.setApp("/Users/nestrock/AndroidStudioProjects/KatanaFlashlight-shake-phone-android-app/app/debug/app-debug.apk");

        // OPTION 2 — If already installed
        // options.setAppPackage("com.example");
        // options.setAppActivity(".MainActivity");

        driver = new AndroidDriver(
                new URL("http://127.0.0.1:4723"),
                options
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

