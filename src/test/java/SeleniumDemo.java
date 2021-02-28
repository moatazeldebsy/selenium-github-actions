import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;
import static org.testng.Assert.assertEquals;

public class SeleniumDemo {
    public String username, password;
    //https://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.navigate().to("https://the-internet.herokuapp.com/login");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.MILLISECONDS);

        // Setup Connection
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("seleniumdemo");
        MongoCollection<Document> collection = database.getCollection("user");
        assertEquals("seleniumdemo", database.getName());

        // Create Operation
        Document doc = new Document("password", "SuperSecretPassword!")
                .append("username", "tomsmith");
        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        System.out.println(id);

        doc = collection.find(eq("_id", new ObjectId(id.toString())))
                .first();
        assert doc != null;
        password = doc.get("password").toString();
        username = doc.get("username").toString();
        System.out.println(password);
        System.out.println(username);
    }

    @Test
    public void userLogin() {
        WebElement usernameTxt = driver.findElement(By.id("username"));
        usernameTxt.sendKeys(username);
        WebElement passwordTxt = driver.findElement(By.id("password"));
        passwordTxt.sendKeys(password);
        WebElement submitBtn = driver.findElement(By.className("radius"));
        submitBtn.click();
        System.out.println("Current URL is:" + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().contains("secure"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
