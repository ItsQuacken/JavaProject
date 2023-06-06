module com.example.projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires com.h2database;
    requires javafx.web;
    requires java.logging;
    requires jdk.jsobject;
    requires java.scripting;
    requires org.json;


    opens hr.java.projekt.glavna to javafx.fxml;
    exports hr.java.projekt.entities;
    exports hr.java.projekt.glavna;
    exports hr.java.projekt.glavna.controllers;
    opens hr.java.projekt.glavna.controllers to javafx.fxml;
}