package logProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import utility.MySqlConn;
import net.sf.json.JSONObject;

/**
 * @author: wuke 
 * @date  : 20160728 9:15:53pm
 * Title  : LogProcess_new
 * Description : 
 */
public class _LogProcess_new {
	static String PATH = "E:\\data\\log\\"; // the catalog where logs are stored
	
	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
	    readWhole(PATH);
		//readSingle("2016-06-15");
		generateCFRecords();
	}
	/**
	 * Read all days' log by call the function readSingle()
	 */
	static void readWhole(String path) {
		 File file = new File(path); // The location of the log files
		 File[] array = file.listFiles();
		 int i;
		 String timeId = null;
		 for(i = 0; i < array.length; i++) {
			 if(array[i].isFile()) {
				// get the name of the JSON file, like"2016-06-08"
				 timeId = array[i].getName().split("\\.")[0];
				 readSingle(timeId);
			 } else {
				 System.out.println("This is not a file!");
			 }
		 }
	}
	
	/**
	 *  Read the log of a day from a JSON file. From this method we get newRecords and newUsers.
	 *  Judge if the "userId,courseId" exits or not in the HashMap,
	 *  if not put it into the HashMap and the value is set as 1,
	 *  if exits then set the value as value++
	 */
	static void readSingle(String date) {
		// read the history data from MySQL table log
		HashMap<String, Long> newRecords = _LogProcess_new.readHistoryLog();
		// read the history user from MySQL table user
		HashSet<String> newUsers = _LogProcess_new.readUser();
		
		String tempPath = PATH + date + ".json"; // the path of the JSON file
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			fileInputStream = new FileInputStream(tempPath);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);

			String JsonContext = null;
			String userId = null;
			String courseId = null;
			Long clickTimes = null;
			String userAcourse = null;
			while ((JsonContext = reader.readLine()) != null) {
				JSONObject jsonObject = JSONObject.fromObject(JsonContext);
				
				userId = jsonObject.getString("url_uid");        // get the userId
				courseId = jsonObject.getString("url_courseid"); // get the courseId
				
				userAcourse = userId + "," + courseId;  // use this String as the key of the HashMap
				
				// check if the userId exits or not
				if(userId != null && (! newUsers.contains(userId)))
					newUsers.add(userId);
				
				// check if the "userId,courseId" exits or not
				if(userAcourse != null) {
					if (newRecords.containsKey(userAcourse)) { 
						clickTimes = newRecords.get(userAcourse);
						newRecords.put(userAcourse, clickTimes + 1);
					} else
						newRecords.put(userAcourse, 1L);
				}
			}		
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		// store the newRecords and the newUsers
		_LogProcess_new.storeNewLog(newRecords,date);
		_LogProcess_new.storeUsers(newUsers,date);
		System.out.println("*******************************************");
	}
	
	/**
	 *  Read the history data from MySQL table 'log' (userId,courseId,clickTimes)
	 */
	static HashMap<String, Long> readHistoryLog() {
		HashMap<String, Long> records = new HashMap<String, Long>();
		
		String userId = null;
		String courseId = null;
		long clickTimes = 0;
		String userCourse = null;
		
		String sql = "select * from log";
		Connection conn = MySqlConn.getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				userId = rs.getString(1);
				courseId = rs.getString(2);
				clickTimes = rs.getLong(3);				
				userCourse = userId + "," + courseId; // (("userId,courseId"),clickTimes)
				
				records.put(userCourse, clickTimes);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully read the history log!");
		return records;
	}
	
	/**
	 *  Read the history user from MySQL table 'user' (user,userId).
	 *  (user,userId) 'user' is the primary key, and it is setted as auto_increment
	 */
	static HashSet<String> readUser() {
		HashSet<String> users = new HashSet<String>(); // userId
		String sql = "select userId from user";
		Connection conn = MySqlConn.getConn();
		PreparedStatement pstmt  = null;
		ResultSet rs  = null;
        try {
        	pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
            	String tem = rs.getString(1);
            	users.add(tem);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully read the history user!");
		//System.out.println(users.toString());
		return users;
	}
	
	/**
	 *  Store the new users into MySQL table 'user' (user,userId).
	 *  firstly, delete the old users; 
	 *  secondly, insert the new users
	 */
	static void storeUsers(HashSet<String> newUsers,String date) {
		String sql = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		conn = MySqlConn.getConn();
		try {
			// delete the old users
			//sql = "delete from user";
			sql = "truncate user"; // let the auto_increment 'user' restart from 1
			pstmt =  conn.prepareStatement(sql);
	        pstmt.executeUpdate();
	        
	        // insert the new users
			for(String userId : newUsers) {
				sql = "insert into user (userId) values (?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userId);
				pstmt.executeUpdate();
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully store the new users of " + date + "!");
	}
	
	/**
	 *  Store the processed log into MySQL table 'log' (userId,courseId,clickTimes).
	 *  Like the method storeUsers() we need to cover the old data
	 */
	static void storeNewLog(HashMap<String, Long> newRecords, String date) {
		String sql = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		conn = MySqlConn.getConn();
		try {
			// delete the old log
			sql = "delete from log";
			pstmt =  conn.prepareStatement(sql);
	        pstmt.executeUpdate();
	        
	        // insert the new log
	        String userId = null;
	        String courseId = null;
	        String userCourse = null;
	        long clickTimes = 0;
	        Iterator iter = newRecords.entrySet().iterator(); // traverse the HashMap
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				userCourse = (String) entry.getKey();
				clickTimes = (long) entry.getValue();
				
				userId = userCourse.split(",")[0];
				courseId = userCourse.split(",")[1];
				
				sql = "insert into log values (?,?,?)";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, userId);
				pstmt.setString(2, courseId);
				pstmt.setLong(3, clickTimes);
				pstmt.executeUpdate();
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully store the new log of " + date + "!");
	}
	
	/**
	 *  Generate the data for the CF.
	 *  CFRecords(user,course,clickTimes)
	 *  Need to improve!
	 */
	static void generateCFRecords() {
		String sql = null;
		Connection conn = MySqlConn.getConn();
		PreparedStatement pstmt  = null;
		ResultSet rs  = null;
		try {
			sql = "delete from cfrecords";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			sql = "insert into cfrecords (select user,course,clickTimes from log, user, course "
					+ "where log.userId = user.userId and log.courseId = course.courseId)";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			System.out.println("Successfully generate the records for the CF algorithm");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
