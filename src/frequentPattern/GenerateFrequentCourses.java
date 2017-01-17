package frequentPattern;

import java.util.ArrayList;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import utility.MongodbConn;

/**
 * @author: wuke 
 * @date  : 2016年12月22日 上午9:41:33
 * Title  : GenerateFrequentCourses
 * Description : 
 */
public class GenerateFrequentCourses {
	private static final String MONGODB_NAME = "mooc";
	private static final String COLLECTION_NAME = "frequentCourses";
	
	public static void main(String[] args) {
		ArrayList<String> dataList = GenerateAprioriDataset.generateRecords();
		
		Map<String, Integer> frequentOneItemsetMap = null;
		frequentOneItemsetMap = MyApriori.findFrequentOneItemset(dataList);
		
		Map<String, Integer> frequentTwoItemsetMap = null;
		frequentTwoItemsetMap = MyApriori.countCandidateTwoItemset(dataList, frequentOneItemsetMap);
		
		GenerateFrequentCourses.storeFrequentCoursesMongobd(frequentTwoItemsetMap);
	}
	
	static void storeFrequentCoursesMongobd(Map<String, Integer> frequentTwoItemsetMap) {
		MongoCollection<Document> collection = MongodbConn.getMongoCollection(MONGODB_NAME, COLLECTION_NAME);
		
		String course1 = "";
		String course2 = "";
		int count = 0;
		for(Map.Entry<String, Integer> entry : frequentTwoItemsetMap.entrySet()) {
			Document doc = new Document();
			
			course1 = entry.getKey().split(",")[0];
			course2 = entry.getKey().split(",")[1];
			count = entry.getValue();
			
			doc.append("course1", course1);
			doc.append("course2", course2);
			doc.append("count", count);
			
			collection.insertOne(doc);
		}
	}
}
