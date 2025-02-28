
package com.ar.bloodbank.main.resources;

import com.ar.bloodbank.helpers.PasswordEncryptionWithAES;

public class UserResource {
    String name,dob,email,gender,blood_group,password;
    int status,sno;

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public int getStatus() {
        return status;
    }
    long mobile;

    public String getPassword() {
        return PasswordEncryptionWithAES.doEncrypt(password);
    }

   
    public UserResource(String name, String dob, String email, String gender, String password, long mobile,int status,int sno) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        
        // this will encrypt the password as soon as the object of this JAVA class is called
        // one example is when we are creating a new user
        this.password = password;
        this.sno=sno;
        this.mobile = mobile;
        this.status=status;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    

    public long getMobile() {
        return mobile;
    }

  

  
    
}
