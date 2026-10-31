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
    private JTextField txtNumero;
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JTextField txtEstado;
    private JButton    btnSalvar;
    private JButton    btnLimpar;

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
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBotoes, gbc);

        return cartao;
    }

    /** Coluna esquerda: Nome, CPF, Telefone, E-mail */
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
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Nome Completo *"), g);

        txtNome = criarCampoTexto("Ex: Mariana Souza Silva");
        txtNome.addActionListener(e -> txtCpf.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 14, 0);
        col.add(txtNome, g);

        // CPF
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("CPF"), g);

        txtCpf = criarCampoTexto("000.000.000-00");
        txtCpf.addActionListener(e -> txtTelefone.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 14, 0);
        col.add(txtCpf, g);

        // Telefone
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Telefone / WhatsApp"), g);

        txtTelefone = criarCampoTexto("(00) 00000-0000");
        txtTelefone.addActionListener(e -> txtEmail.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 14, 0);
        col.add(txtTelefone, g);

        // E-mail
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("E-mail"), g);

        txtEmail = criarCampoTexto("cliente@exemplo.com");
        txtEmail.addActionListener(e -> txtEndereco.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        col.add(txtEmail, g);

        return col;
    }

    /** Coluna direita: Endereço, Número, Bairro, Cidade, Estado (cada um em sua linha) */
    private JPanel criarColunaDireita() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;
        int linha = 0;

        // Endereço (Rua / Av.)
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Endereço (Rua / Av.)"), g);

        txtEndereco = criarCampoTexto("Ex: Rua das Flores");
        txtEndereco.addActionListener(e -> txtNumero.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 10, 0);
        col.add(txtEndereco, g);

        // Número
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Número"), g);

        txtNumero = criarCampoTexto("Ex: 123 ou S/N");
        txtNumero.addActionListener(e -> txtBairro.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 10, 0);
        col.add(txtNumero, g);

        // Bairro
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Bairro"), g);

        txtBairro = criarCampoTexto("Ex: Centro");
        txtBairro.addActionListener(e -> txtCidade.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 10, 0);
        col.add(txtBairro, g);

        // Cidade
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Cidade"), g);

        txtCidade = criarCampoTexto("Ex: Blumenau");
        txtCidade.addActionListener(e -> txtEstado.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 10, 0);
        col.add(txtCidade, g);

        // Estado
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 4, 0);
        col.add(criarLabel("Estado (UF)"), g);

        txtEstado = criarCampoTexto("Ex: SC");
        txtEstado.addActionListener(e -> btnSalvar.doClick());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        col.add(txtEstado, g);

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
        preencherCamposEndereco(cliente.getEndereco());

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
        String endereco = montarEnderecoCompleto();

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
        txtNumero.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtEstado.setText("");
        lblStatusEdicao.setText("● Novo Cadastro");
        lblStatusEdicao.setForeground(new Color(30, 150, 80));
        btnSalvar.setText("Salvar Cliente");
        txtNome.requestFocus();
    }

    private String montarEnderecoCompleto() {
        String rua    = txtEndereco.getText().trim();
        String num    = txtNumero.getText().trim();
        String bairro = txtBairro.getText().trim();
        String cidade = txtCidade.getText().trim();
        String estado = txtEstado.getText().trim().toUpperCase();

        if (rua.isEmpty() && num.isEmpty() && bairro.isEmpty() && cidade.isEmpty() && estado.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (!rua.isEmpty()) {
            sb.append(rua);
        }
        if (!num.isEmpty()) {
            if (sb.length() > 0) sb.append(", Nº ");
            sb.append(num);
        }
        if (!bairro.isEmpty()) {
            if (sb.length() > 0) sb.append(" - Bairro: ");
            sb.append(bairro);
        }
        if (!cidade.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(cidade);
        }
        if (!estado.isEmpty()) {
            if (!cidade.isEmpty()) {
                sb.append("/");
            } else if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(estado);
        }
        return sb.toString();
    }

    private void preencherCamposEndereco(String endCompleto) {
        txtEndereco.setText("");
        txtNumero.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtEstado.setText("");

        if (endCompleto == null || endCompleto.trim().isEmpty()) {
            return;
        }

        String resto = endCompleto.trim();

        if (resto.contains(" - Bairro: ")) {
            int idx = resto.indexOf(" - Bairro: ");
            String antes = resto.substring(0, idx).trim();
            String depois = resto.substring(idx + " - Bairro: ".length()).trim();
            resto = antes;
            if (depois.contains(" - ")) {
                int idxHifen = depois.indexOf(" - ");
                txtBairro.setText(depois.substring(0, idxHifen).trim());
                String cidadeUf = depois.substring(idxHifen + 3).trim();
                extrairCidadeEstado(cidadeUf);
            } else if (depois.contains("/")) {
                extrairCidadeEstado(depois);
            } else {
                txtBairro.setText(depois);
            }
        } else if (resto.contains(" - ")) {
            String[] partes = resto.split(" - ");
            if (partes.length >= 4) {
                resto = partes[0].trim();
                txtBairro.setText(partes[1].trim());
                txtCidade.setText(partes[2].trim());
                txtEstado.setText(partes[3].trim());
            } else if (partes.length == 3) {
                resto = partes[0].trim();
                txtBairro.setText(partes[1].trim());
                extrairCidadeEstado(partes[2].trim());
            } else if (partes.length == 2) {
                resto = partes[0].trim();
                if (partes[1].contains("/")) {
                    extrairCidadeEstado(partes[1].trim());
                } else {
                    txtBairro.setText(partes[1].trim());
                }
            }
        }

        if (resto.contains(", Nº ")) {
            int idx = resto.indexOf(", Nº ");
            txtEndereco.setText(resto.substring(0, idx).trim());
            txtNumero.setText(resto.substring(idx + 5).trim());
        } else if (resto.contains(", nº ")) {
            int idx = resto.indexOf(", nº ");
            txtEndereco.setText(resto.substring(0, idx).trim());
            txtNumero.setText(resto.substring(idx + 5).trim());
        } else if (resto.contains(", ")) {
            int idx = resto.lastIndexOf(", ");
            String possivelNum = resto.substring(idx + 2).trim();
            if (possivelNum.matches("^[0-9]+.*$") || possivelNum.equalsIgnoreCase("s/n") || possivelNum.equalsIgnoreCase("sn")) {
                txtEndereco.setText(resto.substring(0, idx).trim());
                txtNumero.setText(possivelNum);
            } else {
                txtEndereco.setText(resto);
            }
        } else {
            txtEndereco.setText(resto);
        }
    }

    private void extrairCidadeEstado(String str) {
        if (str == null || str.isEmpty()) return;
        if (str.contains("/")) {
            String[] parts = str.split("/", 2);
            txtCidade.setText(parts[0].trim());
            txtEstado.setText(parts[1].trim());
        } else if (str.contains(" - ")) {
            String[] parts = str.split(" - ", 2);
            txtCidade.setText(parts[0].trim());
            txtEstado.setText(parts[1].trim());
        } else if (str.matches("^[A-Za-z]{2}$")) {
            txtEstado.setText(str.trim());
        } else {
            txtCidade.setText(str.trim());
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
