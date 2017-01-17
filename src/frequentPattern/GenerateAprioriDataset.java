package frequentPattern;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import utility.MongodbConn;

/**
 * @author: wuke 
 * @date  : 2016年12月19日 下午9:29:03
 * Title  : GenerateAprioriDataset
 * Description : 
 * 
 * 43293d31-ea98-4769-8eeb-59cf0193ca48
 * 4800fd2b-c9da-4994-af88-95de7c2ef980,53be568c-af84-4e4d-93f1-b8a4c657598d,55e5bc18-b09b-4964-bf3e-69dabd1957d4,65fe9ec6-c084-43a6-970c-97b71a5edba9,c56bc1f9-cbad-4c55-8a0e-6403bceef936
 */
public class GenerateAprioriDataset {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		ArrayList<String> apripriRecords = generateRecords();
		
		long stopTime = System.currentTimeMillis();
		long cost = stopTime-startTime;
		System.out.println("cost " + cost + " !");
		
		System.out.println(apripriRecords.size());
		for(String str : apripriRecords) {
			System.out.println(str);
		}
		
		// apriori
		/*Apriori2 apriori2 = new Apriori2();  
        
        System.out.println("=频繁项集==========");  
          
        Map<String, Integer> frequentSetMap = apriori2.apriori(apripriRecords);  
        Set<String> keySet = frequentSetMap.keySet();  
        for(String key:keySet)  
        {  
            System.out.println(key+" : "+frequentSetMap.get(key));  
        }  
          
        System.out.println("=关联规则==========");  
        Map<String, Double> relationRulesMap = apriori2.getRelationRules(frequentSetMap);  
        Set<String> rrKeySet = relationRulesMap.keySet();  
        for (String rrKey : rrKeySet)  
        {  
            System.out.println(rrKey + "  :  " + relationRulesMap.get(rrKey));  
        }*/
	}
	
	static ArrayList<String> generateRecords() {
		ArrayList<String> apripriRecords = new ArrayList<String>();
		
		ArrayList<String> temp = new ArrayList<String>();
		StringBuilder stringBuilder = null;
		
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		Document doc = null;
		MongoCursor<Document> cursor = collection.find().iterator();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				temp = (ArrayList<String>) doc.get("coursesSet");
				
				stringBuilder = new StringBuilder();
				/*for(String str : temp) {
					stringBuilder.append(str);
					stringBuilder.append(",");
				}*/
				int i;
				String courseId = "";
				for(i = 0; i < (temp.size()-1); i++) {
					courseId = temp.get(i);
					if(!(courseId.equals("undefined"))) {
						stringBuilder.append(courseId);
						stringBuilder.append(",");
					}
				}
				if(!(temp.get(i).equals("undefined")))
				    stringBuilder.append(temp.get(i));
				else
					stringBuilder.deleteCharAt(stringBuilder.length()-1);
				
				apripriRecords.add(stringBuilder.toString());
			}
		} finally {
			cursor.close();
		}
		
		return apripriRecords;
	}
}
