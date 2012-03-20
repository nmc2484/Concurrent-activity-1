package activity_1;

import java.util.Random;

public class Client extends Thread {
	private Banker banker;
	private int nUnits;
	private int nRequests;
	private long minSleepMillis;
	private long maxSleepMillis;

	/**
	 * Fully parameterized construction for a client
	 * which acquires resources from Banker
	 * @param name of Client (e.g. client3)
	 * @param banker in which to register client
	 * @param nUnits maximum claim Client may make
	 * @param nRequests number of request/release cycles
	 * @param minSleepMillis after request/release cycle
	 * @param maxSleepMillis after request/release cycle
	 */
	public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis) {
		super(name);
		this.banker = banker;
		this.nUnits = nUnits;
		this.nRequests = nRequests;
		this.minSleepMillis = minSleepMillis;
		this.maxSleepMillis = maxSleepMillis;
	}

    public boolean registeredUnits() {
		return nUnits > 0;
	}

	/**
	 * Run method for Thread subclass.
	 * 
	 * Makes a claim with banker, and cycles
	 * nRequests times, either requesting or
	 * releasing units each time. Sleeps a 
	 * random time between minSleepMillis and
	 * maxSleepMillis after each cycle.
	 */
	public void run() {
		// Claim nUnits resources		
		this.banker.setClaim(nUnits);
		// Cycle nRequests times
		for (int i = 1; i <= nRequests; i++) {
			if (banker.remaining() == 0) {
				// If bank has no resources left, release
				// resources claimed by this client
				banker.release(banker.allocated());
			} 
			else if (banker.allocated() == 0){
				// If this client has not acquired any resources,
				// request an amount between 1 and the maximum claim
				int requestSize = (int) (1 + (Math.random() * this.nUnits));
				banker.request(requestSize);
			} else {
				// This client has already acquired some resources,
				// and there is room to acquire some more.
				// Flip a coin to decide request/release
				Random r = new Random();
				boolean probAction = r.nextBoolean();
				if(probAction){
					//Release between 1 and numUnitsAllocated units
					int releaseUnits = (int) (1 + (Math.random() * (banker.allocated() - 1)));
					banker.release(releaseUnits);
				}
				else{
					//Request between 1 and numUnitsAvailable units
					int requestUnits = (int) (1 + (Math.random() * (banker.remaining() - 1)));
					banker.request(requestUnits);
				}
			}
			try {
				// Thread sleeps for random time between minSleepMillis and maxSleepMillis
				long randomSleep = (long) (minSleepMillis + (Math.random() * ((maxSleepMillis - minSleepMillis) + 1)));
				Thread.sleep(randomSleep);
			} catch (InterruptedException ignore) {/**/}
		}
		banker.release(nUnits);
		return;
	}
}
