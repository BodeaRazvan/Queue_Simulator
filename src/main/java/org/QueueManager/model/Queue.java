package org.QueueManager.model;

import org.QueueManager.controller.Controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable{
    private final int id;
    private final BlockingQueue<Client> clients;
    private final AtomicInteger waitingPeriod;
    private double averageTime;
    private int peakTime;

    public int getTotalWaitingTime() {
        return totalWaitingTime;
    }

    private int totalWaitingTime;

    public Queue(int id){
        this.id=id;
        this.clients =new ArrayBlockingQueue<Client>(Scheduler.maxClientsPerQueue);
        this.waitingPeriod=new AtomicInteger(0);
        this.averageTime=0;
        this.peakTime=0;
        //initializing queue and waiting period
    }

    public void addClient(Client newClient){
        //add task to queue
        //increment the waitingPeriod
        this.clients.add(newClient);
        newClient.setFinishTime(newClient.getArrivalTime()+ newClient.getProcessingTime()+this.waitingPeriod.intValue());
        this.waitingPeriod.set(this.waitingPeriod.get()+ newClient.getProcessingTime());
    }

    public int getNoOfClients(){
        return clients.size();
    }

    public AtomicInteger getWaitingPeriod(){
        return waitingPeriod;
    }

    public double getAverageTime(){
        return this.averageTime;
    }

    public String getString(){
        String string = "Queue #" + this.id;
        for(Client client :this.clients){
            string=string + "\n" + client.toString();
        }
        if(string.equals("Queue #" + this.id)){
            string=string + "\n" + "Closed";
        }
        return string;
    }

    public void run(){
        while(true){
            int noOfClients=0;
            int totalWaitingTime=0;
            //take next task from queue
            //stop the thread for a time = with the task's processing time (sleep 1000*processingTime)
            //decrement the waitingPeriod
            Client client = new Client();
            try {
                client =this.clients.peek();
                if(client !=null){
                    noOfClients++;
                    int processingTime= client.getFinishTime()- client.getArrivalTime();
                    totalWaitingTime+=processingTime;
                    this.totalWaitingTime+=totalWaitingTime;
                    this.averageTime=totalWaitingTime/noOfClients;
                    if(processingTime>this.peakTime){
                        this.peakTime=processingTime;
                    }
                    Thread.sleep(1000* client.getProcessingTime());
                    this.waitingPeriod.addAndGet(-client.getProcessingTime());
                    this.clients.poll();  //retrieve and remove head of queue
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
