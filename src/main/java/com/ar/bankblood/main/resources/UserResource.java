
package com.ar.bankblood.main.resources;

import com.ar.bloodbank.constants.PasswordEncryptionWithAES;

public class UserResource {
    String name,dob,email,gender,blood_group,password;
    int status;

    public int getStatus() {
        return status;
    }
    long mobile;

    public String getPassword() {
        return PasswordEncryptionWithAES.doEncrypt(password);
    }

   
    public UserResource(String name, String dob, String email, String gender, String password, long mobile,int status) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        
        // this will encrypt the password as soon as the object of this JAVA class is called
        // one example is when we are creating a new user
        this.password = password;
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
