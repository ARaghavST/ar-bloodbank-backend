
package com.ar.bloodbank.constants;

import java.sql.Connection;

// This class is used because we wanted two values in return from DatabaseConnect's ConnectAndReturnConnection function
// SO using this as an encapsulation
public class MysqlConnectionObject {
      
    public Connection connection;
    public String error;
   
    
    public MysqlConnectionObject(Connection c , String e){
        this.connection=c;
        this.error=e;
    }
  
}
