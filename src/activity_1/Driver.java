package activity_1;

import java.util.ArrayList;

public class Driver {
	final private static int numBankerResources = 5;
	final private static int numClients = 0;
	
	final private static int nUnits = 0;
	final private static int nRequests = 0;
	final private static long minSleepMillis = 0;
	final private static long maxSleepMillis = 413;
	
	// Client args
	// Name, nUnits, nRequests, minSleepMillis, maxSleepMillis
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO config based on passed args
		
		// Create a banker object
		Banker banker = new Banker(numBankerResources);
		
		ArrayList<Client> clients = new ArrayList<Client>(numClients);
		for (int i = 1; i < numClients; i++) {
			// TODO finish dis here!
			clients.add(new Client("client" + i, banker, nUnits, nRequests, minSleepMillis, maxSleepMillis));
		}
		for (Client client : clients) {
			client.run();
			// Some shit 'bout "join"
		}
	}

}
