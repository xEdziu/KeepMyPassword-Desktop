package me.goral.keepmypassworddesktop.database;
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
            e.printStackTrace();
            System.out.println(e.getMessage());
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
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                  id integer PRIMARY KEY,
                  username varchar(255) NOT NULL,
                  password varchar(1000) NOT NULL);
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User table created");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void createPasswordsTable(){
        String sql = """
                CREATE TABLE IF NOT EXISTS passwords (
                  id integer PRIMARY KEY,
                  desc varchar(255) NOT NULL,
                  login varchar(255) NOT NULL,
                  pwd varchar(1000) NOT NULL,
                  iv varchar(1000) NOT NULL);
                """;

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Passwords table created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertUser(String uname, String pwd){
        String sql = "INSERT INTO users (username, password) VALUES (?,?);";
        try (Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1, uname);
            preparedStatement.setString(2, pwd);
            preparedStatement.executeUpdate();
            System.out.println("User inserted into database");
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static boolean checkLogin(String uname, String pwd){
        String sql = "SELECT password FROM users WHERE username = ?";
        boolean flag = false;
        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql);){

            stmt.setString(1, uname);
            ResultSet resultSet = stmt.executeQuery();
            String pwdBase = null;

            while (resultSet.next()){
                pwdBase = resultSet.getString("password");
            }

            assert pwdBase != null;
            // remember about hashes
            if (pwdBase.equals(pwd)) flag = true;

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return flag;
    }

    public static void insertPassword(String desc, String login, String pwd){
        String sql = "INSERT INTO passwords (desc, login, pwd) VALUES (?,?,?);";
        try (Connection conn = connect();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setString(1, desc);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, pwd);
            preparedStatement.executeUpdate();
            System.out.println("Password inserted into database");
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static List<List<String>> selectPasswords(){
        String sql = "SELECT desc, login, pwd FROM passwords";
        List<List<String>> results = new ArrayList<>();
        try (Connection conn = connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                List<String> single = new ArrayList<>();
                single.add(rs.getString("desc"));
                single.add(rs.getString("login"));
                single.add(rs.getString("pwd"));
                results.add(single);
            }

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return results;
    }
}