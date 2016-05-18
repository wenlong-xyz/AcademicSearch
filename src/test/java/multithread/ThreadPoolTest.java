package multithread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import team.byr.academicsearch.model.EvaluateResult;
import team.byr.academicsearch.util.HttpClientUtil;
import team.byr.academicsearch.util.RequestType;

public class ThreadPoolTest implements Callable<EvaluateResult>{
	private long id;
	private RequestType type;
	
	public ThreadPoolTest(long id, RequestType type) {
		super();
		this.id = id;
		this.type = type;
	}
	public EvaluateResult call() throws Exception {
		return HttpClientUtil.requestOneTime(id, type);
	}
	public static void main(String[] args) {
		Date start = new Date();
	    ExecutorService service = Executors.newFixedThreadPool(1);
	    List<Future<EvaluateResult>> futures = new ArrayList<Future<EvaluateResult>>();
	    
	    Long[] auidList = {2147152072L,189831743L,2033059188L,676500258L,2019832499L,2114332599L,2004554093L};
	    Long[] idList = {2004554093L,1965061793L,2019911971L,2114804204L,2000215628L,2043343585L,134022301L,2158495995L};
	    
	    
	    for (int i = 0; i < auidList.length; i++) {
	        Future<EvaluateResult> future = service.submit(new ThreadPoolTest(auidList[i],RequestType.AUID));
	        futures.add(future);
	    }
	    
	    for (int i = 0; i < idList.length; i++) {
	        Future<EvaluateResult> future = service.submit(new ThreadPoolTest(idList[i],RequestType.ID));
	        futures.add(future);

	    }

	    for (Future<EvaluateResult> future : futures) {
	    	
	            try {
	            	EvaluateResult  a = future.get();

					System.out.println(a);
	            	System.out.println("adfadsf");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        
	    }
	    Date end = new Date();
	    System.out.println("time--" + (end.getTime() - start.getTime()));
	    service.shutdown();
	    try {
			if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
			    // Timed Out waiting to finish, so force a shutdown
			    service.shutdownNow();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
