/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

import java.util.ArrayList;

public abstract class CPU_Algorithm {
	
	protected Integer NUM_PROCESSES;
	protected Integer NUM_CPUS;
	private Double P_INT = .8;
	private Integer BURST_RANGE = 180;
	private Integer BURST_OFF = 20;
	private Integer CPU_BURST_RANGE = 2800;
	private Integer CPU_BURST_OFF = 200;
	//private Integer TIME_REQ_RANGE = 3000;
	
	
	protected ArrayList<Process> procs;
	
	
	protected abstract void get_next_procs(ArrayList<Process> curr_proc);
	
	public abstract void exec();
	
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
	
	protected void InitProcs(){
		
		procs = new ArrayList<Process>();
		
		//gets the number of interactive processes (rounds down so there will always be a CPU bound)
		int num_inter = (int)(NUM_PROCESSES * P_INT);
		int num_cpu_bound = NUM_PROCESSES - num_inter;
		
		//initialize NUM_PROCESSES number of processes
		for(int i = 0; i < NUM_PROCESSES; i++){
			//generate a burst time
			int burst = gen_num(BURST_RANGE, BURST_OFF);
			
			boolean inter = false;
			
			if(num_inter != 0 && num_cpu_bound != 0){
				int val = gen_num(2,0);
				//interactive
				if(val == 0){
					inter = true;
					num_inter--;
				}else{
					num_cpu_bound--;
				}
			}else if(num_inter != 0){ //num_cpu_bound == 0
				inter = true;
				num_inter--;
			}else if(num_cpu_bound != 0){ //num_inter == 0
				num_cpu_bound--;
			}else{ //both 0
				System.out.println("Error: incorrect num processes");
			}
			
			int cpu_time = -1;
			if(!inter){
				//generate a cpu time
				cpu_time = gen_num(CPU_BURST_RANGE, CPU_BURST_OFF);
			}
			
			Process tmp = new Process("" + i, burst, inter, cpu_time);
			procs.add(tmp);
		}
		
		print_procs();
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
