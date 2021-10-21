package com.geekbrains.netty;

import java.sql.*;

public class SQLhandler {
    private static Connection connection;
    private static PreparedStatement psRegistration;
    private static PreparedStatement psAuthorization;

    public static boolean connect (){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:authReg.db");
            prepareAllStatements();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void prepareAllStatements() throws SQLException {

        psRegistration= connection.prepareStatement("INSERT INTO users(login,password) VALUES ( ?,?);" );
        psAuthorization=connection.prepareStatement("SELECT * FROM users WHERE login = ?;");

    }
    public boolean authorization (String login, String password){
        try {
            psAuthorization.setString(1,login);
            ResultSet rs = psAuthorization.executeQuery();
            if(rs.next()){
                if (password.equals(rs.getString("password"))){
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
            return false;
    }

    public boolean registration(String login, String password){

        try {
            psRegistration.setString(1,login);
            psRegistration.setString(2,password);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
