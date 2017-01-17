package courseProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.bson.Document;

import utility.MongodbConn;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author: wuke 
 * @date  : 2016年11月23日 下午5:36:47
 * Title  : CrawlerGetAllCourseData
 * Description : Get all courses' data from "http://www.mooc2u.com/API/Open/CourseOpen/GetAllCourseData", 
 *               and store them into mongodb
 * Problem : BufferedReader 大小限制 会不会有影响？？？
 */
public class CrawlerGetCoursesStoreMongodb {
	
	public static void main(String[] args) {
		String url = "http://www.mooc2u.com/API/Open/CourseOpen/GetAllCourseData";
		String strCourses = "";
		
		strCourses = getCourses(url, "utf-8");
		storeCoursesIntoMongodb(strCourses);
	}
	
	/**
	 * get all the content and store them into a String 
	 */
	static String getCourses(String url,String param) {
		BufferedReader br = null;
		InputStreamReader isr = null;
		String strCourses = "";
		
		try {
			URL coursesUrl = new URL(url);
			
			isr = new InputStreamReader(coursesUrl.openStream(), param);
			br = new BufferedReader(isr);

			strCourses = br.readLine();
			//System.out.println(strCourses);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return strCourses;
	}
	
	/**
	 * store the String, which actually is a big json object, 
	 * which contains a json array, in which every json object is one course, into mongodb
	 */
	static void storeCoursesIntoMongodb(String strCourses) {
		// transform the String into a Json object, 
		// and then extract the json array from the json object
		JSONObject courses = JSONObject.fromObject(strCourses);
		JSONArray jsonArr = courses.getJSONArray("Data");
		
		// connect to the mongodb mooc, and get it's collection courses
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "courses");
		
		// delete the old data
		/*MongoCursor<Document> cursor = collections.find().iterator();
		try {
			while(cursor.hasNext()) {
				collections.deleteOne(cursor.next());
			}
		} finally {
			cursor.close();
		}*/
		collection.drop();
		
		collection = MongodbConn.getMongoCollection("mooc", "courses");
		//System.out.println(collection.count());
		
		// store the json array into the collection courses one by one
		// transform json object into one mongodb document
		Document document = null;
		for(int i = 0; i < jsonArr.size(); i++) {
			document = Document.parse(jsonArr.getJSONObject(i).toString());
			collection.insertOne(document);
		}
	}
}
