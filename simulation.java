/*
 * Evan Thompson, Tausif Ahmed, Jack Cusick
 */

import java.util.ArrayList;

public class simulation {

	static Integer NUM_PROCESSES;
	static Integer NUM_CPUS;
	static Integer SLICE;

	
	public static Integer gen_num(int range, int offset){
		return (int) (Math.random()*range) + offset;
	}

	public static void InitProcs(ArrayList<Process> procs, int NUM_PROCESSES){
		
		Double P_INT = .8;
		Integer BURST_RANGE = 180;
		Integer BURST_OFF = 20;
		Integer CPU_BURST_RANGE = 2800;
		Integer CPU_BURST_OFF = 200;
	
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
			
			Process tmp = new Process("" + (i + 1), burst, inter, cpu_time);
			
			procs.add(tmp);
		}
		
		//print_procs();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NUM_PROCESSES = 12;
		NUM_CPUS = 4;
		SLICE = 80; //ms
		
		System.out.println("printing arguments: ");
		for(int i = 0; i < args.length; i++){
			System.out.println(i + ": " + args[i]);
		}
		
		if(args.length >= 1){
			NUM_PROCESSES = Integer.parseInt(args[0]);
			if(args.length >= 2){
				NUM_CPUS = Integer.parseInt(args[1]);
				if(args.length >= 3){
					SLICE = Integer.parseInt(args[2]);
				}
			}
			
		}

		ArrayList<Process> procs = new ArrayList<Process>();
		InitProcs(procs, NUM_PROCESSES);
		
		//Algorithms ------------
		
		FCFS algo = new FCFS(procs, NUM_CPUS);
		algo.exec();
		SJF algo1 = new SJF(procs, NUM_CPUS);
		algo1.exec();
		SJF_P algo2 = new SJF_P(procs, NUM_CPUS);
		algo2.exec();
		RR algo3 = new RR(procs, NUM_CPUS, SLICE);
		algo3.exec();
		
	}

}
