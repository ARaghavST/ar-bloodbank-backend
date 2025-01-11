package com.ar.bloodbank.constants;

import java.sql.Connection;


public class Constants {
    
    public MysqlConnectionObject getMySqlConnectionObject(Connection c,String s){
        return new MysqlConnectionObject(c,s);
    }
    
    
}
