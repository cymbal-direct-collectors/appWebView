package com.example.rcewebview;

public class User{
    String email;
    String token;
    String rememberMe;
    String password;
    String type;

    public void setEmail(String str){
        email=str;
    }
    public void setToken(String str){
        token=str;
    }
    public void setRememberMe(String str){
        rememberMe=str;
    }
    public void setPassword(String str) {
        password = str;
    }

    public void setType(String str) {
        type = str;
    }

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getRememberMe() {
        return rememberMe;
    }
    public String getToken() {
        return token;
    }
}
