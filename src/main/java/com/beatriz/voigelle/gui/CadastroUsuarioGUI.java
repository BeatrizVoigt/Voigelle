package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.UsuarioDAO;
import com.beatriz.voigelle.model.Usuario;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

/**
 * Tela de Cadastro e Gerenciamento de Usuários – Loja Voigelle
 *
 * @author avrsoftware
 */
public class CadastroUsuarioGUI extends JInternalFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(250, 245, 255);
    private static final Color COR_PAINEL       = Color.WHITE;
    private static final Color COR_PRIMARIA     = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC = new Color(140, 0, 140);
    private static final Color COR_TEXTO        = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO  = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO  = new Color(200, 180, 210);
    private static final Color COR_LINHA_PAR    = new Color(252, 248, 255);
    private static final Color COR_LINHA_IMPAR  = Color.WHITE;
    private static final Color COR_CABECALHO    = new Color(180, 0, 180);
    private static final Color COR_SELECAO      = new Color(230, 180, 240);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO    = new Font("Segoe UI Light", Font.BOLD, 22);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_LABEL     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_TABELA    = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Componentes ───────────────────────────────────────────────────────────
    private UsuarioDAO        usuarioDAO;
    private JTextField        txtNome;
    private JTextField        txtUsuario;
    private JPasswordField    txtSenha;
    private JPasswordField    txtConfirmarSenha;
    private JButton           btnSalvar;
    private JButton           btnLimpar;
    private JButton           btnExcluir;

    // ── Tabela ────────────────────────────────────────────────────────────────
    private JTable            tabela;
    private DefaultTableModel modeloTabela;
    private JTextField        txtBusca;
    private JLabel            lblTotalUsuarios;

    /**
     * Cria e inicializa a tela de cadastro de usuários.
     */
    public CadastroUsuarioGUI() {
        super("Cadastro de Usuários", true, true, true, true);
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        carregarTabela(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Construção da interface
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(COR_FUNDO);
        setContentPane(raiz);

        // Cabeçalho
        raiz.add(criarCabecalho(), BorderLayout.NORTH);

        // Corpo: formulário à esquerda e tabela à direita
        JPanel corpo = new JPanel(new BorderLayout(16, 0));
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(14, 18, 18, 18));

        corpo.add(criarPainelFormulario(), BorderLayout.WEST);
        corpo.add(criarPainelTabela(),     BorderLayout.CENTER);

        raiz.add(corpo, BorderLayout.CENTER);

        setPreferredSize(new Dimension(840, 520));
        pack();
    }

    // ── Cabeçalho com banner degradê ──────────────────────────────────────────

    private JPanel criarCabecalho() {
        JPanel cab = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(160, 0, 160),
                        0, getHeight(), new Color(200, 30, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cab.setLayout(new BorderLayout());
        cab.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 3));
        titulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("🔑  Cadastro de Usuários");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        titulos.add(lblTitulo);

        JLabel lblSub = new JLabel("Gerencie os acessos e permissões dos operadores do sistema");
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(255, 220, 255));
        titulos.add(lblSub);

        cab.add(titulos, BorderLayout.WEST);
        return cab;
    }

    // ── Formulário (lado esquerdo) ─────────────────────────────────────────────

    private JPanel criarPainelFormulario() {
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
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        int linha   = 0;

        JLabel lblSecao = new JLabel("Novo Usuário");
        lblSecao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSecao.setForeground(COR_PRIMARIA);
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 14, 0);
        cartao.add(lblSecao, gbc);

        // 1. Nome
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("Nome Completo *"), gbc);

        txtNome = criarCampo("Ex: Maria Oliveira");
        txtNome.addActionListener(e -> txtUsuario.requestFocus());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 12, 0);
        cartao.add(txtNome, gbc);

        // 2. Usuário (login)
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("Usuário (Login) *"), gbc);

        txtUsuario = criarCampo("Ex: maria.voigelle");
        txtUsuario.addActionListener(e -> txtSenha.requestFocus());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 12, 0);
        cartao.add(txtUsuario, gbc);

        // 3. Senha
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("Senha *"), gbc);

        txtSenha = criarCampoSenha("Digite a senha");
        txtSenha.addActionListener(e -> txtConfirmarSenha.requestFocus());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 12, 0);
        cartao.add(txtSenha, gbc);

        // 4. Confirmar Senha
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(criarLabel("Confirmar Senha *"), gbc);

        txtConfirmarSenha = criarCampoSenha("Repita a senha");
        txtConfirmarSenha.addActionListener(e -> btnSalvar.doClick());
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 18, 0);
        cartao.add(txtConfirmarSenha, gbc);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(225, 205, 235));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 14, 0);
        cartao.add(sep, gbc);

        // Botões Salvar / Limpar
        JPanel painelBtns = new JPanel(new GridLayout(1, 2, 8, 0));
        painelBtns.setOpaque(false);

        btnLimpar = criarBotaoSecundario("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());
        painelBtns.add(btnLimpar);

        btnSalvar = criarBotaoPrimario("Cadastrar");
        btnSalvar.addActionListener(e -> cadastrarUsuario());
        painelBtns.add(btnSalvar);

        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBtns, gbc);

        cartao.setPreferredSize(new Dimension(260, 0));
        return cartao;
    }

    // ── Tabela (lado direito) ──────────────────────────────────────────────────

    private JPanel criarPainelTabela() {
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
        cartao.setLayout(new BorderLayout());
        cartao.setBorder(new ShadowBorder(6, new Color(150, 0, 150, 25)));

        // Barra superior
        JPanel barraSuperior = new JPanel(new BorderLayout(10, 0));
        barraSuperior.setOpaque(false);
        barraSuperior.setBorder(BorderFactory.createEmptyBorder(12, 16, 10, 16));

        // Busca
        JPanel painelBusca = new JPanel(new BorderLayout(6, 0));
        painelBusca.setOpaque(false);
        JLabel lblLupa = new JLabel("🔍");
        lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        lblLupa.setForeground(COR_PRIMARIA);
        painelBusca.add(lblLupa, BorderLayout.WEST);

        txtBusca = new JTextField();
        txtBusca.setFont(FONTE_CAMPO);
        txtBusca.setPreferredSize(new Dimension(200, 32));
        txtBusca.setBackground(new Color(252, 248, 255));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        txtBusca.putClientProperty("JTextField.placeholderText", "Buscar usuário…");
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e)  { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        painelBusca.add(txtBusca, BorderLayout.CENTER);
        barraSuperior.add(painelBusca, BorderLayout.WEST);

        // Botões de ação da tabela
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelAcoes.setOpaque(false);

        btnExcluir = criarBotaoPerigoTabela("🗑 Excluir");
        btnExcluir.addActionListener(e -> excluirSelecionado());
        painelAcoes.add(btnExcluir);

        JButton btnAtualizar = criarBotaoAcaoTabela("↺ Atualizar");
        btnAtualizar.addActionListener(e -> {
            txtBusca.setText("");
            carregarTabela(null);
        });
        painelAcoes.add(btnAtualizar);

        barraSuperior.add(painelAcoes, BorderLayout.EAST);
        cartao.add(barraSuperior, BorderLayout.NORTH);

        // Modelo da tabela
        String[] colunas = {"ID", "Nome Completo", "Login de Usuário"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modeloTabela) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? COR_LINHA_PAR : COR_LINHA_IMPAR);
                } else {
                    c.setBackground(COR_SELECAO);
                }
                c.setForeground(COR_TEXTO);
                return c;
            }
        };

        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(32);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(COR_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setFocusable(false);
        tabela.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COR_CABECALHO);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setBackground(COR_CABECALHO);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return lbl;
            }
        });

        // Alinhamento
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centralizado);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(160);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_PAINEL);
        cartao.add(scroll, BorderLayout.CENTER);

        // Rodapé
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 210, 240)),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        JLabel lblDica = new JLabel("As senhas são armazenadas com segurança no banco.");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(new Color(150, 110, 160));
        rodape.add(lblDica, BorderLayout.WEST);

        lblTotalUsuarios = new JLabel("0 usuários");
        lblTotalUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalUsuarios.setForeground(COR_PRIMARIA);
        rodape.add(lblTotalUsuarios, BorderLayout.EAST);

        cartao.add(rodape, BorderLayout.SOUTH);
        return cartao;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers de componentes visuais
    // ─────────────────────────────────────────────────────────────────────────

    private JLabel criarLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(COR_TEXTO);
        return lbl;
    }

    private JTextField criarCampo(String placeholder) {
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
        campo.setPreferredSize(new Dimension(220, 34));
        campo.setBackground(new Color(252, 248, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_PRIMARIA, 2, true),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7)));
                campo.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                campo.repaint();
            }
        });
        return campo;
    }

    private JPasswordField criarCampoSenha(String placeholder) {
        JPasswordField campo = new JPasswordField(18) {
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
        campo.setFont(FONTE_CAMPO);
        campo.setForeground(COR_TEXTO_CAMPO);
        campo.setPreferredSize(new Dimension(220, 34));
        campo.setBackground(new Color(252, 248, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_PRIMARIA, 2, true),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7)));
                campo.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
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
                g2.setColor(getModel().isPressed() || getModel().isRollover() ? COR_PRIMARIA_ESC : COR_PRIMARIA);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
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
        btn.setPreferredSize(new Dimension(105, 36));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setForeground(COR_PRIMARIA);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(105, 36));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return btn;
    }

    private JButton criarBotaoAcaoTabela(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(210, 180, 225) : new Color(238, 222, 248);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(COR_PRIMARIA);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 32));
        return btn;
    }

    private JButton criarBotaoPerigoTabela(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(210, 40, 60) : new Color(235, 70, 90);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 32));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lógica de Negócio
    // ─────────────────────────────────────────────────────────────────────────

    public void carregarTabela(String filtro) {
        modeloTabela.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.listar();

        int count = 0;
        for (Usuario u : usuarios) {
            if (filtro != null && !filtro.isEmpty()) {
                String f = filtro.toLowerCase();
                String nome = u.getNome() != null ? u.getNome().toLowerCase() : "";
                String user = u.getUsuario() != null ? u.getUsuario().toLowerCase() : "";
                if (!nome.contains(f) && !user.contains(f)) {
                    continue;
                }
            }

            modeloTabela.addRow(new Object[]{
                u.getId(),
                u.getNome(),
                u.getUsuario()
            });
            count++;
        }

        lblTotalUsuarios.setText(count + " usuário" + (count != 1 ? "s" : ""));
    }

    private void filtrar() {
        carregarTabela(txtBusca.getText().trim());
    }

    private void cadastrarUsuario() {
        String nome           = txtNome.getText().trim();
        String usuario        = txtUsuario.getText().trim();
        String senha          = new String(txtSenha.getPassword()).trim();
        String confirmarSenha = new String(txtConfirmarSenha.getPassword()).trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o nome do usuário!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return;
        }

        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o login do usuário!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtUsuario.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha a senha!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtSenha.requestFocus();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, "As senhas digitadas não conferem!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtConfirmarSenha.requestFocus();
            return;
        }

        if (usuarioDAO.existeUsuario(usuario)) {
            JOptionPane.showMessageDialog(this, "Este usuário já está cadastrado no sistema!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtUsuario.requestFocus();
            return;
        }

        Usuario novo = new Usuario(nome, usuario, senha);
        boolean sucesso = usuarioDAO.cadastrar(novo);

        if (sucesso) {
            JOptionPane.showMessageDialog(this,
                    "Usuário \"" + usuario + "\" cadastrado com sucesso!\nID: " + novo.getId(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            carregarTabela(txtBusca.getText().trim());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar usuário no banco de dados!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um usuário na tabela para excluir!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
        String nome = modeloTabela.getValueAt(linha, 1).toString();
        String user = modeloTabela.getValueAt(linha, 2).toString();

        if ("admin".equalsIgnoreCase(user)) {
            JOptionPane.showMessageDialog(this,
                    "O usuário administrador principal não pode ser excluído!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o usuário '" + nome + "' (" + user + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            if (usuarioDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this,
                        "Usuário excluído com sucesso!",
                        "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                carregarTabela(txtBusca.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir usuário!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtUsuario.setText("");
        txtSenha.setText("");
        txtConfirmarSenha.setText("");
        tabela.clearSelection();
        txtNome.requestFocus();
    }

    /**
     * Permite abrir a tela de cadastro em uma janela independente (modal)
     * quando chamada a partir da tela de login.
     */
    public static void abrirJanelaIndependente(Window parent) {
        JDialog dialog = new JDialog(parent, "Cadastro de Usuários – Voigelle", Dialog.ModalityType.APPLICATION_MODAL);
        CadastroUsuarioGUI painel = new CadastroUsuarioGUI();
        dialog.setContentPane(painel.getContentPane());
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
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
                g2.draw(new RoundRectangle2D.Float(x + i, y + i, w - i * 2 - 1, h - i * 2 - 1, 16, 16));
            }
            g2.dispose();
        }
    }
}
