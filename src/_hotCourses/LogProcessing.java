package _hotCourses;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utility.MySqlConn;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author: wuke 
 * @date  : 20160704 10:16:49
 * Title  : LogProcessing
 * Description : 
 */
public class LogProcessing {
	static String PATH = "E:\\data\\log\\";
	
	/**
	 *  check if the HashMap courses contions the courseId or not
	 */
	static boolean checkCourse(HashMap<String, Integer> courses,String cId) {
		if(courses.containsKey(cId))
		    return true;
		else
			return false;
	}
	
	/**
	 *  read a single day's log
	 */
	static HashMap<String, Integer> readSingle(String timeId) {
		HashMap<String, Integer> courses = new HashMap<String, Integer>(); // store the couserId and the click time
		String tempPath = PATH + timeId + ".json";
		
		BufferedReader reader = null;
		try {
			FileInputStream  fileInputStream = new FileInputStream(tempPath);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			
			while ((tempString = reader.readLine()) != null) {
				String JsonContext = "[" + tempString + "]"; // 
				JSONArray jsonArray = JSONArray.fromObject(JsonContext);
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				
				String courseId = (String) jsonObject.get("url_courseid"); // 
				
				// check if the HashMap courses contions the courseId  or not
				if (checkCourse(courses,courseId)) {
					int temp = courses.get(courseId);
					courses.put(courseId, temp + 1);
				} else
					courses.put(courseId, 1);
			}		
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully read the log of " + timeId);
		return courses;
	}
	
	/**
	 *  store the data into MySql
	 */
	static void store(HashMap<String, Integer> courses,String timeId) {
		Connection conn = MySqlConn.getConn();
		String sql = "insert into hotCourse (courseId,timeId,totalClick) values(?,?,?)";	
		PreparedStatement pstmt;
		
		Iterator iter = courses.entrySet().iterator(); // hashmap
		while(iter.hasNext()) {
		    try {
		    	Map.Entry entry = (Map.Entry) iter.next();
		    	
		        pstmt = conn.prepareStatement(sql);	
		        
		        pstmt.setString(1, (String) entry.getKey());
		        pstmt.setString(2, timeId);
		        pstmt.setString(3, entry.getValue().toString());
		        
		        pstmt.executeUpdate(); 
		        pstmt.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		try {
		    conn.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
		System.out.println("Successfully store the log of " + timeId);
	}
	
	/**
	 * read all days' log by call the function readSingle()
	 */
	static void readWhole(String path) {
		 File file = new File(path);
		 File[] array = file.listFiles();
		 
		 for(int i = 0; i < array.length; i++) {
			 if(array[i].isFile()) {
				 String timeId = array[i].getName().split("\\.")[0]; // 			 
				 HashMap<String, Integer> courses = readSingle(timeId); 
				 store(courses,timeId); 
			 } else {
				 System.out.println("");
			 }
		 }
	}
	
	public static void main(String[] args) {
		//String timeId = "2016-06-15";
		//HashMap<String, Integer> courses = readSingle(timeId);
		//store(courses,timeId);

		readWhole(PATH);
	}
}
