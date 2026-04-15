package models;
import db.db_connector;
import java.sql.SQLException;
import java.util.*;
public class simplePayment implements payment_strategy
{
    private user u=null;
    private loan ln=null;
    Map<String,Object> lm;
    double amount=0;
    db_connector db=null;
    public simplePayment(user u,loan ln,double amount) throws SQLException 
    {
        this.u=u;
        this.ln=ln;
        this.amount=amount;
        db=db_connector.getInstance();
    }
    @Override
   public boolean isApproved() throws SQLException 
   {
       
         String params[]={u.id.toString(),ln.loan_id.toString(),"APPROVED"};
          lm=db_connector.getInstance().fetchOne_db("select * from loan where user_id=? and loan_id=? and status=?",params);
          if(lm==null)
          {
            return false;
          }
          return true;
   }
   public boolean pay() throws SQLException 
   {
      if(this.isApproved()==false)return false;
      double rem=((Number) lm.get("remaining")).doubleValue();
          rem-=amount;
          if(rem<=0)
          {
            String np[]={String.valueOf(0),"CLOSED",ln.loan_id.toString()};
            this.db.update_db("update loan set remaining=?, status=?, closed_at=CURRENT_TIMESTAMP where loan_id=?",np);
            return true;
          }
          String np[]={String.valueOf(rem),ln.loan_id.toString()};
          this.db.update_db("update loan set remaining=? where loan_id=?",np);
          return true;
   }
}
