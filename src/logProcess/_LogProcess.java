package logProcess;

import java.io.*;
import java.math.BigInteger;
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
 * @date  : 2016年7月21日 下午8:41:11
 * Title  : LogProcess
 * Description : read the log, then process it into (userId,courseId,times) for the algorithm CF
 */
public class _LogProcess {
	static String PATH = "E:\\data\\log\\"; // the catalog where tht logs are stored
	static String JSONPATH = "e:\\data\\dataCF.json";
	static String TXTPATH = "e:\\data\\test.txt";
	
	/**
	 *  read the history data from the TXT  (userId,courseId,times)
	 */
	static HashMap<String, Integer> readHistoryTxt() {
		HashMap<String, Integer> records = new HashMap<String, Integer>(); // "userId,courseId" and click time

		try (BufferedReader reader = new BufferedReader(new FileReader(TXTPATH))) {
			String line;
			while((line = reader.readLine()) != null) {
				String userId = line.split(",")[0]; // get the userId
				String courseId = line.split(",")[1]; // get the courseId
				int num = Integer.parseInt(line.split(",")[2]); // get the num
				String userAcourse = userId + "," + courseId; // use this String as the key of the HashMap
				
				records.put(userAcourse, num);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully read the history data");
		//System.out.println(records.toString());
		return records;
	}
	
	/**
	 *  read the history user from the MYSQL  (userId)
	 */
	static HashSet<String> readUser() {
		HashSet<String> users = new HashSet<String>(); // userId
		String sql = "select userId from user";
		Connection conn = MySqlConn.getConn();
		PreparedStatement pstmt;
        try {
        	pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
            	String tem = rs.getString(1);
            	users.add(tem);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully read the history user");
		//System.out.println(users.toString());
		return users;
	}
	
	/**
	 *   judge if the "userId,courseId" exits or not
	 */
	static boolean checkUserCourse(HashMap<String, Integer> newRecords,String userAcourse) {
		if(newRecords.containsKey(userAcourse))
		    return true;
		else
			return false;
	}
	
	/**
	 *  read the log of a day from a JSON file
	 *  judge if the "userId,courseId" exits or not in the HashMap,
	 *  if not put it into the HashMap and the value is set as 1,
	 *  if exits then set the value as value++
	 */
	static HashMap<String, Integer> readSingle(String timeId) {
		HashMap<String, Integer> newRecords = readHistoryTxt(); // read the history data
		HashSet<String> newUsers = readUser(); // read the history user
		
		String tempPath = PATH + timeId + ".json";
		FileInputStream  fileInputStream;
		InputStreamReader inputStreamReader;
		BufferedReader reader = null;
		try {
			fileInputStream = new FileInputStream(tempPath);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			
			String tempString = null;			
			while ((tempString = reader.readLine()) != null) {
				String JsonContext = tempString;			
				JSONObject jsonObject = JSONObject.fromObject(JsonContext);
				
				String userId = (String) jsonObject.get("url_uid");        // get the userId
				String courseId = (String) jsonObject.get("url_courseid"); // get the courseId
				String userAcourse = userId + "," + courseId;  // use this String as the key of the HashMap
				
				// check if the userId exits or not
				if(! newUsers.contains(userId))
					newUsers.add(userId);
				
				// check if the "userId,courseId" exits or not
				if (checkUserCourse(newRecords,userAcourse)) { 
					int temp = newRecords.get(userAcourse);
					newRecords.put(userAcourse, temp + 1);
				} else
					newRecords.put(userAcourse, 1);
			}		
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully read the log of " + timeId );
		
		storeUsers(newUsers); // store the new users into MYSQL  (userId)
		//System.out.println(newRecords.toString());
		return newRecords;
	}
	
	/**
	 *  store the new users into MYSQL  (userId)
	 *  firstly, delete the old data; secondly, insert the new data
	 */
	static void storeUsers(HashSet<String> newUsers) {
		try {
			Connection conn = MySqlConn.getConn();
			String sql;
			PreparedStatement pstmt = null;
			
			// delete the old users
			sql = "delete from user";
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
		System.out.println("Successfully store the new users");
	}
	
	/**
	 *  store the processed data into JSON  (userId,courseId,times)
	 */
	/*static void store(HashMap<String, Integer> newRecords) {
		JSONArray arr = JSONArray.fromObject(newRecords); // convert the HashMap into JSONArray
		
		try(FileWriter writer = new FileWriter(JSONPATH)) {
			arr.write(writer); // store the JSONArray into "e:\\dataCF.json"
		} catch(Exception e) {
			e.printStackTrace();
		}
		//System.out.println(arr);
		System.out.println("Successfully store");
	}*/
	
	/**
	 *  store the processed data into txt  (userId,courseId,times)
	 *  cover the old data
	 *  not use
	 */
	static void storeTxt2(HashMap<String, Integer> newRecords) {
		try {
			File file = new File("e:\\data\\test.txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile()); // FileWriter(file.getName(),true);  true--save the old data
			BufferedWriter bw = new BufferedWriter(fw);
			
			// iterate the HashMap and store it into txt
			Iterator iter = newRecords.entrySet().iterator();
			String line = null;
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String userAcourse = (String) entry.getKey(); // get the userId and courseId
				int num = (int) entry.getValue();             // get the num 
				
				String userId = userAcourse.split(",")[0].replaceAll("-", "");    // get the userId
				String courseId = userAcourse.split(",")[1].replaceAll("-", "");  // get the courseId
				
				BigInteger a = new BigInteger(userId,16);     // transfer the hex to decimal
				BigInteger b = new BigInteger(courseId,16);   // transfer the hex to decimal
				
				line = a + "," + b + "," + num + "\r\n";
				
				bw.write(line); // store one line into txt
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully store the new data");
	}
	static void storeTxt(HashMap<String, Integer> newRecords) {
		try {
			File file = new File("e:\\data\\test.txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile()); // FileWriter(file.getName(),true);  true--save the old data
			BufferedWriter bw = new BufferedWriter(fw);
			
			// iterate the HashMap and store it into txt
			Iterator iter = newRecords.entrySet().iterator();
			String line = null;
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String userAcourse = (String) entry.getKey(); // get the userId and courseId
				int num = (int) entry.getValue();             // get the num 
				
				line = userAcourse + "," + num + "\r\n";
				
				bw.write(line); // store one line into txt
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully store the new data");
	}
	
	public static void main(String[] args) {
		storeTxt(readSingle("2016-06-09"));
	}
}
