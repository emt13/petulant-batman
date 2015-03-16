//package evanmt.opsys.hw2.sim;

import java.util.ArrayList;

public class SJF extends CPU_Algorithm {

	public SJF(int num_procs, int num_cpus){
		super.NUM_PROCESSES = num_procs;
		super.NUM_CPUS = num_cpus;
	}
	
	@Override
	protected void get_next_procs(ArrayList<Process> curr_proc){
		
	}
	
	@Override
	public void exec() {

		System.out.println("---- Executing SJF ----");
		
		//int time
				//while(!should_stop)
				//  load shortest n procs (burst time?, not sure)
				//  if(termination conditions met for a proc in current n procs --> I/O, finished)
				//		get blocktime (1000ms - 4500ms) -> set in proc
				//  	pop it onto the queue
				//		context switch timer (4ms)
				//		put next proc onto the cpu
				//  time++
		
	}

}
