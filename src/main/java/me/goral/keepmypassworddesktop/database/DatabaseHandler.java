package me.goral.keepmypassworddesktop.database;
import me.goral.keepmypassworddesktop.util.AlertsUtil;
import me.goral.keepmypassworddesktop.util.ConfUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    /**
     * Connect to the database and return the connection
     * 
     * @return The connection object.
     */
    private static Connection connect(){
        Connection conn = null;
        String url = "jdbc:sqlite:"+ ConfUtil.getWorkingDirectory() +"database.db";
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return conn;
    }

    /**
     * Create a database if it doesn't already exist
     */
    public static void createDatabase(){
        try (Connection conn = connect()) {
            if (conn != null){
                DatabaseMetaData metaData = conn.getMetaData();
            }
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    /**
     * Create the main table if it doesn't exist
     */
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
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    /**
     * Insert a new password into the database
     * 
     * @param desc description of the password
     * @param login the login name of the user
     * @param pwd the password to be stored
     * @param iv the initialization vector used to encrypt the password.
     * @return Returns boolean value.
     */
    public static boolean insertPassword(String desc, String login, String pwd, String iv){
        String sql = "INSERT INTO main (desc, login, pwd, iv) VALUES (?,?,?,?);";
        try (Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1, desc);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, pwd);
            preparedStatement.setString(4, iv);
            int res = preparedStatement.executeUpdate();
            if (res == 1) return true;
        } catch (SQLException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return false;
    }

    /**
     * `truncateData()`: Deletes all rows from the `main` table
     */
    public static void truncateData() {
        String sql = "DELETE FROM main;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
    }

    /**
     * It selects all passwords from the database.
     * 
     * @return A list of lists. Each list contains the id, description, login, password, and iv.
     */
    public static List<List<String>> selectPasswords(){
        String sql = "SELECT id, desc, login, pwd, iv FROM main";
        List<List<String>> results = new ArrayList<>();
        try (Connection conn = connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                List<String> single = new ArrayList<>();
                int id = rs.getInt("id");
                single.add(String.valueOf(id));
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

    /**
     * Update the password for a given entry
     * 
     * @param desc the description of the password
     * @param login the login name of the user
     * @param pwd the password to be encrypted
     * @param iv the initialization vector used to encrypt the password.
     * @param id the id of the record to update
     * @return Returns boolean value.
     */
    public static boolean updatePassword(String desc, String login, String pwd, String iv, int id) {
        String sql = "UPDATE main SET " +
                "desc = ?, login = ?, pwd = ?, iv = ? " +
                "WHERE id = ?;";
        try (Connection conn = connect()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, desc);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, pwd);
            preparedStatement.setString(4, iv);
            preparedStatement.setInt(5, id);
            int res = preparedStatement.executeUpdate();
            if (res == 1) return true;
            else System.out.println(res);
        } catch (SQLException e) {
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return false;
    }

    /**
     * Delete a password from the database
     * 
     * @param id The id of the password to delete.
     * @return Returns boolean value.
     */
    public static boolean deletePassword(String id) throws SQLException {
        String sql = "DELETE FROM main WHERE id = ?";
        try (Connection conn = connect()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            int res = stmt.executeUpdate();
            if (res == 1) return true;
        } catch (SQLException e){
            AlertsUtil.showExceptionStackTraceDialog(e);
        }
        return false;
    }
}