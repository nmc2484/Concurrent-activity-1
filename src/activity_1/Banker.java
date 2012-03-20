package activity_1;

import java.util.HashMap;
import java.util.Map;

public class Banker {
	private int unitsOfResource;
	private Map<Client,ClientConfig> clientMap;

	public Banker(int nUnits) {
		clientMap = new HashMap<Client, Banker.ClientConfig>();
		unitsOfResource = nUnits;
	}

	public void setClaim(int nUnits) {
		Client client = (Client)Thread.currentThread();

		clientMap.put(client, new ClientConfig(nUnits));
	}

	/**
	 * Request n units for current thread
	 * @param nUnits to request
	 * @return boolean indicating success of request
	 */
	public boolean request(int nUnits) {
		// Only proceed if this client has been registered
		if(clientMap.containsKey((Client)Thread.currentThread())) {
			Client client = (Client)Thread.currentThread();		
			ClientConfig clientConfig = clientMap.get(client);

			// Exit if nUnits is nonpositive or exceeds current thread's remaining claim
			if (nUnits < 1 	|| nUnits > clientConfig.getUnitRemaining()) {
				System.exit(1);
			}
			System.out.println("Thread " + client.getName() + " requests " + nUnits + " units.");
			
			// TODO Detect safety of state
			if (true) {
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
	}

	/**
	 * Release n units allocated to current thread
	 * @param nUnits to deallocate
	 */
	public void release(int nUnits) {
		// Only proceed if this client has been registered
		if(clientMap.containsKey((Client)Thread.currentThread())) {
			Client client = (Client)Thread.currentThread();		
			ClientConfig clientConfig = clientMap.get(client);

			// Exit if nUnits is nonpositive or exceeds units allocated
			if (nUnits < 1 	|| nUnits > clientConfig.getUnitsAllocated()) {
				System.exit(1);
			}

			// Release nUnits
			System.out.println("Thread " + client.getName() + " releases " + nUnits + " units.");
			clientConfig.setUnitsAllocated(clientConfig.getUnitsAllocated() - nUnits);
		} else {
			// Client has not been registered
			System.exit(1);
		}
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

	private class ClientConfig{
		private int unitsAllocated;
		private int unitsRemaining;


		public ClientConfig(int claim){
			this.unitsRemaining = claim;
		}

		public int getUnitsAllocated(){
			return unitsAllocated;
		}

		public void setUnitsAllocated(int allocated){
			int claim = unitsRemaining + unitsAllocated;
			this.unitsAllocated = allocated;
			this.unitsRemaining = claim - allocated;
		}

		public int getUnitRemaining(){
			return unitsRemaining;
		}
	}
}
