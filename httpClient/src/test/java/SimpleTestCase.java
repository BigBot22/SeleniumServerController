import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SimpleTestCase {
    private static int port = 4444;
    private static String SERVER_LINC = "http://127.0.0.1:" + port + "/wd/hub";
    private static DesiredCapabilities browser = DesiredCapabilities.firefox();
    private WebDriver webDriver;


//    @Test
    public void test() throws Exception{
        webDriver = new RemoteWebDriver(new URL(SERVER_LINC), browser);
        webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        webDriver.get("http://ok.ru");

        webDriver.findElement(By.id("field_email")).clear();
        webDriver.findElement(By.id("field_email")).sendKeys("port:" + port);

        Thread.sleep(1000);
    }

//    @After
    public void after() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}