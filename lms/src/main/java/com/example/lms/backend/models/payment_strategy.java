package models;
import java.sql.SQLException;
public interface payment_strategy
{
    boolean isApproved() throws SQLException;
    boolean pay() throws SQLException;
}
