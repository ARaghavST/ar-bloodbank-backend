package com.ar.bloodbank.main.resources;


public class AdminResource {

    public AdminResource(String email, String password, String name, int sno) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.sno = sno;
    }
    
       String email,password,name;
       int sno;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }
       
}
