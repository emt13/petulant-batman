/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;
import java.util.Collections;

public class SRT extends CPU_Algorithm {
	
	// Array to hold preempted processes
	ArrayList<Process> preempt_procs;

	/**
	 * @param in_proc: list of processes
	 * @param num_cpus: # of cpus
	 * @effect intialize SRT object
	 */
	public SRT(ArrayList<Process> in_proc, int num_cpus){
		copy_in_procs(in_proc);
		super.NUM_PROCESSES = procs.size();
		super.NUM_CPUS = num_cpus;
		preempt_procs = new ArrayList<Process>();
	}
	
	/**
	 * @param time: current time
	 * @effect get next set of processes for CPUs
	 */
	@Override
	protected void get_next_procs(int time){
		// If this is the initial first processes      
		if(curr_procs.size() == 0) {
			for(int i = 0; i < super.NUM_CPUS; i++) {
				Process tmp = null;
				curr_procs.add(tmp);
			}
		}
		
		// Sort the preempted processes by smallest remaining time
		Collections.sort(preempt_procs);
		
		// Add preempted processes back to be run by CPU
		for (int i = 0; i < curr_procs.size(); i++) {
			if (preempt_procs.size() > 0 && curr_procs.get(i) == null) {
				curr_procs.set(i, preempt_procs.remove(0));
				curr_procs.get(i).set_wait(time);
			}
		}
		
		// Sort processes by shortest burst times
		Collections.sort(procs);
		
		// Get the first n processes, fill in the null slots
		for(int i = 0; i < curr_procs.size(); i++) {
			if(procs.size() > 0 && curr_procs.get(i) == null) {
				curr_procs.set(i, procs.remove(0));
				curr_procs.get(i).set_wait(time);
			}
		}	
	}
	
	/**
	 * @param context_time:list of context times
	 * @param time: current time
	 * @effect handle processes withing the CPUs
	 */
	private void burst_context_handle(ArrayList<Integer> context_time, int time) {
		// Go through all of the processes and check if any have hit their burst
		for(int i = 0; i < curr_procs.size(); i++) {
			if (curr_procs.get(i) != null && procs.size() > 0) {
				ArrayList<Process> tmpSort = new ArrayList<Process>(procs);
				Collections.sort(tmpSort);
				if (tmpSort.get(0).get_burst() < curr_procs.get(i).get_remaining_burst()) {
					preempt_procs.add(curr_procs.get(i));
					System.out.println("[time " + time + "ms] Preemption (preempting process ID " + curr_procs.get(i).get_pid());
					System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + curr_procs.get(i).get_pid() + " for process ID " + tmpSort.get(0).get_pid() + ")");
					curr_procs.set(i, tmpSort.get(0));
					procs.remove(tmpSort.get(0));
				}
			}
			if(context_time.get(i) == 0 && curr_procs.get(i) != null) {
				Process tmp_p = curr_procs.get(i);
				if(!tmp_p.is_active()) {	
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
	 * @effect runs the algorithm
	 */
	@Override
	public void exec() {
		
		System.out.println("---- Executing SRT ----");
		
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
		for(int i = 0; i < curr_procs.size(); i++){
			if (curr_procs.get(i) != null) {
				curr_procs.get(i).set_wait(time);
				curr_procs.get(i).activate_burst();
			}
		}

		// Go until all the CPU-bound processes are finished (6 bursts)
		while(!super.should_stop()){
			time++;

			dec_context_switch(context_time, time);

			burst_context_handle(context_time, time);

			handle_blocked_processes(time);
		
			remove_finished_procs(time);
		}
		display_data(all_procs, time);
	}
}
