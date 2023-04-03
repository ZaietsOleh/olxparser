package com.olehzaiets.olxparser.common;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
public class WebDriverManagerWrapper {

    public static void setup() {
        WebDriverManager.chromedriver().setup();
    }

    public WebDriver createChromeWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }
}
