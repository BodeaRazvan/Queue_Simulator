module org.QueueManager {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.QueueManager.controller to javafx.fxml;
    exports org.QueueManager;
    exports org.QueueManager.controller;
    exports org.QueueManager.model;
}