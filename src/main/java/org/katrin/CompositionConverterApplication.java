package org.katrin;

import org.katrin.Model.Client;
import org.katrin.Model.InitialInnerClass;
import org.katrin.Model.Purchase;

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
import java.sql.*;
import java.time.LocalDateTime;
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
    private final Font customFont2 = new Font("Verdana", Font.ITALIC, 13);
    private final Font customFont3 = new Font("Dialog", Font.BOLD, 12);
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
    int accessType = 1;
    int convertedClassId = 0;
    LocalDateTime checkoutDate;
    Client client;

    public CompositionConverterApplication() {
        setTitle("Composition Converter");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(CompositionConverterApplication.class.getResource("/icon.png")));
        setIconImage(icon.getImage());

        getContentPane().setBackground(lightBlue);
        userAuthorization();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void userAuthorization() {
        writeTitle();
        writeLabel("Авторизація", 0, 1, 4, 10, 10, customFont, darkBlue);

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

        writeLabel("--- Реєстрація ---", 0, 0, 4, 50, 50, customFont, darkBlue);
        writeLabel("ПІБ:", 0, 1, 2, 46, 10, customFont1, darkBlue);

        JTextField fullNameText = new JTextField(20);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(fullNameText, gbc);

        writeLabel("Номер телефону:", 0, 2, 2, 46, 10, customFont1, darkBlue);

        JTextField phoneNumberText = new JTextField(20);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(phoneNumberText, gbc);

        writeLabel("Пароль:", 0, 3, 2, 46, 10, customFont1, darkBlue);

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

            client = Client.builder()
                    .fullName(fullNameText.getText())
                    .contactData(phoneNumberText.getText())
                    .password(passwordText.getText())
                    .build();

            if (full_name.isEmpty() || contact_data.isEmpty() || user_password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Необхідно заповнити всі поля!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int clientId = repository.addClient(client);
            client.setId(clientId);
            choosePurchaseType();
        });
    }

    public void userSignIn() {
        clearWindow();
        setSize(450, 220);
        writeLabel("--- Вхід ---", 0, 0, 4, 50, 50, customFont, darkBlue);
        writeLabel("Номер телефону:", 0, 1, 2, 46, 10, customFont1, darkBlue);

        JTextField phoneNumberText = new JTextField(20);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(phoneNumberText, gbc);

        writeLabel("Пароль:", 0, 2, 2, 46, 10, customFont1, darkBlue);

        JTextField passwordText = new JTextField(20);
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(passwordText, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        JButton submitButton = new JButton("Підтвердити");
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
            client = repository.findClient(contact_data, user_password, this);
            if (client != null) choosePurchaseType();
        });
    }

    public void choosePurchaseType() {
        clearWindow();
        setSize(450, 220);
        writeTitle();
        writeLabel("Чи є в вас внутрішній клас для композиції?", 0, 1, 4, 10, 10, customFont1, darkBlue);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton hasInnerButton = new JButton("Так");
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

        int outerClassId = 0;
        int initialInnerClassId;

        if (!outerClassName.isEmpty() && outerClassCode != null)
            outerClassId = repository.addOuterClassWithNameAndCode(outerClassName, outerClassCode);
        else if (!outerClassName.isEmpty())
            outerClassId = repository.addOuterClassWithName(outerClassName);

        if (outerClassId != 0)
            initialInnerClassId = repository.addInitialInnerClassWithOuter(innerClassName, outerClassId, initialInnerClassCode);
        else
            initialInnerClassId = repository.addInitialInnerClassWithoutOuter(innerClassName, initialInnerClassCode);

        convertedClassId = repository.addConvertedInnerClass(innerClassName, accessType, initialInnerClassId, convertedInnerClassCode);
        orderExecution(convertedInnerClassCode);
    }

    public void convertingClassWhenNoInner() {
        convertedInnerClassCode = Analysis.analyzePattern(selectedIndexes, editLabel);
        outerClassName = outerClassNameField.getText();

        int outerClassId;
        int initialInnerClassId = 0;
        if (!outerClassName.isEmpty() && outerClassCode != null) {
            outerClassId = repository.addOuterClassWithNameAndCode(outerClassName, outerClassCode);
            if (outerClassId != 0)
                initialInnerClassId = repository.addInitialInnerClassWithOuter(innerClassName, outerClassId, initialInnerClassCode);
        }

        if (initialInnerClassId != 0)
            convertedClassId = repository.addConvertedInnerClass(innerClassName, accessType, initialInnerClassId, convertedInnerClassCode);
        else
            convertedClassId = repository.addConvertedInnerClass(innerClassName, accessType, selectedClassIndex, convertedInnerClassCode);
        orderExecution(convertedInnerClassCode);
    }

    public void hasInner() {
        clearWindow();
        setSize(450, 220);
        writeTitle();
        writeLabel("Завантажте файл з бібліотеки:", 0, 1, 4, 10, 10, customFont1, darkBlue);

        gbc.gridy = 2;
        JButton downloadInnerClassButton = new JButton("Завантажити");
        downloadInnerClassButton.setPreferredSize(new Dimension(180, 25));
        add(downloadInnerClassButton, gbc);
        downloadInnerClassButton.setBackground(Color.WHITE);
        downloadInnerClassButton.setForeground(darkBlue);
        downloadInnerClassButton.setFont(customFont1);

        // Додавання оброблювача на кнопку
        downloadInnerClassButton.addActionListener(el -> {
            // Створення об'єкта JFileChooser
            JFileChooser fileChooser = new JFileChooser();
            // Відкриваємо діалог вибору файлу
            int result = fileChooser.showOpenDialog(CompositionConverterApplication.this);

            // Перевірка, чи вибрано файл
            if (result == JFileChooser.APPROVE_OPTION) {
                // Отримуємо вибраний файл
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Читаємо вміст файлу
                    BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();
                    initialInnerClassCode = content.toString();
                    userClassParsed = Regular.showPattern(initialInnerClassCode);

                    downloadInnerClassButton.setEnabled(false);
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

    public void noInner() {
        clearWindow();
        setSize(4550, 220);
        writeTitle();
        writeLabel("Назва внутрішнього класу:", 0, 1, 2, 46, 10, customFont1, darkBlue);

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

        gbc.gridy = 9;
        add(submitButton, gbc);
        revalidate();
        pack();
    }

    public void downloadOuterFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(CompositionConverterApplication.this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
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

    public void searchForInnerClass() {
        innerClassName = innerClassNameField.getText();
        outerClassName = outerClassNameField.getText();

        int classNumber;
        if (outerClassName.isEmpty())
            classNumber = repository.findInitialInnerClassByName(innerClassName, initialInnerClasses);
        else
            classNumber = repository.findInitialInnerClassByOuter(innerClassName, outerClassName, initialInnerClasses);

        if (classNumber == 0) {
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
        writeTitle();
        writeLabel("Було знайдено наступні класи. Оберіть один серед запропонованих.", 0, 1, 4, 10, 10, customFont1, darkBlue);

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
            continueButton.setBackground(yellow);
            continueButton.setForeground(darkBlue);

            continueButton.addActionListener(e1 -> convertingClassWhenNoInner());
            add(continueButton, gbc);

            selectedClassIndex = selectedClass.getId();  // Збереження індексу обраного класу
        });
    }

    private void outerClassInput(int y) {
        setSize(470, 570);
        writeLabel("Ви можете ввести назву зовнішнього класу, або назву та код.", 0, y + 1, 4, 10, 10, customFont1, darkBlue);
        writeLabel("Назва зовнішнього класу:", 0, 6, 2, 46, 10, customFont1, darkBlue);

        outerClassNameField = new JTextField(15);
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 50);
        add(outerClassNameField, gbc);

        writeLabel("Код зовнішнього класу: (Не обов'язково)", 0, 7, 4, 10, 10, customFont1, darkBlue);

        gbc.gridy = 8;
        downloadButton.setPreferredSize(new Dimension(180, 25));
        add(downloadButton, gbc);
        downloadButton.setBackground(Color.WHITE);
        downloadButton.setForeground(darkBlue);
        downloadButton.setFont(customFont1);

        downloadButton.addActionListener(el -> downloadOuterFile());
    }

    private void addAdditionalComponents() {
        clearWindow();
        setSize(470, 430);
        writeTitle();
        writeLabel("Внутрішній клас:", 0, 1, 2, 50, 10, customFont1, darkBlue);
        writeLabel(Regular.className, 2, 1, 2, 10, 150, customFont3, Color.black);
        writeLabel("Оберіть корисні атрибути зі списку, які хочете зберегти:", 0, 2, 4, 10, 10, customFont3, Color.black);

        // Налаштування моделі списку
        listModel = new DefaultListModel<>();
        // Додавання атрибутів із існуючого списку Regular.attr
        for (String attribute : Regular.attr) {
            listModel.addElement(attribute);
        }

        // Створення JList із використанням моделі
        attributeList = new JList<>(listModel);
        attributeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Створення JScrollPane для списку атрибутів
        JScrollPane scrollPane = new JScrollPane(attributeList);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gbc.gridy = 3;
        add(scrollPane, gbc);

        JButton okButton = new JButton("Додати");
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(darkBlue);
        okButton.setFont(customFont1);

        okButton.addActionListener(e -> {
            // Опрацювання обраних атрибутів
            selectedIndexes.add(attributeList.getSelectedIndex());

            for (int index : selectedIndexes) {
                String attribute = listModel.getElementAt(index);
                // Перевіряємо, чи був він обраний раніше
                selectedAttributesSet.add(attribute);
            }
            // Встановлюємо кастомний рендерер для блокування відображення вибраних атрибутів
            attributeList.setCellRenderer(new DisabledItemRenderer(selectedAttributesSet));
        });

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
        clearWindow();
        setSize(540, 600);
        writeTitle();

        checkoutDate = LocalDateTime.now();

        Purchase purchase = Purchase.builder()
                .clientId(client.getId())
                .convertedInnerClassId(convertedClassId)
                .checkoutDate(checkoutDate)
                .build();
        repository.addPurchase(purchase);

        writeLabel("Інформація про замовлення", 0, 1, 4, 10, 10, customFont2, darkBlue);
        writeLabel("ПІБ замовника:", 0, 2, 2, 50, 10, customFont3, Color.black);
        writeLabel(client.getFullName(), 2, 2, 2, 10, 10, customFont1, darkBlue);
        writeLabel("Дата та час видачі замовлення:", 0, 3, 2, 10, 50, customFont3, Color.black);
        writeLabel(purchase.getCheckoutDate().toString(), 2, 3, 2, 10, 10, customFont1, darkBlue);
        writeLabel("Ваш конвертований внутрішній клас для композиції:", 0, 4, 4, 10, 10, customFont1, darkBlue);

        JTextArea resultTextArea = new JTextArea(convertedInnerClassCode);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane1 = new JScrollPane(resultTextArea);
        scrollPane1.setPreferredSize(new Dimension(500, 350));
        resultTextArea.setBackground(yellow);

        gbc.gridy = 5;
        gbc.gridheight = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        add(scrollPane1, gbc);
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
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    private void writeTitle() {
        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);
        title.setFont(customFont);
        title.setForeground(darkBlue);
    }

    private void writeLabel(String text, int x, int y, int width, int left, int right, Font customFont, Color color) {
        JLabel label1 = new JLabel(text);
        gbc.gridwidth = width;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(10, left, 10, right);
        add(label1, gbc);
        label1.setFont(customFont);
        label1.setForeground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CompositionConverterApplication::new);
    }
}