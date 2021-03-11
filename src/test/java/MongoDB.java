import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class MongoDB {
    public static void main(String[] args) {

        // Connection Setup
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("demoDB");
        MongoCollection<Document> collection = database.getCollection("demoCollection");

        // CRUD Operations
        // Create Operation
        Document doc = new Document("password", "123456")
                .append("username", "moataznabil");
        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        System.out.println(id);

        // Update Operation
        collection.updateOne(
                eq("_id", new ObjectId(id.toString())),
                combine(set("username", "tomsmith")
                        , set("password", "SuperSecretPassword!"),
                        currentDate("lastModified")),
                new UpdateOptions()
                        .upsert(true)
                        .bypassDocumentValidation(true));
        doc = collection.find(eq("_id", new ObjectId(id.toString())))
                .first();
        assert doc != null;
        String password = doc.get("password").toString();
       String username = doc.get("username").toString();
       System.out.println(password);
       System.out.println(username);

       // Retrieve All
       try (MongoCursor<Document> cursor = collection.find().iterator()) {
           while (cursor.hasNext()) {
               System.out.println(cursor.next().toJson());
           }
       }
       // Delete Operation
       // collection.deleteOne(eq("_id", new ObjectId(id.toString())));

       // Retrieve Count
        System.out.println(collection.countDocuments());
    }
}