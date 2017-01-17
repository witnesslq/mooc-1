package userProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import utility.MongodbConn;

/**
 * @author: wuke 
 * @date  : 2016Äê12ÔÂ7ÈÕ
 * Title  : CrawlerGetUserStoreMongodb
 * Description : Get all users' data from "http://www.mooc2u.com/api/open/user/GetAllUserData", 
 *               and store them into mongodb mooc users.
 *               Until 20171207, totally 46142 users!
 */
public class CrawlerGetUserStoreMongodb {
	
	public static void main(String[] args) {
		String url = "http://www.mooc2u.com/api/open/user/GetAllUserData";
		String strUsers = "";
		
		strUsers = getUsers(url, "utf-8");
		
		System.out.println(strUsers.length());
		storeUsersIntoMongodb(strUsers);
	}
	
	/**
	 * get all the content and store them into a String 
	 * */
	static String getUsers(String url,String param) {
		BufferedReader br = null;
		InputStreamReader isr = null;
		String strUsers = "";
		
		try {
			URL usersUrl = new URL(url);
			
			isr = new InputStreamReader(usersUrl.openStream(), param);
			br = new BufferedReader(isr);

			strUsers = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return strUsers;
	}
	
	/**
	 * store the String, which actually is a big json object, 
	 * which contains a json array, in which every json object is one user, into mongodb
	 * */
	static void storeUsersIntoMongodb(String strUsers) {
		// transform the String into a Json object, 
		// and then extract the json array from the json object
		JSONObject users = JSONObject.fromObject(strUsers);
		JSONArray jsonArr = users.getJSONArray("Data");
		
		// connect to the mongodb mooc, and get it's collection courses
		MongoCollection<Document> collections = MongodbConn.getMongoCollection("mooc", "users");
		
		// store the json array into the collection users one by one
		// transform json object into one mongodb document
		Document document = null;
		for(int i = 0; i < jsonArr.size(); i++) {
			document = Document.parse(jsonArr.getJSONObject(i).toString());
			collections.insertOne(document);
		}
		System.out.println("totally " + jsonArr.size() + " users!");
	}
}
