package activity_2;

public class Philosopher extends Thread{
	private int id;
	private Fork left;
	private Fork right;
	private boolean rHanded;
	private int nTimes;
	private long thinkMillis;
	private long eatMillis;

	public Philosopher(int id, Fork left, Fork right, boolean rHanded, int nTimes, long thinkMillis, long eatMillis){
		this.id = id;
		this.left = left;
		this.right = right;
		this.rHanded = rHanded;
		this.nTimes = nTimes;
		this.thinkMillis = thinkMillis;
		this.eatMillis = eatMillis;
	}

	public void run(){
		int i = 0;
		while (nTimes == 0 ? true : i < nTimes) {
			long t = 0 + (long)(Math.random() * ((this.thinkMillis - 0) + 1));
			System.out.println("Philosopher " + this.id + " thinks for " + t + " time units");
			try{
				this.sleep(t);
			} catch( java.lang.InterruptedException ie){
				//TODO do we ignore or return something??
			}
			if(this.rHanded){
				System.out.println("Philosopher " + this.id + " goes for right fork");
				this.right.acquire();
				System.out.println("Philosopher " + this.id + " has right fork");
				Thread.yield();
				System.out.println("Philosopher " + this.id + " goes for left fork");
				this.left.acquire();
				System.out.println("Philosopher " + this.id + " has left fork");
				Thread.yield();
			} else {
				// Philosopher is left handed
				System.out.println("Philosopher " + this.id + " goes for left fork");
				this.left.acquire();
				System.out.println("Philosopher " + this.id + " has left fork");
				Thread.yield();
				System.out.println("Philosopher " + this.id + " goes for right fork");
				this.right.acquire();
				System.out.println("Philosopher " + this.id + " has right fork");
				Thread.yield();
			}
			long eatTime = 0+(long) (Math.random() * ((this.eatMillis - 0)+ 1));
			System.out.println("Philosopher " + this.id + " eats for " + eatTime + " seconds");
			try{
				this.sleep(eatTime);
			} catch(java.lang.InterruptedException ie){
				//TODO do we ignore or return something??
			}
			//Philosopher releases their right fork
			this.right.release();
			System.out.println("Philosopher " + this.id + " releases right fork");
			//Philosopher releases their left fork
			this.left.release();
			System.out.println("Philosopher " + this.id + " releases left fork");
		}
	}
}
