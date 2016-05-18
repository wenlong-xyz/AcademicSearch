import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import team.byr.academicsearch.service.SearchService;

import com.alibaba.fastjson.JSON;


public class Z {
	public static void main(String[] args) {
		SearchService searchService2 = new SearchService();
//		Set<LinkedList<Long>> resultStr = searchService2.search(2251253715L, 2180737804L);
//		Set<LinkedList<Long>> resultStr = searchService2.search(2100837269L, 621499171L);// 10  - 10 id-auid
//		Set<LinkedList<Long>> resultStr = searchService2.search(2175015405L, 2121939561L);
//		Set<LinkedList<Long>> resultStr = searchService2.search(2332023333L, 2100470808L); // 34 - 34 id -id
		Set<LinkedList<Long>> resultStr = searchService2.search(2147764452L, 2147264455L); // 105 - 65 id -auid
//		Set<LinkedList<Long>> resultStr = searchService2.search(621499171L, 2100837269L); // 34 - 34 auid - id
//		Set<LinkedList<Long>> resultStr = searchService2.search(2099685860L, 2097089247L); // 833-833 id-id
//		Set<LinkedList<Long>> resultStr = searchService2.search(189831743L, 2147152072L);  //2708 - 2708 id- id
//		Set<LinkedList<Long>> resultStr = searchService2.search(2147152072L, 189831743L);  // id- id
//		Set<LinkedList<Long>> resultStr = searchService2.search(2180737804L, 2251253715L); // 14 -14
//		Set<LinkedList<Long>> resultStr = searchService2.search(2147152072L, 189831743L); // -18
//		Set<LinkedList<Long>> resultStr = searchService2.search(2153635508L, 2126125555L); // 3592
//		Set<LinkedList<Long>> resultStr = searchService2.search(2126701683L, 2091907464L);
//		Set<LinkedList<Long>> resultStr = searchService2.search(57898110L, 2014261844L);
//		Set<LinkedList<Long>> resultStr = searchService2.search(57898110L,2014261844L);
		
		
		
		
//		Set<LinkedList<Long>> resultStr = searchService2.search(2292217923L, 2100837269L);
//		Set<LinkedList<Long>> resultStr = searchService2.search(2251253715L, 2180737804L);
//		Set<LinkedList<Long>> resultStr = searchService2.search(2147152072L, 189831743L); // 18 - 18 id-id
//		Set<LinkedList<Long>> resultStr = searchService2.search(2332023333L, 2310280492L); // 1- 1
//		Set<LinkedList<Long>> resultStr = searchService2.search(57898110L, 2014261844L);   // 27 - 27 auid-auid
//		Set<LinkedList<Long>> resultStr = searchService2.search(2033660646L, 2088905367L);   // 26 - 26
		System.out.println(resultStr.size());
		System.out.println(JSON.toJSONString(resultStr));

	}

}
