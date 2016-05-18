package team.byr.academicsearch.model;

import java.util.LinkedList;
import java.util.List;

import team.byr.academicsearch.util.RequestType;

public class RequestChainNode {
	private Long currentId;
	private RequestType requestType;
	private RequestChainNode parent;
	private LinkedList<RequestChainNode> nextNodes;
	private int stepNum;
	
	public RequestChainNode() {
		
	}
	public RequestChainNode(Long currentId, RequestType requestType,
			RequestChainNode parent, int stepNum) {
		super();
		this.currentId = currentId;
		this.requestType = requestType;
		this.parent = parent;
		this.stepNum = stepNum;
	}
	public Long getCurrentId() {
		return currentId;
	}
	public void setCurrentId(Long currentId) {
		this.currentId = currentId;
	}
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public RequestChainNode getParent() {
		return parent;
	}
	public void setParent(RequestChainNode parent) {
		this.parent = parent;
	}
	
	public LinkedList<RequestChainNode> getNextNodes() {
		return nextNodes;
	}
	public void setNextNodes(LinkedList<RequestChainNode> nextNodes) {
		this.nextNodes = nextNodes;
	}
	public int getStepNum() {
		return stepNum;
	}
	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}
	@Override
	public String toString() {
		return "RequestChainNode [currentId=" + currentId + ", requestType="
				+ requestType + ", parent=" + parent + ", nextNodes="
				+ nextNodes + ", stepNum=" + stepNum + "]";
	}
	
	

}
