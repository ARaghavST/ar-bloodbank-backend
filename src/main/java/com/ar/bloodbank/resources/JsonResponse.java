package com.ar.bloodbank.resources;

// this class is used in our project to send response in following format -> 

/**
    {
 * "status":200,
 * "message":"OKay",
 * "error":null,
 * "data":<any-datatype>
 * 
 * }
 
 */
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
}
