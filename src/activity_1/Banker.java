package activity_1;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banker {
	private int unitsOfResource;
	private Map<Client, ClientConfig> clientMap;

	public Banker(int nTotalUnits) {
		clientMap = Collections
				.synchronizedMap(new HashMap<Client, Banker.ClientConfig>());
		unitsOfResource = nTotalUnits;
	}

	public synchronized void setClaim(int nUnits) {
		System.out.println("Setting claims....");
		Client client = (Client) Thread.currentThread();
		System.out.println("Found the class of: "
				+ client.getClass().toString());

		if (client.registeredUnits()) {
			if (nUnits <= unitsOfResource) {
				clientMap.put(client, new ClientConfig(nUnits));
				System.out.println("Thread " + client.getName()
						+ " sets a claim for " + nUnits + " units.");
			}
		} else {
			System.out.println("And we're gone....");
			System.exit(1);
		}

	}

	/**
	 * Request n units for current thread
	 * 
	 * @param nUnits
	 *            to request
	 * @return boolean indicating success of request
	 */
	public synchronized boolean request(int nUnits) {
		// Only proceed if this client has been registered
		// TODO optimize synchronization
		if (clientMap.containsKey((Client) Thread.currentThread())) {
			Client client = (Client) Thread.currentThread();
			ClientConfig clientConfig = clientMap.get(client);

			// Exit if nUnits is nonpositive or exceeds current thread's
			// remaining claim
			if (nUnits < 1 || nUnits > clientConfig.getUnitRemaining()) {
				System.exit(1);
			}
			System.out.println("Thread " + client.getName() + " requests "
					+ nUnits + " units.");

			// TODO Detect safety of state
			// Deepcopy ClientConfigs

            //Creates new has map and adds everything from client map to it
           HashMap clientMapCopy = new HashMap();
           clientMapCopy.putAll(clientMap);
			int copy = unitsOfResource;

			if (bankerAlgorithm(copy, clientMapCopy)) {
				System.out.println("Thread " + client.getName() + " has "
						+ nUnits + " units allocated.");
				clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated()
						+ nUnits);
			} else {
				System.out.println("Thread " + client.getName() + " waits.");
				try {
					client.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Thread " + client.getName() + " awakened.");
			}
			// Release nUnits
			clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated()
					- nUnits);
		} else {
			// Client has not been registered
			System.exit(1);
		}
		return true;
	}

	/**
	 * Release n units allocated to current thread
	 * 
	 * @param nUnits
	 *            to deallocate
	 */
	public void release(int nUnits) {
		// Only proceed if this client has been registered
		boolean containsKey;
		Client client = null;
		ClientConfig clientConfig = null;
		synchronized (this) {
			containsKey = clientMap
					.containsKey((Client) Thread.currentThread());
			if (containsKey) {
				client = (Client) Thread.currentThread();
				clientConfig = clientMap.get(client);
			} else {
				// Client has not been registered
				System.exit(1);
			}
		}
		// Exit if nUnits is nonpositive or exceeds units allocated
		if (nUnits < 1 || nUnits > clientConfig.getUnitsAllocated()) {
			System.exit(1);
		}

		// Release nUnits
		System.out.println("Thread " + client.getName() + " releases " + nUnits
				+ " units.");
		clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated()
				- nUnits);
		notifyAll();
		return;
	}

	/**
	 * Get the number of units allocated by the current thread
	 * 
	 * @return int
	 */
	public int allocated() {
		return clientMap.get((Client) Thread.currentThread())
				.getUnitsAllocated();
	}

	/**
	 * Get the number of units claimed by the current thread which have not yet
	 * been allocated
	 * 
	 * @return int
	 */
	public int remaining() {
		return clientMap.get((Client) Thread.currentThread())
				.getUnitRemaining();
	}

	/**
	 * Internal implementation of The Banker's Algorithm
	 * 
	 * @param nUnitsOnHand
	 *            which banker can allocate
	 * @param clientMap
	 *            of Threads to ClientConfig objects containing
	 *            currentAllocation and remainingClaim
	 * @return true if input state is safe, false otherwise
	 */
	private boolean bankerAlgorithm(int nUnitsOnHand,
			HashMap copyValls) {

        //creates the array of client configs
        Object[] ClientConfigPreCopy = copyValls.values().toArray(new ClientConfig[0]);
        ClientConfig[] ClientConfigCopy = (ClientConfig[]) ClientConfigPreCopy;


		System.out.println("pairsArray successfully created!!!!!!");
		// sorts the array
        Arrays.sort(ClientConfigCopy, new ByUnitsRemaining());
		for (int i = 0; i < ClientConfigCopy.length - 1; i++) {
			if (ClientConfigCopy[i].getUnitRemaining() > nUnitsOnHand)
				return false;
			nUnitsOnHand += ClientConfigCopy[i].getUnitsAllocated();
		}
		return true;
	}

        private class ByUnitsRemaining implements Comparator<ClientConfig>{
        public int compare(ClientConfig configA, ClientConfig configB){
            if(configA.getUnitRemaining() < configB.getUnitRemaining()) {
                return -1;
            } else if (configA.getUnitRemaining() > configB.getUnitRemaining()){
                return 1;
            }else {
                return 0;
            }
        }
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
		 * 
		 * @param claim
		 */
		public ClientConfig(int claim) {
			this.unitsRemaining = claim;
		}

		// constructor used for creating a deep copy of client map
		public ClientConfig(int unitsAllocated, int unitsRemaining) {
			this.unitsAllocated = unitsAllocated;
			this.unitsRemaining = unitsRemaining;
		}

		/**
		 * Get the number of units allocated to this client
		 * 
		 * @return int unitsAllocated
		 */
		public synchronized int getUnitsAllocated() {
			return unitsAllocated;
		}

		/**
		 * Set the number of units allocated to this client
		 * 
		 * Note that the sum of unitsAllocated and unitsRemaining is invariant.
		 * 
		 * @param allocated
		 */
		public synchronized void setUnitsAllocated(int allocated) {
			int claim = unitsRemaining + unitsAllocated;
			this.unitsAllocated = allocated;
			this.unitsRemaining = claim - allocated;
		}

		/**
		 * Get the number of units which can be allocated to this client
		 * 
		 * @return int unitsRemaining
		 */
		public synchronized int getUnitRemaining() {
			return unitsRemaining;
		}
	}
}