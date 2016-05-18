package team.byr.academicsearch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import team.byr.academicsearch.model.EvaluateResult;

import com.alibaba.fastjson.JSON;

public class HttpClientUtil {
	/**
	 * httpclient http get请求
	 * 
	 * @param httpClient
	 * @param url
	 *            请求url
	 * @param params
	 *            请求参数,null表示无参数
	 * @return 请求结果
	 */
	public static String httpGetRequest(HttpClient httpClient, String url,
			List<NameValuePair> params) {
		try {
			HttpGet httpget = null;
			if(null == params){
				// 创建httpget连接
				httpget = new HttpGet(url);
			}
			else{
				// 设置参数
				String paraStr = EntityUtils.toString(new UrlEncodedFormEntity(
						params));
				// 创建httpget连接
				httpget = new HttpGet(url + "?" + paraStr);
			}

			HttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String result = null;
			if(entity != null){
				result = EntityUtils.toString(entity);
			}
			
			// 释放get连接
			httpget.releaseConnection();
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

		// System.out.println("---------Get request--------");
		// System.out.println(result);

	}

	/**
	 * httpclient http post请求
	 * 
	 * @param httpClient
	 * @param params
	 *            -- post参数
	 * @param url
	 *            -- 请求 url
	 * @return 请求结果字符串
	 */
	public static String httpPostRequest(HttpClient httpClient,
			List<NameValuePair> params, String url) {
		HttpPost httppost = new HttpPost(url);
		String result = null;

		try {
			httppost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");
			httppost.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("---------Post request--------");
		// System.out.println(result);
		return result;

	}
	
	private static final String URL_EVALUATE = "https://oxfordhk.azure-api.net/academic/v1.0/evaluate";
	public static int requestTotalTime = 0;
	
	public static EvaluateResult requestRIDSimple(Long id){
		Date start = new Date();
		HttpClient httpClient = HttpClientBuilder.create().build();	
		
		//构造请求参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subscription-key","f7cc29509a8443c5b3a5e56b0e38b5a6"));
		params.add(new BasicNameValuePair("attributes","Id"));
		params.add(new BasicNameValuePair("count","500000"));
		params.add(new BasicNameValuePair("expr", "RId=" + id));
		
		//解析结果
		String reqResult = httpGetRequest(httpClient,URL_EVALUATE,params);
		System.out.println(params.get(3));
		Date end = new Date();
		requestTotalTime = requestTotalTime + (int) (end.getTime() - start.getTime());
		System.out.println("Request Time: " + (end.getTime() - start.getTime()));
		if(reqResult == null){
			return null;
		}
		else{
			EvaluateResult evaluateResult = JSON.parseObject(reqResult,EvaluateResult.class);
			return evaluateResult;
		}
		
	}
	
	public static EvaluateResult requestIDSimple(Long id){
		Date start = new Date();
		HttpClient httpClient = HttpClientBuilder.create().build();	
		
		//构造请求参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subscription-key","f7cc29509a8443c5b3a5e56b0e38b5a6"));
		params.add(new BasicNameValuePair("attributes","Id,AA.AuId,RId"));
		params.add(new BasicNameValuePair("count","500000"));
		params.add(new BasicNameValuePair("expr", "Id=" + id));
		
		//解析结果
		String reqResult = httpGetRequest(httpClient,URL_EVALUATE,params);
		System.out.println(params.get(3));
		Date end = new Date();
		requestTotalTime = requestTotalTime + (int) (end.getTime() - start.getTime());
		System.out.println("Request Time: " + (end.getTime() - start.getTime()));
		if(reqResult == null){
			return null;
		}
		else{
			EvaluateResult evaluateResult = JSON.parseObject(reqResult,EvaluateResult.class);
			return evaluateResult;
		}
		
	}
	
	public static EvaluateResult requestOneTime(Long id,RequestType type){
		Date start = new Date();
		HttpClient httpClient = HttpClientBuilder.create().build();	
		
		//构造请求参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subscription-key","f7cc29509a8443c5b3a5e56b0e38b5a6"));
		params.add(new BasicNameValuePair("attributes","Id,AA.AuId,AA.AfId,F.FId,J.JId,C.CId,RId"));
		params.add(new BasicNameValuePair("count","500000"));
		switch(type){
			case ID: case RID:
				params.add(new BasicNameValuePair("expr", type.getName() + "=" + id));
				break;
			case FID:case CID:case JID:case AUID:case AFID:
				params.add(new BasicNameValuePair("expr", "Composite(" + type.getName() + "=" + id +")"));
				break;
		}
		//解析结果
		String reqResult = httpGetRequest(httpClient,URL_EVALUATE,params);
		System.out.println(params.get(3));
		Date end = new Date();
		requestTotalTime = requestTotalTime + (int) (end.getTime() - start.getTime());
		System.out.println("Request Time: " + (end.getTime() - start.getTime()));
//		System.out.println(reqResult);
		if(reqResult == null){
			return null;
		}
		else{
			EvaluateResult evaluateResult = JSON.parseObject(reqResult,EvaluateResult.class);
			return evaluateResult;
		}
		
	}

}
