package activity_1;

import java.util.ArrayList;

public class Driver {
	/**
	 * The total amount of resources the banker is able to allocate
	 */
	final private static int numBankerResources = 10;
	/**
	 * The number of clients which will make claims on resources
	 * owned by the banker
	 */
	final private static int numClients = 3;

	/**
	 * Maximum number which any client may claim from the banker
	 * Note: We randomize the number of units clients may actually claim
	 */
	final private static int nUnits = 5;

	/**
	 * Number of times a client will either request 
	 * or release resources
	 */
	final private static int nRequests = 10;

	/**
	 * The minimum time a client may sleep after
	 * requesting or releasing resources
	 */
	final private static long minSleepMillis = 0;

	/**
	 * The maximum time a client may sleep after
	 * requesting or releasing resources
	 */
	final private static long maxSleepMillis = 413;

	/**
	 * Main method for Concurrent Activity 1
	 * Instantiates a Banker, then registers numClients
	 * clients with it. Finally, runs clients.
	 * @param args unusued.
	 */
	public static void main(String[] args) {
		// Create a banker object
		Banker banker = new Banker(numBankerResources);

		// Generate and register clients based on global vars
		ArrayList<Client> clients = new ArrayList<Client>(numClients);
		for (int i = 1; i < numClients; i++) {
			// We add some nondeterminism by randomizing maximum client claim size
			int claimSize = (int) (1 + (Math.random() * nUnits));
			clients.add(new Client("client" + i, banker, claimSize, nRequests, minSleepMillis, maxSleepMillis));
		}
		// Run clients
		for (Client client : clients) {
			client.run();
		}
		// Kill clients
		for (Client client : clients) {
			try {
				client.join();
			} catch (InterruptedException ignore) {/**/}
		}
	}
}