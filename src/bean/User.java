package bean;
/**
 * @author: wuke 
 * @date  : 2016年12月4日 下午8:24:12
 * Title  : User
 * Description : 
 */
public class User {
	private String userId;
	private String nickName;
	private String email;
	private String source;
	private String sex;
	private String age;
	private String university;
	private String major;
	
	public String getUserID() {
		return userId;
	}
	public void setUserID(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getUniversity() {
		return university;
	}
	public void setUniversity(String university) {
		this.university = university;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
}
