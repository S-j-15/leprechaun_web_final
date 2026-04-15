package controllers;
import funk.user_funk;
import backend.auth;
import models.user;
import models.loan;
import java.sql.*;
import java.util.*;
public class user_controller
{ 
    private user_funk uf=null;
    private user ussr=null;
    public user_controller(user ussr) throws SQLException
    {
        this.uf = new user_funk();
        this.ussr = ussr;
    }
    public void applyLoan(double principle,double rate,int months) throws SQLException
    {
        uf.applyLoan(this.ussr,principle,rate,months);
    }
    public List<loan> getMyLoans() throws SQLException
    {
        return uf.getMyLoans(this.ussr);
    }
    public List<loan> getMyLoansWithStatus(String status)throws SQLException
    {
         return uf.getMyLoansWithStatus(this.ussr,status);
    }
    public boolean payLoan(loan ln,double amount)throws SQLException 
    {
        return uf.payLoan(this.ussr,ln,amount);
    }
    public loan getUserLoan(String lid) throws SQLException
    {
        return uf.getUserLoan(this.ussr,lid);
    }
}
