package org.QueueManager.model;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{
    @Override
    //Suggested Documentation:    https://en.wikipedia.org/wiki/Strategy_pattern
    public int addClient(List<Queue> queues, Client client){
        int minWaitingTime=9999;
        int posServer=-1;
        for(Queue queue : queues){
            if(queue.getNoOfClients()<Scheduler.maxClientsPerQueue){
                //try to put the next client in the queue with the least amount of waiting time
                int waitingTime = queue.getWaitingPeriod().intValue();
                if(waitingTime<minWaitingTime){
                    minWaitingTime=waitingTime;
                    posServer= queues.indexOf(queue);
                }
            }
        }
        if(posServer!=-1){
            queues.get(posServer).addClient(client);
            return 0;
        }else{
            return -1;
        }
    }
}
