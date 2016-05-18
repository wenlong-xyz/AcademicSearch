package team.byr.academicsearch.model;

import java.util.HashSet;
import java.util.Set;

public class PathNode {
	private Long currentId;
	private PathNode nextNode;
	private Set<PathNode> nextNodes;
	private int stepNum;

	public PathNode(Long currentId, PathNode nextNode, int stepNum) {
		super();
		this.currentId = currentId;
		this.nextNode = nextNode;
		this.nextNodes = null;
		this.stepNum = stepNum;
	}

	public PathNode() {

	}

	public PathNode(Long currentId, int stepNum) {
		super();
		nextNode = null;
		nextNodes = null;
		this.currentId = currentId;
		this.stepNum = stepNum;
	}

	public Long getCurrentId() {
		return currentId;
	}

	public void setCurrentId(Long currentId) {
		this.currentId = currentId;
	}

	public PathNode getNextNode() {
		return nextNode;
	}

	public void setNextNode(PathNode nextNode) {
		this.nextNode = nextNode;
	}

	public int getStepNum() {
		return stepNum;
	}

	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}

	public Set<PathNode> getNextNodes() {
		return nextNodes;
	}

	public void setNextNodes(Set<PathNode> nextNodes) {
		this.nextNodes = nextNodes;
	}

	public void addNextNode(PathNode nextNode) {
		if (this.nextNodes == null) {
			this.nextNodes = new HashSet<PathNode>();
		}
		this.nextNodes.add(nextNode);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PathNode))
			return false;
		PathNode cur = (PathNode) o;
		return cur.getCurrentId() == this.currentId;
	}
	
	public int hashCode() {
	       return currentId.hashCode();
	}

	@Override
	public String toString() {
		return "PathNode [currentId=" + currentId + ", nextNode=" + nextNode
				+ ", stepNum=" + stepNum + "]";
	}

}
