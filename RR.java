/*
 * Evan Thompson, Tausif Ahmed
 */

import java.util.ArrayList;

public class RR extends CPU_Algorithm {

	private int slice_time;
	private int time;

	public RR(ArrayList<Process> processes, int num_cpus, int s){
		copy_in_procs(processes);
		super.NUM_PROCESSES = procs.size();
		super.NUM_CPUS = num_cpus;
		slice_time = s;
	}
	
	@Override
	protected void get_next_procs(int t){
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
	
	}

	public void setup_slice(ArrayList<Integer> cpu_slice){
		for(int i = 0; i < super.NUM_CPUS; i++){
			cpu_slice.add(new Integer(0));
		}	
	}

	public void context_handle_processes(ArrayList<Integer> context_cpu, ArrayList<Integer> cpu_slice){
		//check each of the curr_procs, if the process has finished its burst, relinquish.  if slice is done, switch it
		for(int i = 0; i < curr_procs.size(); i++){
			//empty cpu
			if(curr_procs.get(i) == null){
				get_next_procs(time);
				//if a process was loaded into this cpu, context switch
				if(curr_procs.get(i) != null){
					context_cpu.set(i, context_cpu.get(i) + 2);
					curr_procs.get(i).set_wait(time);
					System.out.println("[time " + time + "ms] " + curr_procs.get(i).get_type() + " process ID " + curr_procs.get(i).get_pid() + " has taken unused CPU, " + i);
				}
			}else if( true ){ // process finished burst

			}else if( true ){ // time slice is finished

			}
		}
	
	}

	@Override
	public void exec() {		
		System.out.println("---- Executing RR ----");
		
		time = 0;

		ArrayList<Integer> context_cpu = new ArrayList<Integer>();
		ArrayList<Integer> cpu_slice = new ArrayList<Integer>();

		set_start_ready();
		setup_context_cpu(context_cpu);
		setup_slice(cpu_slice);

		while(!should_stop()){
			time++;
			
//			context_handle_processes(context_cpu)
	
			break;			

		}

		//int time
		//while(!should_stop)
		//  load first n procs from queue	
		//  if(termination conditions met for any procs -> I/O, finished)
		//  	get blocktime (1000ms - 4500ms) -> set in proc
		//		pop it onto the queue
		//	if(timeslice up)
		//		for each current proc in the cpus
		//			get next proc			
		//			if proc->blocked
		//				display swapping output, proc_old with proc_new
		//		context switch timer (4ms) for all cpus
		//  time++
		
	}

}
