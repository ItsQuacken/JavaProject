module hr.java.projektnizadatak {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.net.http;
	requires com.google.gson;
	requires org.slf4j;
	requires java.sql;
	requires com.h2database;
    requires org.json;
    requires javafx.web;
    requires jdk.jsobject;
	
	opens hr.java.projektnizadatak.presentation to javafx.fxml;
	exports hr.java.projektnizadatak.presentation;
	exports hr.java.projektnizadatak.presentation.views;
	exports hr.java.projektnizadatak.shared.exceptions;
	exports hr.java.projektnizadatak.data;
	opens hr.java.projektnizadatak.presentation.views to javafx.fxml;
    exports hr.java.projektnizadatak.entities;
}
