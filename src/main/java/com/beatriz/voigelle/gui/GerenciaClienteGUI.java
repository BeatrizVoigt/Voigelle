package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.ClienteDAO;
import com.beatriz.voigelle.model.Cliente;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Tela de Cadastro e Gerenciamento de Cliente – Loja Voigelle
 *
 * Focada exclusivamente no cadastro e edição dos dados do cliente.
 *
 * @author avrsoftware
 */
public class GerenciaClienteGUI extends JInternalFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(250, 245, 255);
    private static final Color COR_PAINEL       = Color.WHITE;
    private static final Color COR_PRIMARIA     = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC = new Color(140, 0, 140);
    private static final Color COR_TEXTO        = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO  = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO  = new Color(200, 180, 210);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO    = new Font("Segoe UI Light", Font.BOLD, 26);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_LABEL     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 13);

    // ── DAO e Estado ──────────────────────────────────────────────────────────
    private ClienteDAO clienteDAO;
    private Integer    idEmEdicao = null;

    // ── Componentes do formulário ─────────────────────────────────────────────
    private JLabel     lblTitulo;
    private JLabel     lblSubtitulo;
    private JLabel     lblStatusEdicao;
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    private JButton    btnSalvar;
    private JButton    btnLimpar;
    private JButton    btnConsultar;

    /**
     * Construtor padrão (modo novo cadastro).
     */
    public GerenciaClienteGUI() {
        this(null);
    }

    /**
     * Construtor para edição de cliente existente.
     *
     * @param clienteParaEditar cliente a ser editado (ou null para novo)
     */
    public GerenciaClienteGUI(Cliente clienteParaEditar) {
        super("Gerenciar Cadastro de Cliente", true, true, true, true);
        this.clienteDAO = new ClienteDAO();
        initComponents();
        if (clienteParaEditar != null) {
            carregarCliente(clienteParaEditar);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Construção da interface
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        setContentPane(raiz);

        raiz.add(criarCartao());
        pack();
    }

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
        cartao.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(8, new Color(150, 0, 150, 30)),
                BorderFactory.createEmptyBorder(32, 40, 32, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        int linha   = 0;

        // ── Título ────────────────────────────────────────────────────────────
        lblTitulo = new JLabel("Gerenciar Cliente", SwingConstants.CENTER);
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_PRIMARIA);
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(lblTitulo, gbc);

        // ── Subtítulo ─────────────────────────────────────────────────────────
        lblSubtitulo = new JLabel("Preencha as informações do cliente para cadastrar ou atualizar", SwingConstants.CENTER);
        lblSubtitulo.setFont(FONTE_SUBTITULO);
        lblSubtitulo.setForeground(new Color(140, 100, 150));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 8, 0);
        cartao.add(lblSubtitulo, gbc);

        // ── Indicador de status (Novo / Editando) ──────────────────────────────
        lblStatusEdicao = new JLabel("● Novo Cadastro", SwingConstants.CENTER);
        lblStatusEdicao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatusEdicao.setForeground(new Color(30, 150, 80));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 18, 0);
        cartao.add(lblStatusEdicao, gbc);

        // ── Separador ─────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 200, 230));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 22, 0);
        cartao.add(sep, gbc);

        // ── Campos em duas colunas ───────────────────────────────────────────
        JPanel painelDuplo = new JPanel(new GridLayout(1, 2, 24, 0));
        painelDuplo.setOpaque(false);
        painelDuplo.add(criarColunaEsquerda());
        painelDuplo.add(criarColunaDireita());

        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 26, 0);
        cartao.add(painelDuplo, gbc);

        // ── Botões principais (Limpar e Salvar) ────────────────────────────────
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 14, 0));
        painelBotoes.setOpaque(false);

        btnLimpar = criarBotaoSecundario("Limpar");
        btnLimpar.addActionListener(e -> limparFormulario());
        painelBotoes.add(btnLimpar);

        btnSalvar = criarBotaoPrimario("Salvar Cliente");
        btnSalvar.addActionListener(e -> salvarCliente());
        painelBotoes.add(btnSalvar);

        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 14, 0);
        cartao.add(painelBotoes, gbc);

        // ── Botão Secundário para abrir a Consulta ────────────────────────────
        btnConsultar = new JButton("🔍  Ir para Consulta de Clientes");
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConsultar.setForeground(COR_PRIMARIA);
        btnConsultar.setContentAreaFilled(false);
        btnConsultar.setBorderPainted(false);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConsultar.addActionListener(e -> abrirTelaConsulta());

        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(btnConsultar, gbc);

        return cartao;
    }

    /** Coluna esquerda: Nome, CPF, Telefone */
    private JPanel criarColunaEsquerda() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;
        int linha = 0;

        // Nome
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Nome Completo *"), g);

        txtNome = criarCampoTexto("Ex: Mariana Souza Silva");
        txtNome.addActionListener(e -> txtCpf.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtNome, g);

        // CPF
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("CPF"), g);

        txtCpf = criarCampoTexto("000.000.000-00");
        txtCpf.addActionListener(e -> txtTelefone.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtCpf, g);

        // Telefone
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Telefone / WhatsApp"), g);

        txtTelefone = criarCampoTexto("(00) 00000-0000");
        txtTelefone.addActionListener(e -> txtEmail.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        col.add(txtTelefone, g);

        return col;
    }

    /** Coluna direita: E-mail, Endereço */
    private JPanel criarColunaDireita() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;
        int linha = 0;

        // E-mail
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("E-mail"), g);

        txtEmail = criarCampoTexto("cliente@exemplo.com");
        txtEmail.addActionListener(e -> txtEndereco.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtEmail, g);

        // Endereço
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Endereço Completo"), g);

        txtEndereco = criarCampoTexto("Rua, número, bairro e cidade");
        txtEndereco.addActionListener(e -> btnSalvar.doClick());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        col.add(txtEndereco, g);

        return col;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers de componentes visuais
    // ─────────────────────────────────────────────────────────────────────────

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_TEXTO);
        return label;
    }

    private JTextField criarCampoTexto(String placeholder) {
        JTextField campo = new JTextField(18) {
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
        campo.setFont(FONTE_CAMPO);
        campo.setForeground(COR_TEXTO_CAMPO);
        campo.setPreferredSize(new Dimension(240, 38));
        campo.setBackground(new Color(252, 248, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
        return campo;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() || getModel().isRollover()
                        ? COR_PRIMARIA_ESC : COR_PRIMARIA);
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
        btn.setPreferredSize(new Dimension(170, 42));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setForeground(COR_PRIMARIA);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 42));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lógica de Negócio
    // ─────────────────────────────────────────────────────────────────────────

    public void carregarCliente(Cliente cliente) {
        if (cliente == null) return;
        this.idEmEdicao = cliente.getId();
        txtNome.setText(cliente.getNome() != null ? cliente.getNome() : "");
        txtCpf.setText(cliente.getCpf() != null ? cliente.getCpf() : "");
        txtTelefone.setText(cliente.getTelefone() != null ? cliente.getTelefone() : "");
        txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
        txtEndereco.setText(cliente.getEndereco() != null ? cliente.getEndereco() : "");

        lblStatusEdicao.setText("✏ Editando Cliente #" + idEmEdicao);
        lblStatusEdicao.setForeground(COR_PRIMARIA);
        btnSalvar.setText("Atualizar Cliente");
        txtNome.requestFocus();
    }

    private void salvarCliente() {
        String nome     = txtNome.getText().trim();
        String cpf      = txtCpf.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email    = txtEmail.getText().trim();
        String endereco = txtEndereco.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "O nome do cliente é obrigatório!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return;
        }

        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "Informe um e-mail válido com @!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        boolean sucesso;
        String mensagem;

        if (idEmEdicao != null) {
            Cliente c = new Cliente(idEmEdicao, nome, cpf, telefone, email, endereco);
            sucesso = clienteDAO.atualizar(c);
            mensagem = "Cliente \"" + nome + "\" atualizado com sucesso!";
        } else {
            Cliente c = new Cliente(0, nome, cpf, telefone, email, endereco);
            sucesso = clienteDAO.inserir(c);
            mensagem = "Cliente \"" + nome + "\" cadastrado com sucesso!\nID: " + c.getId();
        }

        if (sucesso) {
            JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar cliente no banco de dados!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        idEmEdicao = null;
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEndereco.setText("");
        lblStatusEdicao.setText("● Novo Cadastro");
        lblStatusEdicao.setForeground(new Color(30, 150, 80));
        btnSalvar.setText("Salvar Cliente");
        txtNome.requestFocus();
    }

    private void abrirTelaConsulta() {
        JDesktopPane desktop = getDesktopPane();
        if (desktop != null) {
            // Procura se já existe uma tela de consulta aberta
            for (JInternalFrame f : desktop.getAllFrames()) {
                if (f instanceof ConsultaClienteGUI) {
                    f.setVisible(true);
                    f.toFront();
                    try {
                        f.setSelected(true);
                    } catch (java.beans.PropertyVetoException ignored) {
                    }
                    return;
                }
            }
            // Se não existe, cria e adiciona
            ConsultaClienteGUI consulta = new ConsultaClienteGUI();
            desktop.add(consulta);
            consulta.setVisible(true);
            try {
                consulta.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        } else {
            // Standalone
            JFrame frame = new JFrame("Consulta de Clientes");
            ConsultaClienteGUI consulta = new ConsultaClienteGUI();
            frame.setContentPane(consulta.getContentPane());
            frame.setSize(960, 620);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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
}
