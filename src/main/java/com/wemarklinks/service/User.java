package com.wemarklinks.service;

public class User {

    String userId;
    String name;
    String password;
    int privilege;
    boolean enabled;
    
    @Override
    public String toString() {
        return "User [userId=" + userId + ", name=" + name + ", password=" + password + ", privilege=" + privilege
                + ", enabled=" + enabled + "]";
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPrivilege() {
        return privilege;
    }
    
    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }
}
