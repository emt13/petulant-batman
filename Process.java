/*
 * Evan Thompson, Tausif Ahmed
 */
import java.util.ArrayList;

public class Process {
	//constant
	private Integer INV_CPU = new Integer(-1);
	private Integer MAX_BURST = new Integer(5); //six in total when starting from 0
	
	private Integer burst; //between 20-200ms
	private boolean interactive;
	//private boolean blocked_io;
	private Integer num_bursts; 
	//private Integer cpu_time; //between 200-3000ms
	//private Integer time_required;
	private String pid;
	
	private Integer wait;
	private Integer turnaround;
	
	private Integer blocked_time;

	private Integer ready_start;

	private Integer curr_burst_time;
	
	private ArrayList<Integer> all_turnarounds;
	private ArrayList<Integer> all_waits;

	//Need to add blocking times to know when available
	
	
	/**
	 * @param p the process id
	 * @param t_req the required time for completion of the process
	 * @param b the burst time in ms
	 * @param inter whether or not it is interactive
	 * @param c_time the time required for a cpu_burst.  If inter = true, cpu_time = -1
	 */
	public Process(String p, int b, boolean inter){
		pid = new String(p);
		burst = new Integer(b);
		all_turnarounds = new ArrayList<Integer>();
		all_waits = new ArrayList<Integer>();
		interactive = inter;
		curr_burst_time = new Integer(0);
		num_bursts = new Integer(0);
		//blocked_io = false;
		wait = new Integer(0);
		turnaround = new Integer(0);
		blocked_time = new Integer(0);
		ready_start = new Integer(0);
		/*if(!interactive){
			cpu_time = c_time;
		}else{
			cpu_time = new Integer(INV_CPU);
		}*/
	}

	public Process(Process other){

		all_turnarounds = new ArrayList<Integer>();
		all_waits = new ArrayList<Integer>();
		curr_burst_time = new Integer(other.curr_burst_time);
		blocked_time = new Integer(other.blocked_time);
		pid = other.get_pid();
		burst = new Integer(other.burst);
		interactive = other.interactive;
		num_bursts = new Integer(other.num_bursts);
		//blocked_io = other.blocked_io;
		ready_start = new Integer(other.ready_start);
		wait = new Integer(other.wait);
		turnaround = new Integer(other.turnaround);
		//cpu_time = new Integer(other.cpu_time);
	}

	public String get_pid(){
		return new String(pid);
	}
	
	/**
	 * 
	 * @return the cpu_time required for this process.  -1 if interactive
	 */
	/*public Integer get_cpu_time(){
		return new Integer(cpu_time);
	}*/
	
	/**
	 * 
	 * @return the length of a burst
	 */
	public Integer get_burst(){
		return new Integer(burst);
	}

	public void set_burst(int val){
		burst = new Integer(val);
	}

	/**
	 * 
	 * @return true if the process is currently blocked on i/o
	 */
	public boolean is_blocked(){
		if(blocked_time > 0){ return true; }
		return false;
	}

	/**
	 * @return true if the process is currently in its burst, false otherwise
	 */
	public boolean is_active(){
		if(curr_burst_time > 0 && blocked_time == 0){ return true; }
		return false;
	}

	/**
	 * 
	 * @return true if the process is interactive with the user.  False if cpu bound
	 */
	public boolean is_interactive(){
		return interactive;
	}
	/**
	 * 
	 * @return the number of burst that have occured
	 */
	public int get_num_bursts(){
		return num_bursts;
	}
	/**
	 * 
	 * @return the total time the process has waited in the ready queue
	 */
	/*public Integer get_total_wait(){
		return new Integer(total_wait);
	}*/
	
	/**
	 * @effect starts the burst for this process 
	 */
	public void activate_burst(){
		if(curr_burst_time == 0){
			curr_burst_time = burst;
			num_bursts++;
		}	
	}

	/**
	 * @effect decrements the current burst time if it is greator than 0
	 */
	public void dec_curr_burst(){
		if(curr_burst_time > 0){ 
			curr_burst_time--; 
		}
	}

	/**
	 * 
	 * @return the total turnaround time for the process (start to finish)
	 */
	public Integer get_turnaround(){
		return new Integer(turnaround);
	}

	/**
	 * @param time the time that the process enters the ready queue
	 */
	public void set_ready_entry(int time){
		ready_start = time;
	}

	/**
	 * @param time the time that the process completes its burst
	 */
	public void set_turnaround(int time){
		turnaround = new Integer(time - ready_start);
		all_turnarounds.add(new Integer(turnaround));

		if(turnaround < all_waits.get((all_turnarounds.size()-1))){
			all_waits.set((all_turnarounds.size()-1), wait);
		}
	}

	public void set_wait(int time){
		wait = new Integer(time - ready_start);
		all_waits.add(new Integer(wait));
	}

	public Integer get_wait(){
		return new Integer(wait);
	}

	/**
	 * 
	 * @param val value to increment the total wait time
	 * @effects increments the total wait time by the param val
	 */
	/*public void increment_total_wait(Integer val){
		wait += val;
	}*/
	/**
	 * 
	 * @return the maximum number of burst allowed for the process
	 */
	public Integer get_max_burst(){
		return new Integer(MAX_BURST);
	}
	/**
	 * 
	 * @return true if the maximum number of bursts have been used (cpu bound) or the 
	 * @return computation is done. false otherwise
	 */
	public boolean finished(){
		if(!interactive && MAX_BURST <= num_bursts){ 
			if(curr_burst_time == 0){
				return true;
			}	
		}
		/*Integer comp_time = num_bursts * burst;
		if(comp_time >= time_required){
			return true;
		}*/
		return false;
	}


	/**
	 * @param time the time to be blocked for
	 * @effect blocks the process
	 */
	public void set_blocked_time(int time){
		blocked_time = time;
	}

	/**
	 * @effect decrements the blocked timer
	 */
	public void dec_blocked_time(){
		if(blocked_time > 0)
			blocked_time--;
	}
	
	public Double get_avg_turnaround(){
		Double sum = new Double(0);
		for(int i = 0; i < all_turnarounds.size(); i++){
			sum += new Double(all_turnarounds.get(i));
		}
		return sum / all_turnarounds.size();
	}
	
	public Double get_avg_wait(){
		Double sum = new Double(0);
		for(int i = 0; i < all_waits.size(); i++){
			sum += new Double(all_waits.get(i));
		}
		return sum / all_waits.size();
	}

	public Integer get_min_turnaround(){
		Integer min = all_turnarounds.get(0);
		for(int i = 0; i < all_turnarounds.size(); i++){
			if(min > all_turnarounds.get(i)){
				min = all_turnarounds.get(i);
			}
		}
		return new Integer(min);
	}

	public Integer get_max_turnaround(){	
		Integer max = all_turnarounds.get(0);
		for(int i = 0; i < all_turnarounds.size(); i++){
			if(max < all_turnarounds.get(i)){
				max = all_turnarounds.get(i);
			}
		}
		return new Integer(max);
	}

	public Integer get_max_wait(){
		Integer max = all_waits.get(0);
		for(int i = 0; i < all_waits.size(); i++){
			if(max < all_waits.get(i)){
				max = all_waits.get(i);
			}
		}
		return new Integer(max);
	}

	public Integer get_min_wait(){
		Integer min = all_waits.get(0);
		for(int i = 0; i < all_waits.size(); i++){
			if(min > all_waits.get(i)){
				min = all_waits.get(i);
			}
		}
		return new Integer(min);
	}

	public int get_cpu_use_time(){
		int sum = 0;
		for(int i = 0; i < all_turnarounds.size(); i++){
			sum += (all_turnarounds.get(i) - all_waits.get(i));
		}
		return sum;
	}

	public double get_avg_cpu_time(){
		double sum = 0;
		double all_wait_time = 0;
		for(int i = 0; i < all_turnarounds.size(); i++){
			sum += (all_turnarounds.get(i) - all_waits.get(i));
			all_wait_time += all_turnarounds.get(i);
		}
		if(sum < 0){ sum *= -1; }
		return sum / all_wait_time * 100;
	}


	public String toString(){
		return "(" + pid + ")\n" + 
				"burst: " + burst + "ms\n" +
				"interactive? " + interactive + "\n" + 
				"num burst: " + num_bursts + "\n" +
				"total_wait " + wait + "ms\n" + 
				"turnaround " + turnaround + "ms"; 
	}
}
