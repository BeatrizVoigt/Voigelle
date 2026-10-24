package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.ProdutoDAO;
import com.beatriz.voigelle.model.Produto;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * Tela de Consulta de Estoque – Loja Voigelle
 *
 * Exibe todos os produtos cadastrados com informações completas,
 * totais resumidos e filtro por nome/categoria.
 *
 * @author avrsoftware
 */
public class EstoqueGUI extends JFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO         = new Color(250, 245, 255);
    private static final Color COR_PAINEL        = Color.WHITE;
    private static final Color COR_PRIMARIA      = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC  = new Color(140, 0, 140);
    private static final Color COR_TEXTO         = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO   = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO   = new Color(200, 180, 210);
    private static final Color COR_LINHA_PAR     = new Color(252, 248, 255);
    private static final Color COR_LINHA_IMPAR   = Color.WHITE;
    private static final Color COR_CABECALHO     = new Color(180, 0, 180);
    private static final Color COR_CARD_BORDA    = new Color(230, 210, 240);
    private static final Color COR_ALERTA_BAIXO  = new Color(200, 50, 50);
    private static final Color COR_ALERTA_MEDIO  = new Color(200, 130, 0);
    private static final Color COR_OK            = new Color(30, 150, 80);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO    = new Font("Segoe UI Light", Font.BOLD, 26);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_LABEL     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CARD_NUM  = new Font("Segoe UI Light", Font.BOLD, 22);
    private static final Font FONTE_CARD_LBL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONTE_TABELA    = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Colunas da tabela ─────────────────────────────────────────────────────
    private static final String[] COLUNAS = {
        "ID", "Nome do Produto", "Categoria", "Tamanho", "Cor", "Preço (R$)", "Qtd.", "Situação"
    };

    // ── Componentes ───────────────────────────────────────────────────────────
    private ProdutoDAO             produtoDAO;
    private DefaultTableModel      modeloTabela;
    private JTable                 tabela;
    private JTextField             txtBusca;

    // Cards de resumo
    private JLabel lblTotalProdutos;
    private JLabel lblTotalItens;
    private JLabel lblValorTotal;
    private JLabel lblEstoqueBaixo;

    /**
     * Cria e inicializa a tela de estoque.
     */
    public EstoqueGUI() {
        this.produtoDAO = new ProdutoDAO();
        initComponents();
        carregarDados(null);
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Construção da interface
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        setTitle("Estoque – Voigelle");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(920, 620));

        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(COR_FUNDO);
        setContentPane(raiz);

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        raiz.add(criarCabecalho(), BorderLayout.NORTH);

        // ── Centro: cards + tabela ─────────────────────────────────────────────
        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        centro.add(criarPainelCards(), BorderLayout.NORTH);
        centro.add(criarPainelTabela(), BorderLayout.CENTER);

        raiz.add(centro, BorderLayout.CENTER);

        pack();
        setSize(960, 660);
    }

    /** Cabeçalho roxo com título, subtítulo e barra de busca + botões. */
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradiente vertical roxo
                GradientPaint gp = new GradientPaint(0, 0, new Color(160, 0, 160),
                        0, getHeight(), new Color(200, 30, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cabecalho.setLayout(new BorderLayout(0, 0));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Linha superior: ícone + títulos
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaTitulo.setOpaque(false);

        // Ícone decorativo (emoji/símbolo via label)
        JLabel icone = new JLabel("📦  ");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icone.setForeground(Color.WHITE);
        linhaTitulo.add(icone);

        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Consulta de Estoque");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        titulos.add(lblTitulo);

        JLabel lblSub = new JLabel("Visualize, pesquise e gerencie os produtos cadastrados");
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(255, 220, 255));
        titulos.add(lblSub);

        linhaTitulo.add(titulos);
        cabecalho.add(linhaTitulo, BorderLayout.NORTH);

        // Linha inferior: busca + botões
        JPanel linhaAcoes = new JPanel(new BorderLayout(12, 0));
        linhaAcoes.setOpaque(false);
        linhaAcoes.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        // Campo de busca
        JPanel painelBusca = new JPanel(new BorderLayout(8, 0));
        painelBusca.setOpaque(false);

        JLabel lblLupa = new JLabel("🔍");
        lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblLupa.setForeground(Color.WHITE);
        lblLupa.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        painelBusca.add(lblLupa, BorderLayout.WEST);

        txtBusca = new JTextField();
        txtBusca.setFont(FONTE_CAMPO);
        txtBusca.setForeground(COR_TEXTO_CAMPO);
        txtBusca.setPreferredSize(new Dimension(340, 36));
        txtBusca.setBackground(new Color(255, 252, 255));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 180, 230), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        txtBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou categoria…");
        // Listener em tempo real
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        painelBusca.add(txtBusca, BorderLayout.CENTER);

        linhaAcoes.add(painelBusca, BorderLayout.WEST);

        // Botões
        JPanel painelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBtns.setOpaque(false);

        JButton btnAtualizar = criarBotaoAcao("↺  Atualizar", new Color(255, 255, 255, 40));
        btnAtualizar.addActionListener(e -> {
            txtBusca.setText("");
            carregarDados(null);
        });
        painelBtns.add(btnAtualizar);

        JButton btnExportar = criarBotaoAcao("＋ Novo Produto", new Color(255, 255, 255, 60));
        btnExportar.addActionListener(e -> {
            ProdutosGUI tela = new ProdutosGUI();
            tela.setVisible(true);
            // Recarrega ao fechar o cadastro
            tela.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(WindowEvent e) {
                    carregarDados(txtBusca.getText().trim());
                }
            });
        });
        painelBtns.add(btnExportar);

        linhaAcoes.add(painelBtns, BorderLayout.CENTER);
        cabecalho.add(linhaAcoes, BorderLayout.SOUTH);

        return cabecalho;
    }

    /** Quatro cards de resumo numérico. */
    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 14, 0));
        painel.setOpaque(false);

        lblTotalProdutos = new JLabel("–", SwingConstants.CENTER);
        lblTotalItens    = new JLabel("–", SwingConstants.CENTER);
        lblValorTotal    = new JLabel("–", SwingConstants.CENTER);
        lblEstoqueBaixo  = new JLabel("–", SwingConstants.CENTER);

        painel.add(criarCard("🏷️ Produtos",       lblTotalProdutos, COR_PRIMARIA));
        painel.add(criarCard("📦 Itens em Estoque", lblTotalItens,    new Color(60, 130, 200)));
        painel.add(criarCard("💰 Valor Total",      lblValorTotal,    new Color(30, 150, 80)));
        painel.add(criarCard("⚠️ Estoque Baixo",    lblEstoqueBaixo,  new Color(200, 90, 0)));

        return painel;
    }

    private JPanel criarCard(String titulo, JLabel lblValor, Color corDestaque) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_PAINEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                // Faixa colorida no topo
                g2.setColor(corDestaque);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 5, 0, 0));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(6, new Color(100, 0, 100, 25)),
                BorderFactory.createEmptyBorder(14, 12, 14, 12)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(FONTE_CARD_LBL);
        lblTitulo.setForeground(new Color(120, 80, 130));
        g.gridy = 0; g.insets = new Insets(6, 0, 6, 0);
        card.add(lblTitulo, g);

        lblValor.setFont(FONTE_CARD_NUM);
        lblValor.setForeground(corDestaque);
        g.gridy = 1; g.insets = new Insets(0, 0, 4, 0);
        card.add(lblValor, g);

        return card;
    }

    /** Painel com a tabela de produtos. */
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel() {
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
        painel.setOpaque(false);
        painel.setLayout(new BorderLayout());
        painel.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(6, new Color(100, 0, 100, 25)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Modelo da tabela
        modeloTabela = new DefaultTableModel(COLUNAS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(modeloTabela) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                // Fundo zebrado
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? COR_LINHA_PAR : COR_LINHA_IMPAR);
                } else {
                    c.setBackground(new Color(230, 180, 240));
                }
                // Cor da coluna Situação
                if (col == 7 && c instanceof JLabel) {
                    String sit = getValueAt(row, col).toString();
                    if (sit.contains("Crítico"))  c.setForeground(COR_ALERTA_BAIXO);
                    else if (sit.contains("Baixo")) c.setForeground(COR_ALERTA_MEDIO);
                    else                            c.setForeground(COR_OK);
                } else {
                    c.setForeground(COR_TEXTO);
                }
                return c;
            }
        };

        estilizarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_PAINEL);
        scroll.setBackground(COR_PAINEL);

        painel.add(scroll, BorderLayout.CENTER);

        // Rodapé com total de linhas exibidas
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COR_CARD_BORDA),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        JLabel lblRodape = new JLabel();
        lblRodape.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRodape.setForeground(new Color(150, 100, 160));
        rodape.add(lblRodape);

        // Atualiza rodapé ao mudar dados
        modeloTabela.addTableModelListener(e -> {
            int n = modeloTabela.getRowCount();
            lblRodape.setText(n + " produto" + (n != 1 ? "s" : "") + " exibido" + (n != 1 ? "s" : ""));
        });

        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private void estilizarTabela() {
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(34);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(new Color(230, 180, 240));
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setFocusable(false);
        tabela.getTableHeader().setReorderingAllowed(false);

        // Cabeçalho personalizado
        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COR_CABECALHO);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setBackground(COR_CABECALHO);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        // Alinhamento das colunas
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        direita.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        tabela.getColumnModel().getColumn(0).setCellRenderer(centralizado); // ID
        tabela.getColumnModel().getColumn(3).setCellRenderer(centralizado); // Tamanho
        tabela.getColumnModel().getColumn(5).setCellRenderer(direita);      // Preço
        tabela.getColumnModel().getColumn(6).setCellRenderer(centralizado); // Qtd
        tabela.getColumnModel().getColumn(7).setCellRenderer(centralizado); // Situação

        // Larguras das colunas
        int[] larguras = {50, 200, 120, 80, 100, 100, 60, 100};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Carregamento de dados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Carrega ou filtra os produtos exibidos na tabela.
     *
     * @param filtro texto de filtro (null ou vazio = todos)
     */
    private void carregarDados(String filtro) {
        modeloTabela.setRowCount(0);

        List<Produto> produtos = produtoDAO.listarTodos();

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        int totalItens    = 0;
        double valorTotal = 0.0;
        int estoqueBaixo  = 0;

        for (Produto p : produtos) {
            // Filtro por nome ou categoria
            if (filtro != null && !filtro.isEmpty()) {
                String f = filtro.toLowerCase();
                if (!p.getNome().toLowerCase().contains(f)
                        && !p.getCategoria().toLowerCase().contains(f)) {
                    continue;
                }
            }

            String situacao = classificarEstoque(p.getQuantidade());

            modeloTabela.addRow(new Object[]{
                p.getIdProduto(),
                p.getNome(),
                p.getCategoria(),
                p.getTamanho() != null ? p.getTamanho() : "–",
                p.getCor()     != null ? p.getCor()     : "–",
                fmt.format(p.getPreco()),
                p.getQuantidade(),
                situacao
            });

            totalItens += p.getQuantidade();
            valorTotal += p.getPreco() * p.getQuantidade();
            if (p.getQuantidade() <= 5) estoqueBaixo++;
        }

        // Atualiza cards de resumo
        lblTotalProdutos.setText(String.valueOf(modeloTabela.getRowCount()));
        lblTotalItens.setText(String.valueOf(totalItens));
        lblValorTotal.setText(fmt.format(valorTotal));
        lblEstoqueBaixo.setText(String.valueOf(estoqueBaixo));
    }

    /** Classifica o nível de estoque de um produto. */
    private String classificarEstoque(int quantidade) {
        if (quantidade == 0)  return "Esgotado";
        if (quantidade <= 3)  return "⚠ Crítico";
        if (quantidade <= 10) return "↓ Baixo";
        return "✔ Normal";
    }

    private void filtrar() {
        carregarDados(txtBusca.getText().trim());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers de componentes
    // ─────────────────────────────────────────────────────────────────────────

    private JButton criarBotaoAcao(String texto, Color fundo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(255, 255, 255, 80) : fundo;
                g2.setColor(bg);
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
        btn.setPreferredSize(new Dimension(148, 34));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return btn;
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
            java.util.logging.Logger.getLogger(EstoqueGUI.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new EstoqueGUI().setVisible(true));
    }
}
