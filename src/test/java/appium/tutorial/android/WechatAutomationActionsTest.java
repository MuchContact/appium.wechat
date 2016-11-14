package appium.tutorial.android;

import appium.tutorial.android.util.AppiumTest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.appium.java_client.android.AndroidKeyCode;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static appium.tutorial.android.util.Helpers.*;

public class WechatAutomationActionsTest extends AppiumTest {
    @Test
    public void should_send_msg_to_a_contact() throws Exception {
        openSearchWindow();
        String directory = System.getenv("MSG_PATH");
        readMsgsAndSendMsgs(new File(directory));

        Thread.sleep(2000);
    }

    private void readMsgsAndSendMsgs(final File directory) {
        for (final File file : directory.listFiles()) {
            if (file.isFile()) {
                try {
                    String content = FileUtils.readFileToString(file);
                    Map<String, String> map = new Gson().fromJson(content, Map.class);
                    String to = map.get("to");
                    String message = map.get("msg");
                    if (to == null || message == null) {
                        file.delete();
                        continue;
                    }
                    searchAndOpenChatWndForContact(to);
                    sendMsg(message);
                    back2Main();
                    String newFile = String.format("%s/done/%s/%s", file.getParent(), getDate(), file.getName());
                    String dir = String.format("%s/done/%s", file.getParent(), getDate());
                    Path targetPath = Paths.get(dir);
                    if (!Files.exists(targetPath)) {
                        Files.createDirectories(targetPath);
                    }
                    Files.move(Paths.get(file.getPath()), Paths.get(newFile), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                } catch (JsonSyntaxException jse) {
                    jse.printStackTrace();
                    file.delete();
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }

    }

    private void back2Main() throws InterruptedException {
        driver.sendKeyEvent(AndroidKeyCode.BACK);
    }

    private String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    @Test
    public void should_login_with_qq_account() throws Exception {
        goToLoginPage();
        chooseLoginWay();
        inputUsernameAndPassword();
        clickLogin();

        Thread.sleep(30000);
    }

    @Ignore
    public void should_open_wifi() throws Exception {
        WebElement wlan = text("WLAN");
        wlan.click();
        elements(By.className("android.widget.Switch")).get(0).click();
        text("muco").click();
        WebElement searchContent = elements(By.className("android.widget.EditText")).get(0);
        searchContent.click();
        searchContent.sendKeys("wifi_password");
        element(for_find("确定")).click();
        Thread.sleep(30000);
    }

    @Ignore
    public void should_setup_language() throws Exception {
        WebElement language = text("语言和键盘");
        language.click();
        List<WebElement> searches = elements(By.className("android.widget.CheckBox"));
        WebElement webElement = searches.get(2);
        webElement.click();
        WebElement element = element(for_find("确定"));
        element.click();
    }

    private void openSearchWindow() {
        WebElement search = element(for_find("搜索"));
        search.click();
    }

    private void sendMsg(String msg) {
        WebElement webElement = elements(By.className("android.widget.EditText")).get(0);
        String text = webElement.getText();
        webElement.click();
        int i = 0;
        while (text != null && text.length() > 0 && i < text.length()) {
            driver.sendKeyEvent(67);
            i++;
        }
        webElement.sendKeys(msg);
        clickToSend();
    }

    private void searchAndOpenChatWndForContact(String contactName) {
        WebElement searchContent = elements(By.className("android.widget.EditText")).get(0);
        searchContent.click();
        String text = searchContent.getText();
        int i = 0;
        while (i < text.length()) {
            driver.sendKeyEvent(67);
            i++;
        }
        searchContent.sendKeys(contactName);
        text(contactName).click();
    }

    private void inputUsernameAndPassword() {
        List<WebElement> searches = elements(By.className("android.widget.EditText"));
        WebElement username = searches.get(0);
        // TODO change to your own authentication pair
        username.sendKeys("username");
        WebElement password = searches.get(1);
        password.sendKeys("password");
    }

    private void chooseLoginWay() {
        elements(By.className("android.widget.TextView")).get(0).click();
        element(for_find("使用其他方式登录")).click();
    }

    private void goToLoginPage() {
        clickFirstButtonOnPage();
    }

    private void clickLogin() {
        clickFirstButtonOnPage();
    }

    private void clickToSend() {
        clickFirstButtonOnPage();
    }

    private void clickFirstButtonOnPage() {
        elements(By.className("android.widget.Button")).get(0).click();
    }

}