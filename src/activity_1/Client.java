package activity_1;

import java.util.Random;

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
		// "up to" nUnits
		int claimSize = (int) (1 + (Math.random() * this.nUnits));
		this.banker.setClaim(claimSize);
		for (int i = 1; i <= nRequests; i++) {
			if (banker.remaining() == 0) {
				banker.release(nUnits);
			} 
			else if (banker.allocated() == 0){
				banker.request(nUnits);
			} else {
				// Request *some* units
				Random r = new Random();
				boolean probAction = r.nextBoolean();
				//random 50/50 request or release resources
				if(probAction){
					//if release, release at least 1
					int releaseUnits = (int) (1 + (Math.random() * (banker.allocated() - 1)));
					banker.release(releaseUnits);
				}
				else{
					//if request, request at least 1
					int requestUnits = (int) (1 + (Math.random() * (banker.remaining() - 1)));
					banker.request(requestUnits);
				}
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
