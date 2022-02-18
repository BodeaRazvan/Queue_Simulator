package org.QueueManager.model;

public class Client {
    private int id;
    private int arrivalTime;
    private int processingTime;
    private int finishTime;
    private boolean wasServed;

    public boolean isWasServed() {
        return wasServed;
    }

    public void setWasServed(boolean wasServed) {
        this.wasServed = wasServed;
    }

    public String toString(){
        return "(" + this.id + "," + this.arrivalTime + "," +this.processingTime + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }
}
