package utility;

import java.util.TreeSet;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

import bean.UserLearnedCourses;

/**
 * @author: wuke 
 * @date  : 2016年12月14日 下午8:15:25
 * Title  : JavaBeanToDocument
 * Description : 
 */
public class JavaBeanToDocument {

	public static void main(String[] args) {
		UserLearnedCourses userCourses = new UserLearnedCourses();
		
		userCourses.setUserId("001");
		TreeSet<String> set = new TreeSet<String>();
		set.add("a");
		set.add("b");
		userCourses.setCoursesSet(set);
		
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		
		Gson gson = new Gson();
		String jsonStr = gson.toJson(userCourses);
		System.out.println(jsonStr);
		
		Document doc = Document.parse(jsonStr);
		
		collection.insertOne(doc);
	}
}
