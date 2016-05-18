package team.byr.academicsearch.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import team.byr.academicsearch.service.SearchService;
import team.byr.academicsearch.util.HttpClientUtil;

import com.alibaba.fastjson.JSON;

@Controller
public class SearchController {
	@Autowired
	private SearchService searchService;


	@ResponseBody
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public String showCarType(Long id1, Long id2) {
		System.out.println(id1 + " -- " + id2);

		Date start = new Date();
		HttpClientUtil.requestTotalTime = 0;
		Set<LinkedList<Long>> result = searchService.search(id1, id2);
		System.out.println(result.size());
		System.out.println(result);
		System.out.println("total time ---" + (new Date().getTime() - start.getTime()));
		System.out.println("total request time ---" + HttpClientUtil.requestTotalTime);
		return JSON.toJSONString(result);
	
	}

}
