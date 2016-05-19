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
	private static final int TOTAL_HOP_NUM = 3;
	
	private Set<LinkedList<Long>> searchResult;	//记录查询结果
	private long start, end;					//开始结束节点
	private RequestType endType;				//起点类型
	private RequestType startType;				//终点类型
	
	private Map<Long,PathNode> endMap;			//从尾部开始的节点路径集合
	private Map<Long,PathNode> endMapSec;		//从尾部开始的第二次请求节点路径集合，主要是为了解决第一次请求和第二次请求节点重复但路径不重复的问题
	
	private List<Entity> startEtities;			//以初始节点为请求参数查询到的实体集合
	private List<Entity> endEtities;			//以终止节点为请求参数查询到的实体集合
	
	/**
	 * 路径搜索
	 * @param start - 起点
	 * @param end - 终点
	 * @return 路径集合
	 */
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
	
	/**
	 * 路径分析器
	 * 整体思路：尾节点向前搜索，初始化map；头结点向后探索，查询map,检查是否命中
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
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
	
	/**
	 * Id - Id 模式
	 */
	private void buildIdIdPath(){
		endIdConfig(true);
		startIdCheck(true);	
	}
	/**
	 * Id - AuId 模式
	 */
	private void buildIdAuIdPath(){
		endAuIdConfig();
		startIdCheck(false);	
	}
	/**
	 * AuId - Id 模式
	 */
	private void buildAuIdIdPath(){
		endIdConfig(false);
		startAuIdCheck();	
	}
	/**
	 * AuId - AuId 模式
	 */
	private void buildAuIdAuIdPath(){
		endAuIdConfig();
		startAuIdCheck();	
	}
	

	
	/**
	 * 终止节点是Id,由终结点向前进行路径搜索,初始化map
	 * @param isIDID -- 是否是id-id模式
	 */
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
	/**
	 * 终止节点是Id,由终结点向前进行路径搜索时，多线程请求
	 * @param auidList - 待查询auid列表
	 * @param endNode - 终止节点
	 * @param isIDID - 是否是id-id模式
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void endIdMultiConfig(List<PathNode> auidList,PathNode endNode,boolean isIDID) throws InterruptedException, ExecutionException{
		List<Future<EvaluateResult>> auidFutures = new LinkedList<Future<EvaluateResult>>();
		Future<EvaluateResult> endFuture = null;
		
		//多线程请求
		for(PathNode auid : auidList){
			auidFutures.add(service.submit(new SearchCallable(auid.getCurrentId(),RequestType.AUID,RequestMode.NORMAL)));
		}

		if(isIDID == false){
			endFuture = service.submit(new SearchCallable(endNode.getCurrentId(),RequestType.RID,RequestMode.RID_SIMPLE));
		}
		else{
			endFuture = service.submit(new SearchCallable(endNode.getCurrentId(),RequestType.RID,RequestMode.NORMAL));
		}
			
		//响应结果处理
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
	
	/**
	 * 终止节点是Auid ,由终结点向前进行路径搜索 进行路径搜索
	 */
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
	
	/**
	 * 开始节点是id, 从开始节点进行路径探索
	 * @param isIDID - 是否是 id-id模式
	 */
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
			hitCheck(startNode);
			
			List<AA> aa = entity.getAA();
			if(aa != null){
				for(AA a : aa){
					PathNode auidNode = new PathNode(a.getAuId(),startNode,startNode.getStepNum() + 1);
					hitCheck(auidNode);
					if(isIDID == false){
						if(a.getAfId() != 0){
							PathNode afidNode = new PathNode(a.getAfId(),auidNode,auidNode.getStepNum() + 1);
							hitCheck(afidNode);
						}
					}
					auidList.add(auidNode);		
				}
			}
			
			C cTemp = entity.getC();
			if(cTemp != null){
				PathNode cNode = new PathNode(cTemp.getCId(),startNode,startNode.getStepNum() + 1);
				hitCheck(cNode);
			}
			
			J jTemp = entity.getJ();
			if(jTemp != null){
				PathNode jNode = new PathNode(jTemp.getJId(),startNode,startNode.getStepNum() + 1);
				hitCheck(jNode);
			}
			List<F> fList = entity.getF();
			if(fList != null){
				for(F f: fList){
					PathNode fNode = new PathNode(f.getFId(),startNode,startNode.getStepNum() + 1);
					hitCheck(fNode);
				}
			}
			
			List<Long> ridList = entity.getRId();
			if(ridList != null){
				for(Long rid: ridList){
					PathNode ridNode = new PathNode(rid,startNode,startNode.getStepNum() + 1);
					hitCheck(ridNode);
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
	
	/**
	 * 开始节点是id, 从开始节点进行路径探索,多线程发送请求
	 * @param auidList - 待处理auid列表
	 * @param ridList - 待处理rid列表
	 * @param isIDID - 是否是id-id模式
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void startIdMultiCheck(List<PathNode> auidList,List<PathNode> ridList,boolean isIDID) throws InterruptedException, ExecutionException{
		List<Future<EvaluateResult>> auidFutures = new LinkedList<Future<EvaluateResult>>();
		List<Future<EvaluateResult>> ridFutures = new LinkedList<Future<EvaluateResult>>();
		
		//多线程发送请求
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
		
		//响应处理
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
							hitCheck(afidNodeSec);
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
							hitCheck(auidNodeSec);
						}
					}
					List<Long> ridListSec = entitySec.getRId();
					if(ridListSec != null){
						for(Long ridSec: ridListSec){
							PathNode ridNodeSec = new PathNode(ridSec,ridNode,ridNode.getStepNum() + 1);
							hitCheck(ridNodeSec);
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
							hitCheck(auidNodeSec);
						}
					}
					
					C cTempSec = entitySec.getC();
					if(cTempSec != null){
						PathNode cNodeSec = new PathNode(cTempSec.getCId(),ridNode,ridNode.getStepNum() + 1);
						hitCheck(cNodeSec);
					}
					
					J jTempSec = entitySec.getJ();
					if(jTempSec != null){
						PathNode jNodeSec = new PathNode(jTempSec.getJId(),ridNode,ridNode.getStepNum() + 1);
						hitCheck(jNodeSec);
					}
					List<F> fListSec = entitySec.getF();
					if(fListSec != null){
						for(F fSec: fListSec){
							PathNode fNodeSec = new PathNode(fSec.getFId(),ridNode,ridNode.getStepNum() + 1);
							hitCheck(fNodeSec);
						}
					}
					
					List<Long> ridListSec = entitySec.getRId();
					if(ridListSec != null){
						for(Long ridSec: ridListSec){
							PathNode ridNodeSec = new PathNode(ridSec,ridNode,ridNode.getStepNum() + 1);
							hitCheck(ridNodeSec);
						}
					}
				}
				i++;
			}
			
		}	
	}
	
	/**
	 * 开始节点是auid, 从开始节点进行路径探索
	 */
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
			hitCheck(idNode);
			
			List<AA> aa = entity.getAA();
			if(aa != null){
				for(AA a : aa){
					if(a.getAuId() == start){
						if(a.getAfId() != 0){
							PathNode afidNode = new PathNode(a.getAfId(),startNode,startNode.getStepNum() + 1);
							hitCheck(afidNode);
						}
					}
					PathNode auidNode = new PathNode(a.getAuId(),idNode,idNode.getStepNum() + 1);
					hitCheck(auidNode);
				}
			}
			
			C cTemp = entity.getC();
			if(cTemp != null){
				PathNode cNode = new PathNode(cTemp.getCId(),idNode,idNode.getStepNum() + 1);
				hitCheck(cNode);
			}
			
			J jTemp = entity.getJ();
			if(jTemp != null){
				PathNode jNode = new PathNode(jTemp.getJId(),idNode,idNode.getStepNum() + 1);
				hitCheck(jNode);
			}
			List<F> fList = entity.getF();
			if(fList != null){
				for(F f: fList){
					PathNode fNode = new PathNode(f.getFId(),idNode,idNode.getStepNum() + 1);
					hitCheck(fNode);
				}
			}
			List<Long> ridList = entity.getRId();
			if(ridList != null){
				for(Long rid: ridList){
					PathNode ridNode = new PathNode(rid,idNode,idNode.getStepNum() + 1);
					hitCheck(ridNode);
				}
			}
		}
	}
	
	/**
	 * 路径中的一对多情况，比如一个作者对应多个论文id
	 * @param id - 当前节点id
	 * @param next - 下一个节点
	 * @param map - 归属map
	 */
	private void addListNode(long id,PathNode next,Map<Long,PathNode> map){
		PathNode node = map.get(id);
		if(node == null){
			node = new PathNode(id, next.getStepNum() + 1);
		}
		node.addNextNode(next);
		map.put(id,node);
	}
	
	/**
	 * 检查做节点是否命中
	 * @param left - 当前节点
	 */
	private void hitCheck(PathNode left){
		
		// endMap 中是否命中
		PathNode right =  endMap.get(left.getCurrentId());
		if(right != null && left.getStepNum() + right.getStepNum() <= TOTAL_HOP_NUM){
			//命中，添加路径
			addTwoDirectionResult(left.getNextNode(),right);
		}
		// endMapSec 中是否命中
		PathNode rightSec =  endMapSec.get(left.getCurrentId());
		if(rightSec != null && left.getStepNum() + rightSec.getStepNum() <= TOTAL_HOP_NUM){
			//命中，添加路径
			addTwoDirectionResult(left.getNextNode(),rightSec);
		}
	}
	
	/**
	 * 添加路径：起点->左节点->右节点->终点
	 * @param left - 左节点
	 * @param right - 右节点
	 */
	private void addTwoDirectionResult(PathNode left, PathNode right) {
		//下一跳节点只有一个
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
		else {//下一跳节点不只一个
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
