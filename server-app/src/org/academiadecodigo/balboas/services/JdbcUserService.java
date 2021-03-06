package org.academiadecodigo.balboas.services;

import org.academiadecodigo.balboas.persistance.ConnectionManager;
import org.academiadecodigo.balboas.utils.Security;


import java.sql.*;

/**
 * Created by codecadet on 15/11/17.
 */
public class JdbcUserService implements UserService {

    private Connection dbConnection = null;

    // get the results
    @Override
    public long count() {

        int result = 0;

        // create a new statement
        Statement statement = null;
        try {
            statement = dbConnection.createStatement();

            // create a query
            String query = "SELECT COUNT(*) FROM userData";

            // execute the query
            ResultSet resultSet = null;

            resultSet = statement.executeQuery(query);

            // get the results

            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return result;
    }


    @Override
    public boolean authenticate(String username, String password) {

        System.out.println("Trying to authenticate a user");

        dbConnection = new ConnectionManager().getConnection();
        PreparedStatement statement = null;

        try {

            // create a query
            String query = "SELECT name, password FROM userData WHERE name =? AND password =?";

            // create a new statement
            statement = dbConnection.prepareStatement(query);

            statement.setString(1, username);
            statement.setString(2, Security.getHash(password));

            // execute the query
            ResultSet answer = statement.executeQuery();

            return answer.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    @Override
    public boolean addUser(String username, String password, String email) {

        if (findByName(username)) {
            return false;
        }

        dbConnection = new ConnectionManager().getConnection();
        try {
            // create a new statement
            Statement statement = dbConnection.createStatement();

            // create a query
            String query = "INSERT INTO userData(name, password, email, life, strength) VALUES ('" + username + "','" + Security.getHash(password) + "','" + email + "'," + 0 + "," + 0 + ")";

            // execute the query
            statement.executeUpdate(query);

            System.out.println("User added");


            statement.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public boolean findByName(String username) {

        dbConnection = new ConnectionManager().getConnection();

        try {

            // create a query
            String query = "SELECT name FROM userData WHERE name = ?";

            // create a new statement
            PreparedStatement statement = dbConnection.prepareStatement(query);

            statement.setString(1, username);

            // execute the query
            ResultSet resultSet = statement.executeQuery();

            // user exists
            if (resultSet.next()) {
                statement.close();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public void registerData(String name, String gym, String diet, String beer, String smoke) {

        dbConnection = new ConnectionManager().getConnection();
        try {
            // create a new statement
            Statement statement = dbConnection.createStatement();

            // create a query
            String query = "INSERT INTO userRecords(name, gym, diet, beer, smoke) VALUES ('" + name + "','" + gym + "','" + diet + "','" + beer + "','" + smoke + "')";

            // execute the query
            int i = statement.executeUpdate(query);

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLife(String name) {

        dbConnection = new ConnectionManager().getConnection();

        try {

            // create a query
            String query = "SELECT life FROM userData WHERE name = ?";

            // create a new statement
            PreparedStatement statement = dbConnection.prepareStatement(query);

            statement.setString(1, name);

            // execute the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("life");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getStrength(String name) {

        dbConnection = new ConnectionManager().getConnection();

        try {

            // create a query
            String query = "SELECT strength FROM userData WHERE name = ?";

            // create a new statement
            PreparedStatement statement = dbConnection.prepareStatement(query);

            statement.setString(1, name);

            // execute the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("strength");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
