package com.expensetracker;

import com.expensetracker.ui.LoginFrame;
import javax.swing.*;

/**
 * Main.java - Entry point of the Expense Tracker Application.
 * Sets the Look and Feel and launches the Login screen.
 */
public class
Main {

    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread (EDT) - Swing requirement
        SwingUtilities.invokeLater(() -> {
            try {
                // Set modern system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Launch Login Screen
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
