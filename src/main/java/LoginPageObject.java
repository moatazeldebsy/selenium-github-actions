import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPageObject {
    WebDriver driver;
    By UserName = By.id("username");
    By Password = By.id("password");
    By LoginBtn = By.className("radius");

    public LoginPageObject(WebDriver driver) {
        this.driver = driver;
    }

    public void typeEmailId(String username) {
        driver.findElement(UserName).sendKeys(username);
    }

    public void typePassword(String password) {
        driver.findElement(Password).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(LoginBtn).click();
    }
}
