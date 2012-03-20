package activity_1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banker {
	private int unitsOfResource;
	private Map<Client,ClientConfig> clientMap;

	public Banker(int nTotalUnits) {
		clientMap = Collections.synchronizedMap(new HashMap<Client, Banker.ClientConfig>());
		unitsOfResource = nTotalUnits;
	}

	public synchronized void setClaim(int nUnits) {
		Client client = (Client)Thread.currentThread();
		clientMap.put(client, new ClientConfig(nUnits));
	}

	/**
	 * Request n units for current thread
	 * @param nUnits to request
	 * @return boolean indicating success of request
	 */
	public synchronized boolean request(int nUnits) {
		// Only proceed if this client has been registered
		// TODO optimize synchronization
		if(clientMap.containsKey((Client)Thread.currentThread())) {
			Client client = (Client)Thread.currentThread();		
			ClientConfig clientConfig = clientMap.get(client);

			// Exit if nUnits is nonpositive or exceeds current thread's remaining claim
			if (nUnits < 1 	|| nUnits > clientConfig.getUnitRemaining()) {
				System.exit(1);
			}
			System.out.println("Thread " + client.getName() + " requests " + nUnits + " units.");

			// TODO Detect safety of state
			// Deepcopy ClientConfigs
			List<ClientConfig> copyVals = new ArrayList<ClientConfig>(clientMap.values().size());
			for (ClientConfig config : clientMap.values()) {
				copyVals.add(new ClientConfig(config.getUnitsAllocated(), config.getUnitRemaining()));
			}
			int copy = unitsOfResource;
			if ( bankerAlgorithm(copy,copyVals)) {
				System.out.println("Thread " + client.getName() + " has " + nUnits + " units allocated.");
				clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated() + nUnits);
				System.out.println("(Thread " + client.getName() + " has " + clientConfig.getUnitsAllocated() + " total units allocated.)");
			} else if (false) {
				System.out.println("Thread " + client.getName() + " waits.");
				System.out.println("Thread " + client.getName() + " awakened.");
				try {
					client.wait();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Release nUnits
			clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated() - nUnits);
		} else {
			// Client has not been registered
			System.exit(1);
		}
		return false;
	}

	/**
	 * Release n units allocated to current thread
	 * @param nUnits to deallocate
	 */
	public void release(int nUnits) {
		// Only proceed if this client has been registered
		boolean containsKey;
		Client client = null;
		ClientConfig clientConfig = null;
		synchronized(this) {
			containsKey = clientMap.containsKey((Client)Thread.currentThread());
			if (containsKey) {
				client = (Client)Thread.currentThread();		
				clientConfig = clientMap.get(client);
			} else {
				// Client has not been registered
				System.exit(1);
			}
		}
		// Exit if nUnits is nonpositive or exceeds units allocated
		if (nUnits < 1 	|| nUnits > clientConfig.getUnitsAllocated()) {
			System.exit(1);
		}

		// Release nUnits
		System.out.println("Thread " + client.getName() + " releases " + nUnits + " units.");
		clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated() - nUnits);
	}

	/**
	 * Get the number of units allocated by the current thread
	 * @return int
	 */
	public int allocated() {
		return clientMap.get((Client)Thread.currentThread()).getUnitsAllocated();
	}

	/**
	 * Get the number of units claimed by the current thread which have not yet been allocated 
	 * @return int
	 */
	public int remaining() {
		return clientMap.get((Client)Thread.currentThread()).getUnitRemaining();
	}

	/**
	 * Internal implementation of The Banker's Algorithm
	 * @param nUnitsOnHand which banker can allocate
	 * @param clientMap of Threads to ClientConfig objects containing currentAllocation and remainingClaim
	 * @return true if input state is safe, false otherwise
	 */
	private boolean bankerAlgorithm(int nUnitsOnHand,List<ClientConfig> copyVals){
		ClientConfig[] pairsArray = (ClientConfig[]) clientMap.values().toArray(); 
		// TODO sort the goddamn array
		for (int i = 0; i < pairsArray.length - 1; i++) {
			if (pairsArray[i].getUnitRemaining() > nUnitsOnHand) return false;
			nUnitsOnHand += pairsArray[i].getUnitsAllocated();
		}
		return true;
	}

	/**
	 * Threadsafe class containing client allocation data 
	 *
	 */
	private class ClientConfig {
		private int unitsAllocated;
		private int unitsRemaining;

		/**
		 * Construct, setting claim
		 * 
		 * Claim == unitsRemaining since unitsAllocated is initially 0
		 * @param claim
		 */
		public ClientConfig(int claim){
			this.unitsRemaining = claim;
		}
		
		//constructor used for creating a deep copy of client map
		public ClientConfig(int unitsAllocated, int unitsRemaining ){
			this.unitsAllocated = unitsAllocated;
			this.unitsRemaining = unitsRemaining;
		}
		/**
		 * Get the number of units allocated to this client
		 * @return int unitsAllocated
		 */
		public synchronized int getUnitsAllocated(){
			return unitsAllocated;
		}

		/**
		 * Set the number of units allocated to this client
		 * 
		 * Note that the sum of unitsAllocated and unitsRemaining is invariant.
		 * @param allocated
		 */
		public synchronized void setUnitsAllocated(int allocated){
			int claim = unitsRemaining + unitsAllocated;
			this.unitsAllocated = allocated;
			this.unitsRemaining = claim - allocated;
		}

		/**
		 * Get the number of units which can be allocated to this client
		 * @return int unitsRemaining
		 */
		public synchronized int getUnitRemaining(){
			return unitsRemaining;
		}
	}
}
