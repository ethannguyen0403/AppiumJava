package com.ethan.common;

import com.ethan.drivers.DriverManager;
import com.ethan.helpers.SystemHelpers;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    private AppiumDriverLocalService service;
    private String HOST = "127.0.0.1";
    private String PORT = "4723";
    private int TIMEOUT_SERVICE = 60;

    @BeforeSuite
    public void runAppiumServer() {
        // Print ANDROID_HOME for debugging
        System.out.println("##### ANDROID_HOME: " + System.getenv("ANDROID_HOME"));

        // If ANDROID_HOME is not set, try setting it manually
        if (System.getenv("ANDROID_HOME") == null) {
            System.setProperty("ANDROID_HOME", "/Users/your-username/Library/Android/sdk");
        }

        loadBashProfile();

        //Kill process on port
        SystemHelpers.killProcessOnPort("4723");

        //Build the Appium service
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withIPAddress(HOST);
        builder.usingPort(Integer.parseInt(PORT));
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "info"); // Set log level (optional)
        builder.withTimeout(Duration.ofSeconds(TIMEOUT_SERVICE));

        //Start the server with the builder
        service = AppiumDriverLocalService.buildService(builder);
        service.start();

        if (service.isRunning()) {
            System.out.println("##### Appium server started on " + HOST + ":" + PORT);
        } else {
            System.out.println("Failed to start Appium server.");
        }

    }

    @BeforeTest
    public void setUpDriver() {
        AppiumDriver driver;
        UiAutomator2Options options = new UiAutomator2Options();

        System.out.println("***SERVER ADDRESS: " + HOST);
        System.out.println("***SERVER POST: " + PORT);

        options.setPlatformName("Android");
        options.setPlatformVersion("14");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName("K_Pin");
        options.setAppPackage("com.anhtester.mobile_app.taurus");
        options.setAppActivity("com.anhtester.mobile_app.taurus.MainActivity");
        options.setNoReset(false);
        options.setFullReset(false);

        try {
            driver = new AppiumDriver(new URL("http://" + HOST + ":" + PORT), options);
            DriverManager.setDriver(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

    }

    @AfterTest
    public void tearDownDriver() {
        if (DriverManager.getDriver() != null) {
            DriverManager.quitDriver();
        }
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("##### Appium server stopped.");
        }
    }

    public void sleep(double second) {
        try {
            Thread.sleep((long) (1000 * second));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadBashProfile() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            // Run 'source ~/.bash_profile' and print environment variables
            processBuilder.command("bash", "-c", "source ~/.bash_profile && env");
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Map<String, String> envVars = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Debugging: Print all environment variables
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envVars.put(parts[0], parts[1]);
                }
            }

            process.waitFor();

            // Apply loaded environment variables to the Java process
            for (Map.Entry<String, String> entry : envVars.entrySet()) {
                System.setProperty(entry.getKey(), entry.getValue());
            }

            System.out.println("##### Successfully loaded .bash_profile");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("##### Failed to load .bash_profile");
        }
    }
}
