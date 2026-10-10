package com.beatriz.voigelle;

import com.beatriz.voigelle.gui.ConsoleGUI;
import com.beatriz.voigelle.gui.TelaPrincipal;

public class Main {

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

}
