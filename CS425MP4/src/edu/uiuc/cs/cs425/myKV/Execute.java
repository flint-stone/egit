package edu.uiuc.cs.cs425.myKV;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uiuc.cs.cs425.gossip.NodeID;
import edu.uiuc.cs.cs425.myKV.TCP.QuerySender;
/**
 * Excute class, invoked by coordinator
 * @author lexu1, wwang84
 *
 */
public class Execute   {

	public static int WRITE=0;
	public static int READ=1;
	
	int consistencyLevel;
	Command command;
	NodeID[] neighbours;
	int type;
	QuerySender sender;
	List<Record> resultRecord;
	List<NodeID> resultNode;
	ArrayList<Integer> needRepair;
	int latest_idx;
	Timestamp latest_time;
	
	
	
	public Execute(Command command, NodeID[] neighbours, QuerySender sender){
		this.command=command;
		this.neighbours=neighbours;
		this.consistencyLevel=command.getConsistentLevel();
		if(command.getCommand().equals("lookup"))
			this.type=READ;
		else
			this.type=WRITE;
		this.sender=sender;
		this.resultRecord=new ArrayList<Record>();
		this.resultNode=new ArrayList<NodeID>();
	}
	/**
	 * main control procedure
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Object executeWriteRead() throws InterruptedException, ExecutionException{
		//http://stackoverflow.com/questions/3096842/wait-for-one-of-several-threads
		int i=0;
		ExecutorService ex = Executors.newFixedThreadPool(3);
		ExecutorCompletionService<WorkerResult> ecs=new ExecutorCompletionService<WorkerResult>(ex);
		
		for(i=0;i<3;i++){
			Callable<WorkerResult> task=new WriteReadWorker(neighbours[i]);
			ecs.submit(task);
		}
		ArrayList<WorkerResult> workresult=new ArrayList<WorkerResult>();
		for(i=0;i<3-this.consistencyLevel;i++){
			WorkerResult r=ecs.take().get();
			workresult.add(r);
			resultRecord.add(r.record);
			resultNode.add(r.target);
		}
		
		latest_idx=0;
		int passflag=0;
		
	
		if(this.consistencyLevel==ReplicationManager.ONE){
			//one	
			ex.shutdown();
			return this.resultRecord.get(0).getContent();	
		}	
		else{
			//two or three
			Object mark=this.resultRecord.get(0).getContent();
			for( i=0;i<this.resultRecord.size();i++){
				Object obj=this.resultRecord.get(i).getContent();
				if(ifequal(mark, obj)!=0){
					passflag=1;
				}
			}
			if(passflag==0){
				ex.shutdown();
				return this.resultRecord.get(0).getContent();
			}
				
			//EXIST INCONSISTENT
			Timestamp marktime=this.resultRecord.get(0).getTimeStamp();
			i=0;
			while(marktime==null){
				i++;
				marktime=this.resultRecord.get(i).getTimeStamp();
			}
			for( i=i;i<this.resultRecord.size();i++){
				Timestamp curtime=this.resultRecord.get(i).getTimeStamp();
				if(curtime==null)
					continue;
				if(curtime.after(marktime)){
					marktime=curtime;
					latest_idx=i;
				}
			}
			latest_time=marktime;
			Thread t=new Thread(new RepairWorker());
			t.start();
			ex.shutdown();
			return this.resultRecord.get(latest_idx).getContent();
			
		}
		
	}

	
	private int ifequal(Object mark, Object obj) {
		// TODO Auto-generated method stub
		if(mark==null&&obj==null)
			return 0;
		else if(mark==null)
			return 1;
		else if(obj==null)
			return 1;
		else if(mark.equals(obj))
			return 0;
		return 1;
	}


	class WorkerResult{
		NodeID target;
		Record record;
	}
	
	/**
	 * send query to target nodes who have replicas
	 * @author lexu
	 *
	 */
	class WriteReadWorker implements Callable<WorkerResult>{
		NodeID target;
		public WriteReadWorker(NodeID target){
			this.target=target;
		}
		
		@Override
		public WorkerResult call() {
			// Wenting's change
			WorkerResult ret=new WorkerResult();
			Record result=(Record)sender.send(command, target);
			if(result !=null ){
					ret.record=result;
					ret.target=target;	
			}
			return ret;	
		}
	}
	
	/**
	 * repair worker class
	 * repair inconsistency among replicas
	 * @author lexu1, wwang84
	 *
	 */
	class RepairWorker implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			needRepair=new ArrayList<Integer>();
			int i=0;
			for( i=0;i<resultRecord.size();i++){
				Timestamp curtime=resultRecord.get(i).getTimeStamp();
				if(curtime==null)
					needRepair.add(i);
				else if(curtime.before(latest_time)){
					needRepair.add(i);
				}
			}
			//start repair
			if(type==WRITE){
				Boolean latest=(Boolean)resultRecord.get(latest_idx).getContent();
				if(latest){
					Command newcommand=command;
					newcommand.setCommand("insert");
					for( i=0;i<needRepair.size();i++){
						Record result=(Record)sender.send(newcommand, resultNode.get(needRepair.get(i)));
					}
				}
				if(!latest){
					Command newcommand=command;
					newcommand.setCommand("delete");
					for( i=0;i<needRepair.size();i++){
						Record result=(Record)sender.send(newcommand, resultNode.get(needRepair.get(i)));
					}
				}
			}
			else if(type==READ){
				Object latest=resultRecord.get(latest_idx).getContent();
				if(latest!=null){
					Command newcommand=command;
					newcommand.setCommand("insert");
					newcommand.setValue(new Record(resultRecord.get(latest_idx).getTimeStamp(),latest));
					for( i=0;i<needRepair.size();i++){
						Record result=(Record)sender.send(newcommand, resultNode.get(needRepair.get(i)));
					}
				}
				if(latest==null){
					Command newcommand=command;
					newcommand.setCommand("delete");
					for( i=0;i<needRepair.size();i++){
						Record result=(Record)sender.send(newcommand, resultNode.get(needRepair.get(i)));
					}
				}
			}
		}
		
	}

}
