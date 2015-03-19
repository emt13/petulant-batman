/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;

public class RR extends CPU_Algorithm {

	private int slice_time;
	private int time;

	/**
	 * @param processes: current time
	 * @param num_cpus: # of cpus
	 * @param s: time slice value
	 * @effect initialize RR (Round Robin) object
	 * @modifies slice_time, in_procs
	 */
	public RR(ArrayList<Process> processes, int num_cpus, int s) {
		copy_in_procs(processes);
		super.NUM_PROCESSES = procs.size();
		super.NUM_CPUS = num_cpus;
		slice_time = s;
	}
	
	/**
	 * @param t: current time
	 * @effect load the next processes from procs into the curr_proc buffer
	 * @modifies curr_proc, procs
	 */
	@Override
	protected void get_next_procs(int t){
		// If this is the initial first processes      
		if(curr_procs.size() == 0) {
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
	 * @param cpu_slice: list of cpu slices
	 * @effect add another slice to cpu_slice
	 * @modifies cpu_slice
	 */
	public void setup_slice(ArrayList<Integer> cpu_slice) {
		for(int i = 0; i < super.NUM_CPUS; i++) {
			cpu_slice.add(new Integer(slice_time));
		}	
	}

	/**
	 * @param i: context index
	 * @param context_cpu: list of context times
	 * @param tmp: Process of interest
	 * @effect print the context time of tmp
	 */
	private void print_context_switch(int i, ArrayList<Integer> context_cpu, Process tmp) {
		curr_procs.set(i, null);
		get_next_procs(time);
		if(curr_procs.get(i) != null) {
			System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
			context_cpu.set(i, 4);
			curr_procs.get(i).set_wait(time);
			curr_procs.get(i).activate_burst();
	
		} else {
			System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " but no process to replace it)");		
			context_cpu.set(i, 2);
		}
	}

	/**
	 * @param context_cpu: list of context times
	 * @param cpu_slice: list of cpu_slices
	 * @effect handle the context and cpu_slices
	 */
	public void context_handle_processes(ArrayList<Integer> context_cpu, ArrayList<Integer> cpu_slice) {
		for(int i = 0; i < curr_procs.size(); i++) {
			if(context_cpu.get(i) == 0 && cpu_slice.get(i) != 0 && curr_procs.get(i) != null) {
				Process tmp = curr_procs.get(i);
				if(!tmp.is_active()) {
					tmp.activate_burst();
					tmp.set_wait(time);	
				}

				tmp.dec_curr_burst();
				cpu_slice.set(i, cpu_slice.get(i) - 1);
				// If the burst process is completed
				if(!tmp.is_active()) {
					// Set turnaround
					if(tmp.get_wait() == 0) {
						tmp.set_turnaround(time + 1);
					} else {
						tmp.set_turnaround(time + 1);
					}
					// Setup for I/O blocking
					print_proc_end(tmp, time);

					// Add to blocked procs
					if(!tmp.finished()) {
						int val = gen_num(IO_BLOCK_RANGE, IO_BLOCK_OFF);
						int burst_val;
						if(tmp.is_interactive()) {
							burst_val = gen_num(BURST_RANGE, BURST_OFF);
						} else {
							burst_val = gen_num(CPU_BURST_RANGE, CPU_BURST_OFF);
						}
						tmp.set_burst(burst_val);
						tmp.set_blocked_time(val);
						blocked_procs.add(tmp);
					}

					// Remove from curr_procs
					curr_procs.set(i, null);

					// Set slice back to slice time
					cpu_slice.set(i, slice_time);

					// Context switch
					get_next_procs(time);

					if(curr_procs.get(i) != null) {
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
						context_cpu.set(i, new Integer(4));
					} else {
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " but no process to replace it)");
						context_cpu.set(i, new Integer(2));
					}
				} else if(cpu_slice.get(i) == 0) {
					// Bump tmp into the ready queue
					procs.add(tmp);
						
					// Add context switch
					curr_procs.set(i, null);
					get_next_procs(time);
					cpu_slice.set(i, slice_time);

					if(curr_procs.get(i) != null) {
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
						context_cpu.set(i, new Integer(4));
					} else {
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " but no process to replace it)");
						context_cpu.set(i, new Integer(2));
					}
				}
			} else {
				if(curr_procs.get(i) == null) {
					get_next_procs(time);
					if(curr_procs.get(i) != null) {
						context_cpu.set(i, context_cpu.get(i) + 2);
						if(curr_procs.get(i).is_interactive()) {
							System.out.println("[time " + time + "ms] " + curr_procs.get(i).get_type() + " process ID " + curr_procs.get(i).get_pid() + " has taken unused cpu, " +
								(i + 1));	
						}
					}
				}
			}
		}
	}

	/**
	 * @effect activate the processes within the cpus
	 */
	private void activate_curr_procs() {
		for(int i = 0; i < curr_procs.size(); i++) {
			if(curr_procs.get(i) != null) {
				curr_procs.get(i).activate_burst();
			}
		}
	}

	/**
	 * @effect execute the algorithm
	 */
	@Override
	public void exec() {		
		System.out.println("---- Executing RR ----");
		
		time = 0;

		ArrayList<Process> all_procs = new ArrayList<Process>(procs);
		
		set_start_ready();
		print_ready_entry();
		

		ArrayList<Integer> context_cpu = new ArrayList<Integer>();
		ArrayList<Integer> cpu_slice = new ArrayList<Integer>();
		blocked_procs = new ArrayList<Process>();
		curr_procs = new ArrayList<Process>();
		get_next_procs(time);

		activate_curr_procs();

		setup_context_cpu(context_cpu);
		setup_slice(cpu_slice);

		while(!should_stop()) {
			context_handle_processes(context_cpu, cpu_slice);
			handle_blocked_processes(time);
			
			dec_context_switch(context_cpu, time);
			remove_finished_procs(time);
			time++;
		}
		display_data(all_procs, time);		
	}
}
