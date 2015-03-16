/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

public class Process {
	//constant
	private Integer INV_CPU = new Integer(-1);
	private Integer MAX_BURST = new Integer(6);
	
	private Integer burst; //between 20-200ms
	private boolean interactive;
	private boolean blocked_io;
	private Integer num_bursts; 
	private Integer cpu_time; //between 200-3000ms
	//private Integer time_required;
	private String pid;
	
	private Integer total_wait;
	private Integer turnaround;
	
	
	
	//Need to add blocking times to know when available
	
	
	/**
	 * @param p the process id
	 * @param t_req the required time for completion of the process
	 * @param b the burst time in ms
	 * @param inter whether or not it is interactive
	 * @param c_time the time required for a cpu_burst.  If inter = true, cpu_time = -1
	 */
	public Process(String p, /*int t_req,*/ int b, boolean inter, int c_time){
		pid = new String(p);
		//time_required = new Integer(t_req);
		burst = new Integer(b);
		interactive = inter;
		num_bursts = new Integer(0);
		blocked_io = false;
		total_wait = new Integer(0);
		turnaround = new Integer(0);
		if(!interactive){
			cpu_time = c_time;
		}else{
			cpu_time = new Integer(INV_CPU);
		}
	}
	
	public String get_pid(){
		return new String(pid);
	}
	
	/**
	 * 
	 * @return the time required for the process
	 */
	/*public Integer get_time_required(){
		return new Integer(time_required);
	}*/
	
	/**
	 * 
	 * @return the cpu_time required for this process.  -1 if interactive
	 */
	public Double get_cpu_time(){
		return new Double(cpu_time);
	}
	
	/**
	 * 
	 * @return the length of a burst
	 */
	public Double get_burst(){
		return new Double(burst);
	}
	/**
	 * 
	 * @return true if the process is currently blocked on i/o
	 */
	public boolean is_blocked(){
		return blocked_io;
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
	public Double get_total_wait(){
		return new Double(total_wait);
	}
	
	/**
	 * 
	 * @return true if the burst was successfully incremented, false if the num_burst was too great
	 */
	public boolean burst(){
		if(burst >= MAX_BURST){ return false; }
		num_bursts++;
		return true;
	}
	/**
	 * 
	 * @return the total turnaround time for the process (start to finish)
	 */
	public Integer get_turnaround(){
		return new Integer(turnaround);
	}
	/**
	 * 
	 * @param val value to increment the turnaround by
	 * @effects increments the turnaround time by the param val
	 */
	public void increment_turnaround(Integer val){
		turnaround += val;
	}
	
	/**
	 * 
	 * @param val value to increment the total wait time
	 * @effects increments the total wait time by the param val
	 */
	public void increment_total_wait(Integer val){
		total_wait += val;
	}
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
		if(!interactive && MAX_BURST <= num_bursts){ return true; }
		/*Integer comp_time = num_bursts * burst;
		if(comp_time >= time_required){
			return true;
		}*/
		return false;
	}
	
	public String toString(){
		return "(" + pid + ")\n" + 
				"blocked: " + blocked_io + "\n" + 
				"burst: " + burst + "ms\n" +
				"cpu time: " + cpu_time + "ms\n" + 
				"interactive? " + interactive + "\n" + 
				"num burst: " + num_bursts + "\n" +
				"total_wait " + total_wait + "ms\n" + 
				"turnaround " + turnaround + "ms"; 
	}
}
