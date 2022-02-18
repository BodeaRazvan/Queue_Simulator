package org.QueueManager.model;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{
    @Override
    public int addClient(List<Queue> queues, Client client){
        //not implemented
        //put the client in the queue that has the smallest nr or clients
        return 1;
    }
}
