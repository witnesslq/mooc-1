package logProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bson.Document;

import utility.MongodbConn;

import com.mongodb.client.MongoCollection;

/**
 * @author: wuke 
 * @date  : 2016年12月8日 上午9:04:24
 * Title  : ProcesssLogsJsonFileStoreMongodb
 * Description : 
 */
public class ProcesssLogsJsonFileStoreMongodb {
	static String PATH = "E:\\data\\log\\"; // the catalog where logs are stored
	
	public static void main(String[] args) {
		/*
		 * test readOneDayLogs()
		 */
		/*ArrayList<Document> documents = null;
		documents = readOneDayLogs("2016-06-08");
		storeOneDayLogs(documents);*/
		
		readStoreLogsUntilYesterday(PATH);
	}
	
	/**
	 * readStoreLogsUntilYesterday
	 * @param path
	 */
	static void readStoreLogsUntilYesterday(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		int i;
		String date;
		for(i = 0; i < array.length; i++) {
			if(array[i].isFile()) {
				date = array[i].getName().split("\\.")[0]; // 在字符串里，转义字符要加两个\
				
				storeOneDayLogs(readOneDayLogs(date));
				System.out.println("Successfully store " + date +" logs into Mongodb mooc.logs!");
			} else {
				 System.out.println(array[i].getPath() + " is not a file!");
			 }
		}
	}
	
	/**
	 * readOneDayLogs(), read one day's logs from one json file, for example "2016-08-31.json"
	 * @param filePath the path of the json file 
	 * @return logsDocuments a ArrayList of documents, which are the logs
	 */
	static ArrayList<Document> readOneDayLogs(String date) {
		String tempPath = PATH + date + ".json"; // the path of the JSON file
		
		ArrayList<Document> logsDocuments = new ArrayList<Document>();
		Document doc = new Document();
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			fileInputStream = new FileInputStream(tempPath);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);

			String JsonContext = null;
			while ((JsonContext = reader.readLine()) != null) {
				// Parses a string in MongoDB Extended JSON format to a Document
				doc = Document.parse(JsonContext); 
				logsDocuments.add(doc); 
			}		
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return logsDocuments;
	}
	
	/**
	 * store one ArrayList of logs documents, which are one day's logs
	 * @param documents ArrayList<Document> documents
	 * @return null
	 */
	static void storeOneDayLogs(ArrayList<Document> documents) {
		MongoCollection logsCollection = MongodbConn.getMongoCollection("mooc", "logs");
		logsCollection.insertMany(documents);
	}
}


