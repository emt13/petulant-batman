/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

import java.util.ArrayList;

public class FCFS extends CPU_Algorithm{
	
	//used to hold processes that aren't ready for the ready queue
	ArrayList<Process> blocked_processes;
	
	public FCFS(ArrayList<Process> in_proc, int num_cpus){
		copy_in_procs(in_proc);
		super.NUM_PROCESSES = procs.size();
		super.NUM_CPUS = num_cpus;
		
		
		//super.InitProcs();
		blocked_processes = new ArrayList<Process>();
		//time = new AtomicInteger(0);
	}

	@Override
	protected void get_next_procs(ArrayList<Process> curr_procs){
		//if this is the initial first processes      
		if(curr_procs.size() == 0){
			for(int i = 0; i < super.NUM_CPUS; i++){
				Process tmp = null;
				curr_procs.add(tmp);
			}
		}
		
		//get the first n processes, fill in the null slots
		for(int i = 0; i < super.NUM_CPUS; i++){
			if(procs.size() > 0 && (curr_procs.get(0) == null || !curr_procs.get(0).is_blocked())){
				Process p = procs.get(0);
				procs.remove(0);
				curr_procs.set(i, p);	
			}
		}
	}
	
	
	
	@Override
	public void exec() {
	
		System.out.println("---- Executing FCFS ----");
		
		print_ready_entry();
		
		int time = 0;
		//cpu queue
		ArrayList<Process> curr_proc = new ArrayList<Process>();
		
		while(!super.should_stop()){
			//load the next processes into the current ones
			get_next_procs(curr_proc);
			
			//for(int i = 0; i < procs.size(); i++){
				//if
			//}
			
			time++;
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
