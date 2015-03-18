/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

import java.util.ArrayList;

public abstract class CPU_Algorithm {
	
	protected Integer NUM_PROCESSES;
	protected Integer NUM_CPUS;
	//private Integer TIME_REQ_RANGE = 3000;
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
	 * @param curr_proc the current buffer of available cpus
	 * @effect load the next processes from procs into the curr_proc buffer
	 * @modifies curr_proc, procs
	 */
	protected abstract void get_next_procs(int time);
	
	/**
	 * @effect executes the algorithm
	 */
	public abstract void exec();

	//copies in new processes
	protected void copy_in_procs(ArrayList<Process> in_procs){
		procs = new ArrayList<Process>();
		for(int i = 0; i < in_procs.size(); i++){
			procs.add(new Process(in_procs.get(i)));
		}
	}

	protected Integer gen_num(int range, int offset){
		return (int) (Math.random()*range) + offset;
	}

	protected void set_start_ready(){
		for(int i = 0; i < procs.size(); i++){
			procs.get(i).set_ready_entry(0);
		}
	}

	protected void print_curr_procs(){
		System.out.println("Current Processes: ");
		for(int i = 0; i < curr_procs.size(); i++){
			System.out.println("*********************");
			System.out.println(curr_procs.get(i));
			System.out.println("*********************");
		}
	}

	protected void print_procs(){
		System.out.println("Processes: ");
		for(int i = 0; i < procs.size(); i++){
			System.out.println("*********************");
			System.out.println(procs.get(i));
			System.out.println("*********************");
		}
	}

	protected void print_ready_entry(){
		
		for(int i = 0; i < procs.size(); i++){
			if(procs.get(i).is_interactive()){
				System.out.println("[time 0ms] Interactive process ID " + procs.get(i).get_pid() + " entered the ready queue (requires " + procs.get(i).get_burst() + 
							"ms CPU time)"  );
			}else{
				System.out.println("[time 0ms] CPU-Bound process ID " + procs.get(i).get_pid() + " entered the ready queue (requires " + procs.get(i).get_burst() + 
							"ms CPU time)"  );
			}
		}
	}


	protected void setup_context_cpu(ArrayList<Integer> arr){
		for(int i = 0; i < NUM_CPUS; i++){
			arr.add(new Integer(0));
		}
	}

	protected void print_proc_end(Process p, int time){
		if(p.is_interactive()){
			System.out.println("[time " + time + "ms] Interactive process ID " + p.get_pid() + " CPU burst done (turnaround time " + p.get_turnaround() + 
						"ms, total wait time " + p.get_wait() + "ms)");
		}else{
			int avg_turn = (int)((double)p.get_avg_turnaround());
			int avg_wait = (int)((double)p.get_avg_wait());
			//DecimalFormat df = new DecimalFormat("#######0");
			if(p.get_num_bursts() == p.get_max_burst()){
				System.out.println("[time " + time + "ms] CPU-Bound process ID " + p.get_pid() + " terminated (avg turnaround " + avg_turn + 
					       		"ms, avg total wait time " + avg_wait + "ms)");	
			}else{
				System.out.println("[time " + time + "ms] CPU-Bound process ID " + p.get_pid() + " CPU burst done (turnaround time " + p.get_turnaround() + 
							"ms, total wait time " + p.get_wait() + "ms)");
			}
		}
	}

	protected void dec_context_switch(ArrayList<Integer> context_time, int time){
		for(int i = 0; i < context_time.size(); i++){
			if(context_time.get(i) > 0){
				context_time.set(i, new Integer(context_time.get(i) - 1));
				//ready to go
				if(context_time.get(i) == 0){
					if(curr_procs.get(i) != null){
						curr_procs.get(i).set_wait(time);
					}
				}
			}
		}
	}

	protected void handle_blocked_processes(int time){
		//go through all of the blocked processes and decrement their blocked time
		for(int i = blocked_procs.size() - 1; i >= 0; i--){
			Process tmp_p = blocked_procs.get(i);
			//subtract 1 from the time it needs to be blocked
			tmp_p.dec_blocked_time();
			if(!tmp_p.is_blocked()){
				procs.add(tmp_p);
				blocked_procs.remove(i);
				tmp_p.set_ready_entry(time);
				if(tmp_p.is_interactive()){
					tmp_p.set_burst(gen_num(BURST_RANGE, BURST_OFF));
					System.out.println("[time " + time + "ms] Interactive process ID " + tmp_p.get_pid() + " entered ready queue (requires " + tmp_p.get_burst() + "ms CPU time)");
				}else{
					tmp_p.set_burst(gen_num(CPU_BURST_RANGE, CPU_BURST_OFF));
					System.out.println("[time " + time + "ms] CPU-Bound process ID " + tmp_p.get_pid() + " entered ready queue (requires " + tmp_p.get_burst() + "ms CPU time)");
				}
				
			}
		}
	}

	protected void print_context(ArrayList<Integer> context){
		for(int i = 0; i < context.size(); i++){
			System.out.print("(" + context.get(i) + ") ");
		}
		System.out.print("\n");
	}

	protected void remove_finished_procs(int time){
		for(int i = blocked_procs.size()-1; i >= 0; i--){
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
	protected boolean should_stop(){
		//search the processes to find unfinished processes
		for(int i = 0; i < procs.size(); i++){
			//if a process is cpu_bound and not finished
			if(!procs.get(i).is_interactive() && !procs.get(i).finished()){ 
				return false; 
			}
		}

		for(int i = 0; i < curr_procs.size(); i++){
			if(curr_procs.get(i) != null){
				if(!curr_procs.get(i).is_interactive() && !curr_procs.get(i).finished()){
					return false;
				}
			}
		}

		for(int i = 0; i < blocked_procs.size(); i++){
			if(!blocked_procs.get(i).is_interactive() && !blocked_procs.get(i).finished()){
				return false;
			}
		}
	//	print_curr_procs();
	//	System.out.println("<**************************>");
	//	print_procs();
		return true;
	}
	
	private void print_turnaround(){
		/*
		double t_turn = 0;
		double min_turn = 2147483647; //max int
		double max_turn = -1;
		for( int i = 0; i < procs.size(); i++){
			double tmp_turn = procs.get(i).get_turnaround();
			t_turn += tmp_turn;
			if(tmp_turn < min_turn){ 
				min_turn = tmp_turn; 
			}
			if(tmp_turn > max_turn){
				max_turn = tmp_turn;
			}
		}
		
		double avg_turn = t_turn / procs.size();
		
		System.out.printf("Turnaround time: min %f.000 ms; avg: %f.000 ms; max: %f.000\n", min_turn, avg_turn, max_turn);
		*/
	}
	
	public void display_data(ArrayList<Process> all_procs){
		
	}


	private void print_total_wait(){
		/*
		double t_wait = 0;
		double min_wait = 2147483647; //max int
		double max_wait = -1;
		for( int i = 0; i < procs.size(); i++){
			Integer tmp_wait = procs.get(i).get_wait();
			t_wait += tmp_wait;
			if(tmp_wait < min_wait){ 
				min_wait = tmp_wait; 
			}
			if(tmp_wait > max_wait){
				max_wait = tmp_wait;
			}
		}
		
		double avg_wait = t_wait / procs.size();
		
		System.out.printf("Total wait time: min %f.000 ms; avg: %f.000 ms; max: %f.000 ms\n", min_wait, avg_wait, max_wait);
		*/
	}
	
	protected void print_data(){
		print_turnaround();
		print_total_wait();
	}
	
}
