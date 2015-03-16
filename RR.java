//package evanmt.opsys.hw2.sim;

import java.util.ArrayList;

public class RR extends CPU_Algorithm {

	private int slice_time;
	
	public RR(int num_procs, int num_cpus, int s){
		super.NUM_PROCESSES = num_procs;
		super.NUM_CPUS = num_cpus;
		slice_time = s;
	}
	
	@Override
	protected void get_next_procs(ArrayList<Process> curr_proc){
		
	}
	
	@Override
	public void exec() {
		
		System.out.println("---- Executing RR ----");
		
		//int time
				//while(!should_stop)
				//  load first n procs from queue
				//  if(termination conditions met for any procs -> I/O, finished)
				//  	get blocktime (1000ms - 4500ms) -> set in proc
				//		pop it onto the queue
				//	if(timeslice up)
				//		for each current proc in the cpus
				//			get next proc			
				//			if proc->blocked
				//				display swapping output, proc_old with proc_new
				//		context switch timer (4ms) for all cpus
				//  time++
		
	}

}
