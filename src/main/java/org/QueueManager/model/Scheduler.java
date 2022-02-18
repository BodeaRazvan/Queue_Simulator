package org.QueueManager.model;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private final List<Queue> queues;
    public static int maxClientsPerQueue;
    private Strategy strategy=new ConcreteStrategyTime();

    public Scheduler(int maxNoQueues, int maxClientsPerQueue){
        Scheduler.maxClientsPerQueue = maxClientsPerQueue;
        this.queues =new ArrayList<>();
        //Start new threads (Queues) equal to the nr given by the user
        for(int i = 1; i<= maxNoQueues; i++){
            Queue queue = new Queue(i);
            this.queues.add(queue);
            Thread thread = new Thread(queue);
            thread.start();
        }
    }

    //Useful if you implement more strategies and want to swap between them
    public void changeStrategy(SelectionPolicy policy){
        if(policy==SelectionPolicy.SHORTEST_TIME){
            strategy = new ConcreteStrategyTime();
        }
        if(policy==SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }
    }

    public void dispatchClients(Client client){
        strategy.addClient(queues, client);
    }

    public List<Queue> getQueues(){
        return queues;
    }
}
