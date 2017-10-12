/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 *
 * @author GHANSHYAM
 */

class MyExc extends Exception {

    public MyExc(String message) {
        super(message);
    }
    
}
public class DbConnect {
    
    static Connection conn = null;
    static Statement stmt = null;
    static ResultSet rs = null;
    static PreparedStatement pStmt = null;
    
    private static void createConnection() {
        try {
            String DATABASE = "collegedb";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE, "root", "");
        } catch(SQLException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            conn = null;
        }
    }
    
    public static ResultSet selectQuery(String query) {
        try {
            createConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            
        } catch (SQLException ex) {
            rs = null;
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
    
    public static boolean insertQuery(List<String> userDetails) {
        createConnection();
        try {
            String query = "insert into userdetails values (?, ?, ?)";
            pStmt = conn.prepareStatement(query);
            for (int i = 1; i <= userDetails.size(); i++) {
                pStmt.setString(i, userDetails.get(i - 1));
            }
            int rValue = pStmt.executeUpdate();
            //System.out.println(rValue);
            closeConnection(rs);
            return (rValue == 1);
            //return false;
        } catch (SQLException ex) {
            try {
                throw new Exception("User with same name already exists.");
                //Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex1) {
                Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return false;
        }
    }
    
    public static void closeConnection(ResultSet rSet) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            if (rs != null) rs.close();
            if (rSet != null) rSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}