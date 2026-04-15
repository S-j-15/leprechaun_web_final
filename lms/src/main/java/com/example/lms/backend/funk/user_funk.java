package funk;
import models.loan;
import db.db_connector;
import models.payment_strategy;
import models.user;
import java.sql.*;
import java.util.*;
import models.simplePayment;

public class user_funk
{
    private db_connector db=null;

    public user_funk() throws SQLException
    {
        this.db = db_connector.getInstance();
    }
    public double calculateEMI(double principal, int months, double rate) 
    {
        return (principal*rate*Math.pow(1 + rate,months))/(Math.pow(1+rate,months)-1);
    }
    public void applyLoan(user u, double principle,double rate,int months) throws SQLException
    {
        double rem=calculateEMI(principle,months,rate)*months;
        String params[]={String.valueOf(u.id),String.valueOf(principle),String.valueOf(rate),String.valueOf(months),"PENDING",String.valueOf(rem)};
        this.db.update_db("insert into loan (user_id,principal,interest_rate,months,status,remaining) values (?,?,?,?,?,?)",params);
    }

    public List<loan> getMyLoans(user u) throws SQLException
    {  String params[]={u.id.toString()};
       List<loan> lns=new ArrayList<loan>();
       List<Map<String,Object>> ls=this.db.fetch_db("select * from loan where user_id=?",params);
       for(Map<String,Object> curln : ls)
       {
         loan ll=new loan(curln.get("loan_id").toString(),((Number) curln.get("months")).intValue(),((Number) curln.get("principal")).doubleValue(),((Number) curln.get("interest_rate")).doubleValue(),curln.get("status").toString(),((Number) curln.get("remaining")).doubleValue());
         lns.add(ll);
       } 
       return lns;
    }
    public List<loan> getMyLoansWithStatus(user u,String status) throws SQLException
    {
        String params[]={u.id.toString(),status};
       List<loan> lns=new ArrayList<loan>();
       List<Map<String,Object>> ls=this.db.fetch_db("select * from loan where user_id=? and status=?",params);
       for(Map<String,Object> curln : ls)
       {
         loan ll=new loan(curln.get("loan_id").toString(),((Number) curln.get("months")).intValue(),((Number) curln.get("principal")).doubleValue(),((Number) curln.get("interest_rate")).doubleValue(),curln.get("status").toString(),((Number) curln.get("remaining")).doubleValue());
         lns.add(ll);
       } 
       return lns;
    }
    public loan getUserLoan(user u,String lid) throws SQLException
    {
        String params[]={u.id.toString(),lid};
       Map<String,Object> curln=this.db.fetchOne_db("select * from loan where user_id=? and loan_id=?",params);
       if(curln==null)return null;
       loan ll=new loan(curln.get("loan_id").toString(),((Number) curln.get("months")).intValue(),((Number) curln.get("principal")).doubleValue(),((Number) curln.get("interest_rate")).doubleValue(),curln.get("status").toString(),((Number) curln.get("remaining")).doubleValue());
       return ll;
    }
    public boolean payLoan(user u,loan ln,double amount)throws SQLException 
    {
          payment_strategy payment = new simplePayment(u,ln,amount);
          return payment.pay();
    }
  
}
