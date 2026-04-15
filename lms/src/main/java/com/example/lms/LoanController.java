package com.example.lms.controller;

import com.example.lms.service.evilFacade;
import models.loan;
import models.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
public class LoanController {

    @Autowired
    private evilFacade facade;

    // ─── AUTH ────────────────────────────────────────────────────────────────

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password) {
        try {
            return facade.signup(username, password) ? "SUCCESS" : "USERNAME EXISTS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        try {
            user you = facade.login(username, password);
            if (you == null) return "INVALID";
            session.setAttribute("user", you);
            return "SUCCESS:" + you.type;
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "LOGGED OUT";
    }

    // ─── USER ────────────────────────────────────────────────────────────────

    @PostMapping("/apply-loan")
    public String applyLoan(@RequestParam double amount,
                            @RequestParam int months,
                            HttpSession session) {
        try {
            user you = (user) session.getAttribute("user");
            if (you == null) return "NOT LOGGED IN";
            double rate = months < 6 ? 0.12 : 0.10;
            facade.applyLoan(amount, rate, months);
            return "LOAN APPLIED";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @GetMapping("/my-loans")
    public String myLoans(HttpSession session) {
        try {
            user you = (user) session.getAttribute("user");
            if (you == null) return "NOT LOGGED IN";
            String[] stats = {"PENDING", "APPROVED", "REJECTED", "CLOSED", "DEFAULTED"};
            StringBuilder res = new StringBuilder();
            for (String s : stats) {
                List<loan> lnz = facade.getMyLoansWithStatus(s);
                res.append("STATUS: ").append(s).append("\n");
                for (loan l : lnz) {
                    res.append(l.loan_id).append(" ")
                       .append(l.months).append(" ")
                       .append(l.principle).append(" ")
                       .append(l.rate).append(" ")
                       .append(l.remaining).append("\n");
                }
                res.append("\n");
            }
            return res.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/pay-loan")
    public String payLoan(@RequestParam String loan_id,
                          @RequestParam double amount,
                          HttpSession session) {
        try {
            user you = (user) session.getAttribute("user");
            if (you == null) return "NOT LOGGED IN";
            loan l = facade.getUserLoan(loan_id);
            if (l == null) return "NOT FOUND";
            if (!l.status.toString().equals("APPROVED")) return "NOT PAYABLE";
            return facade.payLoan(l, amount) ? "SUCCESS" : "FAIL";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────

    @PostMapping("/admin/add")
    public String adminAdd(@RequestParam String username,
                           @RequestParam String password,
                           HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            return facade.addNewAdmin(username, password) ? "ADDED" : "FAIL";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/admin/remove")
    public String adminRemove(@RequestParam String username,
                              HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            user target = facade.getUser(username);
            return facade.removeAdmin(target) ? "REMOVED" : "FAIL";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/admin/make-admin")
    public String makeAdmin(@RequestParam String username,
                            HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            user target = facade.getUser(username);
            return facade.makeAdmin(target) ? "DONE" : "FAIL";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/admin/terminate")
    public String terminate(@RequestParam String username,
                            HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            user target = facade.getUser(username);
            return facade.terminateAccount(target) ? "TERMINATED" : "FAIL";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @GetMapping("/admin/users")
    public String adminUsers(HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            List<user> users = facade.getAllUsers();
            StringBuilder res = new StringBuilder();
            for (user u : users) {
                res.append(u.id).append(" ")
                   .append(u.username).append(" ")
                   .append(u.type).append("\n");
            }
            return res.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @GetMapping("/admin/user-loans")
    public String userLoans(@RequestParam String username,
                            HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            user target = facade.getUser(username);
            List<loan> loans = facade.getLoansOfUser(target);
            StringBuilder res = new StringBuilder();
            for (loan l : loans) {
                res.append(l.loan_id).append(" ")
                   .append(l.months).append(" ")
                   .append(l.principle).append(" ")
                   .append(l.rate).append(" ")
                   .append(l.remaining).append(" ")
                   .append(l.status).append("\n");
            }
            return res.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @GetMapping("/admin/pending-loans")
    public String pendingLoans(@RequestParam String username,
                               HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            user target = facade.getUser(username);
            List<loan> loans = facade.getLoansOfUserWithStatus(target, "PENDING");
            StringBuilder res = new StringBuilder();
            for (loan l : loans) {
                res.append(l.loan_id).append(" ")
                   .append(l.months).append(" ")
                   .append(l.principle).append(" ")
                   .append(l.rate).append(" ")
                   .append(l.remaining).append("\n");
            }
            return res.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/admin/loan-action")
    public String loanAction(@RequestParam String loan_id,
                             @RequestParam String action,
                             HttpSession session) {
        try {
            if (session.getAttribute("user") == null) return "NOT LOGGED IN";
            loan l = facade.getLoanById(loan_id);   // uses adminController, not userController
            if (l == null) return "NOT FOUND";
            if (action.equals("accept")) facade.approveLoan(l);
            else facade.rejectLoan(l);
            return "DONE";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
