package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.ProdutoDAO;
import com.beatriz.voigelle.model.Produto;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Tela de Cadastro de Produtos – Loja Voigelle
 *
 * @author avrsoftware
 */
public class ProdutosGUI extends JFrame {

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

    // ── DAO e campos do formulário ────────────────────────────────────────────
    private ProdutoDAO produtoDAO;

    private JTextField txtNome;
    private JTextField txtCategoria;
    private JTextField txtTamanho;
    private JTextField txtCor;
    private JTextField txtPreco;
    private JTextField txtQuantidade;
    private JButton    btnSalvar;
    private JButton    btnLimpar;

    /**
     * Cria e inicializa a tela de cadastro de produtos.
     */
    public ProdutosGUI() {
        this.produtoDAO = new ProdutoDAO();
        initComponents();
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Construção da interface
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        setTitle("Cadastrar Produto – Voigelle");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
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
                BorderFactory.createEmptyBorder(36, 44, 36, 44)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        int linha   = 0;

        // ── Título ────────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Cadastrar Produto", SwingConstants.CENTER);
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_PRIMARIA);
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 4, 0);
        cartao.add(lblTitulo, gbc);

        // ── Subtítulo ─────────────────────────────────────────────────────────
        JLabel lblSub = new JLabel("Preencha os dados para adicionar ao estoque", SwingConstants.CENTER);
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(140, 100, 150));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 20, 0);
        cartao.add(lblSub, gbc);

        // ── Separador ─────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 200, 230));
        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 22, 0);
        cartao.add(sep, gbc);

        // ── Campos em dois paineis lado a lado ────────────────────────────────
        JPanel painelDuplo = new JPanel(new GridLayout(1, 2, 20, 0));
        painelDuplo.setOpaque(false);
        painelDuplo.add(criarColunaEsquerda());
        painelDuplo.add(criarColunaDireita());

        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 28, 0);
        cartao.add(painelDuplo, gbc);

        // ── Botões ────────────────────────────────────────────────────────────
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 12, 0));
        painelBotoes.setOpaque(false);

        btnLimpar = criarBotaoSecundario("Limpar");
        btnLimpar.addActionListener(e -> limparFormulario());
        painelBotoes.add(btnLimpar);

        btnSalvar = criarBotaoPrimario("Salvar Produto");
        btnSalvar.addActionListener(e -> salvarProduto());
        painelBotoes.add(btnSalvar);

        gbc.gridy  = linha++;
        gbc.insets = new Insets(0, 0, 0, 0);
        cartao.add(painelBotoes, gbc);

        return cartao;
    }

    /** Coluna esquerda: Nome, Categoria, Tamanho */
    private JPanel criarColunaEsquerda() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;
        int linha = 0;

        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Nome do Produto"), g);
        txtNome = criarCampoTexto("Ex: Blusa Floral");
        txtNome.addActionListener(e -> txtCategoria.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtNome, g);

        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Categoria"), g);
        txtCategoria = criarCampoTexto("Ex: Roupas, Calçados…");
        txtCategoria.addActionListener(e -> txtTamanho.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtCategoria, g);

        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Tamanho"), g);
        txtTamanho = criarCampoTexto("Ex: P, M, G, GG, 38…");
        txtTamanho.addActionListener(e -> txtCor.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        col.add(txtTamanho, g);

        return col;
    }

    /** Coluna direita: Cor, Preço, Quantidade */
    private JPanel criarColunaDireita() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;
        int linha = 0;

        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Cor"), g);
        txtCor = criarCampoTexto("Ex: Rosa, Azul Marinho…");
        txtCor.addActionListener(e -> txtPreco.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtCor, g);

        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Preço (R$)"), g);
        txtPreco = criarCampoTexto("Ex: 89,90");
        txtPreco.addActionListener(e -> txtQuantidade.requestFocus());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 16, 0);
        col.add(txtPreco, g);

        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 6, 0);
        col.add(criarLabel("Quantidade em Estoque"), g);
        txtQuantidade = criarCampoTexto("Ex: 50");
        txtQuantidade.addActionListener(e -> btnSalvar.doClick());
        g.gridy  = linha++;
        g.insets = new Insets(0, 0, 0, 0);
        col.add(txtQuantidade, g);

        return col;
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
        campo.setPreferredSize(new Dimension(220, 38));
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
        btn.setPreferredSize(new Dimension(160, 42));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setForeground(COR_PRIMARIA);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lógica de negócio
    // ─────────────────────────────────────────────────────────────────────────

    private void salvarProduto() {
        String nome       = txtNome.getText().trim();
        String categoria  = txtCategoria.getText().trim();
        String tamanho    = txtTamanho.getText().trim();
        String cor        = txtCor.getText().trim();
        String precoStr   = txtPreco.getText().trim().replace(",", ".");
        String qtdStr     = txtQuantidade.getText().trim();

        // Validações básicas
        if (nome.isEmpty()) {
            avisar("O nome do produto não pode estar vazio!", txtNome);
            return;
        }
        if (categoria.isEmpty()) {
            avisar("Informe a categoria do produto!", txtCategoria);
            return;
        }

        double preco;
        int    quantidade;

        try {
            preco = Double.parseDouble(precoStr);
            if (preco < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            avisar("Informe um preço válido (Ex: 89,90)!", txtPreco);
            return;
        }

        try {
            quantidade = Integer.parseInt(qtdStr);
            if (quantidade < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            avisar("Informe uma quantidade válida (número inteiro)!", txtQuantidade);
            return;
        }

        Produto produto = new Produto(0, nome, categoria, tamanho, cor, preco, quantidade);
        produtoDAO.inserir(produto);

        JOptionPane.showMessageDialog(this,
                "Produto \"" + nome + "\" cadastrado com sucesso!\nID gerado: " + produto.getIdProduto(),
                "Produto Salvo", JOptionPane.INFORMATION_MESSAGE);

        limparFormulario();
    }

    private void avisar(String mensagem, JTextField foco) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
        foco.requestFocus();
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtCategoria.setText("");
        txtTamanho.setText("");
        txtCor.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
        txtNome.requestFocus();
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
            java.util.logging.Logger.getLogger(ProdutosGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new ProdutosGUI().setVisible(true));
    }
}
