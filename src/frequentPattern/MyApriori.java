package frequentPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author: wuke 
 * @date  : 2016年12月20日 下午4:29:08
 * Title  : MyApriori
 * Description : 
 */
public class MyApriori {

	private final static int MIN_SUPPORT = 5; // min support
	private final static double MIN_CONFIDENCE = 0.5; // min support
	
	public static void main(String[] args) {
		ArrayList<String> dataList = GenerateAprioriDataset.generateRecords();
		
		// test MyApriori.findFrequentOneItemset()
		Map<String, Integer> frequentOneItemsetMap = null;
		frequentOneItemsetMap = MyApriori.findFrequentOneItemset(dataList);
		
		/*System.out.println(frequentOneItemset.size());
		for(Map.Entry<String, Integer> entry : frequentOneItemset.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
		
		// test MyApriori.aprioriGenTwoCandidateSets
		/*Set<String> candidateTwoItemset = new TreeSet<String>();
		candidateTwoItemset = aprioriGenTwoCandidateSets(frequentOneItemset);
		
		System.out.println(candidateTwoItemset.size());
		for(String str : candidateTwoItemset) {
			System.out.println(str);
		}*/
		
		Map<String, Integer> frequentTwoItemsetMap = null;
		frequentTwoItemsetMap = MyApriori.countCandidateTwoItemset(dataList, frequentOneItemsetMap);
		System.out.println(frequentTwoItemsetMap.size());
		for(Map.Entry<String, Integer> entry : frequentTwoItemsetMap.entrySet()) {
			//System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	
	/**
	 * find the frequent one itemset
	 * @param dataList
	 * @return frequentOneItemset - Map<String, Integer>
	 */
	static Map<String, Integer> findFrequentOneItemset(ArrayList<String> dataList) {
		Map<String, Integer> candidateOneItemsetMap = new TreeMap<String, Integer>(); // orderly
		
		// iterate the dataList, count every item's appearance times
		String[] strArr = null;
		for(String str : dataList) { // iterate the dataList
			strArr = str.split(",");
			
			for(String temp : strArr) { // iterate one record's items
				if(candidateOneItemsetMap.containsKey(temp)) {
					candidateOneItemsetMap.put(temp, candidateOneItemsetMap.get(temp)+1);
				} else {
					candidateOneItemsetMap.put(temp, 1);
				}
			}
		}
		
		// judge if the items in candidateOneItemsetMap get support bigger than the MIN_SUPPORT
		Map<String, Integer> frequentOneItemsetMap = candidateOneItemsetMap;
		
		Iterator<Map.Entry<String, Integer>> iter = frequentOneItemsetMap.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if(entry.getValue() < MIN_SUPPORT) { // smaller, delete the item
				iter.remove();
			}
		}
		
		return frequentOneItemsetMap;
	}
	
	/**
	 * iterate the dataList, count the appearance of the candidateTwoItemset,
	 * then compare with the MIN_SUPPORT, return the 
	 * @param dataList
	 * @param frequentOneItemset
	 * @return 
	 */
	static Map<String, Integer> countCandidateTwoItemset(ArrayList<String> dataList, 
			Map<String, Integer> frequentOneItemset) {
		Set<String> candidateTwoItemset = new TreeSet<String>();
		candidateTwoItemset = aprioriGenTwoCandidateSets(frequentOneItemset);
		
		Map<String, Integer> candidateTwoItemsetMap = new TreeMap<String, Integer>();
		
		for(String data : dataList) {
			for(String candidate : candidateTwoItemset) {
				boolean flag = true;
				
				String[] items = candidate.split(",");
				for(String str : items) {
					if(data.indexOf(str) == (-1)) {
						flag = false;
						break;
					}
				}
				
				if(flag) {
					if(candidateTwoItemsetMap.containsKey(candidate))
						candidateTwoItemsetMap.put(candidate, candidateTwoItemsetMap.get(candidate)+1);
					else
						candidateTwoItemsetMap.put(candidate, 1);
				}					
			}
		}
		
		// judge if the items in candidateTwoItemsetMap get support bigger than the MIN_SUPPORT
		Map<String, Integer> frequentTwoItemsetMap = candidateTwoItemsetMap;
		
		Iterator<Map.Entry<String, Integer>> iter = frequentTwoItemsetMap.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if(entry.getValue() < MIN_SUPPORT) { // smaller, delete the item
				iter.remove();
			}
		}
		
		return frequentTwoItemsetMap;
	}
	
	/**
	 * 
	 * @param frequentOneItemset
	 * @return candidateTwoItemset
	 */
	static Set<String> aprioriGenTwoCandidateSets(Map<String, Integer> frequentOneItemset) {
		Set<String> candidateTwoItemset = new TreeSet<String>();
		
		for(Map.Entry<String, Integer> entry1 : frequentOneItemset.entrySet()) {
			String str1 = entry1.getKey();
			
			for(Map.Entry<String, Integer> entry2 : frequentOneItemset.entrySet()) {
				String str2 = entry2.getKey();
				
				StringBuilder temp = new StringBuilder();
				if(str1.compareTo(str2) < 0) {
					temp.append(str1).append(",").append(str2);
					
					candidateTwoItemset.add(temp.toString()); // 连接步
				}
			}
		}
		
		return candidateTwoItemset;
	}
	
	/**
	 * 
	 * @param frequentKMinusOneItemset
	 * @return candidateItemset
	 */
	static Map<String, Integer> aprioriGen(Map<String, Integer> frequentKMinusOneItemset) {
		Map<String, Integer> candidateItemset = new HashMap<String, Integer>();
		
		// 
		
		return candidateItemset;
	}
	
	/**
	 * 
	 * @param candidateItem
	 * @param frequentKMinusOneSets
	 * @return
	 */
	static boolean hasInfrequentSubset(String candidateItem, Map<String, Integer> frequentKMinusOneItemset) {
		return true;
	}
	
    /**
     * 
     * @param str
     * @return
     */
	static List<String> subset(String str) {
		List<String> result = new ArrayList<>();
		
		return result;
	}
}
