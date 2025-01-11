
package com.ar.bankblood.main.resources;

import com.ar.bloodbank.constants.PasswordEncryptionWithAES;

public class UserResource {
    String name,dob,email,gender,blood_group,password;
    long mobile;

    public String getPassword() {
        return password;
    }

   
    public UserResource(String name, String dob, String email, String gender, String blood_group, String password, long mobile) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.blood_group = blood_group;
        
        // this will encrypt the password as soon as the object of this JAVA class is called
        // one example is when we are creating a new user
        this.password = PasswordEncryptionWithAES.doEncrypt(password);
        this.mobile = mobile;
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

    public String getBlood_group() {
        return blood_group;
    }

    public long getMobile() {
        return mobile;
    }

    @Override
    public String toString() {
        return "UserResource{" + "name=" + name + ", dob=" + dob + ", email=" + email + ", gender=" + gender + ", blood_group=" + blood_group + ", password=" + password + ", mobile=" + mobile + '}';
    }

  
    
}
