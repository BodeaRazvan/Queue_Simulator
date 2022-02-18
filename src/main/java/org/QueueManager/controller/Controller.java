package org.QueueManager.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.*;
import java.util.*;

import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import org.QueueManager.model.Scheduler;
import org.QueueManager.model.SelectionPolicy;
import org.QueueManager.model.Queue;
import org.QueueManager.model.Client;

public class Controller implements Runnable{
    public TitledPane UserInterface;
    //declaration of text fields from UI
    @FXML private TextField fieldNoOfClients;
    @FXML private TextField fieldNoOfQueues;
    @FXML private TextField fieldSimulationInterval;
    @FXML private TextField fieldMinArrivalTime;
    @FXML private TextField fieldMaxArrivalTime;
    @FXML private TextField fieldMinServiceTime;
    @FXML private TextField fieldMaxServiceTime;
    @FXML public TextArea  eventLog;

     //data read from UI
     private int noOfClients;
     private int noOfQueues;
     private int simulationInterval;
     private int minArrivalTime;
     private int maxArrivalTime;
     private int minServiceTime;
     private int maxServiceTime;
     public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
     BufferedWriter writer = new BufferedWriter(new FileWriter("Log.txt"));

     private Scheduler scheduler;
     private ArrayList<Client> generatedClients;

    public Controller() throws IOException {
    }

    @FXML
     private void startProgram() throws IOException {
         fetchData();
         this.generatedClients =new ArrayList<Client>();
         generateNRandomClients(noOfClients);
         for(Client client : generatedClients){
             writer.write(client.toString()+"\n");
             eventLog.appendText(client.toString()+"\n");
         }
         writer.write("\nStarting simulation:");
         System.out.println("\nStarting simulation:");
         Controller.this.scheduler=new Scheduler(noOfQueues,noOfClients);

         Thread thread= new Thread(Controller.this);
         thread.start();
     }

    private void fetchData() throws IOException {
        try {
            noOfClients = Integer.parseInt(fieldNoOfClients.getText());
            noOfQueues = Integer.parseInt(fieldNoOfQueues.getText());
            simulationInterval = Integer.parseInt(fieldSimulationInterval.getText());
            minArrivalTime = Integer.parseInt(fieldMinArrivalTime.getText());
            maxArrivalTime = Integer.parseInt(fieldMaxArrivalTime.getText());
            minServiceTime = Integer.parseInt(fieldMinServiceTime.getText());
            maxServiceTime = Integer.parseInt(fieldMaxServiceTime.getText());
        }catch (final NumberFormatException e){
            eventLog.setText("Error Parsing the information, try giving only integer numbers!");
            return;
        }
        if(noOfClients<0 || noOfQueues<0 || simulationInterval<0 || minArrivalTime<0 || maxArrivalTime<0 || minServiceTime<0 || maxServiceTime<0){
            eventLog.setText("Data is not allowed to be <0");
            return;
        }
        if(minArrivalTime>maxArrivalTime || minServiceTime>maxServiceTime){
            eventLog.setText("Minimum time cannot be greater than maximum time");
            return;
        }
        eventLog.clear();
        eventLog.appendText("Starting information:\nNumber of clients: " + noOfClients +"\n");
        eventLog.appendText("Number of queues: " + noOfQueues +"\n");
        eventLog.appendText("Duration of Simulation: " + simulationInterval +"\n");
        eventLog.appendText("Arrival time: [" + minArrivalTime + "," + maxArrivalTime +"]\n");
        eventLog.appendText("Service time: [" + minServiceTime + "," + maxServiceTime +"]\n\nGenerated Clients\n");

        String text=eventLog.getText();
        writer.write(text);
    }

    public void generateNRandomClients(int noOfClients){
        //generate N random clients
        Random random=new Random();
       for(int i=0;i<noOfClients;i++){
           Client client = new Client();
           client.setId(i+1);
           int randomArrivalTime=random.nextInt(maxArrivalTime-minArrivalTime+1)+minArrivalTime;
           client.setArrivalTime(randomArrivalTime);
           int randomServiceTime=random.nextInt(maxServiceTime-minServiceTime+1)+minServiceTime;
           client.setProcessingTime(randomServiceTime);

           this.generatedClients.add(client);
        }
    }

    //This method exceeds the 30 lines rule but most if it is just try catch statements and printing to both log file
    //and text field + client handling
    @Override
    public void run(){
        //iterate generatedTasks list and pick clients that have the arrivalTime=currentTime
        //send client to queue by calling the dispatchTask method from Scheduler
        //delete client from list  --> I used a boolean to check if the clients were served
        int currentTime=0;
        int peakHour=0;
        int maxTasks=0; //used to determine when is the peak hour
        while(currentTime<=simulationInterval){
            try {
                writer.write("\nTime:"+currentTime+"\n");
                writer.write("Waiting Clients:");
                for(Client client :this.generatedClients){
                    if(!client.isWasServed()){
                        writer.write(client.toString()+" ");
                    }
                }
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int finalCurrentTime = currentTime;
            Platform.runLater(() -> eventLog.appendText("\nTime:"+ finalCurrentTime +"\n"));
            Platform.runLater(() -> eventLog.appendText("Waiting Clients:") );
            for(Client client:this.generatedClients){
                if(!client.isWasServed()){
                    Platform.runLater(() -> eventLog.appendText(client.toString()+" "));
                }
            }
            Platform.runLater(() -> eventLog.appendText("\n"));
            System.out.println("Time:"+currentTime);
            for (Client client : this.generatedClients) {
                if (currentTime == client.getArrivalTime()) {
                    this.scheduler.dispatchClients(client);
                    client.setWasServed(true);
                }
            }
            int tasks=0;
            for(Queue queue: scheduler.getQueues()){
                // Platform.runLater(() -> { })
                Platform.runLater(() -> eventLog.appendText(queue.getString()+"\n"));
                tasks+=queue.getNoOfClients();
            }
            if(tasks>maxTasks){
                maxTasks=tasks;
                peakHour=currentTime;
            }
            try{
                for(Queue queue: scheduler.getQueues()){
                    writer.write(queue.getString()+"\n");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            //wait an interval of 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            currentTime++;
        }
        double totalWaitingTime=0;
        double averageTime=0;
        for(Queue queue :scheduler.getQueues()){
            averageTime += queue.getAverageTime();
            totalWaitingTime+=queue.getTotalWaitingTime();
        }
        try {
            writer.write("\nPeak hour (Moment with the highest total waiting time) : " + peakHour);
            writer.write("\nAverage waiting time (Until client gets to the head of the queue): " + averageTime/(double)noOfClients);
            writer.write("\nAverage processing time (Total time from getting to the queue to leaving): " + totalWaitingTime/ (double) noOfClients);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int finalPeakHour = peakHour;
        Platform.runLater(() -> eventLog.appendText("\nPeak hour (Moment with the highest total waiting time): " + finalPeakHour));
        double finalAverageTime = averageTime;
        Platform.runLater(() -> eventLog.appendText("\nAverage waiting time (Until client gets to the head of the queue): " + finalAverageTime /(double)noOfClients));
        double finalTotalWaitingTime = totalWaitingTime;
        Platform.runLater(() -> eventLog.appendText("\nAverage processing time (Total time from getting to the queue to leaving): " + finalTotalWaitingTime / (double) noOfClients));
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //The app stays open for 10 seconds after finishing , then closes the process
        Platform.runLater(() -> eventLog.appendText("\n\nThe app will close in 15 seconds"));
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void updateEventLog(String string){
         eventLog.appendText(string);
    }

}
