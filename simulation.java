//package evanmt.opsys.hw2.sim;
/*
 * Evan Thompson, Tausif Ahmed
 */


public class simulation {

	static Integer NUM_PROCESSES;
	static Integer NUM_CPUS;
	static Integer SLICE;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NUM_PROCESSES = 10;
		NUM_CPUS = 4;
		SLICE = 80; //ms
		
		if(args.length >= 2){
			NUM_PROCESSES = Integer.parseInt(args[1]);
			if(args.length >= 3){
				NUM_CPUS = Integer.parseInt(args[2]);
				if(args.length >= 4){
					SLICE = Integer.parseInt(args[3]);
				}
			}
			
		}
		
		//Algorithms ------------
		
		FCFS algo = new FCFS(NUM_PROCESSES, NUM_CPUS);
		algo.exec();
		SJF algo1 = new SJF(NUM_PROCESSES, NUM_CPUS);
		algo1.exec();
		SJF_P algo2 = new SJF_P(NUM_PROCESSES, NUM_CPUS);
		algo2.exec();
		RR algo3 = new RR(NUM_PROCESSES, NUM_CPUS, SLICE);
		algo3.exec();
		
	}

}
