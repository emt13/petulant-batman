/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;
import java.text.DecimalFormat;

public abstract class CPU_Algorithm {
	
	// Global Variables
	protected Integer NUM_PROCESSES;
	protected Integer NUM_CPUS;
	private Double P_INT = .8;
	protected Integer BURST_RANGE = 180;
	protected Integer BURST_OFF = 20;
	protected Integer CPU_BURST_RANGE = 2800;
	protected Integer CPU_BURST_OFF = 200;
	protected Integer IO_BLOCK_RANGE = 3500;
	protected Integer IO_BLOCK_OFF = 1000;

	
	protected ArrayList<Process> procs;
	protected ArrayList<Process> curr_procs;
	protected ArrayList<Process> blocked_procs;

	/**
	 * @param curr_proc: the current buffer of available cpus
	 * @effect load the next processes from procs into the curr_proc buffer
	 * @modifies curr_proc, procs
	 */
	protected abstract void get_next_procs(int time);
	
	/**
	 * @effect executes the algorithm
	 */
	public abstract void exec();

	/**
	 * @param in_procs: the list of procs to be copied
	 * @effect copy the processes in in_procs to procs
	 * @modifies procs
	 */
	protected void copy_in_procs(ArrayList<Process> in_procs) {
		procs = new ArrayList<Process>();
		for(int i = 0; i < in_procs.size(); i++) {
			procs.add(new Process(in_procs.get(i)));
		}
	}

	/**
	 * @param range: largest number possible for CPU burst
	 * @param offset: offset from 0 ms for a CPU burst to start from
	 * @return randomly generated CPU time
	 */
	protected Integer gen_num(int range, int offset) {
		return (int) (Math.random()*range) + offset;
	}

	/**
	 * @effect have all processes ready to start
	 * @modifies procs
	 */
	protected void set_start_ready() {
		for(int i = 0; i < procs.size(); i++) {
			procs.get(i).set_ready_entry(0);
		}
	}

	/**
	 * @effect print currently running processes
	 */
	protected void print_curr_procs() {
		System.out.println("Current Processes: ");
		for(int i = 0; i < curr_procs.size(); i++) {
			System.out.println("*********************");
			System.out.println(curr_procs.get(i));
			System.out.println("*********************");
		}
	}

	/**
	 * @effect print out all processes
	 */
	protected void print_procs() {
		System.out.println("Processes: ");
		for(int i = 0; i < procs.size(); i++) {
			System.out.println("*********************");
			System.out.println(procs.get(i));
			System.out.println("*********************");
		}
	}

	/**
	 * @effect print out the processes that eneter the ready queue
	 */
	protected void print_ready_entry() {
		for(int i = 0; i < procs.size(); i++) {
			if(procs.get(i).is_interactive()) {
				System.out.println("[time 0ms] Interactive process ID " + procs.get(i).get_pid() + " entered the ready queue (requires " + procs.get(i).get_burst() + 
							"ms CPU time)"  );
			} else {
				System.out.println("[time 0ms] CPU-Bound process ID " + procs.get(i).get_pid() + " entered the ready queue (requires " + procs.get(i).get_burst() + 
							"ms CPU time)"  );
			}
		}
	}


	/**
	 * @param arr: arraylist of context cpu to set up
	 * @effect add empty ints to arr as placeholders
	 * @modifies arr
	 */
	protected void setup_context_cpu(ArrayList<Integer> arr) {
		for(int i = 0; i < NUM_CPUS; i++) {
			arr.add(new Integer(0));
		}
	}

	/**
	 * @param p: Process that has finished
	 * @param time: the time when the process finished
	 * @effect print out the process that finished and the time it did
	 */
	protected void print_proc_end(Process p, int time) {
		if(p.is_interactive()) {
			System.out.println("[time " + time + "ms] Interactive process ID " + p.get_pid() + " CPU burst done (turnaround time " + p.get_turnaround() + 
						"ms, total wait time " + p.get_wait() + "ms)");
		} else {
			int avg_turn = (int)((double)p.get_avg_turnaround());
			int avg_wait = (int)((double)p.get_avg_wait());
			if(p.get_num_bursts() == p.get_max_burst()) {
				System.out.println("[time " + time + "ms] CPU-Bound process ID " + p.get_pid() + " terminated (avg turnaround " + avg_turn + 
					       		"ms, avg total wait time " + avg_wait + "ms)");	
			} else {
				System.out.println("[time " + time + "ms] CPU-Bound process ID " + p.get_pid() + " CPU burst done (turnaround time " + p.get_turnaround() + 
							"ms, total wait time " + p.get_wait() + "ms)");
			}
		}
	}

	/**
	 * @param context_time: list of context times
	 * @param time: the current time to be put in
	 * @effect Shift down the context times and add the time to context_time
	 * @modifies context_time, time
	 */
	protected void dec_context_switch(ArrayList<Integer> context_time, int time) {
		for(int i = 0; i < context_time.size(); i++) {
			if(context_time.get(i) > 0) {
				context_time.set(i, new Integer(context_time.get(i) - 1));
				// Ready to go
				if(context_time.get(i) == 0) {
					if(curr_procs.get(i) != null) {
						curr_procs.get(i).set_wait(time);
					}
				}
			}
		}
	}

	/**
	 * @param time: current time
	 * @effect handle all processes that have been blocked
	 * @modifies procs, blocked_procs
	 */
	protected void handle_blocked_processes(int time) {
		// Go through all of the blocked processes and decrement their blocked time
		for(int i = blocked_procs.size() - 1; i >= 0; i--) {
			Process tmp_p = blocked_procs.get(i);
			// Subtract 1 from the time it needs to be blocked
			tmp_p.dec_blocked_time();
			// If the process is not blocked
			if(!tmp_p.is_blocked()) {
				procs.add(tmp_p);
				blocked_procs.remove(i);
				tmp_p.set_ready_entry(time);
				// If the process is I/O
				if(tmp_p.is_interactive()) {
					tmp_p.set_burst(gen_num(BURST_RANGE, BURST_OFF));
					System.out.println("[time " + time + "ms] Interactive process ID " + tmp_p.get_pid() + " entered ready queue (requires " + tmp_p.get_burst() + "ms CPU time)");
				} else {
					tmp_p.set_burst(gen_num(CPU_BURST_RANGE, CPU_BURST_OFF));
					System.out.println("[time " + time + "ms] CPU-Bound process ID " + tmp_p.get_pid() + " entered ready queue (requires " + tmp_p.get_burst() + "ms CPU time)");
				}
			}
		}
	}

	/**
	 * @param context: list of context times
	 * @effect print out the elements of context
	 */
	protected void print_context(ArrayList<Integer> context) {
		for(int i = 0; i < context.size(); i++) {
			System.out.print("(" + context.get(i) + ") ");
		}
		System.out.print("\n");
	}

	/**
	 * @param time: current time
	 * @effect remove finished processes
	 * @modifies blocked_procs
	 */
	protected void remove_finished_procs(int time) {
		for(int i = blocked_procs.size()-1; i >= 0; i--) {
			if(blocked_procs.get(i).finished()){
				print_proc_end(blocked_procs.get(i), time);
				blocked_procs.remove(i);
			}
		}
	}

	/**
	 * 
	 * @return true if the sim should stop (all CPU bound procs done) otherwise, false
	 */
	protected boolean should_stop() {
		// Search the processes to find unfinished processes
		for(int i = 0; i < procs.size(); i++) {
			// If a process is cpu_bound and not finished
			if(!procs.get(i).is_interactive() && !procs.get(i).finished()) { 
				return false; 
			}
		}

		// Search to see if there are any processes running
		for(int i = 0; i < curr_procs.size(); i++) {
			if(curr_procs.get(i) != null) {
				if(!curr_procs.get(i).is_interactive() && !curr_procs.get(i).finished()) {
					return false;
				}
			}
		}

		// Search to see if there are any blocked processes
		for(int i = 0; i < blocked_procs.size(); i++) {
			if(!blocked_procs.get(i).is_interactive() && !blocked_procs.get(i).finished()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param all_procs: list of all processes
	 * @effect print the turnaround time of all processes
	 */
	private void print_turnaround(ArrayList<Process> all_procs) {

		int min = all_procs.get(0).get_min_turnaround();
		int max = all_procs.get(0).get_max_turnaround();
		double avg = 0;

		for(int i = 0; i < all_procs.size(); i++) {
			Process tmp = all_procs.get(i);
			int tmp_min = tmp.get_min_turnaround();
			int tmp_max = tmp.get_max_turnaround();
			double tmp_avg = tmp.get_avg_turnaround();

			if(tmp_min < min) {
				min = tmp_min;
			}
			if(tmp_max > max) {
				max = tmp_max;
			}
			avg += tmp_avg;
		}

		avg = avg/all_procs.size();
	
		DecimalFormat df = new DecimalFormat("#########0.000");
		
		System.out.println("Turnaround time: min: "+ min+ "ms; avg: "+df.format(avg) + "ms; max: " + max +"ms");
	}

	/**
	 * @param all_procs: list of all processes
	 * @param time: current time
	 * @effect print out cpu utilization fo each process
	 */
	public void print_cpu_util(ArrayList<Process> all_procs, int time) {
		int total_use = 0;
		for(int i = 0; i < all_procs.size(); i++) {
			total_use += all_procs.get(i).get_cpu_use_time();
		}
		double avg_cpu = (total_use/4.0)/time * 100;
		DecimalFormat df = new DecimalFormat("########0.000");
		System.out.println("Average CPU utilization: " + df.format(avg_cpu) + "%");

		System.out.println("Average CPU utilization per process: ");
		for(int i = 0; i < all_procs.size(); i++) {
			System.out.println(all_procs.get(i).get_pid() + ": " + df.format(all_procs.get(i).get_avg_cpu_time()) + "%");
		}

	}
	
	/**
	 * @param all_procs: list of all processes
	 * @param time: current time
	 * @effect print all data
	 */
	public void display_data(ArrayList<Process> all_procs, int time) {
		print_turnaround(all_procs);	
		print_total_wait(all_procs);
		print_cpu_util(all_procs, time);
	}


	/**
	 * @param all_procs: list of all processes
	 * @effect print total wait time of a process
	 */
	private void print_total_wait(ArrayList<Process> all_procs) {
		int min = all_procs.get(0).get_min_wait();
		int max = all_procs.get(0).get_max_wait();
		double avg = 0;

		for(int i = 0; i < all_procs.size(); i++) {
			Process tmp = all_procs.get(i);
			int tmp_min = tmp.get_min_wait();
			int tmp_max = tmp.get_max_wait();
			double tmp_avg = tmp.get_avg_wait();

			if(tmp_min < min) {
				min = tmp_min;
			}
			if(tmp_max > max) {
				max = tmp_max;
			}
			avg += tmp_avg;
		}

		avg = avg/all_procs.size();
	
		DecimalFormat df = new DecimalFormat("#########0.000");
		
		System.out.println("Total wait time: min: "+ min+ "ms; avg: "+df.format(avg) + "ms; max: " + max +"ms");
	}
}
