package com.common;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {

    private final String username;
    private int password;

    public User(String username, String password) {
        this.username = username;
        this.password = password.hashCode();
    }

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password.hashCode();
    }

    @Override
    public String toString() {
        return "Username: " + username;
    }

    @Override
    public int compareTo(User o) {
        if (o == null)
            return 1;
        return this.username.compareTo(o.username);
    }

    @Override
    public int hashCode(){
        return this.username.hashCode();
    }
}