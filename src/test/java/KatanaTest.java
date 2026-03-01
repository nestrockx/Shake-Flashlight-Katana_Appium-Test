import io.appium.java_client.AppiumBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KatanaTest extends BaseTest {

    private static final int MAX_PERMISSION_RETRIES = 2;
    private static final Duration WAIT_SHORT = Duration.ofSeconds(2);
    private static final Duration WAIT_STANDARD = Duration.ofSeconds(5);


    @BeforeEach
    public void setup() throws InterruptedException {
        int apiLevel = Integer.parseInt(
                Objects.requireNonNull(driver.getCapabilities().getCapability("deviceApiLevel")).toString()
        );

        if (apiLevel >= 34) {
            WebElement permissionButton = waitForElement(
                    AppiumBy.accessibilityId("notification permission button"), WAIT_STANDARD
            );
            assertTrue(permissionButton.isDisplayed(), "Permission button should be visible");

            Thread.sleep(500);
            clickWithRetry(permissionButton, MAX_PERMISSION_RETRIES, () -> {
                allowPermission();
                allowPermission();
            });

            new WebDriverWait(driver, WAIT_STANDARD).until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            AppiumBy.accessibilityId("notification permission button")
                    )
            );

            WebElement understandButton = waitForElement(
                    AppiumBy.accessibilityId("understand button"), WAIT_STANDARD
            );
            understandButton.click();
        } else if (apiLevel == 33) {
            WebElement permissionButton = waitForElement(
                    AppiumBy.accessibilityId("notification permission button"), WAIT_STANDARD
            );
            assertTrue(permissionButton.isDisplayed(), "Permission button should be visible");

            clickWithRetry(permissionButton, MAX_PERMISSION_RETRIES, this::allowPermission);

            new WebDriverWait(driver, WAIT_STANDARD).until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            AppiumBy.accessibilityId("notification permission button")
                    )
            );

            WebElement understandButton = waitForElement(
                    AppiumBy.accessibilityId("understand button"), WAIT_STANDARD
            );
            understandButton.click();
        } else {
            WebElement startButton = waitForElement(
                    AppiumBy.accessibilityId("start button"), WAIT_STANDARD
            );
            startButton.click();

            WebElement understandButton = waitForElement(
                    AppiumBy.accessibilityId("understand button"), WAIT_STANDARD
            );
            understandButton.click();
        }
    }

    @Test
    public void testSensitivitySlider() {
        WebElement sensitivitySlider = driver.findElement(
                AppiumBy.xpath("//*[contains(@content-desc, 'shake sensitivity slider')]")
        );

        assertEquals("shake sensitivity slider 5", sensitivitySlider.getAttribute("content-desc"));
    }

    @Test
    public void testNavigation() {
        WebElement menuIconButton = driver.findElement(AppiumBy.accessibilityId("menu icon button"));

        menuIconButton.click();

        WebElement backButton = waitForElement(
                AppiumBy.accessibilityId("back icon button"), WAIT_STANDARD
        );

        backButton.click();

        new WebDriverWait(driver, WAIT_STANDARD).until(
                ExpectedConditions.visibilityOfElementLocated(
                        AppiumBy.accessibilityId("menu icon button")
                )
        );
    }

    @Test
    public void testFlashlight() throws InterruptedException {
        WebElement flashlightButton = driver.findElement(AppiumBy.accessibilityId("flashlight button"));
        flashlightButton.click();
        Thread.sleep(1000);
        flashlightButton.click();
    }

    @Test
    public void testVibrationSwitch() throws InterruptedException {
        WebElement vibrationSwitch = driver.findElement(
                AppiumBy.xpath("//*[contains(@content-desc, 'vibrations switch')]")
        );

        vibrationSwitch.click();
        Thread.sleep(1000);
        assertEquals("vibrations switch off", vibrationSwitch.getAttribute("content-desc"));

        vibrationSwitch.click();
        Thread.sleep(1000);
        assertEquals("vibrations switch on", vibrationSwitch.getAttribute("content-desc"));
    }

    @Test
    public void testKatanaService() throws InterruptedException {
        WebElement katanaServiceSwitch = driver.findElement(
                AppiumBy.xpath("//*[contains(@content-desc, 'katana service switch')]")
        );

        Thread.sleep(1000);

        driver.openNotifications();

        WebElement notification = waitForElement(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Phone shake detection is running\")"
                ), WAIT_STANDARD
        );

        assertEquals("Phone shake detection is running", notification.getText());

        WebElement closeAction = waitForElement(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().textContains(\"Turn off\")"
                ), WAIT_STANDARD
        );

        assertEquals("Turn off", closeAction.getText());
        closeAction.click();

        driver.navigate().back();

        Thread.sleep(1000);
        assertEquals("katana service switch off", katanaServiceSwitch.getAttribute("content-desc"));

        katanaServiceSwitch.click();
        Thread.sleep(1000);
        assertEquals("katana service switch on", katanaServiceSwitch.getAttribute("content-desc"));
    }

    private void allowPermission() {
        WebElement allowButton = waitForElement(
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*permission_allow.*\")"),
                WAIT_STANDARD
        );
        allowButton.click();
    }

    private void clickWithRetry(WebElement element, int maxRetries, Runnable action) {
        String tagName = element.getTagName();

        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                element.click();
                action.run();
                return; // Success
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Failed to perform action after retries", e);
                }
                // Re-find the element before retry
                element = waitForElement(AppiumBy.accessibilityId(tagName), WAIT_SHORT);
            }
        }
    }

    private WebElement waitForElement(By locator, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}