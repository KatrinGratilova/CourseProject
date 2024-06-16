package org.katrin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSingleton {
    private static Connection connection;
    private static final String userName = "postgres";
    private static final String password = "230218";
    private static final String connectionUrl = "jdbc:postgresql://localhost:5432/course_project";

    public static Connection getConnection(){
        try {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(connectionUrl, userName, password);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
