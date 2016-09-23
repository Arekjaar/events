/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webservices.events;

import javax.mail.PasswordAuthentication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author martin.martorellcuco
 */
public class DBconector {
    Connection connection = null;
    public DBconector(){
        conectionDB();
    }
    
    public Connection conectionDB(){
        
        try {
            Class.forName("org.postgresql.Driver");
            try {
                connection = DriverManager.getConnection("jdbc:derby://localhost:1527/EventsUsers","arekjaar","arekjaar");
                
                //Logger.getLogger(EventsWebService.class.getName()).log(Level.SEVERE, null,"ESTOY AQUI--> "+);
            } catch (SQLException ex) {
                Logger.getLogger(EventsWebService.class.getName()).log(Level.SEVERE, null, ex);
            }
            //connection.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EventsWebService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
    
    public String setNewUser(String... args){
        String ret = "mail already exist";
        if(getMail(args[0]).equals("ok")){
            try {
                String pas = calculatePasIni(args[1].toCharArray());
                Statement stmt = connection.createStatement();
                String strSelect = "INSERT INTO AREKJAAR.USERS(mail, password, name, sex, bornDate, s_password) VALUES ('"+args[0]+"', '"+pas+"', '"+args[2]+"', '"+args[3]+"', '"+args[4]+"', '"+args[1]+"')";
                Boolean rset = stmt.execute(strSelect);
                ret = "fail";
                if(!rset) {   // Move the cursor to the next row
                    ret = "{mail:"+args[0]+",password:"+args[1]+"}";
                }
                new MailClient().mail(args[0], pas, args[1]);
            } catch (SQLException ex) {
                Logger.getLogger(DBconector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    public String setUserLogin(String mail, String pas){
        String ret = "Ok";
        try {
            Statement stmt = connection.createStatement();
            String strSelect = "SELECT * FROM AREKJAAR.USERS where mail = '"+mail+"'";
            ResultSet rs = stmt.executeQuery(strSelect);
            if(rs==null || !rs.next()){
                ret = "Usuario no encontrado";
            }else{
                String password = "";
                String s_password = "";
                password = rs.getString("password");
                s_password = rs.getString("s_password");
                if(!password.equals(pas)){
                    ret = "fail";
                }else{
                    if(!password.equals(s_password)){
                        updatePass(s_password);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBconector.class.getName()).log(Level.SEVERE, null, ex);
            ret = "Error";
        }
        return ret;
    }
    
    public void updatePass(String pas){
        try {
            Statement stmt = connection.createStatement();
            String strSelect = "UPDATE AREKJAAR.USERS SET password = '"+pas+"'";
            Boolean rset = stmt.execute(strSelect);
        } catch (SQLException ex) {
            Logger.getLogger(DBconector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String calculatePasIni(char[] pas){
        int code = (int)pas[0];
        code += (int)pas[1];
        code += (int)pas[2];
        code += (int)pas[3];
        code += (int)pas[4];
        code += (int)pas[5];
        return String.valueOf(code);
    }
    
    public String getMail(String mail){
        String ret = "mail already exist";
        try {
            Statement stmt = connection.createStatement();
            String strSelect = "SELECT mail FROM AREKJAAR.USERS where mail = '"+mail+"'";
            ResultSet rset = stmt.executeQuery(strSelect);
             
            if(rset==null || !rset.next()){
                ret = "ok";
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBconector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public void clossConectionDB(){
        
    }

}

final class MailClient
{
    private class SMTPAuthenticator extends Authenticator
    {
        private PasswordAuthentication authentication;

        public SMTPAuthenticator(String login, String password)
        {
             authentication = new PasswordAuthentication(login, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
             return authentication;
        }
    }

    public void mail(String mailto, String pasinit, String userpas)
    {
        System.setProperty("java.net.preferIPv4Stack" , "true");
        String from = "arekjaarevents@gmail.com";
        String to = mailto;
        String subject = "Register succes";
        String message = "Mail: "+mailto +"\n Password init: "+pasinit+"\n Youre password: "+userpas;
        final String login = "arekjaarevents@gmail.com";
        final String password = "eventsarekjaar";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "465"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
            props.put("mail.smtp.ssl.enable", true);
         Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, password);
                }
            };
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);
        try
        {
            msg.setText(message);
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            Transport.send(msg);
        }
        catch (MessagingException ex)
        {
            Logger.getLogger(MailClient.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
