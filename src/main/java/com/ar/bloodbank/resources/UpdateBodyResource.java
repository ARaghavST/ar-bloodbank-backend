package com.ar.bloodbank.resources;

public class UpdateBodyResource {

    public int id;
    public String blood_group;
    public Double amount;
    public String email, name;

    public UpdateBodyResource(int id, String bloodGroup, Double amount, String name, String email) {
        this.id = id;
        this.blood_group = bloodGroup;
        this.amount = amount;
        this.email = email;
        this.name = name;
    }
}
