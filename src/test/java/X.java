import team.byr.academicsearch.service.SearchService;

import com.alibaba.fastjson.JSON;


public class X {
	public static void main(String[] args) {
		SearchService searchService2 = new SearchService();
		System.out.println(JSON.toJSONString(searchService2.search(2251253715L, 2251253715L)));

	}

}