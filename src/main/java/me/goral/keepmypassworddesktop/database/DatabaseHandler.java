package me.goral.keepmypassworddesktop.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    public static void connect(){
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:database.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection established");
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
            }
        }
    }
}
