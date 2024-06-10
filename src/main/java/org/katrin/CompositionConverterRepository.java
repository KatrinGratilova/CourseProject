package org.katrin;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompositionConverterRepository {
    Connection connection;
    CompositionConverterRepository(Connection connection){
        this.connection = connection;
    }
    public void addClient(String full_name, String contact_data, String user_password){
        String s = "INSERT INTO client (full_name, contact_data, user_password) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(s)) {

            statement.setString(1, full_name);
            statement.setString(2, contact_data);
            statement.setString(3, user_password);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Пользователь успешно добавлен в базу данных.");
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при добавлении пользователя в базу данных:");
            System.exit(1);
        }
    }

    public boolean findClient(String contact_data, String user_password, CompositionConverterApplication appl){
        String s = "SELECT * FROM client WHERE contact_data = ? AND user_password = ?";

        try (PreparedStatement statement = connection.prepareStatement(s)) {

            statement.setString(1, contact_data);
            statement.setString(2, user_password);

            ResultSet r = statement.executeQuery();
            if (!r.next()) {
                JOptionPane.showMessageDialog(appl, "User not found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                System.out.println("Пользователь успешно найден в базе данных.");
                System.out.println("Имя: " + r.getString("full_name"));
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при поиске пользователя в базе данных:");

        }
    return false;
    }

    public void findInitialInnerClass(){

    }

    public void addConvertedInnerClass(String name, int access_type_id, int initial_inner_class_id, String code){
        String s = "INSERT INTO converted_inner_class (name, access_type_id, initial_inner_class_id, code) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(s)) {

            statement.setString(1, name);
            statement.setInt(2, access_type_id);
            statement.setInt(3, initial_inner_class_id);
            statement.setString(4, code);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Пользователь успешно добавлен в базу данных.");
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при добавлении пользователя в базу данных:" + ex.getMessage());
            ex.printStackTrace();

        }
    }
}
