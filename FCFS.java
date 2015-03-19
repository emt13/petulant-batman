/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;
import java.text.DecimalFormat;

public class FCFS extends CPU_Algorithm {
	
	// Used to hold processes that aren't ready for the ready queue
	ArrayList<Process> blocked_processes;
	
	/**
	 * @param int_proc: list of unblocked processes
	 * @param num_cpus: # of CPUS
	 * @effect intialize FCFS object
	 * @modifies in_proc, blocked_processes
	 */
	public FCFS(ArrayList<Process> in_proc, int num_cpus) {
		copy_in_procs(in_proc);
		super.NUM_PROCESSES = procs.size();
		super.NUM_CPUS = num_cpus;
		blocked_processes = new ArrayList<Process>();
	}

	/**
	 * @param time: current time
	 * @effect load the next processes from procs into the curr_proc buffer
	 * @modifies curr_proc, procs
	 */
	@Override
	protected void get_next_procs(int time) {
		// If this is the initial first processes      
		if(curr_procs.size() == 0){
			for(int i = 0; i < super.NUM_CPUS; i++) {
				Process tmp = null;
				curr_procs.add(tmp);
			}
		}
		
		// Get the first n processes, fill in the null slots
		for(int i = 0; i < curr_procs.size(); i++) {
			if(procs.size() > 0 && curr_procs.get(i) == null) {
				curr_procs.set(i, procs.remove(0));
				curr_procs.get(i).set_wait(time);
			}	
		}
	}

	/**
	 * @param context_time: list of context times
	 * @effect handle the cpu bursts at each cpu
	 * @modifies curr_proc, procs
	 */
	private void burst_context_handle(ArrayList<Integer> context_time, int time) {
		// Go through all of the processes and check if any have hit their burst
		for(int i = 0; i < curr_procs.size(); i++) {
			if(context_time.get(i) == 0 && curr_procs.get(i) != null) {
				Process tmp_p = curr_procs.get(i);
				if(!tmp_p.is_active()){	
					tmp_p.activate_burst();
					tmp_p.set_wait(time);
				}

				// Decrement the timer in this process
				tmp_p.dec_curr_burst();	
				// Check if this completes the burst for this process
				if(!tmp_p.is_active()) {

					// Set the turnaround time for this burst
					if(tmp_p.get_wait() > 0) {
						tmp_p.set_turnaround(time + 1);
					} else {
						tmp_p.set_turnaround(time);
					}


					print_proc_end(tmp_p, time);
					if(!tmp_p.finished()) {  
						// Generate its blocking time
						int val = gen_num(IO_BLOCK_RANGE, IO_BLOCK_OFF);
						tmp_p.set_blocked_time(val);
						// Add the process to the list of blocked processes
						blocked_procs.add(tmp_p);
					}
						
					// Set the spot in the current proc to null
					curr_procs.set(i,null);
					// Populate with the next process
					get_next_procs(time);
	
					// If the current process isn't null
					if(curr_procs.get(i) != null) {
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp_p.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
						context_time.set(i, new Integer(4));
					} else {
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp_p.get_pid() + " with no process to replace it)");
						context_time.set(i, new Integer(2));
					}
				}
			} else {
				if(curr_procs.get(i) == null) {
					get_next_procs(time);
					if(curr_procs.get(i) != null) {
						context_time.set(i, context_time.get(i) + 2);
						if(curr_procs.get(i).is_interactive()) {
							System.out.println("[time " + time + "ms] Interactive process ID " + curr_procs.get(i).get_pid() + " has taken unused cpu, " + (i+1)); 
						} else {
							System.out.println("[time " + time + "ms] CPU-Bound process ID " + curr_procs.get(i).get_pid() + " has taken unused cpu, " + (i+1)); 	
						}
					}
				}
			}
		}
	}

	/**
	 * @effect Run the FCFS algorithm
	 * @modifies curr_proc, procs
	 */	@Override
	public void exec() {
	
		System.out.println("---- Executing FCFS ----");

		print_ready_entry();
		
		int time = 0;
		// CPU queue
		curr_procs = new ArrayList<Process>();
		// Blocked queue
		blocked_procs = new ArrayList<Process>();

		ArrayList<Process> all_procs = new ArrayList<Process>(procs);

		// Load the next processes into the current ones
		get_next_procs(time);
	
		// Used to keep track of context switches
		ArrayList<Integer> context_time = new ArrayList<Integer>();

		// Sets up the context switch
		setup_context_cpu(context_time);

		// Set all of them to enter at time 0
		set_start_ready();

		// Start up each of the processes
		for(int i = 0; i < curr_procs.size(); i++) {
			if(curr_procs.get(i) != null){
				curr_procs.get(i).set_wait(time);
				curr_procs.get(i).activate_burst();
			}
		}

		// Go until all the CPU-bound processes are finished (6 bursts)
		while(!super.should_stop()) {
			time++;

			dec_context_switch(context_time, time);

			burst_context_handle(context_time, time);

			handle_blocked_processes(time);
		
			remove_finished_procs(time);
		}
		display_data(all_procs, time);
	}
}
