package hotCourses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import utility.MySqlConn;

/**
 * @author: wuke 
 * @date  : 2016年6月30日 上午9:00:14
 * Title  : HotCourses
 * Description : 
 */
public class HotCourses {
	//static HashSet<String> res ult = new HashSet<String>(); // 存储热门课程   注意，此处不能使用HashSet，其输出无序！
	static ArrayList<String> result = new ArrayList<String>();
	
	/**
	 *  不设时间限制
	 *  从数据库中计算最大次数
	 */
	static ArrayList<String> searchHot() {
		ArrayList<String> hotCourses = new ArrayList<String>();	
		Connection conn = MySqlConn.getConn();
		String sql = "select courseId,sum(totalClick) as totalClicks from hotcourse "
				+ "group by courseId order by totalClicks desc limit 3;";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();			
			while(rs.next()) {
				hotCourses.add(rs.getString(1)); // 将查询到的结果一个一个存入ArrayList中
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return hotCourses;
	}
	
	/**
	 *  设定时间限制，给定时间范围，String类型
	 *  从数据库中计算最大次数
	 */
	static ArrayList<String> searchHotLimited(String start,String end) {
		ArrayList<String> hotCourses = new ArrayList<String>();	
		Connection conn = MySqlConn.getConn();
		String sql = "select courseId,sum(totalClick) as totalClicks from hotcourse "
				+ "where timeId between \"" + start + "\" and \"" + end
				+ "\" group by courseId order by totalClicks desc limit 3;";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();			
			while(rs.next()) {
				hotCourses.add(rs.getString(1)); // 将查询到的结果一个一个存入ArrayList中
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return hotCourses;
	}
	
	/**
	 *  主函数
	 */
	public static void main(String[] args) {
		//result = searchHot();    // 从数据库中查找观看次数最多的几门课程
		result = searchHotLimited("2016-06-08","2016-06-12");    // 从数据库中查找观看次数最多的几门课程
		System.out.println(result.toString());
	}
}
