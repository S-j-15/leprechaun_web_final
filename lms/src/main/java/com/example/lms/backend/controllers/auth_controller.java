package controllers;
import backend.auth;
import models.user;
import java.sql.*;
public class auth_controller
{  
    private auth au=null;
    public auth_controller() throws SQLException
    {
        this.au=new auth();
    }
    public boolean signup(String name,String passw)throws SQLException
    {
        String typ="USER";
        user u=new user(name,typ);
        return this.au.signup(u,passw);
    }
    public user login(String name,String passw)throws SQLException
    {
        return this.au.login(name,passw);
    }
}
