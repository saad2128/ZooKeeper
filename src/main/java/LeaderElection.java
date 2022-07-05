import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class LeaderElection implements Watcher {

    private static final String ZOOKEEPER_ADDRESS="localhost:2181";
    private static final int SESSION_TIMEOUT=3000;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException,InterruptedException{
        LeaderElection leaderElection=new LeaderElection();
        leaderElection.connectToZooKeeper();
        leaderElection.run();
        leaderElection.close();

    }

    public void connectToZooKeeper() throws  IOException{
        this.zooKeeper=new ZooKeeper(ZOOKEEPER_ADDRESS,SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException{

        synchronized(zooKeeper){
            zooKeeper.wait();
        }
    }
    public void process(WatchedEvent event){
         switch (event.getType()){
             case None:
                 if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                     System.out.println("Successfully connected");
                 }
                 else{
                     synchronized (zooKeeper){
                         zooKeeper.notifyAll();
                     }
                 }
         }
    }
    public void close() throws InterruptedException{
        zooKeeper.close();
    }
}
