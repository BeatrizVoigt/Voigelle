package com.beatriz.voigelle.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author avrsoftware
 */
public class TelaPrincipal extends javax.swing.JFrame {

    // Armazena a imagem que será usada como fundo da tela.
    private Image imagemFundo;
    
    // Guarda a tela de gerenciamento de clientes aberta.
    private GerenciaClienteGUI gerenciaClienteGUI;
    
    // Guarda a tela de consulta de clientes aberta.
    private ConsultaClienteGUI consultaClienteGUI;
    
    // Guarda a tela de cadastro de usuários aberta.
    private CadastroUsuarioGUI cadastroUsuarioGUI;

    /**
     * Creates new form TelaPrincipal
     */
    // Construtor: prepara a tela principal.
    public TelaPrincipal() {
        carregarImagemFundo();
        initComponents();
        // A tela principal NÃO abre nenhuma tela ao iniciar
    }

    // Carrega a imagem de fundo.
    private void carregarImagemFundo() {
        try {
            // Primeiro, tenta localizar a imagem nos recursos do projeto. (Na pastinha IMG)
            URL url = getClass().getResource("/com/beatriz/voigelle/img/logoVoigelle.png");
            if (url != null) {
                imagemFundo = new ImageIcon(url).getImage();
                return;
            }
            
            // Se não encontrar, procura na pasta de recursos.
            File file = new File("src/main/resources/com/beatriz/voigelle/img/logoVoigelle.png");
            if (file.exists()) {
                imagemFundo = ImageIO.read(file);
                return;
            }
            
            // Como última tentativa, procura na pasta do código Java.
            file = new File("src/main/java/com/beatriz/voigelle/img/logoVoigelle.png");
            if (file.exists()) {
                imagemFundo = ImageIO.read(file);
            }
            
            // Mensagem de erro.
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem de fundo: " + e.getMessage());
        }
    }

    // Abre a tela de gerenciamento de clientes.
    public void abrirGerenciaCliente() {
        
         // Cria uma nova tela se ela ainda não existir ou estiver fechada.
        if (gerenciaClienteGUI == null || gerenciaClienteGUI.isClosed()) {
            gerenciaClienteGUI = new GerenciaClienteGUI();
            
             // Adiciona a tela dentro da área de trabalho principal.
            jDesktopPane1.add(gerenciaClienteGUI);
            gerenciaClienteGUI.setVisible(true);
            
            // Tenta deixar a tela selecionada.
            try {
                gerenciaClienteGUI.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
            
            // Se a tela já existir, torna visível e a coloca na frente.
        } else {
            gerenciaClienteGUI.setVisible(true);
            gerenciaClienteGUI.toFront();
            try {
                gerenciaClienteGUI.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        }
    }

    // Abre a tela de consulta de clientes.
    public void abrirConsultaCliente() {
        if (consultaClienteGUI == null || consultaClienteGUI.isClosed()) {
            consultaClienteGUI = new ConsultaClienteGUI();
            jDesktopPane1.add(consultaClienteGUI);
            consultaClienteGUI.setVisible(true);
            try {
                consultaClienteGUI.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        } else {
            consultaClienteGUI.setVisible(true);
            consultaClienteGUI.toFront();
            try {
                consultaClienteGUI.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        }
    }

    // Abre a tela de cadastro de usuários.
    public void abrirCadastroUsuario() {
        
         // Cria a tela se ela ainda não existir ou estiver fechada.
        if (cadastroUsuarioGUI == null || cadastroUsuarioGUI.isClosed()) {
            cadastroUsuarioGUI = new CadastroUsuarioGUI();
            
             // Adiciona a tela dentro da área de trabalho principal.
            jDesktopPane1.add(cadastroUsuarioGUI);
            cadastroUsuarioGUI.setVisible(true);
            
             // Tenta selecionar a tela aberta.
            try {
                cadastroUsuarioGUI.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
            
             // Se já estiver aberta, mostra a tela e a coloca na frente.
        } else {
            cadastroUsuarioGUI.setVisible(true);
            cadastroUsuarioGUI.toFront();
            try {
                cadastroUsuarioGUI.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        }
    }

     // Abre a tela de gerenciamento de produtos.
    public void abrirProdutos() {
        ProdutosGUI produtos = new ProdutosGUI();
        
        // Define que a tela será fechada sem encerrar o sistema inteiro.
        produtos.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
         // Centraliza a tela em relação à janela principal.
        produtos.setLocationRelativeTo(this);
        
        // Exibe a tela de produtos.
        produtos.setVisible(true);
    }

     // Abre a tela de consulta do estoque.
    public void abrirEstoque() {
        EstoqueGUI estoque = new EstoqueGUI();
        
        // Centraliza a tela em relação à janela principal.
        estoque.setLocationRelativeTo(this);
        
        // Exibe a tela de estoque.
        estoque.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    //// Inicializa e organiza os componentes visuais da tela.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (imagemFundo != null) {
                    int imgW = imagemFundo.getWidth(this);
                    int imgH = imagemFundo.getHeight(this);
                    if (imgW > 0 && imgH > 0) {
                        double scale = Math.min((double) getWidth() / imgW, (double) getHeight() / imgH);
                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);
                        int x = (getWidth() - drawW) / 2;
                        int y = (getHeight() - drawH) / 2;
                        g.drawImage(imagemFundo, x, y, drawW, drawH, this);
                    }
                }
            }
        };
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuContatos = new javax.swing.JMenu();
        jMenuItemClientes = new javax.swing.JMenuItem();
        jMenuItemConsultaClientes = new javax.swing.JMenuItem();
        jMenuUsuarios = new javax.swing.JMenu();
        jMenuItemUsuarios = new javax.swing.JMenuItem();
        jMenuProdutos = new javax.swing.JMenu();
        jMenuItemProdutos = new javax.swing.JMenuItem();
        jMenuItemEstoque = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Voigelle - Sistema Principal");

        jMenuContatos.setText("Clientes");

        jMenuItemClientes.setText("Cadastrar / Gerenciar Cliente");
        jMenuItemClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClientesActionPerformed(evt);
            }
        });
        jMenuContatos.add(jMenuItemClientes);

        jMenuItemConsultaClientes.setText("Consultar Clientes");
        jMenuItemConsultaClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirConsultaCliente();
            }
        });
        jMenuContatos.add(jMenuItemConsultaClientes);

        jMenuBar1.add(jMenuContatos);

        jMenuUsuarios.setText("Usuários");

        jMenuItemUsuarios.setText("Cadastrar Usuário");
        jMenuItemUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUsuariosActionPerformed(evt);
            }
        });
        jMenuUsuarios.add(jMenuItemUsuarios);

        jMenuBar1.add(jMenuUsuarios);

        jMenuProdutos.setText("Produtos");

        jMenuItemProdutos.setText("Gerenciar Produtos");
        jMenuItemProdutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProdutosActionPerformed(evt);
            }
        });
        jMenuProdutos.add(jMenuItemProdutos);

        jMenuItemEstoque.setText("Ver Estoque");
        jMenuItemEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirEstoque();
            }
        });
        jMenuProdutos.add(jMenuItemEstoque);

        jMenuBar1.add(jMenuProdutos);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClientesActionPerformed
        abrirGerenciaCliente();
    }//GEN-LAST:event_jMenuItemClientesActionPerformed

    private void jMenuItemUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUsuariosActionPerformed
        abrirCadastroUsuario();
    }//GEN-LAST:event_jMenuItemUsuariosActionPerformed

    private void jMenuItemProdutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProdutosActionPerformed
        abrirProdutos();
    }//GEN-LAST:event_jMenuItemProdutosActionPerformed

    /**
     * @param args the command line arguments
     */
    
    // Método principal: inicia a aplicação pela tela principal.
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        // Cria e exibe a tela principal na fila de eventos do Swing.
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuContatos;
    private javax.swing.JMenuItem jMenuItemClientes;
    private javax.swing.JMenuItem jMenuItemConsultaClientes;
    private javax.swing.JMenuItem jMenuItemEstoque;
    private javax.swing.JMenuItem jMenuItemProdutos;
    private javax.swing.JMenuItem jMenuItemUsuarios;
    private javax.swing.JMenu jMenuProdutos;
    private javax.swing.JMenu jMenuUsuarios;
    // End of variables declaration//GEN-END:variables
}
