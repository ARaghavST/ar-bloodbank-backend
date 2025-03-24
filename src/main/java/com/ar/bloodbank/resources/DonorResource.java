package com.ar.bloodbank.resources;

public class DonorResource {

    public int id, e_ready, status;
    public String name, dob, gender, blood_group, email, phno, last_donation, availability;
    public String reg_on, req_on;

    public DonorResource(int id, int e_ready, int status, String name, String dob, String gender, String blood_group, String email, String phno, String last_donation, String availability, String reg_on, String req_on) {
        this.id = id;
        this.e_ready = e_ready;
        this.status = status;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.blood_group = blood_group;
        this.email = email;

        this.phno = phno;
        this.last_donation = last_donation;
        this.availability = availability;
        this.reg_on = reg_on;
        this.req_on = req_on;
    }

    @Override
    public String toString() {
        return "DonorResource{" + "id=" + id + ", e_ready=" + e_ready + ", status=" + status + ", name=" + name + ", dob=" + dob + ", gender=" + gender + ", blood_group=" + blood_group + ", email=" + ", phno=" + phno + ", last_donation=" + last_donation + ", availability=" + availability + ", reg_on=" + reg_on + '}';
    }

}
