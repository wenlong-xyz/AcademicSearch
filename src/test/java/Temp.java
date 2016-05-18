import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Temp {
	public static void main(String[] args) {
		Set<List<Long>> longSet = new HashSet<List<Long>>();
		LinkedList<Long> aa = new LinkedList<Long>();
		Long aaa = new Long(222);
		Long aaa2 = new Long(333);
		aa.add(aaa);
		aa.add(aaa2);
		LinkedList<Long> bb = new LinkedList<Long>();
		Long bbb = new Long(222);
		Long bbb2 = new Long(333);
		bb.add(bbb);
		bb.add(bbb2);
		longSet.add(aa);
		longSet.add(bb);
		System.out.println(longSet.size());
		
	}

}
