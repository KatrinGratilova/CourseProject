package org.katrin;

import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("Hello world!");
        String userName = "postgres";
        String password = "230218";
        String connectionUrl = "jdbc:postgresql://localhost:5432/course_project";
        Class.forName("org.postgresql.Driver");
        Scanner sc = new Scanner(System.in);

        System.out.println("Input numb");
        String numb = sc.nextLine();
        System.out.println("Input pass");
        String pass = sc.nextLine();
        System.out.println("Input name");
        String name = sc.nextLine();

        String s = "INSERT INTO client (full_name, contact_data, password) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
             PreparedStatement statement = connection.prepareStatement(s)) {



            statement.setString(1, name);
            statement.setString(2, numb);
            statement.setString(3, pass);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Пользователь успешно добавлен в базу данных.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении пользователя в базу данных:");
            e.printStackTrace();
        }
    }


}