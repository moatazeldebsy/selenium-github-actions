import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;
import static org.testng.Assert.assertEquals;

public class TestBase {
    public String databaseName = "seleniumdemo" ,
            collectionName = "user"
            , mongoDBServerURL = "localhost";
    public int mongoDBPort = 27017;

    public String username = "tomsmith", password = "SuperSecretPassword!";
    public String baseURL = "https://the-internet.herokuapp.com/login";

    protected WebDriver driver;
    LoginPageObject loginPage;

    @BeforeSuite
    public void setUp() {
        mongoDBSetup();
        browserSetup();
    }

    private void browserSetup(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // for CI Purpose
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.navigate().to(baseURL);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.MILLISECONDS);
    }

    private void mongoDBSetup(){
        // Setup Mongo DB Connection
        MongoClient mongoClient = new MongoClient(mongoDBServerURL, mongoDBPort);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        assertEquals(databaseName, database.getName());

        // Create Document
        Document doc = new Document("password", password)
                .append("username", username);
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

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
