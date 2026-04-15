package com.example.lms.service;
import controllers.admin_controller;
import controllers.auth_controller;
import controllers.user_controller;
import models.loan;
import models.user;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class evilFacade {

    private auth_controller authController;
    private user_controller userController;
    private admin_controller adminController;

    public evilFacade() throws SQLException 
    {
        this.authController=new auth_controller();
        this.userController=null; 
        this.adminController=null;
    }
    public boolean signup(String username, String password) throws SQLException 
    {
        return authController.signup(username, password);
    }

    public user login(String username, String password) throws SQLException 
    {
        user u=authController.login(username, password);
        if (u != null) {
            this.userController=new user_controller(u);
            if ("ADMIN".equals(u.type)) {
                this.adminController=new admin_controller(u);
            }
        }
        return u;
    }
    public void applyLoan(double principle, double rate, int months) throws SQLException {
        userController.applyLoan(principle, rate, months);
    }

    public List<loan> getMyLoans() throws SQLException {
        return userController.getMyLoans();
    }

    public List<loan> getMyLoansWithStatus(String status) throws SQLException {
        return userController.getMyLoansWithStatus(status);
    }

    public boolean payLoan(loan ln, double amount) throws SQLException {
        return userController.payLoan(ln, amount);
    }

    public loan getUserLoan(String lid) throws SQLException {
        return userController.getUserLoan(lid);
    }

    public boolean addNewAdmin(String username, String password) throws SQLException {
        if (adminController==null) return false;
        return adminController.addNewAdmin(username, password);
    }
    
    public boolean makeAdmin(user u) throws SQLException {
        if (adminController == null) return false;
        return adminController.makeAdmin(u);
    }

    public boolean removeAdmin(user u) throws SQLException {
        if (adminController == null) return false;
        return adminController.removeAdmin(u);
    }

    public boolean terminateAccount(user u) throws SQLException {
        if (adminController == null) return false;
        return adminController.terminateAccount(u);
    }

    public List<user> getAllUsers() throws SQLException {
        if (adminController == null) return null;
        return adminController.getAllUsers();
    }
    
    public user getUser(String username) throws SQLException
    {
        if (adminController == null) return null;
        return adminController.getUser(username);
    }

    public List<loan> getLoansOfUser(user u) throws SQLException {
        if (adminController == null) return null;
        return adminController.getLoansOfUser(u);
    }

    public boolean approveLoan(loan ln) throws SQLException {
        if (adminController == null) return false;
        return adminController.approveLoan(ln);
    }

    public boolean rejectLoan(loan ln) throws SQLException {
        if (adminController == null) return false;
        return adminController.rejectLoan(ln);
    }

     public List<loan> getLoansOfUserWithStatus(user u, String status) throws SQLException
    {
        if (adminController == null) return null;
        return adminController.getLoansOfUserWithStatus(u, status);
    }
    public loan getLoanById(String lid) throws SQLException
    {
        if (adminController == null) return null;
        return adminController.get_loan_from_loanId(lid);
    }
}
