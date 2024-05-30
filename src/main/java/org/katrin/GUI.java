package org.katrin;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GUI extends JFrame {

    private JLabel editLabel;
    private JButton downloadButton;
    private JButton signInButton;
    private JButton signUpButton;
    private JButton nextButton;
    private final Font customFont1 = new Font("Verdana", Font.PLAIN, 12);
    private JList<String> attributeList;
    private DefaultListModel<String> listModel;
    private final Color darkBlue = new Color(24, 31, 84);
    private final Color yellow = new Color(254, 253, 223);
    private final Color lightBlue =new Color(241, 247, 254);

    // Множество для отслеживания выбранных атрибутов
    private final Set<String> selectedAttributesSet = new HashSet<>();
    GridBagConstraints gbc = new GridBagConstraints();
    ArrayList<Integer> selectedIndexes = new ArrayList<>();
    Font customFont = new Font("Courier New", Font.BOLD, 15);

    String userName = "postgres";
    String password = "230218";
    String connectionUrl = "jdbc:postgresql://localhost:5432/course_project";


    public GUI() {
        setTitle("Composition Converter");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

//        try {
//            Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("icon.png")));
//            if (icon != null) {
//                setIconImage(icon);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        getContentPane().setBackground(lightBlue);

        userRegistration();



        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void userRegistration(){
        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);


        title.setFont(customFont);
        title.setForeground(darkBlue);

        JLabel chooseOption = new JLabel("Authorization");
        gbc.gridy = 1;
        add(chooseOption, gbc);

        chooseOption.setFont(customFont1);
        chooseOption.setForeground(darkBlue);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        signInButton = new JButton("Sign In");
        gbc.gridx = 0;
        signInButton.setPreferredSize(new Dimension(180, 25));
        add(signInButton, gbc);

        signInButton.setBackground(Color.WHITE);
        signInButton.setForeground(darkBlue);
        signInButton.setFont(customFont1);

        signUpButton = new JButton("Sign Up");
        gbc.gridx = 2;
        signUpButton.setPreferredSize(new Dimension(180, 25));
        add(signUpButton, gbc);

        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(darkBlue);
        signUpButton.setFont(customFont1);

        signUpButton.addActionListener(e -> {
            clearWindow();
            setSize(450, 250);
            JLabel label = new JLabel("---Registration---");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 4;
            gbc.insets = new Insets(10, 50, 10, 50);
            add(label, gbc);

            label.setFont(customFont);
            label.setForeground(darkBlue);


            JLabel fullNameLabel = new JLabel("Full name:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(fullNameLabel, gbc);

            fullNameLabel.setFont(customFont1);
            fullNameLabel.setForeground(darkBlue);

            JTextField fullNameText = new JTextField(20);
            gbc.gridx = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(fullNameText, gbc);


            JLabel phoneNumberLabel = new JLabel("Phone number:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(phoneNumberLabel, gbc);

            phoneNumberLabel.setFont(customFont1);
            phoneNumberLabel.setForeground(darkBlue);

            JTextField phoneNumberText = new JTextField(20);
            gbc.gridx = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(phoneNumberText, gbc);


            JLabel passwordLabel = new JLabel("Password:");
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(passwordLabel, gbc);

            passwordLabel.setFont(customFont1);
            passwordLabel.setForeground(darkBlue);

            JTextField passwordText = new JTextField(20);
            gbc.gridx = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(passwordText, gbc);


            gbc.gridy = 4;
            gbc.gridwidth = 4;
            JButton submitButton = new JButton("Submit");
            gbc.gridx = 0;
            submitButton.setPreferredSize(new Dimension(180, 25));
            add(submitButton, gbc);

            submitButton.setBackground(Color.WHITE);
            submitButton.setForeground(darkBlue);
            submitButton.setFont(customFont1);

            submitButton.addActionListener(ev -> {

                String full_name = fullNameText.getText();
                String contact_data = phoneNumberText.getText();
                String user_password = passwordText.getText();

                if (full_name.isEmpty() || contact_data.isEmpty() || user_password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String s = "INSERT INTO client (full_name, contact_data, user_password) VALUES (?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                     PreparedStatement statement = connection.prepareStatement(s)) {

                    statement.setString(1, full_name);
                    statement.setString(2, contact_data);
                    statement.setString(3, user_password);

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Пользователь успешно добавлен в базу данных.");
                    }
                    downloadFile();
                } catch (SQLException ex) {
                    System.err.println("Ошибка при добавлении пользователя в базу данных:");
                    ex.printStackTrace();
                }
            });
        });

        signInButton.addActionListener(e -> {
            clearWindow();
            setSize(450, 220);
            JLabel label = new JLabel("---Sign In---");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 4;
            gbc.insets = new Insets(10, 50, 10, 50);
            add(label, gbc);

            label.setFont(customFont);
            label.setForeground(darkBlue);


            JLabel phoneNumberLabel = new JLabel("Phone number:");
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(phoneNumberLabel, gbc);

            phoneNumberLabel.setFont(customFont1);
            phoneNumberLabel.setForeground(darkBlue);

            JTextField phoneNumberText = new JTextField(20);
            gbc.gridx = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(phoneNumberText, gbc);


            JLabel passwordLabel = new JLabel("Password:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(passwordLabel, gbc);

            passwordLabel.setFont(customFont1);
            passwordLabel.setForeground(darkBlue);

            JTextField passwordText = new JTextField(20);
            gbc.gridx = 2;
            gbc.insets = new Insets(10, 46, 10, 10);
            add(passwordText, gbc);


            gbc.gridy = 3;
            gbc.gridwidth = 4;
            JButton submitButton = new JButton("Submit");
            gbc.gridx = 0;
            submitButton.setPreferredSize(new Dimension(180, 25));
            add(submitButton, gbc);

            submitButton.setBackground(Color.WHITE);
            submitButton.setForeground(darkBlue);
            submitButton.setFont(customFont1);

            submitButton.addActionListener(ev -> {
                String contact_data = phoneNumberText.getText();
                String user_password = passwordText.getText();

                String s = "SELECT * FROM client WHERE contact_data = ? AND user_password = ?";

                try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
                     PreparedStatement statement = connection.prepareStatement(s)) {

                    statement.setString(1, contact_data);
                    statement.setString(2, user_password);

                    ResultSet r = statement.executeQuery();
                    if (r.next()) {
                        System.out.println("Пользователь успешно найден в базе данных.");
                        System.out.println("Имя: " + r.getString("full_name"));
                    } else {
                        System.out.println("Пользователь не найден.");
                    }
                    downloadFile();

                } catch (SQLException ex) {
                    System.err.println("Ошибка при поиске пользователя в базе данных:");
                    ex.printStackTrace();
                }
            });
        });

    }

    public void downloadFile(){
        clearWindow();
        setSize(450, 220);
        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);


        title.setFont(customFont);
        title.setForeground(darkBlue);

        JLabel downloadFile = new JLabel("Download your file from the library:");
        gbc.gridy = 1;
        add(downloadFile, gbc);

        downloadFile.setFont(customFont1);
        downloadFile.setForeground(darkBlue);

        downloadButton = new JButton("Download");
        gbc.gridy = 2;
        downloadButton.setPreferredSize(new Dimension(180, 25));
        add(downloadButton, gbc);

        downloadButton.setBackground(Color.WHITE);
        downloadButton.setForeground(darkBlue);
        downloadButton.setFont(customFont1);

        // Добавление обработчика на кнопку
        downloadButton.addActionListener(e -> {
            // Создание объекта JFileChooser
            JFileChooser fileChooser = new JFileChooser();
            // Открываем диалог выбора файла
            int result = fileChooser.showOpenDialog(GUI.this);

            // Проверка, был ли выбран файл
            if (result == JFileChooser.APPROVE_OPTION) {
                // Получаем выбранный файл
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Читаем содержимое файла
                    BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();

                    // Сохраняем текст в переменную (здесь content.toString())
                    String fileContent = content.toString();

                    String userClass = Regular.showPattern(fileContent);
                    downloadButton.setEnabled(false);
                    showResultDialog(userClass);
                    addAdditionalComponents(); // Додавання додаткових компонентів
                    // Теперь вы можете использовать переменную fileContent по своему усмотрению

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Створення списку зберігання вибраних номерів чекбоксов
        nextButton = new JButton("Continue");
        nextButton.setBackground(yellow);
        nextButton.setForeground(darkBlue);

        editLabel = new JLabel();

        nextButton.addActionListener((ActionEvent e) -> {
            String result = Analysis.analyzePattern(selectedIndexes, editLabel);

            JTextArea resultTextArea = new JTextArea(result);
            resultTextArea.setLineWrap(true);
            resultTextArea.setWrapStyleWord(true);
            JScrollPane scrollPane1 = new JScrollPane(resultTextArea);
            scrollPane1.setPreferredSize(new Dimension(500, 300));
            resultTextArea.setBackground(yellow);

            int option2 = JOptionPane.showOptionDialog(
                    this,
                    scrollPane1,
                    "Composition Converter",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"OK"},
                    "OK");

            if (option2 == JOptionPane.OK_OPTION) {
                System.exit(0);
            }
        });

    }


    private void addAdditionalComponents() {
        JLabel label = new JLabel("Outer class:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(label, gbc);

        label.setFont(customFont1);
        label.setForeground(darkBlue);

        JTextField externalClass = new JTextField(15);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 10, 10, 50);
        add(externalClass, gbc);

        JLabel label1 = new JLabel("Inner class:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 50, 10, 10);
        add(label1, gbc);

        label1.setFont(customFont1);
        label1.setForeground(darkBlue);

        JLabel name = new JLabel(Regular.className);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 10, 10, 150);
        add(name, gbc);

        JLabel label2 = new JLabel("Select the useful attributes from the list:");

        label2.setPreferredSize(new Dimension(240, 25));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(label2, gbc);

        // Настройка модели списка
        listModel = new DefaultListModel<>();
        // Добавление атрибутов из существующего списка Regular.attr
        for (String attribute : Regular.attr) {
            listModel.addElement(attribute);
        }

        // Создание JList с использованием модели
        attributeList = new JList<>(listModel);
        attributeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Создание JScrollPane для списка атрибутов
        JScrollPane scrollPane = new JScrollPane(attributeList);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Добавление JScrollPane на форму
        gbc.gridy = 3;
        add(scrollPane, gbc);

        JButton okButton = new JButton("Add");
        okButton.addActionListener(e -> {
            // Обработка выбранных атрибутов
            selectedIndexes.add(attributeList.getSelectedIndex());

            for (int index : selectedIndexes) {
                String attribute = listModel.getElementAt(index);
                // Проверяем, не выбран ли атрибут ранее
                selectedAttributesSet.add(attribute);
            }
            // Устанавливаем кастомный рендерер для блокировки отображения выбранных атрибутов
            attributeList.setCellRenderer(new DisabledItemRenderer(selectedAttributesSet));
        });

        // Добавление кнопки на форму
        gbc.gridy = 4;
        add(okButton, gbc);

        gbc.gridy = 5;
        add(nextButton, gbc);

        // Отображение формы
        setLocationRelativeTo(null);
        setVisible(true);

        editLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem copyItem = new JMenuItem("Copy");
                    copyItem.addActionListener(actionEvent -> {
                        String textWithHtml = editLabel.getText();
                        String textWithoutHtml = textWithHtml.replaceAll("<.*?>", ""); // Видаляємо HTML-теги
                        StringSelection stringSelection = new StringSelection(textWithoutHtml);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    });
                    popupMenu.add(copyItem);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        revalidate();
        pack();
    }

    private static class DisabledItemRenderer extends DefaultListCellRenderer {
        private final Set<String> disabledItems;

        public DisabledItemRenderer(Set<String> disabledItems) {
            this.disabledItems = disabledItems;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (disabledItems.contains(value)) {
                component.setEnabled(false);
            }
            return component;
        }
    }

    private void showResultDialog(String result) {
        JTextArea resultTextArea = new JTextArea(result);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        scrollPane.setPreferredSize(new Dimension(500, 300));
        resultTextArea.setBackground(yellow);

        // Відображення інформації у діалоговому вікні
        int option = JOptionPane.showOptionDialog(
                this,
                scrollPane,
                "Composition Converter",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK");

        // Проверка, была ли нажата кнопка "OK"
        if (option == JOptionPane.OK_OPTION) {
            clearWindow(); // Очистка окна после нажатия "OK"
        }
    }

    private void clearWindow() {
        // Удаление всех компонентов из контента окна
        getContentPane().removeAll();

        // Перерасчет компонентов
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}