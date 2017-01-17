package bean;

import java.util.ArrayList;

/**
 * @author: wuke 
 * @date  : 2016年11月24日 下午5:29:46
 * Title  : Course
 * Description : 
 */
public class Course {
	String courseId;
	String couresName;
	ArrayList<String> categoryList;
	
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		courseId = courseId;
	}
	public String getCouresName() {
		return couresName;
	}
	public void setCouresName(String couresName) {
		couresName = couresName;
	}
	public ArrayList<String> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<String> categoryList) {
		categoryList = categoryList;
	}
}