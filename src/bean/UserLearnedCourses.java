package bean;

import java.util.TreeSet;

/**
 * @author: wuke 
 * @date  : 2016年12月12日 下午9:14:18
 * Title  : UserLearnedCourses
 * Description : store one user's id and the courses id which he has learned
 */
public class UserLearnedCourses {
	String userId;
	TreeSet<String> coursesSet; // the elements are distinct and sorted
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public TreeSet<String> getCoursesSet() {
		return coursesSet;
	}
	public void setCoursesSet(TreeSet<String> coursesSet) {
		this.coursesSet = coursesSet;
	}
}