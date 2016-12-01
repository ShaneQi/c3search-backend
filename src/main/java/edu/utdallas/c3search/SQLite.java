package edu.utdallas.c3search;

import java.sql.*;

/**
 * Created by Shadow on 11/30/16.
 */
public class SQLite
{
    Connection connection;
    Statement statement;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:/Users/Shadow/Desktop/db.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SQLite() {
        try
        {
            Class.forName("org.sqlite.JDBC");
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/Shadow/Desktop/db.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(5);  // set timeout to 30 sec.
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public void executeUpdate(String query) {
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}