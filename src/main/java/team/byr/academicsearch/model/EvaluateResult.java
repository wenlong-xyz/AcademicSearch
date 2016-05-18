package team.byr.academicsearch.model;

import java.util.List;

public class EvaluateResult {
	private RequestChainNode from;
	private String expr;
	private List<Entity> entities;
	public RequestChainNode getFrom() {
		return from;
	}
	public void setFrom(RequestChainNode from) {
		this.from = from;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	@Override
	public String toString() {
		return "EvaluateResult [from=" + from + ", expr=" + expr
				+ ", entities=" + entities + "]";
	}

	
	

}
