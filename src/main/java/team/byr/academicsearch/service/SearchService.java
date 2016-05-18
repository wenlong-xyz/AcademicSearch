package team.byr.academicsearch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import team.byr.academicsearch.model.AA;
import team.byr.academicsearch.model.C;
import team.byr.academicsearch.model.Entity;
import team.byr.academicsearch.model.EvaluateResult;
import team.byr.academicsearch.model.F;
import team.byr.academicsearch.model.J;
import team.byr.academicsearch.model.PathNode;
import team.byr.academicsearch.util.RequestMode;
import team.byr.academicsearch.util.RequestType;
import team.byr.academicsearch.util.SearchCallable;


@Service
public class SearchService {
	public static ExecutorService service = Executors.newCachedThreadPool();
	public Set<LinkedList<Long>> search(long start,long end){
		this.start = start;
		this.end = end;
		try {
			pathAnalyser();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResult;
	}
	

	private static final int TOTAL_HOP_NUM = 3;

	private Set<LinkedList<Long>> searchResult;

	private long start, end;

	private RequestType endType;
	private RequestType startType;
	
	private Map<Long,PathNode> endMap;
	private Map<Long,PathNode> endMapSec;
	
	private List<Entity> startEtities;
	private List<Entity> endEtities;
	
	private void pathAnalyser() throws InterruptedException, ExecutionException{
		startEtities = null;
		endEtities = null;
		// start type 判断
		Future<EvaluateResult> startFuture = service.submit(new SearchCallable(start,RequestType.AUID,RequestMode.NORMAL));
		Future<EvaluateResult> endFuture = service.submit(new SearchCallable(end,RequestType.AUID,RequestMode.NORMAL));
		
		startEtities = startFuture.get().getEntities();
		if (startEtities == null || startEtities.size() == 0) {
			startType = RequestType.ID;
			startFuture = service.submit(new SearchCallable(start,RequestType.ID,RequestMode.NORMAL));
			startEtities = startFuture.get().getEntities();
		}
		else{
			startType = RequestType.AUID;
		}
		
		// end type 判断
		
		endEtities = endFuture.get().getEntities();
		if (endEtities == null || endEtities.size() == 0) {
			endType = RequestType.ID;
			endFuture = service.submit(new SearchCallable(end,RequestType.ID,RequestMode.NORMAL));
			endEtities = endFuture.get().getEntities();
		}
		else{
			endType = RequestType.AUID;
		}
		
		//初始化start end Map
		endMap = new HashMap<Long, PathNode>();
		endMapSec = new HashMap<Long, PathNode>();
		searchResult = new HashSet<LinkedList<Long>>();
		//设置请求模式
		if(startType == RequestType.ID){
			if(endType == RequestType.ID){
				System.out.println("id-id");
				buildIdIdPath();
			}
			else{
				System.out.println("id-auid");
				buildIdAuIdPath();
			}
		}
		else{
			if(endType == RequestType.ID){
				System.out.println("auid-id");
				buildAuIdIdPath();
			}
			else{
				System.out.println("auid-auid");
				buildAuIdAuIdPath();
			}
		}
	}
	
	private void buildIdIdPath(){
		endIdConfig(true);
		startIdCheck(true);	
	}
	private void buildIdAuIdPath(){
		endAuIdConfig();
		startIdCheck(false);	
	}
	private void buildAuIdIdPath(){
		endIdConfig(false);
		startAuIdCheck();	
	}
	
	private void buildAuIdAuIdPath(){
		endAuIdConfig();
		startAuIdCheck();	
	}
	private void endIdMultiConfig(List<PathNode> auidList,PathNode endNode,boolean isIDID) throws InterruptedException, ExecutionException{
		List<Future<EvaluateResult>> auidFutures = new LinkedList<Future<EvaluateResult>>();
		Future<EvaluateResult> endFuture = null;
		
		for(PathNode auid : auidList){
			auidFutures.add(service.submit(new SearchCallable(auid.getCurrentId(),RequestType.AUID,RequestMode.NORMAL)));
		}

		if(isIDID == false){
			endFuture = service.submit(new SearchCallable(endNode.getCurrentId(),RequestType.RID,RequestMode.RID_SIMPLE));
		}
		else{
			endFuture = service.submit(new SearchCallable(endNode.getCurrentId(),RequestType.RID,RequestMode.NORMAL));
		}
			
		int i = 0;
		for (Future<EvaluateResult> future : auidFutures) {
			List<Entity> entities = future.get().getEntities();
			PathNode auidNode = auidList.get(i);
			for(Entity entity : entities){
				List<AA> aaSec = entity.getAA();
				for(AA aSec : aaSec){
					if(aSec.getAuId() == auidNode.getCurrentId()){
						if(aSec.getAfId() != 0){
							addListNode(aSec.getAfId(),auidNode,endMap);
						}
					}
				}
			} 
			i++;
		 }
		List<Entity> entities = endFuture.get().getEntities();
		if(isIDID == false){	
			for(Entity entity : entities){
				PathNode idNode = new PathNode(entity.getId(),endNode,endNode.getStepNum() + 1);
				endMap.put(entity.getId(),idNode);
			}
		}
		else{
			for(Entity entity : entities){				
				PathNode idNode = new PathNode(entity.getId(),endNode,endNode.getStepNum() + 1);
				endMap.put(entity.getId(),idNode);
				List<AA> aa = entity.getAA();
				if(aa != null){
					for(AA a : aa){
						addListNode(a.getAuId(),idNode,endMapSec);
					}
				}
				
				C cTemp = entity.getC();
				if(cTemp != null){
					addListNode(cTemp.getCId(),idNode,endMapSec);
				}
				
				J jTemp = entity.getJ();
				if(jTemp != null){
					addListNode(jTemp.getJId(),idNode,endMapSec);
				}
				
				List<F> fList = entity.getF();
				if(fList != null){
					for(F f: fList){
						addListNode(f.getFId(),idNode,endMapSec);
					}
				}
			}
		}

	}
	private void endIdConfig(boolean isIDID){
		List<PathNode> auidList = new ArrayList<PathNode>();
		//end 第一次请求
		PathNode endNode = new PathNode(end,null,0);
		endMap.put(end,endNode);
		for(Entity entity : endEtities){
			List<AA> aa = entity.getAA();
			if(aa != null){
				for(AA a : aa){
					PathNode auidNode = new PathNode(a.getAuId(),endNode,endNode.getStepNum() + 1);
					endMap.put(auidNode.getCurrentId(),auidNode);
					if(isIDID == false){
						if(a.getAfId() != 0)
							addListNode(a.getAfId(),auidNode,endMap);
						auidList.add(auidNode);
					}
				}
			}
			
			C cTemp = entity.getC();
			if(cTemp != null){
				PathNode cNode = new PathNode(cTemp.getCId(),endNode,endNode.getStepNum() + 1);
				endMap.put(cNode.getCurrentId(),cNode);
			}
			
			J jTemp = entity.getJ();
			if(jTemp != null){
				PathNode jNode = new PathNode(jTemp.getJId(),endNode,endNode.getStepNum() + 1);
				endMap.put(jNode.getCurrentId(),jNode);
			}
			
			List<F> fList = entity.getF();
			if(fList != null){
				for(F f: fList){
					PathNode fNode = new PathNode(f.getFId(),endNode,endNode.getStepNum() + 1);
					endMap.put(fNode.getCurrentId(),fNode);
				}
			}
		}
		
		//第二次请求
		try {
			endIdMultiConfig(auidList,endNode,isIDID);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void endAuIdConfig(){
		//end 第一次请求
		PathNode endNode = new PathNode(end,null,0);
		endMap.put(end,endNode);
		for(Entity entity : endEtities){
			PathNode idNode = new PathNode(entity.getId(),endNode,endNode.getStepNum() + 1);
			endMap.put(entity.getId(),idNode);
			List<AA> aa = entity.getAA();
			if(aa != null){
				for(AA a : aa){
					if(a.getAuId() == end){
						addListNode(a.getAuId(),idNode,endMapSec);
						if(a.getAfId() != 0){
							PathNode afidNode = new PathNode(a.getAfId(),endNode,endNode.getStepNum() + 1);
							endMap.put(afidNode.getCurrentId(),afidNode);
						}
					}
					else{
						addListNode(a.getAuId(),idNode,endMap);
					}
					

				}
			}
			
			C cTemp = entity.getC();
			if(cTemp != null){
				addListNode(cTemp.getCId(),idNode,endMap);
			}
			
			J jTemp = entity.getJ();
			if(jTemp != null){
				addListNode(jTemp.getJId(),idNode,endMap);
			}
			
			List<F> fList = entity.getF();
			if(fList != null){
				for(F f: fList){
					addListNode(f.getFId(),idNode,endMap);
				}
			}
			
		}
	}
	
	private void startIdMultiCheck(List<PathNode> auidList,List<PathNode> ridList,boolean isIDID) throws InterruptedException, ExecutionException{
		List<Future<EvaluateResult>> auidFutures = new LinkedList<Future<EvaluateResult>>();
		List<Future<EvaluateResult>> ridFutures = new LinkedList<Future<EvaluateResult>>();
		
		for(PathNode auid : auidList){
			auidFutures.add(service.submit(new SearchCallable(auid.getCurrentId(),RequestType.AUID,RequestMode.NORMAL)));
		}
		
		if(isIDID == false){
			for(PathNode rid : ridList){
				ridFutures.add(service.submit(new SearchCallable(rid.getCurrentId(),RequestType.ID,RequestMode.ID_SIMPLE)));
			}
		}
		else{
			for(PathNode rid : ridList){
				ridFutures.add(service.submit(new SearchCallable(rid.getCurrentId(),RequestType.ID,RequestMode.NORMAL)));
			}
		}
		
		
		
		int i = 0;
		for (Future<EvaluateResult> future : auidFutures) {
			List<Entity> startEtitiesSec = future.get().getEntities();
			PathNode auidNode = auidList.get(i);
			for(Entity entitySec : startEtitiesSec){
				List<AA> aaSec = entitySec.getAA();
				for(AA aSec : aaSec){
					if(aSec.getAuId() == auidNode.getCurrentId()){
						if(aSec.getAfId() != 0){
							PathNode afidNodeSec = new PathNode(aSec.getAfId(),auidNode,auidNode.getStepNum() + 1);
							hitCheck(afidNodeSec,afidNodeSec.getCurrentId());
						}
					}
				}
			}
			i++;  
		}
		
		i = 0;
		if(isIDID == false){
			for (Future<EvaluateResult> future : ridFutures) {
				PathNode ridNode = ridList.get(i);
				List<Entity> startEtitiesSec = future.get().getEntities();
				for(Entity entitySec : startEtitiesSec){	
					List<AA> aaSec = entitySec.getAA();
					if(aaSec != null){
						for(AA aSec : aaSec){
							PathNode auidNodeSec = new PathNode(aSec.getAuId(),ridNode,ridNode.getStepNum() + 1);
							hitCheck(auidNodeSec,auidNodeSec.getCurrentId());
						}
					}
					List<Long> ridListSec = entitySec.getRId();
					if(ridListSec != null){
						for(Long ridSec: ridListSec){
							PathNode ridNodeSec = new PathNode(ridSec,ridNode,ridNode.getStepNum() + 1);
							hitCheck(ridNodeSec,ridNodeSec.getCurrentId());
						}
					}
				}
				i++;
			}
			
		}
		else{
			for (Future<EvaluateResult> future : ridFutures) {
				PathNode ridNode = ridList.get(i);
				List<Entity> startEtitiesSec = future.get().getEntities();
				for(Entity entitySec : startEtitiesSec){
					
					List<AA> aaSec = entitySec.getAA();
					if(aaSec != null){
						for(AA aSec : aaSec){
							PathNode auidNodeSec = new PathNode(aSec.getAuId(),ridNode,ridNode.getStepNum() + 1);
							hitCheck(auidNodeSec,auidNodeSec.getCurrentId());
						}
					}
					
					C cTempSec = entitySec.getC();
					if(cTempSec != null){
						PathNode cNodeSec = new PathNode(cTempSec.getCId(),ridNode,ridNode.getStepNum() + 1);
						hitCheck(cNodeSec,cNodeSec.getCurrentId());
					}
					
					J jTempSec = entitySec.getJ();
					if(jTempSec != null){
						PathNode jNodeSec = new PathNode(jTempSec.getJId(),ridNode,ridNode.getStepNum() + 1);
						hitCheck(jNodeSec,jNodeSec.getCurrentId());
					}
					List<F> fListSec = entitySec.getF();
					if(fListSec != null){
						for(F fSec: fListSec){
							PathNode fNodeSec = new PathNode(fSec.getFId(),ridNode,ridNode.getStepNum() + 1);
							hitCheck(fNodeSec,fNodeSec.getCurrentId());
						}
					}
					
					List<Long> ridListSec = entitySec.getRId();
					if(ridListSec != null){
						for(Long ridSec: ridListSec){
							PathNode ridNodeSec = new PathNode(ridSec,ridNode,ridNode.getStepNum() + 1);
							hitCheck(ridNodeSec,ridNodeSec.getCurrentId());
						}
					}
				}
				i++;
			}
			
		}	
	}
	private void startIdCheck(boolean isIDID){
		List<PathNode> auidList = new ArrayList<PathNode>();
		List<PathNode> ridNodeList = new ArrayList<PathNode>();
		
		//start请求
		PathNode startNode = new PathNode(start,null,0);
		if(start == end){
			LinkedList<Long> resultTemp = new LinkedList<Long>();
			resultTemp.add(start);
			resultTemp.add(end);
			searchResult.add(resultTemp);
		}
		for(Entity entity : startEtities){
			
			//id hit
			hitCheck(startNode,entity.getId());
			
			List<AA> aa = entity.getAA();
			if(aa != null){
				for(AA a : aa){
					PathNode auidNode = new PathNode(a.getAuId(),startNode,startNode.getStepNum() + 1);
					hitCheck(auidNode,auidNode.getCurrentId());
					if(isIDID == false){
						if(a.getAfId() != 0){
							PathNode afidNode = new PathNode(a.getAfId(),auidNode,auidNode.getStepNum() + 1);
							hitCheck(afidNode,afidNode.getCurrentId());
						}
					}
					auidList.add(auidNode);		
				}
			}
			
			C cTemp = entity.getC();
			if(cTemp != null){
				PathNode cNode = new PathNode(cTemp.getCId(),startNode,startNode.getStepNum() + 1);
				hitCheck(cNode,cNode.getCurrentId());
			}
			
			J jTemp = entity.getJ();
			if(jTemp != null){
				PathNode jNode = new PathNode(jTemp.getJId(),startNode,startNode.getStepNum() + 1);
				hitCheck(jNode,jNode.getCurrentId());
			}
			List<F> fList = entity.getF();
			if(fList != null){
				for(F f: fList){
					PathNode fNode = new PathNode(f.getFId(),startNode,startNode.getStepNum() + 1);
					hitCheck(fNode,fNode.getCurrentId());
				}
			}
			
			List<Long> ridList = entity.getRId();
			if(ridList != null){
				for(Long rid: ridList){
					PathNode ridNode = new PathNode(rid,startNode,startNode.getStepNum() + 1);
					hitCheck(ridNode,ridNode.getCurrentId());
					ridNodeList.add(ridNode);
				}
			}
		}
		
		try {
			startIdMultiCheck(auidList,ridNodeList,isIDID);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void startAuIdCheck(){
		//start请求
		PathNode startNode = new PathNode(start,null,0);
		if(start == end){
			LinkedList<Long> resultTemp = new LinkedList<Long>();
			resultTemp.add(start);
			resultTemp.add(end);
			searchResult.add(resultTemp);
		}
		for(Entity entity : startEtities){
			PathNode idNode = new PathNode(entity.getId(),startNode,startNode.getStepNum() + 1);
			//id hit
			hitCheck(idNode,entity.getId());
			
			List<AA> aa = entity.getAA();
			if(aa != null){
				for(AA a : aa){
					if(a.getAuId() == start){
						if(a.getAfId() != 0){
							PathNode afidNode = new PathNode(a.getAfId(),startNode,startNode.getStepNum() + 1);
							hitCheck(afidNode,a.getAfId());
						}
					}
//					else{
						PathNode auidNode = new PathNode(a.getAuId(),idNode,idNode.getStepNum() + 1);
						hitCheck(auidNode,auidNode.getCurrentId());
//					}
				}
			}
			
			C cTemp = entity.getC();
			if(cTemp != null){
				PathNode cNode = new PathNode(cTemp.getCId(),idNode,idNode.getStepNum() + 1);
				hitCheck(cNode,cNode.getCurrentId());
			}
			
			J jTemp = entity.getJ();
			if(jTemp != null){
				PathNode jNode = new PathNode(jTemp.getJId(),idNode,idNode.getStepNum() + 1);
				hitCheck(jNode,jNode.getCurrentId());
			}
			List<F> fList = entity.getF();
			if(fList != null){
				for(F f: fList){
					PathNode fNode = new PathNode(f.getFId(),idNode,idNode.getStepNum() + 1);
					hitCheck(fNode,fNode.getCurrentId());
				}
			}
			List<Long> ridList = entity.getRId();
			if(ridList != null){
				for(Long rid: ridList){
					PathNode ridNode = new PathNode(rid,idNode,idNode.getStepNum() + 1);
					hitCheck(ridNode,ridNode.getCurrentId());
				}
			}
		}
	}
	private void addListNode(long id,PathNode next,Map<Long,PathNode> map){
		PathNode node = map.get(id);
		if(node == null){
			node = new PathNode(id, next.getStepNum() + 1);
		}
		node.addNextNode(next);
		map.put(id,node);
	}
	private void hitCheck(PathNode left,long rightId){
		PathNode right =  endMap.get(rightId);
		if(right != null && left.getStepNum() + right.getStepNum() <= TOTAL_HOP_NUM){
			addTwoDirectionResult(left.getNextNode(),right);
		}
		PathNode rightSec =  endMapSec.get(rightId);
		if(rightSec != null && left.getStepNum() + rightSec.getStepNum() <= TOTAL_HOP_NUM){
			addTwoDirectionResult(left.getNextNode(),rightSec);
		}
	}
	private void addTwoDirectionResult(PathNode left, PathNode right) {
		if(right.getNextNodes() == null){
			LinkedList<Long> cur = new LinkedList<Long>();
			while(right != null){
				cur.addLast(right.getCurrentId());
				right = right.getNextNode();
			}
			//头部
			while (left != null) {
				cur.addFirst(left.getCurrentId());
				left = left.getNextNode();
			}
			searchResult.add(cur);
		}
		else {
			for(PathNode node : right.getNextNodes()){
				LinkedList<Long> cur = new LinkedList<Long>();
				cur.addLast(right.getCurrentId());
				PathNode temp = node;
				if(temp != null){
					while(temp != null){
						cur.addLast(temp.getCurrentId());
						temp = temp.getNextNode();
						
					}
				}
				//头部
				temp = left;
				while (temp != null) {
					cur.addFirst(temp.getCurrentId());
					temp = temp.getNextNode();
				}
				searchResult.add(cur);
			}
		}
		
	}
	
	
}
