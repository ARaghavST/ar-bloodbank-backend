package com.ar.bloodbank.resources;

public class UpdateBodyResource {

    public int id;
    public String blood_group;
    public Double amount;

    public UpdateBodyResource(int id, String bloodGroup, Double amount) {
        this.id = id;
        this.blood_group = bloodGroup;
        this.amount = amount;
    }
}
