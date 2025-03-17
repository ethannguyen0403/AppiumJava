package com.ethan.testcases;

import com.ethan.common.BaseTest;
import com.ethan.common.BaseTest;
import com.ethan.drivers.DriverManager;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class DemoInputButton extends BaseTest {

    @Test
    public void testLoginTaurusApp() throws InterruptedException {
        Thread.sleep(2000);
        WebElement inputUsername = DriverManager.getDriver().findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"Mobile App Flutter Beta\"]/following-sibling::android.widget.EditText[1]"));
        inputUsername.click();
        inputUsername.sendKeys("admin");
        Thread.sleep(1000);
        WebElement inputPassword = DriverManager.getDriver().findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"Mobile App Flutter Beta\"]/following-sibling::android.widget.EditText[2]"));
        inputPassword.click();
        inputPassword.sendKeys("admin");

        DriverManager.getDriver().findElement(AppiumBy.xpath("//android.widget.Button[@content-desc=\"Sign in\"]")).click();

        Thread.sleep(2000);
        WebElement menuMenu = DriverManager.getDriver().findElement(AppiumBy.accessibilityId("Menu"));
        if(menuMenu.isDisplayed()){
            System.out.println("Login success.");
        }else {
            System.out.println("Login failed.");
        }
    }
}
