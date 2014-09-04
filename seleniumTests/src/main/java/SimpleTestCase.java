import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SimpleTestCase {
    private static int port = 4443;
    private static String SERVER_LINC = "http://172.18.67.72:" + port + "/wd/hub";
    private static DesiredCapabilities browser = DesiredCapabilities.firefox();


    public static void main(String args[]) throws Exception{
        WebDriver webDriver = new RemoteWebDriver(new URL(SERVER_LINC), browser);
        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        webDriver.get("http://ok.ru");

        webDriver.findElement(By.id("field_email")).clear();
        webDriver.findElement(By.id("field_email")).sendKeys("port:" + port);

        Thread.sleep(5000);

        if (webDriver != null) {
            webDriver.quit();
        }
    }
}