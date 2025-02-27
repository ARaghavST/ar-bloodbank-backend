package com.ar.bloodbank.main.resources;

public class JsonResponse {
    
    int status;
    String message,error;
    Object data;
    
    public JsonResponse(int s,String m,String e,Object d){
        this.status=s;
        this.message=m;
        this.error=e;
        this.data=d;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return '{' + "status:" + status + ", message:" + message + ", error:" + error + ", data : "+data.toString()+ '}';
        
        
        /**
         { "status":499,
         * "error":"asbc",
         * "message":"message"
         * }
         
         **/
    }

    public Object getData() {
        return data;
    }
    
}
