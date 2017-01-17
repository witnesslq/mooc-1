package utility;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author: wuke 
 * @date  : 2016年12月4日 下午8:27:52
 * Title  : MongoConn
 * Description : connect to local MongoDB
 */
public class MongodbConn {
	public static MongoCollection<Document> getMongoCollection(String databaseName, String mongoCollectionName) {
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			
			MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
			//System.out.println("Successfully connect to mongodb " + databaseName);
			
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(mongoCollectionName);
			System.out.println("Successfully get collection " + mongoCollectionName + "!");
			
			return mongoCollection;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Get collection" + mongoCollectionName + " failed!");
			return null;
		}
	}
}
