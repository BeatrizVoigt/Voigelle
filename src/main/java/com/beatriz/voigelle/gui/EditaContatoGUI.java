package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.model.Contato;
import com.beatriz.voigelle.service.ContatoService;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Modal de Edição de Contato – Loja Voigelle
 *
 * @author rosilva
 */
public class EditaContatoGUI extends JDialog {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(250, 245, 255);
    private static final Color COR_PAINEL       = Color.WHITE;
    private static final Color COR_PRIMARIA     = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC = new Color(140, 0, 140);
    private static final Color COR_TEXTO        = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO  = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO  = new Color(200, 180, 210);

    private static final Font FONTE_TITULO = new Font("Segoe UI Light", Font.BOLD, 20);
    private static final Font FONTE_LABEL  = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CAMPO  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO  = new Font("Segoe UI", Font.BOLD, 12);

    private Contato        contato;
    private ContatoService contatoService;

    private JTextField txtNome;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JButton    btnSalvar;
    private JButton    btnCancelar;

    public EditaContatoGUI(Frame parent, boolean modal, Contato contato) {
        super(parent, modal);
        this.contato = contato;
        this.contatoService = new ContatoService();
        initComponents();
        preencheFormulario();
        setLocationRelativeTo(parent);
    }

    public void preencheFormulario() {
        if (contato != null) {
            txtNome.setText(contato.getNome());
            txtTelefone.setText(contato.getTelefone());
            txtEmail.setText(contato.getEmail());
        }
    }

    private void initComponents() {
        setTitle("Editar Contato – Voigelle");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setContentPane(raiz);

        JPanel cartao = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_PAINEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cartao.setOpaque(false);
        cartao.setLayout(new GridBagLayout());
        cartao.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(6, new Color(150, 0, 150, 25)),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0;
        int linha = 0;

        JLabel lblTitulo = new JLabel("✏  Editar Contato", SwingConstants.CENTER);
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_PRIMARIA);
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        cartao.add(lblTitulo, g);

        // Nome
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("Nome"), g);

        txtNome = criarCampo();
        txtNome.setEnabled(false);
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 12, 0);
        cartao.add(txtNome, g);

        // Telefone
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("Telefone"), g);

        txtTelefone = criarCampo();
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 12, 0);
        cartao.add(txtTelefone, g);

        // Email
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("E-mail"), g);

        txtEmail = criarCampo();
        g.gridy = linha++;
        g.insets = new Insets(0, 0, 20, 0);
        cartao.add(txtEmail, g);

        // Botões
        JPanel painelBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        painelBtns.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(FONTE_BOTAO);
        btnCancelar.setForeground(COR_PRIMARIA);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        btnCancelar.addActionListener(e -> dispose());
        painelBtns.add(btnCancelar);

        btnSalvar = new JButton("Salvar") {
            @Override
            protected void paintComponent(Graphics gr) {
                Graphics2D g2 = (Graphics2D) gr.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() || getModel().isRollover() ? COR_PRIMARIA_ESC : COR_PRIMARIA);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(gr);
            }
        };
        btnSalvar.setFont(FONTE_BOTAO);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setContentAreaFilled(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.setPreferredSize(new Dimension(110, 36));
        btnSalvar.addActionListener(e -> salvar());
        painelBtns.add(btnSalvar);

        g.gridy = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBtns, g);

        raiz.add(cartao);
        pack();
    }

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_TEXTO);
        return lbl;
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField(18);
        campo.setFont(FONTE_CAMPO);
        campo.setForeground(COR_TEXTO_CAMPO);
        campo.setPreferredSize(new Dimension(240, 34));
        campo.setBackground(new Color(252, 248, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return campo;
    }

    private void salvar() {
        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        if (telefone == null || telefone.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O telefone não pode ser vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtTelefone.requestFocus();
            return;
        }

        if (email == null || !email.contains("@") || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "E-mail inválido!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        contatoService.atualizarContato(contato.getId(), nome, telefone, email);
        JOptionPane.showMessageDialog(this, "Contato atualizado com sucesso!", "Confirmação", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private static class ShadowBorder extends AbstractBorder {
        private final int raio;
        private final Color cor;
        ShadowBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(raio, raio, raio + 4, raio + 4); }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < raio; i++) {
                int alpha = (int) (80 * Math.exp(-0.35 * i));
                g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), Math.max(0, alpha)));
                g2.draw(new RoundRectangle2D.Float(x + i, y + i, w - i * 2 - 1, h - i * 2 - 1, 16, 16));
            }
            g2.dispose();
        }
    }
}
