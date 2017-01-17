package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;

/**
 * @author: wuke 
 * @date  : 20160725 11:15:36
 * Title  : Transfer16to10
 * Description : not successful write ?
 */
public class Transfer16to10 {
	public static void main(String[] args) {
		/*try (BufferedReader reader = new BufferedReader(new FileReader("E:\\data\\test.txt"))) {
			File file = new File("E:\\data\\newtest.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			String line;
			String newLine;
			while((line = reader.readLine()) != null) {
				String userId = line.split(",")[0].replaceAll("-", "");   // get the userId
				String courseId = line.split(",")[1].replaceAll("-", ""); // get the courseId
				int num = Integer.parseInt(line.split(",")[2]);           // get the num
				
				BigInteger a = new BigInteger(userId,16);
				BigInteger b = new BigInteger(courseId,16);
				newLine = a + "," + b + "," + num + "\r\n";
				
				bw.write(newLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		String s1 = "126656864e144ad88d7ff96badd2f68b"; // 16进制数
		BigInteger b = new BigInteger(s1,16);           // 16进制转成大数类型	
		
		System.out.println(b);		
		System.out.println(b.toString(16));
	}
}
