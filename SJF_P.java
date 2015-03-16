//package evanmt.opsys.hw2.sim;

import java.util.ArrayList;

public class SJF_P extends CPU_Algorithm {

	public SJF_P(int num_procs, int num_cpus){
		super.NUM_PROCESSES = num_procs;
		super.NUM_CPUS = num_cpus;
	}
	
	@Override
	protected void get_next_procs(ArrayList<Process> curr_proc){
		
	}
	
	@Override
	public void exec() {
		
		System.out.println("---- Executing SRT ----");
		
		
		//int time
				//while(!should_stop)
				//  load shortest n procs in terms of time remaining (not sure what that means, use 
				//													  priority queue?)
				//  if(termination conditions met for a proc in current n procs --> I/O, finished)
				//		get blocktime (1000ms - 4500ms) -> set in proc
				//		pop it onto the queue
				//		context switch timer (4ms)
				//		put next proc onto that cpu
				//  time++

	}

}
