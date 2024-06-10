package org.katrin;

import org.katrin.Model.InitialInnerClass;

import javax.imageio.ImageIO;
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
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;

public class CompositionConverterApplication extends JFrame {
    private final JLabel editLabel = new JLabel();
    private final JButton downloadButton = new JButton("Завантажити");
    private final JButton continueButton = new JButton("Продовжити");
    private JList<String> attributeList;
    private DefaultListModel<String> listModel;
    private final Set<String> selectedAttributesSet = new HashSet<>();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final ArrayList<Integer> selectedIndexes = new ArrayList<>();


    private final Font customFont1 = new Font("Verdana", Font.PLAIN, 12);
    private final Font customFont = new Font("Courier New", Font.BOLD, 15);
    private final Color darkBlue = new Color(24, 31, 84);
    private final Color yellow = new Color(254, 253, 223);
    private final Color lightBlue = new Color(241, 247, 254);


    private final Connection connection = ConnectionSingleton.getConnection();
    CompositionConverterRepository repository = new CompositionConverterRepository(connection);
    private String outerClassName = null;
    private String outerClassCode = null;
    private String userClassParsed;
    private String initialInnerClassCode;
    String convertedInnerClassCode;

    private JTextField outerClassNameField;
    private JTextField innerClassNameField;
    static String innerClassName;

    private final List<InitialInnerClass> initialInnerClasses = new ArrayList<>();
    private int currentIndex = 0;
    private JTextArea classDetailsArea;
    int selectedClassIndex;

    public CompositionConverterApplication() {
        setTitle("Composition Converter");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

//        try {
//            Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("src/icon.png")));
//            if (icon != null) {
//                setIconImage(icon);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Failed to load icon image.");
//        }


        getContentPane().setBackground(lightBlue);
        userAuthorization();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void userAuthorization() {
        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);
        title.setFont(customFont);
        title.setForeground(darkBlue);

        JLabel chooseOption = new JLabel("Авторизація");
        gbc.gridy = 1;
        add(chooseOption, gbc);
        chooseOption.setFont(customFont1);
        chooseOption.setForeground(darkBlue);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton signInButton = new JButton("Вхід");
        gbc.gridx = 0;
        signInButton.setPreferredSize(new Dimension(180, 25));
        add(signInButton, gbc);
        signInButton.setBackground(Color.WHITE);
        signInButton.setForeground(darkBlue);
        signInButton.setFont(customFont1);

        JButton signUpButton = new JButton("Реєстрація");
        gbc.gridx = 2;
        signUpButton.setPreferredSize(new Dimension(180, 25));
        add(signUpButton, gbc);
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(darkBlue);
        signUpButton.setFont(customFont1);

        signUpButton.addActionListener(e -> userSignUp());
        signInButton.addActionListener(e -> userSignIn());
    }

    public void userSignUp() {
        clearWindow();
        setSize(450, 250);
        JLabel label = new JLabel("--- Реєстрація ---");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 50, 10, 50);
        add(label, gbc);

        label.setFont(customFont);
        label.setForeground(darkBlue);


        JLabel fullNameLabel = new JLabel("ПІБ:");
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


        JLabel phoneNumberLabel = new JLabel("Номер телефону:");
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


        JLabel passwordLabel = new JLabel("Пароль:");
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
        JButton submitButton = new JButton("Підтвердити");
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
                JOptionPane.showMessageDialog(this, "Необхідно заповнити всі поля!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            repository.addClient(full_name, contact_data, user_password);
            choosePurchaseType();
        });

    }

    public void userSignIn() {
        clearWindow();
        setSize(450, 220);
        JLabel label = new JLabel("--- Вхід---");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 50, 10, 50);
        add(label, gbc);
        label.setFont(customFont);
        label.setForeground(darkBlue);

        JLabel phoneNumberLabel = new JLabel("Номер телефону:");
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

        JLabel passwordLabel = new JLabel("Пароль:");
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
        JButton submitButton = new JButton("Підтвердити");
        gbc.gridx = 0;
        submitButton.setPreferredSize(new Dimension(180, 25));
        add(submitButton, gbc);

        submitButton.setBackground(Color.WHITE);
        submitButton.setForeground(darkBlue);
        submitButton.setFont(customFont1);

        submitButton.addActionListener(ev -> {
            String contact_data = phoneNumberText.getText();
            String user_password = passwordText.getText();

            if (contact_data.isEmpty() || user_password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Необхідно заповнити всі поля!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isFound = repository.findClient(contact_data, user_password, this);

            if (isFound) choosePurchaseType();
        });
    }

    public void choosePurchaseType() {
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

        JLabel chooseOption = new JLabel("Чи є в вас внутрішній клас для композиції?");
        gbc.gridy = 1;
        add(chooseOption, gbc);

        chooseOption.setFont(customFont1);
        chooseOption.setForeground(darkBlue);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton hasInnerButton = new JButton("Так");
        gbc.gridx = 0;
        hasInnerButton.setPreferredSize(new Dimension(180, 25));
        add(hasInnerButton, gbc);

        hasInnerButton.setBackground(Color.WHITE);
        hasInnerButton.setForeground(darkBlue);
        hasInnerButton.setFont(customFont1);

        JButton noInnerButton = new JButton("Ні");
        gbc.gridx = 2;
        noInnerButton.setPreferredSize(new Dimension(180, 25));
        add(noInnerButton, gbc);
        noInnerButton.setBackground(Color.WHITE);
        noInnerButton.setForeground(darkBlue);
        noInnerButton.setFont(customFont1);

        hasInnerButton.addActionListener(e -> hasInner());
        noInnerButton.addActionListener(e -> noInner());
    }

    public void convertingClassWhenHasInner() {
        convertedInnerClassCode = Analysis.analyzePattern(selectedIndexes, editLabel);
        outerClassName = outerClassNameField.getText();

        String s;
        int outerClassId = 0;
        int initialInnerClassId = 0;
        if (!outerClassName.isEmpty() && outerClassCode != null) {
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
        } else if (!outerClassName.isEmpty()) {
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
        }

        if (outerClassId != 0) {
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
        } else {
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
        }
        repository.addConvertedInnerClass(innerClassName, 1, initialInnerClassId, convertedInnerClassCode);
        orderExecution(convertedInnerClassCode);
    }

    public void convertingClassWhenNoInner() {
        convertedInnerClassCode = Analysis.analyzePattern(selectedIndexes, editLabel);
        outerClassName = outerClassNameField.getText();

        String s;
        int outerClassId = 0;
        int initialInnerClassId = 0;
        if (!outerClassName.isEmpty() && outerClassCode != null) {
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
            if (outerClassId != 0) {
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
            }
        }


        if (initialInnerClassId != 0)
            repository.addConvertedInnerClass(innerClassName, 1, initialInnerClassId, convertedInnerClassCode);
        else repository.addConvertedInnerClass(innerClassName, 1, selectedClassIndex, convertedInnerClassCode);
        orderExecution(convertedInnerClassCode);
    }

    public void hasInner() {
        clearWindow();
        setSize(450, 220);
        JLabel label = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 50, 10, 50);
        add(label, gbc);
        label.setFont(customFont);
        label.setForeground(darkBlue);

        JLabel downloadFile = new JLabel("Завантажте файл з бібліотеки:");
        gbc.gridy = 1;
        add(downloadFile, gbc);
        downloadFile.setFont(customFont1);
        downloadFile.setForeground(darkBlue);


        gbc.gridy = 2;
        downloadButton.setPreferredSize(new Dimension(180, 25));
        add(downloadButton, gbc);
        downloadButton.setBackground(Color.WHITE);
        downloadButton.setForeground(darkBlue);
        downloadButton.setFont(customFont1);

        // Добавление обработчика на кнопку
        downloadButton.addActionListener(el -> {
            // Создание объекта JFileChooser
            JFileChooser fileChooser = new JFileChooser();
            // Открываем диалог выбора файла
            int result = fileChooser.showOpenDialog(CompositionConverterApplication.this);

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

                    initialInnerClassCode = content.toString();
                    userClassParsed = Regular.showPattern(initialInnerClassCode);

                    downloadButton.setEnabled(false);
                    showResultDialog(userClassParsed);
                    addAdditionalComponents(); // Додавання додаткових компонентів

                    outerClassInput(gbc.gridy);
                    // Створення списку зберігання вибраних номерів чекбоксов
                    continueButton.setBackground(yellow);
                    continueButton.setForeground(darkBlue);
                    continueButton.addActionListener((ActionEvent e) -> convertingClassWhenHasInner());


                    gbc.gridy = 9;
                    add(continueButton, gbc);
                } catch (Exception ex) {
                    System.exit(1);
                }
            }
        });
    }

    public void downloadOuterFile() {
        // Создание объекта JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        // Открываем диалог выбора файла
        int result = fileChooser.showOpenDialog(CompositionConverterApplication.this);

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

                outerClassCode = content.toString();

                downloadButton.setEnabled(false);

            } catch (Exception ex) {
                System.exit(1);
            }
        }
    }

    public void noInner() {
        clearWindow();
        setSize(4550, 220);
        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 50, 10, 50);
        add(title, gbc);
        title.setFont(customFont);
        title.setForeground(darkBlue);

        JLabel innerClassNameLabel = new JLabel("Назва внутрішнього класу:");
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(innerClassNameLabel, gbc);
        innerClassNameLabel.setFont(customFont1);
        innerClassNameLabel.setForeground(darkBlue);

        innerClassNameField = new JTextField(15);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 10, 10, 50);
        add(innerClassNameField, gbc);

        outerClassInput(gbc.gridy);

        JButton submitButton = new JButton("Підтвердити");
        submitButton.setBackground(yellow);
        submitButton.setForeground(darkBlue);
        submitButton.setFont(customFont1);

        submitButton.addActionListener(e -> {
            outerClassName = outerClassNameField.getText();
            searchForInnerClass();
        });

        continueButton.setBackground(yellow);
        continueButton.setForeground(darkBlue);

        continueButton.addActionListener(e -> convertingClassWhenNoInner());

        gbc.gridy = 9;
        add(submitButton, gbc);
        revalidate();
        pack();
    }

    public void searchForInnerClass() {
        innerClassName = innerClassNameField.getText();
        outerClassName = outerClassNameField.getText();
        String s;
        int classNumbers = 0;
        if (outerClassName.isEmpty()) {
            s = "SELECT initial_inner_class_id, name, outer_class_id, code FROM initial_inner_class WHERE name = ?;";
            try (PreparedStatement statement = connection.prepareStatement(s)) {
                statement.setString(1, innerClassName);
                ResultSet classes = statement.executeQuery();
                while (classes.next()) {
                    InitialInnerClass innerClass = InitialInnerClass.builder().id(classes.getInt(1)).name(classes.getString(2)).outerClassId(classes.getInt(3)).code(classes.getString(4)).build();
                    initialInnerClasses.add(innerClass);
                    classNumbers++;
                }
            } catch (SQLException ex) {
                System.err.println("Ошибка при поиске внутреннего класса: " + ex.getMessage());
            }
        } else {
            s = """
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
                    InitialInnerClass innerClass = InitialInnerClass.builder().id(classes.getInt(1)).name(classes.getString(2)).outerClassId(classes.getInt(3)).code(classes.getString(4)).build();
                    initialInnerClasses.add(innerClass);
                    classNumbers++;
                }
            } catch (SQLException ex) {
                System.err.println("Ошибка при поиске внутреннего класса: " + ex.getMessage());
            }
        }

        if (classNumbers == 0) {
            JOptionPane.showMessageDialog(null, "Класи не знайдені.");
        } else {
            innerClassSelection();
        }
    }

    private void showCurrentClass() {
        if (!initialInnerClasses.isEmpty()) {
            InitialInnerClass currentClass = initialInnerClasses.get(currentIndex);
            classDetailsArea.setText(currentClass.getCode());
        }
    }

    private void innerClassSelection() {
        clearWindow();
        setSize(560, 580);
        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 50, 10, 50);
        add(title, gbc);
        title.setFont(customFont);
        title.setForeground(darkBlue);

        JLabel label = new JLabel("Було знайдено наступні класи. Оберіть один серед запропонованих.");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(label, gbc);
        label.setFont(customFont1);
        label.setForeground(darkBlue);

        classDetailsArea = new JTextArea(20, 50);
        classDetailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(classDetailsArea);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);
        showCurrentClass();

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton prevButton = new JButton("Назад");
        gbc.insets = new Insets(10, 50, 10, 10);
        prevButton.setPreferredSize(new Dimension(180, 25));
        prevButton.setBackground(Color.WHITE);
        prevButton.setForeground(darkBlue);
        prevButton.setFont(customFont1);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(prevButton, gbc);

        JButton nextButton = new JButton("Вперед");
        gbc.insets = new Insets(10, 40, 10, 10);
        nextButton.setPreferredSize(new Dimension(180, 25));
        nextButton.setBackground(Color.WHITE);
        nextButton.setForeground(darkBlue);
        nextButton.setFont(customFont1);
        gbc.gridx = 2;

        add(nextButton, gbc);

        JButton selectButton = new JButton("Обрати");
        gbc.insets = new Insets(10, 10, 10, 10);
        selectButton.setPreferredSize(new Dimension(180, 25));
        selectButton.setBackground(yellow);
        selectButton.setForeground(darkBlue);
        selectButton.setFont(customFont1);

        gbc.gridwidth = 4;
        gbc.gridy = 4;
        gbc.gridx = 1;
        add(selectButton, gbc);


        nextButton.addActionListener(e -> {
            currentIndex = (currentIndex + 1) % initialInnerClasses.size();
            showCurrentClass();
        });
        prevButton.addActionListener(e -> {
            currentIndex = (currentIndex - 1 + initialInnerClasses.size()) % initialInnerClasses.size();
            showCurrentClass();
        });
        selectButton.addActionListener(e -> {
            InitialInnerClass selectedClass = initialInnerClasses.get(currentIndex);
            initialInnerClassCode = selectedClass.getCode();
            userClassParsed = Regular.showPattern(initialInnerClassCode);
            showResultDialog(userClassParsed);

            addAdditionalComponents();
            gbc.gridy++;
            add(continueButton, gbc);


            selectedClassIndex = selectedClass.getId();  // Сохранение индекса выбранного класса
            System.out.println("Selected class index in DB: " + selectedClassIndex);  // Проверка, что индекс сохранен правильно
        });


    }

    private void outerClassInput(int y) {
        setSize(470, 570);
        JLabel label = new JLabel("Ви можете ввести назву зовнішнього класу, або назву та код.");
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = y + 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(label, gbc);
        label.setFont(customFont1);
        label.setForeground(darkBlue);

        JLabel label3 = new JLabel("Назва зовнішнього класу:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(label3, gbc);
        label3.setFont(customFont1);
        label3.setForeground(darkBlue);

        outerClassNameField = new JTextField(15);
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 50);
        add(outerClassNameField, gbc);

        JLabel label4 = new JLabel("Код зовнішнього класу: (Не обов'язково)");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(label4, gbc);
        label4.setFont(customFont1);
        label4.setForeground(darkBlue);

        gbc.gridy = 8;
        downloadButton.setPreferredSize(new Dimension(180, 25));
        add(downloadButton, gbc);
        downloadButton.setBackground(Color.WHITE);
        downloadButton.setForeground(darkBlue);
        downloadButton.setFont(customFont1);

        // Добавление обработчика на кнопку
        downloadButton.addActionListener(el -> downloadOuterFile());
    }

    private void addAdditionalComponents() {
        clearWindow();
        setSize(470, 430);

        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);
        title.setFont(customFont);
        title.setForeground(darkBlue);

        gbc.gridwidth = 2;
        JLabel label1 = new JLabel("Внутрішній клас:");
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

        JLabel label2 = new JLabel("Оберіть корисні атрибути зі списку, які хочете зберегти:");
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

        JButton okButton = new JButton("Додати");
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(darkBlue);
        okButton.setFont(customFont1);

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

        editLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem copyItem = new JMenuItem("Скопіювати");
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


    }

    public void orderExecution(String convertedInnerClassCode) {

        JTextArea resultTextArea = new JTextArea(convertedInnerClassCode);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane1 = new JScrollPane(resultTextArea);
        scrollPane1.setPreferredSize(new Dimension(500, 300));
        resultTextArea.setBackground(yellow);

        int option2 = JOptionPane.showOptionDialog(this, scrollPane1, "Composition Converter", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK"}, "OK");

        if (option2 == JOptionPane.OK_OPTION) {
            System.exit(0);
        }


    }

    private static class DisabledItemRenderer extends DefaultListCellRenderer {
        private final Set<String> disabledItems;

        public DisabledItemRenderer(Set<String> disabledItems) {
            this.disabledItems = disabledItems;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
        int option = JOptionPane.showOptionDialog(this, scrollPane, "Composition Converter", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK"}, "OK");

        // Проверка, была ли нажата кнопка "OK"
        if (option == JOptionPane.OK_OPTION) {
            clearWindow(); // Очистка окна после нажатия "OK"
        }
    }

    private void clearWindow() {
        // Удаление всех компонентов из контента окна
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        getContentPane().removeAll();

        // Перерасчет компонентов
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CompositionConverterApplication::new);
    }
}