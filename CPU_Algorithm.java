/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

import java.util.ArrayList;

public abstract class CPU_Algorithm {
	
	protected Integer NUM_PROCESSES;
	protected Integer NUM_CPUS;
	//private Integer TIME_REQ_RANGE = 3000;
	private Double P_INT = .8;
	private Integer BURST_RANGE = 180;
	private Integer BURST_OFF = 20;
	private Integer CPU_BURST_RANGE = 2800;
	private Integer CPU_BURST_OFF = 200;
	
	
	protected ArrayList<Process> procs;
	
	/**
	 * @param curr_proc the current buffer of available cpus
	 * @effect load the next processes from procs into the curr_proc buffer
	 * @modifies curr_proc, procs
	 */
	protected abstract void get_next_procs(ArrayList<Process> curr_proc);
	
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

	private Integer gen_num(int range, int offset){
		return (int) (Math.random()*range) + offset;
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
				System.out.println("[time 0ms] CPU-Bound process ID " + procs.get(i).get_pid() + " entered the ready queue (requires " + procs.get(i).get_cpu_time() + 
							"ms CPU time)"  );
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
		return true;
	}
	
	private void print_turnaround(){
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
	}
	
	private void print_total_wait(){
		double t_wait = 0;
		double min_wait = 2147483647; //max int
		double max_wait = -1;
		for( int i = 0; i < procs.size(); i++){
			double tmp_wait = procs.get(i).get_total_wait();
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
	}
	
	protected void print_data(){
		print_turnaround();
		print_total_wait();
	}
	
}
