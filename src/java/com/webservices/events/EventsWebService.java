/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webservices.events;

import java.text.SimpleDateFormat;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author martin.martorellcuco
 */
@WebService(serviceName = "EventsWebService")
@Stateless()
public class EventsWebService {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        
        
        //DBconector d = new DBconector();
        return "Hello " + txt + " !";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "userRegist")
    public String userRegist(@WebParam(name = "mail") String mail, @WebParam(name = "password") String password, @WebParam(name = "name") String name, @WebParam(name = "sex") String sex, @WebParam(name = "bornDate") String bornDate) {
        //TODO write your implementation code here:
       
        DBconector d = new DBconector();
        return d.setNewUser(mail,password,name,sex,bornDate);//"Ok";
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "userLogin")
    public String userLogin(@WebParam(name = "mail") String mail, @WebParam(name = "password") String password) {
        //TODO write your implementation code here:
       
        DBconector d = new DBconector();
        return d.setUserLogin(mail,password);//"Ok";
    }
    
}
