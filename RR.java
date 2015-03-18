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
			cpu_slice.add(new Integer(slice_time));
		}	
	}

	private void print_context_switch(int i, ArrayList<Integer> context_cpu, Process tmp){
		curr_procs.set(i, null);
		get_next_procs(time);
		if(curr_procs.get(i) != null){
			System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
			context_cpu.set(i, 4);
			curr_procs.get(i).set_wait(time);
			curr_procs.get(i).activate_burst();
	
		}else{
			System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " but no process to replace it)");		
			context_cpu.set(i, 2);
		}
	}

	public void context_handle_processes(ArrayList<Integer> context_cpu, ArrayList<Integer> cpu_slice){
	
		for(int i = 0; i < curr_procs.size(); i++){
			if(context_cpu.get(i) == 0 && cpu_slice.get(i) != 0 && curr_procs.get(i) != null){
				
				Process tmp = curr_procs.get(i);

				if(!tmp.is_active()){
					tmp.activate_burst();
					tmp.set_wait(time);	
				}

				tmp.dec_curr_burst();
				cpu_slice.set(i, cpu_slice.get(i) - 1);

				//if the burst process is completed
				if(!tmp.is_active()){
					//set turnaround
					if(tmp.get_wait() == 0){
						tmp.set_turnaround(time + 1);
					}else{
						tmp.set_turnaround(time + 1);
					}
					//setup for I/O blocking
					print_proc_end(tmp, time);

					//add to blocked procs
					if(!tmp.finished()){
						int val = gen_num(IO_BLOCK_RANGE, IO_BLOCK_OFF);
						int burst_val;
						if(tmp.is_interactive()){
							burst_val = gen_num(BURST_RANGE, BURST_OFF);
						}else{
							burst_val = gen_num(CPU_BURST_RANGE, CPU_BURST_OFF);
						}
						tmp.set_burst(burst_val);
						tmp.set_blocked_time(val);

						blocked_procs.add(tmp);
					}

					//remove from curr_procs
					curr_procs.set(i, null);

					//set slice back to slice time
					cpu_slice.set(i, slice_time);

					//context switch
					get_next_procs(time);

					if(curr_procs.get(i) != null){
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
						context_cpu.set(i, new Integer(4));
					}else{
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " but no process to replace it)");
						context_cpu.set(i, new Integer(2));
					}
				}else if(cpu_slice.get(i) == 0){
					//bump tmp into the ready queue
					procs.add(tmp);
						
					//add context switch
					curr_procs.set(i, null);
					get_next_procs(time);
					cpu_slice.set(i, slice_time);

					if(curr_procs.get(i) != null){
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " for process ID " + curr_procs.get(i).get_pid() + ")");
						context_cpu.set(i, new Integer(4));
					}else{
						System.out.println("[time " + time + "ms] Context switch (swapping out process ID " + tmp.get_pid() + " but no process to replace it)");
						context_cpu.set(i, new Integer(2));
					}
				}

			}else{
				if(curr_procs.get(i) == null){
					get_next_procs(time);
					if(curr_procs.get(i) != null){
						context_cpu.set(i, context_cpu.get(i) + 2);
						if(curr_procs.get(i).is_interactive()){
							System.out.println("[time " + time + "ms] " + curr_procs.get(i).get_type() + " process ID " + curr_procs.get(i).get_pid() + " has taken unused cpu, " +
								(i + 1));	
						}
					}
				}
			}
		}













		
		/*	
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
					if(context_cpu.get(i) == 0){
						curr_procs.get(i).activate_burst();
					}
				}
			}else if( !curr_procs.get(i).is_active() && !curr_procs.get(i).is_blocked() ){ // process finished burst
		//		System.out.println("not active: " + curr_procs.get(i).get_pid() + "\n" + curr_procs.get(i));
				Process tmp = curr_procs.get(i);
				//tell it how long it was active
				tmp.set_turnaround(time);
				if(!tmp.finished()){
					
					//add to blocked for a random time
					int blocked_time = gen_num(IO_BLOCK_RANGE, IO_BLOCK_OFF);
					tmp.set_blocked_time(blocked_time);
					blocked_procs.add(tmp);
		
					System.out.println("time: " + time + "   PROCESS BLOCKED\n" + tmp);
				}
				int new_burst;
				if(tmp.is_interactive()){
					new_burst = gen_num(BURST_RANGE, BURST_OFF);
				}else{
					new_burst = gen_num(CPU_BURST_RANGE, CPU_BURST_OFF);
				}
				tmp.set_burst(new_burst);
				//swap in the new process
				print_context_switch(i, context_cpu, tmp);

				cpu_slice.set(i, slice_time);	

			}else if( cpu_slice.get(i) == 0 ){ // time slice is finished
				//take the process off the cpu and then add it to the procs
				//set the ready_entry
				Process tmp = curr_procs.get(i);
				
				//tmp.set_turnaround(time);
			
				tmp.set_ready_entry(time);

				System.out.println("[time " + time + "ms] " + tmp.get_type() + " process ID " + tmp.get_pid() + " entered ready queue (requires " + tmp.get_remaining_burst() + "ms CPU time)");

				procs.add(tmp);

				//setup context switch
				print_context_switch(i, context_cpu, tmp);

				cpu_slice.set(i, slice_time);
			}else{ //normal running of current_proc
				if(context_cpu.get(i) == 0){	
					curr_procs.get(i).dec_curr_burst();
					cpu_slice.set(i, cpu_slice.get(i) - 1);
				}
			}
			/*if(curr_procs.get(i) != null){
				if(!curr_procs.get(i).is_active()){
					curr_procs.get(i).activate_burst();
				}
			}
		}*/
	}
/*
	private void print_arr(ArrayList<Object> arr){
		System.out.println("Printing array: ");

		for(int i = 0; i < arr.size(); i++){
			System.out.println(arr.get(i));
			System.out.println("-----------------------");
		}
	}

*/	/*private void print_context(ArrayList<Integer> arr){
		System.out.println("printing values:");
		for(int i = 0; i < arr.size(); i++){
			System.out.print("("+arr.get(i) + ") ");
		}
		System.out.println("---------------------------");
	}*/

	private void activate_curr_procs(){
		for(int i = 0; i < curr_procs.size(); i++){
			if(curr_procs.get(i) != null){
				curr_procs.get(i).activate_burst();
			}
		}
	}


/*	private void attempt_activation(ArrayList<Integer> context_cpu){
		for(int i = 0; i < curr_procs.size(); i++){
			if(context_cpu.get(i) == slice_time && curr_procs.get(i) != null){
				if(!curr_procs.get(i).is_active()){
					curr_procs.get(i).activate_burst();
				}
			}
		}
	}
*/
//	private void handle_blocked_processes_rr(int t){
		//for(int i = 0; i < blocked_procs.size(); i++){

			//if(blocked_procs.get(i).is_active() || blocked_procs.get(i).
		//}
//	}


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

		while(!should_stop()){
			
			//attempt_activation(context_cpu);

//			System.out.println("TIME:----------------------------- " + time);
			context_handle_processes(context_cpu, cpu_slice);
//			System.out.println("after context handle");
			/*
			print_curr_procs();
			print_procs();
			print_context(context_cpu);
			print_context(cpu_slice);
			*/
//			System.out.println("before handle blocked");
			handle_blocked_processes(time);
//			System.out.println("After handle blocked");
			
			dec_context_switch(context_cpu, time);
//			System.out.println("After dec context");
			remove_finished_procs(time);
//			System.out.println("after remove finished");

			time++;
//			System.out.println("blocked_size: " + blocked_procs.size() + " ||  procs size: " + procs.size()); 

		//	System.out.println("time: " + time + " procs size: " + procs.size() + " blocked size: " + blocked_procs.size());

/*			for(int i = 0; i < curr_procs.size(); i++){
				if(curr_procs.get(i) != null && curr_procs.get(i).is_active() && !curr_procs.get(i).finished()){
					curr_procs.get(i).activate_burst();
				}
			}
*/

		/*	if(blocked_procs.size() == super.NUM_PROCESSES){
				System.out.println("12 processes in the blocked queue");
				for(int i = 0; i < blocked_procs.size(); i++){
					System.out.println(blocked_procs.get(i));
					System.out.println("---------------------------");
				}
				break;
			}
*/
			//print_context(context_cpu);

			//break;			

			//time++;
		}

/*		System.out.println("Printing all processes:");
		for(int i = 0; i < all_procs.size(); i++){
			System.out.println(all_procs.get(i));
			System.out.println("---------------------");
		}
*/
		display_data(all_procs, time);

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
