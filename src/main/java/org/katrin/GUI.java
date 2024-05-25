package org.katrin;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GUI extends JFrame {
    private final JLabel editLabel;
    private final JButton downloadButton;
    private final JButton nextButton;
    private final Font customFont1 = new Font("Verdana", Font.PLAIN, 12);
    private JList<String> attributeList;
    private DefaultListModel<String> listModel;

    // Множество для отслеживания выбранных атрибутов
    private final Set<String> selectedAttributesSet = new HashSet<>();
    GridBagConstraints gbc = new GridBagConstraints();
    ArrayList<Integer> selectedIndexes = new ArrayList<>();

    public GUI() {
        setTitle("Composition Converter");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        try {
            Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("icon.png")));
            if (icon != null) {
                setIconImage(icon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        getContentPane().setBackground(new Color(241, 247, 254));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);

        Font customFont = new Font("Courier New", Font.BOLD, 15);
        title.setFont(customFont);
        title.setForeground(new Color(24, 31, 84));

        JLabel label = new JLabel("Download your file from the library:");
        gbc.gridy = 1;
        add(label, gbc);

        label.setFont(customFont1);
        label.setForeground(new Color(24, 31, 84));

        downloadButton = new JButton("Download");
        gbc.gridy = 2;
        downloadButton.setPreferredSize(new Dimension(180, 25));
        add(downloadButton, gbc);

        downloadButton.setBackground(Color.WHITE);
        downloadButton.setForeground(new Color(24, 31, 84));
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
        nextButton.setBackground(new Color(254, 253, 223));
        nextButton.setForeground(new Color(24, 31, 84));

        editLabel = new JLabel();

        nextButton.addActionListener((ActionEvent e) -> {
            String result = Analysis.analyzePattern(selectedIndexes, editLabel);

            JTextArea resultTextArea = new JTextArea(result);
            resultTextArea.setLineWrap(true);
            resultTextArea.setWrapStyleWord(true);
            JScrollPane scrollPane1 = new JScrollPane(resultTextArea);
            scrollPane1.setPreferredSize(new Dimension(500, 300));
            resultTextArea.setBackground(new Color(254, 253, 223));

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

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addAdditionalComponents() {
        JLabel label = new JLabel("Outer class:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 46, 10, 10);
        add(label, gbc);

        label.setFont(customFont1);
        label.setForeground(new Color(24, 31, 84));

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
        label1.setForeground(new Color(24, 31, 84));

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
        resultTextArea.setBackground(new Color(254, 253, 223));

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