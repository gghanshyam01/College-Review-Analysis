/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
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
    
    public static boolean registerUser(List<String> userDetails) throws Exception {
        createConnection();
        try {
            String query = "insert into userdetails values (?, ?, ?)";
            pStmt = conn.prepareStatement(query);
            for (int i = 1; i <= userDetails.size(); i++) {
                pStmt.setString(i, (i == userDetails.size())? generateHash(userDetails.get(i - 1)) : userDetails.get(i - 1));
            }
            int rValue = pStmt.executeUpdate();
            //System.out.println(rValue);
            closeConnection(rs);
            return (rValue == 1);
            //return false;
        } catch (MySQLIntegrityConstraintViolationException ex) {
                throw new Exception("User with same name already exists.");
        }
    }

    public static String generateHash(String input) throws Exception {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
			for (byte b : hashedBytes) {
				hash.append(HEX_CHARS[(b & 0xF0) >> 4]);
				hash.append(HEX_CHARS[b & 0x0F]);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("Error storing into database.");
		}

		return hash.toString();
	}
    
    
    public static boolean verifyUser(String username, String password) {
        createConnection();
        try {
            String query = "select Email from userdetails where Username=binary ? and Password= binary ?";
            pStmt = conn.prepareStatement(query);
            pStmt.setString(1, username);
            pStmt.setString(2, generateHash(password));
            rs = pStmt.executeQuery();
            boolean isAuthentic = rs.next();
            closeConnection(rs);
            return isAuthentic;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static void closeConnection(ResultSet rSet) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
            if (rSet != null) rSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}