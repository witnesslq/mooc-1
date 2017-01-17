package courseProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;

import utility.MySqlConn;
import utility.ReadFileToString;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author: wuke 
 * @date  : 20160728 7:30:46
 * Title  : CoursesProcess
 * Description : Read the all the courses from the json file, 
 *               and then store them into mysql table mooc.course.
 *               需要手动生成 Json 文件
 * An example of one course in the json file:
 * {
      "CourseID": "36de94a6-c421-43c9-9d8a-069476b2eab7",
      "CourseName": "钢琴即兴伴奏",
      "CategoryList": [
        {
          "CategoryID": "3668bb19-4906-42d6-83b7-914f5e4ff34f",
          "CategoryName": "影视音乐",
          "Level": 2
        }
      ]
    }
 */

public class _ProcessCoursesJsonFileStoreMysql {
	static void storeCourses() {
		String JsonContext = ReadFileToString.readFile("E:\\data\\moocCourses.json");
    	JSONObject jsonObject = JSONObject.fromObject(JsonContext);
    	JSONArray jsonArr = jsonObject.getJSONArray("Data");
    	//System.out.println(jsonArr.toString());
    	
    	String courseId = null; 
    	String courseName = null;
    	JSONObject temp = null;
    	
    	Connection conn = MySqlConn.getConn();
    	PreparedStatement pstmt;
    	String sql = "insert into course(courseId, courseName) values (?,?)";
    	try {
    		
    		deleteOldDataInTable(); // delete the old data
    		
	    	pstmt = conn.prepareStatement(sql);
	    	
	    	int size = jsonArr.size();
	    	for(int i = 0; i < size; i++) {
	    		temp = jsonArr.getJSONObject(i);
	    		courseId = temp.getString("CourseID");
	    		courseName = temp.getString("CourseName");
	    		
	    		pstmt.setString(1, courseId);
	    		pstmt.setString(2, courseName);
	    		pstmt.executeUpdate();
	    	}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	System.out.println("Successfully store the data into table course!");
	}
	
	/**
	 * drop the old table
	 * use method truncate not delete to let the auto_increment restart from 1
	 */
	static void deleteOldDataInTable() {
		String sql = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		conn = MySqlConn.getConn();
		try {
			sql = "truncate course";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully delete the data in table course!");
	}
	
    public static void main(String[] args) {
    	//deleteOldDataInTable();
    	storeCourses();
    }
}
