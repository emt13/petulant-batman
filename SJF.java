/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;

public class SJF extends CPU_Algorithm {

	public SJF(ArrayList<Process> in_procs, int num_cpus){
		copy_in_procs(in_procs);
		super.NUM_PROCESSES = procs.size();
		super.NUM_CPUS = num_cpus;
	}
	
	@Override
	protected void get_next_procs(int time){
		
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
