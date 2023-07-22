package hr.java.projektnizadatak.entities;

import java.io.Serializable;

public class ChangeLog implements Serializable {
    private String action;
    private String timestamp;
    private String deletedUser;
    private String deleterUser;
    private UserRole userRole;

    public ChangeLog(String action, String timestamp, String deletedUser, String deleterUser, UserRole userRole) {
        this.action = action;
        this.timestamp = timestamp;
        this.deletedUser = deletedUser;
        this.deleterUser = deleterUser;
        this.userRole = userRole;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeletedUser() {
        return deletedUser;
    }

    public void setDeletedUser(String deletedUser) {
        this.deletedUser = deletedUser;
    }

    public String getDeleterUser() {
        return deleterUser;
    }

    public void setDeleterUser(String deleterUser) {
        this.deleterUser = deleterUser;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
