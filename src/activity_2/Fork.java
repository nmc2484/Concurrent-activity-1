package activity_2;

public class Fork implements IFork {

    private boolean allocated = false;

    public void acquire(){
        if (this.allocated){
            try{
            Thread.currentThread().wait();
            } catch(InterruptedException ie){
                //TODO do we ignore or return something??
            }
        }
        this.allocated = true;
    }

    public void release(){
        this.allocated = false;
    }

}
