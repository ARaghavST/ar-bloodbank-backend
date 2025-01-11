

package com.ar.bloodbank.constants;

public class ReturnObject {
    Object object;
    String error;
    
    public ReturnObject(Object o,String e){
        this.object=o;
        this.error=e;
    }

    public Object getObject() {
        return object;
    }

    public String getError() {
        return error;
    }
    
    
}