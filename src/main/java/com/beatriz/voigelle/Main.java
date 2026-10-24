package com.beatriz.voigelle;

import com.beatriz.voigelle.gui.loginGUI;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

// Cria a classe principal do programa.
public class Main {

    public static void main(String args[]) {
        
        // Tenta configurar a aparência do programa.
        try {
        // Aplica o tema claro do FlatLaf nas janelas.
            UIManager.setLookAndFeel(new FlatLightLaf());
            
        // Executa este bloco caso aconteça algum erro.
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Cria e exibe a tela de login.
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new loginGUI().setVisible(true);
            }
        });
    }

}
