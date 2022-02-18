package org.QueueManager.model;

import java.util.List;

public interface Strategy {
    int addClient(List<Queue> queues, Client client);
}
