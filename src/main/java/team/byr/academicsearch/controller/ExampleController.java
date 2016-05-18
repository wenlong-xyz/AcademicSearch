package team.byr.academicsearch.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import team.byr.academicsearch.model.EvaluateResult;
import team.byr.academicsearch.util.HttpClientUtil;

@Controller
public class ExampleController {

	public static void main(String[] args) {
		String URL_EVALUATE = "https://oxfordhk.azure-api.net/academic/v1.0/evaluate";

		HttpClient httpClient = HttpClientBuilder.create().build();

		// 假设是AA.Auid --作者Id
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subscription-key",
				"f7cc29509a8443c5b3a5e56b0e38b5a6"));
		params.add(new BasicNameValuePair("expr",
				"Composite(AA.AuId=2251253715)"));
		params.add(new BasicNameValuePair("attributes", "Id,AA.AuId"));
		params.add(new BasicNameValuePair("count", "2000"));

		String result = HttpClientUtil.httpGetRequest(httpClient, URL_EVALUATE,
				params);

		EvaluateResult evaluateResult = JSON.parseObject(result,
				EvaluateResult.class);
		System.out.println(evaluateResult);

	}

	@ResponseBody
	@RequestMapping(value = "test", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public String test() {
		HttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://oxfordhk.azure-api.net/academic/v1.0/evaluate";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subscription-key",
				"f7cc29509a8443c5b3a5e56b0e38b5a6"));
		params.add(new BasicNameValuePair("expr", "Id=2140251882"));
		params.add(new BasicNameValuePair("attributes", "Id,AA.AuId,AA.AfId"));

		String result = HttpClientUtil.httpGetRequest(httpClient, url, params);
		return result;
	}

}
