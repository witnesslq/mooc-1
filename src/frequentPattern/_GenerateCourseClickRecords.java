package frequentPattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import utility.MySqlConn;
import net.sf.json.JSONObject;

/**
 * @author: wuke 
 * @date  : 2016年11月11日 下午5:27:44
 * Title  : GenerateCourseClickRecords
 * Description : mysql, logs(date,userId,courseId)
 */
public class _GenerateCourseClickRecords {
	static String FILEPATH = "E:\\data\\log\\"; // the catalog where logs are stored

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//readSingleDayLogs("2016-06-08");
		long startMillsecond = System.currentTimeMillis();
		readAllDaysLogs(FILEPATH);
		long stopMillsecond = System.currentTimeMillis();
		
		System.out.println(stopMillsecond - startMillsecond);
	}
	
	/**
	 * Read all days' logs by calling the function readSingleDayLogs()
	 * @param path
	 */
	static void readAllDaysLogs(String path) {
		 File file = new File(path);       // The location of the log files
		 File[] array = file.listFiles();  // get all the files in the folder
		 
		 for(int i = 0; i < array.length; i++) {
			 if(array[i].isFile()) {
				 // get the name of JSON file, like"2016-06-08"
				 String timeId = array[i].getName().split("\\.")[0];
				 readSingleDayLogs(timeId);
			 } 
			 else {
				 System.out.println("This is not a file!");
			 }
		 }
	}
	
	/**
	 * Read the log of a day from a JSON file.
	 * And store them into MySQL table mooc.logs
	 * @param date
	 */
	static void readSingleDayLogs(String date) {
		String jsonFilePath = FILEPATH + date + ".json"; // the full path of oneday's JSON file
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		String insertSql = null;
		
		// read the Json file, in which one line contains a json object
		try {
			fileInputStream = new FileInputStream(jsonFilePath);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			
			conn = MySqlConn.getConn();
			insertSql = "insert into logs(date,userId,courseId) values(?,?,?)";
			
			String jsonContext = null;
			String userId = null;
			String courseId = null;
			
			// read the json file, one line by one line
			while ((jsonContext = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = JSONObject.fromObject(jsonContext);
				
				// net.sf.json.JSONException: JSONObject["url_uid"] not found
				userId = jsonObject.getString("url_uid");
				courseId = jsonObject.getString("url_courseid");
				
				pstmt = conn.prepareStatement(insertSql);
				pstmt.setString(1, date);
				pstmt.setString(2, userId);
				pstmt.setString(3, courseId);
				pstmt.executeUpdate();
			}
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully store the " + date + " logs!");
	}
}