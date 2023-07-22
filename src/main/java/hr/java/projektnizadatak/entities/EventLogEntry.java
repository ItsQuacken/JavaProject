package hr.java.projektnizadatak.entities;

import java.time.LocalDateTime;

public class EventLogEntry {
    private LocalDateTime timestamp;
    private String level;
    private String thread;
    private String logger;
    private String file;
    private int line;
    private String message;

    public EventLogEntry(String message) {
        this.message = message;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
