
package com.ar.bloodbank.main.resources;


public class DonorResource {
    String donor_id,blood_group,name,email;
    Double amount;
    long phno;

    @Override
    public String toString() {
        return "DonorResource{" + "donor_id=" + donor_id + ", blood_group=" + blood_group + ", name=" + name + ", email=" + email + ", amount=" + amount + ", phno=" + phno + '}';
    }

    public DonorResource(String donorID, String bloodGroup, String name, String email, Double amount, long phno) {
        this.donor_id = donorID;
        this.blood_group = bloodGroup;
        this.name = name;
        this.email = email;
        this.amount = amount;
        this.phno = phno;
    }
    
    
}
