package org.katrin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.katrin.Model.Client;

import javax.swing.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CompositionConverterApplicationTest {
    private CompositionConverterApplication application;
    private CompositionConverterRepository mockRepository;
    private Client mockClient;

    @BeforeEach
    public void setUp() {
        mockRepository = mock(CompositionConverterRepository.class);
        application = new CompositionConverterApplication();
        application.repository = mockRepository;

        mockClient = Client.builder()
                .id(1)
                .fullName("Test User")
                .contactData("1234567890")
                .password("password")
                .build();
    }

    @Test
    public void testUserAuthorization() {
        SwingUtilities.invokeLater(() -> {
            application.userAuthorization();
            JFrame frame = application;
            assertNotNull(frame);
            assertTrue(frame.isVisible());
            assertTrue(frame.getContentPane().getComponentCount() > 0);
        });
    }

    @Test
    public void testUserSignUp() {
        SwingUtilities.invokeLater(() -> {
            application.userSignUp();
            JFrame frame = application;
            assertNotNull(frame);
            assertTrue(frame.isVisible());
            assertTrue(frame.getContentPane().getComponentCount() > 0);

            // Mocking repository behavior
            when(mockRepository.addClient(any(Client.class))).thenReturn(1);

            JTextField fullNameField = (JTextField) frame.getContentPane().getComponent(3);
            JTextField phoneNumberField = (JTextField) frame.getContentPane().getComponent(5);
            JTextField passwordField = (JTextField) frame.getContentPane().getComponent(7);
            JButton submitButton = (JButton) frame.getContentPane().getComponent(9);

            fullNameField.setText("Test User");
            phoneNumberField.setText("1234567890");
            passwordField.setText("password");

            // Triggering the submit button action
            submitButton.doClick();

            verify(mockRepository, times(1)).addClient(any(Client.class));
        });
    }

    @Test
    public void testUserSignIn() {
        SwingUtilities.invokeLater(() -> {
            application.userSignIn();
            JFrame frame = application;
            assertNotNull(frame);
            assertTrue(frame.isVisible());
            assertTrue(frame.getContentPane().getComponentCount() > 0);

            JTextField phoneNumberField = (JTextField) frame.getContentPane().getComponent(3);
            JTextField passwordField = (JTextField) frame.getContentPane().getComponent(5);
            JButton submitButton = (JButton) frame.getContentPane().getComponent(7);

            phoneNumberField.setText("1234567890");
            passwordField.setText("password");

            // Mocking repository behavior
            when(mockRepository.findClient(anyString(), anyString(), any())).thenReturn(mockClient);

            // Triggering the submit button action
            submitButton.doClick();

            verify(mockRepository, times(1)).findClient(anyString(), anyString(), any());
        });
    }
}