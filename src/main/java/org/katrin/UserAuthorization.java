//package org.katrin;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class UserAuthorization extends JFrame{
//    private final GridBagConstraints gbc = new GridBagConstraints();
//    private final Font customFont1 = new Font("Verdana", Font.PLAIN, 12);
//    private final Font customFont = new Font("Courier New", Font.BOLD, 15);
//    private final Color darkBlue = new Color(24, 31, 84);
//
//    public UserAuthorization(){
//
//    }
//    public void userRegistration() {
//        JLabel title = new JLabel("---- CREATE YOUR COMPOSITION CLASS ----");
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 4;
//        gbc.insets = new Insets(10, 10, 10, 10);
//        add(title, gbc);
//        title.setFont(customFont);
//        title.setForeground(darkBlue);
//
//        JLabel chooseOption = new JLabel("Authorization");
//        gbc.gridy = 1;
//        add(chooseOption, gbc);
//        chooseOption.setFont(customFont1);
//        chooseOption.setForeground(darkBlue);
//
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        JButton signInButton = new JButton("Sign In");
//        gbc.gridx = 0;
//        signInButton.setPreferredSize(new Dimension(180, 25));
//        add(signInButton, gbc);
//        signInButton.setBackground(Color.WHITE);
//        signInButton.setForeground(darkBlue);
//        signInButton.setFont(customFont1);
//
//        JButton signUpButton = new JButton("Sign Up");
//        gbc.gridx = 2;
//        signUpButton.setPreferredSize(new Dimension(180, 25));
//        add(signUpButton, gbc);
//        signUpButton.setBackground(Color.WHITE);
//        signUpButton.setForeground(darkBlue);
//        signUpButton.setFont(customFont1);
//
//        signUpButton.addActionListener(e -> userSignUp());
//        signInButton.addActionListener(e -> userSignIn());
//    }
//
//    public void userSignUp() {
//        clearWindow();
//        setSize(450, 250);
//        JLabel label = new JLabel("---Registration---");
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 4;
//        gbc.insets = new Insets(10, 50, 10, 50);
//        add(label, gbc);
//
//        label.setFont(customFont);
//        label.setForeground(darkBlue);
//
//        JLabel fullNameLabel = new JLabel("Full name:");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.gridwidth = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(fullNameLabel, gbc);
//
//        fullNameLabel.setFont(customFont1);
//        fullNameLabel.setForeground(darkBlue);
//
//        JTextField fullNameText = new JTextField(20);
//        gbc.gridx = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(fullNameText, gbc);
//
//        JLabel phoneNumberLabel = new JLabel("Phone number:");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(phoneNumberLabel, gbc);
//
//        phoneNumberLabel.setFont(customFont1);
//        phoneNumberLabel.setForeground(darkBlue);
//
//        JTextField phoneNumberText = new JTextField(20);
//        gbc.gridx = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(phoneNumberText, gbc);
//
//        JLabel passwordLabel = new JLabel("Password:");
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(passwordLabel, gbc);
//
//        passwordLabel.setFont(customFont1);
//        passwordLabel.setForeground(darkBlue);
//
//        JTextField passwordText = new JTextField(20);
//        gbc.gridx = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(passwordText, gbc);
//
//        gbc.gridy = 4;
//        gbc.gridwidth = 4;
//        JButton submitButton = new JButton("Submit");
//        gbc.gridx = 0;
//        submitButton.setPreferredSize(new Dimension(180, 25));
//        add(submitButton, gbc);
//
//        submitButton.setBackground(Color.WHITE);
//        submitButton.setForeground(darkBlue);
//        submitButton.setFont(customFont1);
//
//        submitButton.addActionListener(ev -> {
//
//            String full_name = fullNameText.getText();
//            String contact_data = phoneNumberText.getText();
//            String user_password = passwordText.getText();
//
//            if (full_name.isEmpty() || contact_data.isEmpty() || user_password.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            repository.addClient(full_name, contact_data, user_password);
//
//            choosePurchaseType();
//        });
//
//    }
//
//    public void userSignIn() {
//        clearWindow();
//        setSize(450, 220);
//        JLabel label = new JLabel("---Sign In---");
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 4;
//        gbc.insets = new Insets(10, 50, 10, 50);
//        add(label, gbc);
//        label.setFont(customFont);
//        label.setForeground(darkBlue);
//
//        JLabel phoneNumberLabel = new JLabel("Phone number:");
//        gbc.gridy = 1;
//        gbc.gridwidth = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(phoneNumberLabel, gbc);
//        phoneNumberLabel.setFont(customFont1);
//        phoneNumberLabel.setForeground(darkBlue);
//
//        JTextField phoneNumberText = new JTextField(20);
//        gbc.gridx = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(phoneNumberText, gbc);
//
//        JLabel passwordLabel = new JLabel("Password:");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(passwordLabel, gbc);
//        passwordLabel.setFont(customFont1);
//        passwordLabel.setForeground(darkBlue);
//
//        JTextField passwordText = new JTextField(20);
//        gbc.gridx = 2;
//        gbc.insets = new Insets(10, 46, 10, 10);
//        add(passwordText, gbc);
//
//        gbc.gridy = 3;
//        gbc.gridwidth = 4;
//        JButton submitButton = new JButton("Submit");
//        gbc.gridx = 0;
//        submitButton.setPreferredSize(new Dimension(180, 25));
//        add(submitButton, gbc);
//
//        submitButton.setBackground(Color.WHITE);
//        submitButton.setForeground(darkBlue);
//        submitButton.setFont(customFont1);
//
//        submitButton.addActionListener(ev -> {
//            String contact_data = phoneNumberText.getText();
//            String user_password = passwordText.getText();
//
//            if (contact_data.isEmpty() || user_password.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            boolean isFound = repository.findClient(contact_data, user_password, this);
//
//            if (isFound)
//                choosePurchaseType();
//        });
//    }
//
//    private void clearWindow() {
//        // Удаление всех компонентов из контента окна
//        getContentPane().removeAll();
//
//        // Перерасчет компонентов
//        revalidate();
//        repaint();
//    }
//}
