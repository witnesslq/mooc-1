package frequentPattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import utility.MongodbConn;
import bean.UserLearnedCourses;

/**
 * @author: wuke 
 * @date  : 2016年12月12日 下午9:06:17
 * Title  : GenerateUserLearnedCoursesSets
 * Description : generate records of user-learned-courses, like:
 * {
 * 	  "_id" : ObjectId("5857a528d51d2e46cc5ea749"),
 * 	  "userId" : "b5cfa615-35b0-43ca-a856-6e300949b0f3",
 * 	  "coursesSet" : [
 * 		  "4800fd2b-c9da-4994-af88-95de7c2ef980",
 * 		  "53be568c-af84-4e4d-93f1-b8a4c657598d",
 * 		  "55e5bc18-b09b-4964-bf3e-69dabd1957d4",
 * 		  "65fe9ec6-c084-43a6-970c-97b71a5edba9",
 * 		  "c56bc1f9-cbad-4c55-8a0e-6403bceef936"
 * 	  ]
 * }
 */
public class GenerateUserLearnedCoursesSets {
	static String PATH = "E:\\data\\mooc_logs\\"; // the catalog where logs are stored
	
	/**
	 * 
	 * @param args   
	 */
	public static void main(String[] args) {
		/*String date = "2016-06-08";
		generateOneDayUserLearnedCourses(date);*/
		batchProcessLogs(PATH);
	}

	/**
	 * batch processing logs files in one folder 
	 * by calling the method generateOneDayUserLearnedCourses(date)  
	 * @param path
	 */
	static void batchProcessLogs(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		int i;
		String date;
		for(i = 0; i < array.length; i++) {
			if(array[i].isFile()) {
				date = array[i].getName().split("\\.")[0]; // 在字符串里，转义字符要加两个\
				
				generateOneDayUserLearnedCourses(date);
				System.out.println("*************************" + date + "*************************");
			} else {
				 System.out.println(array[i].getPath() + " is not a file!");
			 }
		}
	}
	
	/**
	 * generateOneDayUserLearnedCourses
	 * firstly, read one day's log -> List<Document> logsDocuments, 
	 * secondly, process the Document list -> Map<String, TreeSet<String>> userCoursesMap,
	 * thirdly, read the history records of user-learned-courses -> Map<String, ArrayList<String>> historyUserCoursesMap,
	 * then merge the new and histoty records of user-learned-courses -> Map<String, ArrayList<String>> newUserCoursesMap,
	 * finally, store the new records into mongodb mooc.userCourses
	 * @param date
	 */
	static void generateOneDayUserLearnedCourses(String date) {		
		List<Document> logsDocuments = readOneDayLogs(date);
		Map<String, TreeSet<String>> userCoursesMap = processOneDayLogs(logsDocuments);
		/*for(Map.Entry<String, TreeSet<String>> entry : userCoursesMap.entrySet()) {
			System.out.println("KEY = " + entry.getKey() + ", VALUE = " + entry.getValue());
		}*/
		
		Map<String, ArrayList<String>> historyUserCoursesMap = readHistoryUserCoursesFromMongodb();
		
		Map<String, ArrayList<String>> newUserCoursesMap = mergeUserCourses(historyUserCoursesMap, userCoursesMap);
		
		storeUserCoursesIntoMongodb(newUserCoursesMap);
	}
    
	/**
	 * readOneDayLogs(), read one day's logs from one json file, for example "2016-08-31.json"
	 * @param filePath the path of the json file 
	 * @return logsDocuments a ArrayList of documents, which are the logs
	 */
	static List<Document> readOneDayLogs(String date) {
		String tempPath = PATH + date + ".json"; // the path of the JSON file
		
		List<Document> logsDocuments = new ArrayList<Document>();
		Document doc = new Document();
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			fileInputStream = new FileInputStream(tempPath);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);

			String JsonContext = null;
			while ((JsonContext = reader.readLine()) != null) {
				// Parses a string in MongoDB Extended JSON format to a Document
				doc = Document.parse(JsonContext); 
				logsDocuments.add(doc); 
			}		
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Successfully read " + date + " log!");
		return logsDocuments;
	}
	
	/**
	 * process the Document list which is one day's logs,
	 * store the result into one map "Map<String, TreeSet<String>> userCoursesMap", 
	 * then transform it into one list "List<UserLearnedCourses> userCoursesList"
	 * @param logsDocuments 
	 * @return List<UserLearnedCourses> userCoursesList
	 */
    //static List<UserLearnedCourses> processOneDayLogs(List<Document> logsDocuments) {
    static Map<String, TreeSet<String>> processOneDayLogs(List<Document> logsDocuments) {
    	Map<String, TreeSet<String>> userCoursesMap = new HashMap<String, TreeSet<String>>();
    	
    	TreeSet<String> courseSet = null;
    	
    	String userId = "";
    	String courseId = "";
    	for(Document doc : logsDocuments) {
    		userId = (String) doc.get("url_uid");
    		courseId = (String) doc.get("url_courseid");
    		
    		if((userId != null) && (courseId != null)) {
				if (userCoursesMap.containsKey(userId)) { // old user
					// insert the courseId into the courseSet
					courseSet = userCoursesMap.get(userId);
					courseSet.add(courseId);
					
					userCoursesMap.put(userId, courseSet);				
				} else { // new user
					/*userCourses = new UserLearnedCourses();
	
					userCourses.setUserId(userId);
					
					courseSet = new TreeSet<String>();
					courseSet.add(courseId);
					userCourses.setCoursesSet(courseSet);
					
					userCoursesList.add(userCourses);*/
					
					courseSet = new TreeSet<String>();
					courseSet.add(courseId);
					
					userCoursesMap.put(userId, courseSet);
				}
    		}
    	}
    	
    	System.out.println("Successfully generate the day's records of user-learned-courses!");
    	return userCoursesMap;
    	
    	// transform the "Map<String, TreeSet<String>> userCoursesMap" to "List<UserLearnedCourses> userCoursesList"
    	/*List<UserLearnedCourses> userCoursesList = new ArrayList<UserLearnedCourses>();
    	UserLearnedCourses userCourses = null;
    	
    	for(Map.Entry<String, TreeSet<String>> entry : userCoursesMap.entrySet()) {
    		userCourses = new UserLearnedCourses();
    		
    		userCourses.setUserId(entry.getKey());
    		userCourses.setCoursesSet(entry.getValue());
    		
    		userCoursesList.add(userCourses);
    	}
    	
    	return userCoursesList;*/
    }
	
	/**
	 * read history records, user-learned-courses collection, form mongodb,
	 * store into one HashMap historyUserCoursesMap
	 * notice : TreeSet<String> will change to ArrayList<String> 
	 * @return
	 */
	static Map<String, ArrayList<String>> readHistoryUserCoursesFromMongodb() {
		Map<String, ArrayList<String>> historyUserCoursesMap = new HashMap<String, ArrayList<String>>();
		
		// connect to Mongodb, get collection mooc.userCourses
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		
		MongoCursor<Document> cursor = collection.find().iterator();
		Document doc = new Document();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				historyUserCoursesMap.put(doc.getString("userId"), (ArrayList<String>) doc.get("coursesSet"));
			}
		} finally {
			cursor.close();
		}
		
    	System.out.println("Successfully read the old records of user-learned-courses!");
		return historyUserCoursesMap;
	}
	
	/**
	 * merge the new and old records of user-learned-courses
	 * @param historyUserCoursesMap
	 * @param userCoursesMap
	 */
	static Map<String, ArrayList<String>> mergeUserCourses(Map<String, ArrayList<String>> historyUserCoursesMap, 
			Map<String, TreeSet<String>> userCoursesMap) {
		Map<String, ArrayList<String>> newUserCoursesMap = null;
		
		ArrayList<String> arrayList = null;
		TreeSet<String> treeSet = null;
		String key = "";
		// iterate "Map<String, TreeSet<String>> userCoursesMap"
		for(Map.Entry<String, TreeSet<String>> entry : userCoursesMap.entrySet()) {
			key = entry.getKey();
			
			if(historyUserCoursesMap.containsKey(key)) { // old user
				// first merge the ArrayList into TreeSet, then put the result TreeSet into the ArrayList 
				arrayList = historyUserCoursesMap.get(key);
				treeSet = entry.getValue();
				
				treeSet.addAll(arrayList);
				
				arrayList.clear();
				arrayList.addAll(treeSet);
				
				historyUserCoursesMap.put(key, arrayList);
			} else { // new user
				// directly store into ArrayList
				treeSet = entry.getValue();
				
				arrayList = new ArrayList<String>();
				arrayList.addAll(treeSet);
				
				historyUserCoursesMap.put(key, arrayList);
			}
		}
		
    	System.out.println("Successfully merge the old and new records of user-learned-courses!");
		newUserCoursesMap = historyUserCoursesMap;
		return newUserCoursesMap;
	}
	
	/**
	 * store new user learned courses records into mongodb
	 * @param userCoursesMap Map<String, ArrayList<String>>
	 */
	static void storeUserCoursesIntoMongodb(Map<String, ArrayList<String>> userCoursesMap) {
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		collection.drop();
		
		collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		
		Document doc = null;
		for(Map.Entry<String, ArrayList<String>> entry : userCoursesMap.entrySet()) {
			doc = new Document(); // need new Document object every time, because ObjectId
			doc.append("userId", entry.getKey());
			doc.append("coursesSet", entry.getValue());
			
			collection.insertOne(doc);
		}
		
    	System.out.println("Successfully store the merged records of user-learned-courses!");
	}
}
