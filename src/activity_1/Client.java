package activity_1;

public class Client extends Thread {
	private Banker banker;
	private int nUnits;
	private int nRequests;
	private long minSleepMillis;
	private long maxSleepMillis;
	
	public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis) {
		super(name);
		this.banker = banker;
		this.nUnits = nUnits;
		this.nRequests = nRequests;
		this.minSleepMillis = minSleepMillis;
		this.maxSleepMillis = maxSleepMillis;
	}

	public void run() {
		// TODO "up to" nUnits
		this.banker.setClaim(this.nUnits);
		for (int i = 1; i <= nRequests; i++) {
			if (banker.remaining() == 0) {
				banker.release(nUnits);
			} else {
				// Request *some* units
				// TODO randomize
				banker.request(1);
			}
			try {
				// Thread sleeps for random time between minSleepMillis and maxSleepMillis
				long randomSleep = (long) (minSleepMillis + (Math.random() * ((maxSleepMillis - minSleepMillis) + 1)));
				Thread.sleep(randomSleep);
			} catch (InterruptedException e) {
				// TODO How to respond?
				e.printStackTrace();
			}
		}
		banker.release(nUnits);
		return;
	}
}
