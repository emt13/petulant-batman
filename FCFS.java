/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;
import java.text.DecimalFormat;

public class FCFS extends CPU_Algorithm {
	
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
	protected void get_next_procs(int time){
		//if this is the initial first processes      
		if(curr_procs.size() == 0){
			for(int i = 0; i < super.NUM_CPUS; i++){
				Process tmp = null;
				curr_procs.add(tmp);
			}
		}
		
		//get the first n processes, fill in the null slots
		for(int i = 0; i < curr_procs.size(); i++){
			if(procs.size() > 0 && curr_procs.get(i) == null){
				curr_procs.set(i, procs.remove(0));
				curr_procs.get(i).set_wait(time);
			}	
		}
		//System.out.println("<------------------------------------>");
		//print_curr_procs();
	}
	
	
	
	

	private void burst_context_handle(ArrayList<Integer> context_time, int time) {
		//go through all of the processes and check if any have hit their burst
		for(int i = 0; i < curr_procs.size(); i++){
			if(context_time.get(i) == 0 && curr_procs.get(i) != null){
				Process tmp_p = curr_procs.get(i);
				if(!tmp_p.is_active()){	
					tmp_p.activate_burst();
					tmp_p.set_wait(time);
				}
				//decrement the timer in this process
				tmp_p.dec_curr_burst();	
				//check if this completes the burst for this process
				if(!tmp_p.is_active()){

					//set the turnaround time for this burst
					if(tmp_p.get_wait() > 0){
						tmp_p.set_turnaround(time + 1);
					}else{
						tmp_p.set_turnaround(time);
					}


					print_proc_end(tmp_p, time);
					if(!tmp_p.finished()){  
						//generate its blocking time
						int val = gen_num(IO_BLOCK_RANGE, IO_BLOCK_OFF);
						tmp_p.set_blocked_time(val);
						//add the process to the list of blocked processes
						blocked_procs.add(tmp_p);
					}
						
					//set the spot in the current proc to null
					curr_procs.set(i,null);
					//populate with the next process
					get_next_procs(time);
	
					//if the current process isn't null
					if(curr_procs.get(i) != null){
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp_p.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
						context_time.set(i, new Integer(4));
					}else{
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp_p.get_pid() + " with no process to replace it)");
						context_time.set(i, new Integer(2));
					}
					//print_curr_procs();					
				}
			}else{
				if(curr_procs.get(i) == null){
					get_next_procs(time);
					if(curr_procs.get(i) != null){
						context_time.set(i, context_time.get(i) + 2);
						if(curr_procs.get(i).is_interactive()){
							System.out.println("[time " + time + "ms] Interactive process ID " + curr_procs.get(i).get_pid() + " has taken unused cpu, " + (i+1)); 
						}else{
							System.out.println("[time " + time + "ms] CPU-Bound process ID " + curr_procs.get(i).get_pid() + " has taken unused cpu, " + (i+1)); 	
						}
					}
				}
			}
			
		}
	}

	@Override
	public void exec() {
	
		System.out.println("---- Executing FCFS ----");

		print_ready_entry();
		
		int time = 0;
		//cpu queue
		curr_procs = new ArrayList<Process>();
		//blocked queue
		blocked_procs = new ArrayList<Process>();

		ArrayList<Process> all_procs = new ArrayList<Process>(procs);

		//load the next processes into the current ones
		get_next_procs(time);
	
		//used to keep track of context switches
		ArrayList<Integer> context_time = new ArrayList<Integer>();

		//sets up the context switch
		setup_context_cpu(context_time);

		//set all of them to enter at time 0
		set_start_ready();

		//start up each of the processes
		for(int i = 0; i < curr_procs.size(); i++){
			if(curr_procs.get(i) != null){
				curr_procs.get(i).set_wait(time);
				curr_procs.get(i).activate_burst();
			}
		}

		//print_curr_procs();

		//go until all the CPU-bound processes are finished (6 bursts)
		while(!super.should_stop()){
			time++;

			dec_context_switch(context_time, time);

			burst_context_handle(context_time, time);

			handle_blocked_processes(time);
		
			remove_finished_procs(time);
		
		}

		display_data(all_procs, time);
		
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
