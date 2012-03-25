package activity_2;

import java.util.ArrayList;

public class Driver {

	public static void main(String[] args){
		//TODO get this working
		boolean rightHanded = true;
		int np = 4;
		int nt = 10;
		int tm = 0;
		int em = 0;

		// Create np forks
		Fork[] forks = new Fork[np];
		for (Fork fork : forks) {
			fork = new Fork();
		}

		// Create np philosophers and assign forks
		ArrayList<Philosopher> philosophers = new ArrayList<Philosopher>(np);
		for (int i = 0; i < np; i++) {
			int lf = (np + i - 1) % np;
			Philosopher philosopher;
			if (!rightHanded && i % 2 == 1) {
				philosopher = new Philosopher(i, forks[lf], forks[i], false, nt, tm, em);
			} else {
				philosopher = new Philosopher(i, forks[lf], forks[i], false, nt, tm, em);
			}
			philosophers.add(philosopher);
		}
		// Start philosophizing 
		for (Philosopher philosopher : philosophers) philosopher.start();
	}
}
