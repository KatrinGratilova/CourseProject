package org.katrin;

import org.katrin.Model.Client;
import org.katrin.Model.InitialInnerClass;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CompositionConverterRepository {
    Connection connection;

    CompositionConverterRepository(Connection connection) {
        this.connection = connection;
    }

    public void addClient(Client client) {
        String s = "INSERT INTO client (full_name, contact_data, user_password) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(s)) {

            statement.setString(1, client.getFullName());
            statement.setString(2, client.getContactData());
            statement.setString(3, client.getPassword());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Пользователь успешно добавлен в базу данных.");
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при добавлении пользователя в базу данных:");
            System.exit(1);
        }
    }

    public boolean findClient(String contact_data, String user_password, CompositionConverterApplication appl) {
        String s = "SELECT * FROM client WHERE contact_data = ? AND user_password = ?";

        try (PreparedStatement statement = connection.prepareStatement(s)) {

            statement.setString(1, contact_data);
            statement.setString(2, user_password);

            ResultSet r = statement.executeQuery();
            if (!r.next()) {
                JOptionPane.showMessageDialog(appl, "Такого користувача не існує. Спробуйте ще раз.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            } else
                return true;

        } catch (SQLException ex) {
            System.err.println("Ошибка при поиске пользователя в базе данных:");

        }
        return false;
    }


    public void addConvertedInnerClass(String name, int access_type_id, int initial_inner_class_id, String code) {
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

    public int addOuterClassWithNameAndCode(String outerClassName, String outerClassCode) {
        String s;
        int outerClassId = 0;

        s = "INSERT INTO outer_class (name, source_id, code) VALUES (?, 1, ?)";
        try (PreparedStatement statement = connection.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, outerClassName);
            statement.setString(2, outerClassCode);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) outerClassId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при вставке внешнего класса:");
            System.exit(1);
        }
        return outerClassId;
    }

    public int addOuterClassWithName(String outerClassName) {
        String s;
        int outerClassId = 0;

        s = "INSERT INTO outer_class (name, source_id) VALUES (?, 1)";
        try (PreparedStatement statement = connection.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, outerClassName);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) outerClassId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при вставке внешнего класса:");
            System.exit(1);
        }
        return outerClassId;
    }

    public int addInitialInnerClassWithOuter(String innerClassName, int outerClassId, String initialInnerClassCode) {
        String s;
        int initialInnerClassId = 0;
        s = "INSERT INTO initial_inner_class (name, source_id, outer_class_id, code) VALUES (?, 1, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, innerClassName);
            statement.setInt(2, outerClassId);
            statement.setString(3, initialInnerClassCode);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) initialInnerClassId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при вставке внутеннего класса:");
            System.exit(1);
        }
        return initialInnerClassId;
    }

    public int addInitialInnerClassWithoutOuter(String innerClassName, String initialInnerClassCode) {
        String s;
        int initialInnerClassId = 0;
        s = "INSERT INTO initial_inner_class (name, source_id, code) VALUES (?, 1, ?)";
        try (PreparedStatement statement = connection.prepareStatement(s, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, innerClassName);
            statement.setString(2, initialInnerClassCode);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) initialInnerClassId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при вставке внутеннего класса:");
            System.exit(1);
        }
        return initialInnerClassId;
    }

    public int findInitialInnerClassByName(String innerClassName, List<InitialInnerClass> initialInnerClasses) {
        int classNumber = 0;

        String s = "SELECT initial_inner_class_id, name, outer_class_id, code FROM initial_inner_class WHERE name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(s)) {
            statement.setString(1, innerClassName);
            ResultSet classes = statement.executeQuery();
            while (classes.next()) {
                InitialInnerClass innerClass = InitialInnerClass
                        .builder().id(classes.getInt(1))
                        .name(classes.getString(2))
                        .outerClassId(classes.getInt(3))
                        .code(classes.getString(4))
                        .build();
                initialInnerClasses.add(innerClass);
                classNumber++;
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при поиске внутреннего класса: " + ex.getMessage());
        }
        return classNumber;
    }

    public int findInitialInnerClassByOuter(String innerClassName, String outerClassName, List<InitialInnerClass> initialInnerClasses) {
        int classNumber = 0;
        String s = """
                SELECT initial_inner_class_id, iic.name, iic.outer_class_id, iic.code
                FROM initial_inner_class iic
                JOIN outer_class oc
                ON  iic.outer_class_id = oc.outer_class_id
                WHERE iic.name = ? AND oc.name = ?;
                """;
        try (PreparedStatement statement = connection.prepareStatement(s)) {
            statement.setString(1, innerClassName);
            statement.setString(2, outerClassName);
            ResultSet classes = statement.executeQuery();
            while (classes.next()) {
                InitialInnerClass innerClass = InitialInnerClass
                        .builder()
                        .id(classes.getInt(1))
                        .name(classes.getString(2))
                        .outerClassId(classes.getInt(3))
                        .code(classes.getString(4))
                        .build();
                initialInnerClasses.add(innerClass);
                classNumber++;
            }
        } catch (SQLException ex) {
            System.err.println("Ошибка при поиске внутреннего класса: " + ex.getMessage());
        }
        return classNumber;
    }


}
