package team.byr.academicsearch.util;

import java.util.concurrent.Callable;

import team.byr.academicsearch.model.EvaluateResult;

public class SearchCallable implements Callable<EvaluateResult>{
	private long id;
	private RequestType type;
	private RequestMode mode;
	

	public SearchCallable(long id, RequestType type, RequestMode mode) {
		super();
		this.id = id;
		this.type = type;
		this.mode = mode;
	}
	public EvaluateResult call() throws Exception {
		switch(mode){
			case RID_SIMPLE:
				return HttpClientUtil.requestRIDSimple(id);
			case ID_SIMPLE:
				return HttpClientUtil.requestIDSimple(id);
			case NORMAL:
				return HttpClientUtil.requestOneTime(id, type);
		}
		return null;
	}
	

}
