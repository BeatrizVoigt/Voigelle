package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.UsuarioDAO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Tela de Login - Loja Voigelle
 *
 * @author avrsoftware
 */
public class loginGUI extends JFrame {

    // ── Paleta de cores ──────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(250, 245, 255);
    private static final Color COR_PAINEL       = Color.WHITE;
    private static final Color COR_PRIMARIA     = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC = new Color(140, 0, 140);
    private static final Color COR_TEXTO        = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO  = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO  = new Color(200, 180, 210);
    private static final Color COR_LINK         = new Color(180, 0, 180);

    // ── Fontes ───────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO      = new Font("Segoe UI Light", Font.BOLD, 28);
    private static final Font FONTE_SUBTITULO   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_LABEL       = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CAMPO       = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_BOTAO       = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_LINK        = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Componentes ──────────────────────────────────────────────────────────
    private UsuarioDAO usuarioDAO;
    private JTextField  txtUsuario;
    private JPasswordField txtSenha;
    private JButton     btnEntrar;
    private JButton     btnCadastrar;

    /**
     * Cria e inicializa a tela de login.
     */
    public loginGUI() {
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Construção da interface
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        setTitle("Login – Loja Voigelle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Painel raiz com cor de fundo suave
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        setContentPane(raiz);

        // Cartão branco centralizado
        JPanel cartao = criarCartao();
        raiz.add(cartao);

        pack();
    }

    /** Monta o painel-cartão com todos os elementos do formulário. */
    private JPanel criarCartao() {
        JPanel cartao = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_PAINEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cartao.setOpaque(false);
        cartao.setLayout(new GridBagLayout());
        cartao.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));

        // Sombra simulada via borda composta
        cartao.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(8, new Color(150, 0, 150, 30)),
                BorderFactory.createEmptyBorder(36, 44, 36, 44)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(0, 0, 0, 0);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        int linha   = 0;

        // ── Título ───────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Loja Voigelle", SwingConstants.CENTER);
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_PRIMARIA);
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(lblTitulo, gbc);

        // ── Subtítulo ─────────────────────────────────────────────────────────
        JLabel lblSub = new JLabel("Acesse sua conta para continuar", SwingConstants.CENTER);
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(140, 100, 150));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 28, 0);
        cartao.add(lblSub, gbc);

        // ── Separador decorativo ──────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 200, 230));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 24, 0);
        cartao.add(sep, gbc);

        // ── Campo: Usuário ────────────────────────────────────────────────────
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 6, 0);
        cartao.add(criarLabel("Usuário"), gbc);

        txtUsuario = criarCampoTexto("Digite seu usuário");
        txtUsuario.addActionListener(e -> txtSenha.requestFocus());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 18, 0);
        cartao.add(txtUsuario, gbc);

        // ── Campo: Senha ──────────────────────────────────────────────────────
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 6, 0);
        cartao.add(criarLabel("Senha"), gbc);

        txtSenha = criarCampoSenha("Digite sua senha");
        txtSenha.addActionListener(e -> fazerLogin());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 28, 0);
        cartao.add(txtSenha, gbc);

        // ── Botão Entrar ──────────────────────────────────────────────────────
        btnEntrar = criarBotaoPrimario("Entrar");
        btnEntrar.addActionListener(e -> fazerLogin());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 14, 0);
        cartao.add(btnEntrar, gbc);

        // ── Separador ─────────────────────────────────────────────────────────
        JPanel linhaOu = criarLinhaOu();
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 14, 0);
        cartao.add(linhaOu, gbc);

        // ── Botão Cadastrar ───────────────────────────────────────────────────
        btnCadastrar = criarBotaoSecundario("Cadastrar Novo Usuário");
        btnCadastrar.addActionListener(e -> CadastroUsuarioGUI.abrirJanelaIndependente(this));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(btnCadastrar, gbc);

        return cartao;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers de componentes
    // ─────────────────────────────────────────────────────────────────────────

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_TEXTO);
        return label;
    }

    private JTextField criarCampoTexto(String placeholder) {
        JTextField campo = new JTextField(22) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(180, 160, 190));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        estilizarCampo(campo);
        return campo;
    }

    private JPasswordField criarCampoSenha(String placeholder) {
        JPasswordField campo = new JPasswordField(22) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(180, 160, 190));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        estilizarCampo(campo);
        return campo;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(FONTE_CAMPO);
        campo.setForeground(COR_TEXTO_CAMPO);
        campo.setPreferredSize(new Dimension(300, 40));
        campo.setBackground(new Color(252, 248, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Highlight ao focar
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_PRIMARIA, 2, true),
                        BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
                campo.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                campo.repaint();
            }
        });
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COR_PRIMARIA_ESC);
                } else if (getModel().isRollover()) {
                    g2.setColor(COR_PRIMARIA_ESC);
                } else {
                    g2.setColor(COR_PRIMARIA);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONTE_BOTAO);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 42));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_LINK);
        btn.setForeground(COR_LINK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 36));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return btn;
    }

    /** Linha "— ou —" entre os botões. */
    private JPanel criarLinhaOu() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();

        JSeparator s1 = new JSeparator();
        s1.setForeground(new Color(210, 195, 220));
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        painel.add(s1, g);

        JLabel ou = new JLabel("  ou  ");
        ou.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ou.setForeground(new Color(160, 130, 170));
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        painel.add(ou, g);

        JSeparator s2 = new JSeparator();
        s2.setForeground(new Color(210, 195, 220));
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        painel.add(s2, g);

        return painel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lógica de negócio
    // ─────────────────────────────────────────────────────────────────────────

    private void fazerLogin() {
        String usuario = txtUsuario.getText().trim();
        String senha   = new String(txtSenha.getPassword()).trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha o usuário e a senha!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuarioDAO.autenticar(usuario, senha)) {
            JOptionPane.showMessageDialog(this,
                    "Login realizado com sucesso! Bem-vindo(a), " + usuario + "!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            new TelaPrincipal().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha incorretos!",
                    "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Borda com sombra suave
    // ─────────────────────────────────────────────────────────────────────────

    private static class ShadowBorder extends AbstractBorder {
        private final int   raio;
        private final Color cor;

        ShadowBorder(int raio, Color cor) {
            this.raio = raio;
            this.cor  = cor;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(raio, raio, raio + 4, raio + 4);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < raio; i++) {
                int alpha = (int) (80 * Math.exp(-0.35 * i));
                g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), Math.max(0, alpha)));
                g2.draw(new RoundRectangle2D.Float(x + i, y + i, w - i * 2 - 1, h - i * 2 - 1, 24, 24));
            }
            g2.dispose();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Main
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(loginGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new loginGUI().setVisible(true));
    }
}
