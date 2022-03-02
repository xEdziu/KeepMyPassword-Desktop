package me.goral.keepmypassworddesktop.database;
import me.goral.keepmypassworddesktop.util.AlertsUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static Connection connect(){
        Connection conn = null;
        String url = "jdbc:sqlite:database.db";
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection established");
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return conn;
    }

    public static void createDatabase(){
        String url = "jdbc:sqlite:database.db";
        try (Connection conn = connect()) {
            if (conn != null){
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("DB driver name: " + metaData.getDriverName());
                System.out.println("Database created");
            }
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static void createMainTable(){
        String sql = """
                CREATE TABLE IF NOT EXISTS main (
                  id integer PRIMARY KEY,
                  desc varchar(255) NOT NULL,
                  login varchar(255) NOT NULL,
                  pwd varchar(1000) NOT NULL,
                  iv varchar(1000) NOT NULL);
                """;

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Main table created");
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static void insertPassword(String desc, String login, String pwd, String iv){
        String sql = "INSERT INTO main (desc, login, pwd) VALUES (?,?,?,?);";
        try (Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1, desc);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, pwd);
            preparedStatement.setString(4, iv);
            // Base64.getEncoder().encodeToString(AESUtil.generateIv().getIV())
            preparedStatement.executeUpdate();
            System.out.println("Password inserted into database");
        } catch (SQLException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static void truncateData() {
        String sql = "DELETE FROM main;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    public static List<List<String>> selectPasswords(){
        String sql = "SELECT desc, login, pwd, iv FROM main";
        List<List<String>> results = new ArrayList<>();
        try (Connection conn = connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                List<String> single = new ArrayList<>();
                single.add(rs.getString("desc"));
                single.add(rs.getString("login"));
                single.add(rs.getString("pwd"));
                single.add(rs.getString("iv"));
                results.add(single);
            }

        } catch (SQLException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return results;
    }
}