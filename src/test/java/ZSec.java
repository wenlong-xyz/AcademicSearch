import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class ZSec {

	public static void main(String[] args) throws IOException {
		File myFile = new File("E:\\PlanB\\Desktop\\114.txt");
		File youFile = new File("E:\\PlanB\\Desktop\\115.txt");
		BufferedReader br = new BufferedReader(new FileReader("E:\\PlanB\\Desktop\\114.txt"));
		Set<String> set = new HashSet<String>();
		String line = br.readLine();
		while(line != null){
			set.add(line);
			line = br.readLine();
		}
		
		br = new BufferedReader(new FileReader("E:\\PlanB\\Desktop\\115.txt"));
		
		line = br.readLine();
		while(line != null){
			if(!set.contains(line)){
				System.out.println(line);
			}
			line = br.readLine();
		}
	}

}
