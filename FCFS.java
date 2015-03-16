/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

import java.util.ArrayList;

public class FCFS extends CPU_Algorithm{
	
	//private AtomicInteger time;
	
	public FCFS(int num_procs, int num_cpus){
		super.NUM_PROCESSES = num_procs;
		super.NUM_CPUS = num_cpus;
		super.InitProcs();
		//time = new AtomicInteger(0);
	}

	@Override
	protected void get_next_procs(ArrayList<Process> curr_procs){
		if(curr_procs.size() == 0){
			for(int i = 0; i < super.NUM_CPUS; i++){
				Process tmp = null;
				curr_procs.add(tmp);
			}
		}
		
		//get the first n processes, fill in the null slots
		for(int i = 0; i < super.NUM_CPUS; i++){
			if(procs.size() > 0 && curr_procs.get(i) == null){
				Process p = procs.get(0);
				procs.remove(0);
				curr_procs.set(i, p);	
			}
		}
	}
	
	@Override
	public void exec() {
		
		System.out.println("---- Executing FCFS ----");
		
		int time = 0;
		ArrayList<Process> curr_proc = new ArrayList<Process>();
		
		//load the next processes into the current ones
		get_next_procs(curr_proc);
		
		while(!super.should_stop()){
			
			break;
		}
		
		//int time
		//while(!should_stop)
		//	load first n procs
		//  if(termination conditions met for a proc in first n procs --> I/O, finished)
		//		get blocktime (1000ms - 4500ms) -> set in proc
		//		pop it onto queue
		//		context switch timer (4ms)
		//		put next proc on
		//	time++
		
		
		
	}
	
}