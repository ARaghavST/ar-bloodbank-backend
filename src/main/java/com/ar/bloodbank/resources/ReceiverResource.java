package com.ar.bloodbank.resources;

public class ReceiverResource {

    public int sno, status;
    public Double quantity;
    public String name, phno, email, aadhar, bg_needed;
    public String req_date, rec_date;

    public ReceiverResource(int sno, int status, String name, String phno, String email, String aadhar, String bg_needed, String req_date, String rec_date,Double quantity) {
        this.sno = sno;
        this.status = status;
        this.name = name;
        this.phno = phno;
        this.email = email;
        this.aadhar = aadhar;
        this.bg_needed = bg_needed;
        this.req_date = req_date;
        this.rec_date = rec_date;
        this.quantity=quantity;
    }

    
}
