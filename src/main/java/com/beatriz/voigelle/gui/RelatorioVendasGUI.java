package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.VendaDAO;
import com.beatriz.voigelle.dao.VendaDAO.ItemDetalhe;
import com.beatriz.voigelle.dao.VendaDAO.RelatorioVenda;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Tela de Relatório de Vendas – Loja Voigelle
 *
 * Apresenta o histórico consolidado de vendas, faturamento total,
 * ticket médio, quantidade de peças comercializadas e detalhamento por venda.
 */
public class RelatorioVendasGUI extends JInternalFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(250, 245, 255);
    private static final Color COR_PAINEL       = Color.WHITE;
    private static final Color COR_PRIMARIA     = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC = new Color(140, 0, 140);
    private static final Color COR_DESTAQUE     = new Color(30, 150, 80);
    private static final Color COR_TEXTO        = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO  = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO  = new Color(200, 180, 210);
    private static final Color COR_LINHA_PAR    = new Color(252, 248, 255);
    private static final Color COR_LINHA_IMPAR  = Color.WHITE;
    private static final Color COR_SELECAO      = new Color(230, 180, 240);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO    = new Font("Segoe UI Light", Font.BOLD, 24);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CARD_NUM  = new Font("Segoe UI Light", Font.BOLD, 22);
    private static final Font FONTE_CARD_LBL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONTE_TABELA    = new Font("Segoe UI", Font.PLAIN, 13);

    private final NumberFormat fmtMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── DAO e Estado ──────────────────────────────────────────────────────────
    private final VendaDAO vendaDAO;
    private List<RelatorioVenda> listaTodasVendas;

    // ── Componentes Visuais ───────────────────────────────────────────────────
    private JTextField        txtBusca;
    private JComboBox<String> cbPeriodo;
    private JButton           btnAtualizar;
    private JButton           btnVerDetalhes;

    // Cards
    private JLabel lblTotalVendas;
    private JLabel lblFaturamentoTotal;
    private JLabel lblTicketMedio;
    private JLabel lblTotalPecas;

    // Tabela
    private JTable            tabela;
    private DefaultTableModel modeloTabela;

    public RelatorioVendasGUI() {
        super("Relatório de Vendas – Loja Voigelle", true, true, true, true);
        this.vendaDAO = new VendaDAO();
        this.listaTodasVendas = new ArrayList<>();

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(COR_FUNDO);
        setContentPane(raiz);

        raiz.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(14, 20, 20, 20));

        centro.add(criarPainelCards(), BorderLayout.NORTH);
        centro.add(criarPainelTabela(), BorderLayout.CENTER);

        raiz.add(centro, BorderLayout.CENTER);

        setPreferredSize(new Dimension(940, 600));
        pack();
    }

    // ── Cabeçalho com degradê e filtros ───────────────────────────────────────
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(160, 0, 160),
                        getWidth(), getHeight(), new Color(90, 0, 120)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        cabecalho.setLayout(new BorderLayout(16, 8));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        // Título e Subtítulo
        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("📊 Relatório de Vendas");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Histórico consolidado, faturamento e acompanhamento de vendas");
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(230, 200, 245));

        titulos.add(lblTitulo);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(lblSub);

        // Barra de Ações (Busca, Filtro de Período e Botões)
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acoes.setOpaque(false);

        // Campo de busca
        txtBusca = new JTextField(16);
        txtBusca.setFont(FONTE_CAMPO);
        txtBusca.setPreferredSize(new Dimension(190, 34));
        txtBusca.putClientProperty("JTextField.placeholderText", "Buscar por cliente ou ID...");
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        // ComboBox de Período
        cbPeriodo = new JComboBox<>(new String[]{
            "Todas as Vendas", "Hoje", "Últimos 7 Dias", "Este Mês"
        });
        cbPeriodo.setFont(FONTE_CAMPO);
        cbPeriodo.setPreferredSize(new Dimension(150, 34));
        cbPeriodo.addActionListener(e -> filtrar());

        // Botão Atualizar
        btnAtualizar = new JButton("🔄 Atualizar");
        btnAtualizar.setFont(FONTE_BOTAO);
        btnAtualizar.setPreferredSize(new Dimension(110, 34));
        btnAtualizar.setBackground(new Color(255, 255, 255, 230));
        btnAtualizar.setForeground(COR_PRIMARIA);
        btnAtualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(e -> carregarDados());

        acoes.add(new JLabel("🔍") {{ setForeground(Color.WHITE); }});
        acoes.add(txtBusca);
        acoes.add(cbPeriodo);
        acoes.add(btnAtualizar);

        cabecalho.add(titulos, BorderLayout.WEST);
        cabecalho.add(acoes, BorderLayout.EAST);

        return cabecalho;
    }

    // ── Cards Numéricos de Resumo ─────────────────────────────────────────────
    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 14, 0));
        painel.setOpaque(false);

        lblTotalVendas       = new JLabel("-", SwingConstants.CENTER);
        lblFaturamentoTotal  = new JLabel("-", SwingConstants.CENTER);
        lblTicketMedio       = new JLabel("-", SwingConstants.CENTER);
        lblTotalPecas        = new JLabel("-", SwingConstants.CENTER);

        painel.add(criarCard("💳 Total de Vendas",     lblTotalVendas,      COR_PRIMARIA));
        painel.add(criarCard("💰 Faturamento Total",   lblFaturamentoTotal, COR_DESTAQUE));
        painel.add(criarCard("🏷 Média por Venda",     lblTicketMedio,      new Color(0, 110, 180)));
        painel.add(criarCard("👗 Peças Vendidas",      lblTotalPecas,       new Color(190, 80, 0)));

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
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(4, new Color(150, 0, 150, 20)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1.0;
        g.anchor = GridBagConstraints.CENTER;

        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setFont(FONTE_CARD_LBL);
        lblTit.setForeground(new Color(110, 110, 120));
        card.add(lblTit, g);

        g.gridy = 1;
        g.insets = new Insets(4, 0, 0, 0);
        lblValor.setFont(FONTE_CARD_NUM);
        lblValor.setForeground(corDestaque);
        card.add(lblValor, g);

        return card;
    }

    // ── Painel da Tabela de Vendas ────────────────────────────────────────────
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(0, 10)) {
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
        painel.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(4, new Color(150, 0, 150, 20)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        String[] colunas = {"ID Venda", "Data da Venda", "Cliente", "Peças", "Valor Total (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(COR_SELECAO);
        tabela.setSelectionForeground(Color.BLACK);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));

        // Cabeçalho da tabela
        JTableHeader th = tabela.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(245, 235, 252));
        th.setForeground(COR_PRIMARIA);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 34));

        // Alinhamentos
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centro);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centro);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(320);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(3).setCellRenderer(centro);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(4).setCellRenderer(direita);

        // Duplo clique na linha abre os detalhes da venda
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalhesVendaSelecionada();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 220, 240)));
        painel.add(scroll, BorderLayout.CENTER);

        // Barra inferior da tabela
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);

        JLabel lblDica = new JLabel("💡 Dica: Dê dois cliques em uma venda ou use o botão para ver os produtos vendidos.");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(new Color(130, 120, 140));

        btnVerDetalhes = new JButton("🔍 Ver Itens da Venda");
        btnVerDetalhes.setFont(FONTE_BOTAO);
        btnVerDetalhes.setBackground(COR_PRIMARIA);
        btnVerDetalhes.setForeground(Color.WHITE);
        btnVerDetalhes.setPreferredSize(new Dimension(170, 36));
        btnVerDetalhes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerDetalhes.addActionListener(e -> abrirDetalhesVendaSelecionada());

        rodape.add(lblDica, BorderLayout.WEST);
        rodape.add(btnVerDetalhes, BorderLayout.EAST);

        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Carregamento e Filtros
    // ─────────────────────────────────────────────────────────────────────────

    public void carregarDados() {
        try {
            listaTodasVendas = vendaDAO.listarRelatorioVendas();
            filtrar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados do relatório de vendas: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrar() {
        String busca = txtBusca.getText().trim().toLowerCase();
        int filtroPeriodo = cbPeriodo.getSelectedIndex();
        LocalDate hoje = LocalDate.now();

        modeloTabela.setRowCount(0);

        int countVendas = 0;
        double faturamento = 0.0;
        int totalPecas = 0;

        for (RelatorioVenda v : listaTodasVendas) {
            // Filtro por período
            if (filtroPeriodo == 1) { // Hoje
                if (!v.getDataVenda().isEqual(hoje)) continue;
            } else if (filtroPeriodo == 2) { // Últimos 7 Dias
                if (v.getDataVenda().isBefore(hoje.minusDays(7))) continue;
            } else if (filtroPeriodo == 3) { // Este Mês
                if (v.getDataVenda().getMonth() != hoje.getMonth() || v.getDataVenda().getYear() != hoje.getYear()) {
                    continue;
                }
            }

            // Filtro por texto (cliente ou ID)
            if (!busca.isEmpty()) {
                boolean bateuId = String.valueOf(v.getIdVenda()).contains(busca);
                boolean bateuCliente = v.getClienteNome().toLowerCase().contains(busca);
                if (!bateuId && !bateuCliente) {
                    continue;
                }
            }

            modeloTabela.addRow(new Object[]{
                v.getIdVenda(),
                v.getDataVenda().format(fmtData),
                v.getClienteNome(),
                v.getTotalItens(),
                fmtMoeda.format(v.getValorTotal())
            });

            countVendas++;
            faturamento += v.getValorTotal();
            totalPecas += v.getTotalItens();
        }

        // Atualiza cards
        lblTotalVendas.setText(String.valueOf(countVendas));
        lblFaturamentoTotal.setText(fmtMoeda.format(faturamento));
        double ticketMedio = countVendas > 0 ? (faturamento / countVendas) : 0.0;
        lblTicketMedio.setText(fmtMoeda.format(ticketMedio));
        lblTotalPecas.setText(String.valueOf(totalPecas));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Visualização dos Detalhes dos Itens da Venda
    // ─────────────────────────────────────────────────────────────────────────

    private void abrirDetalhesVendaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma venda na tabela para visualizar os detalhes!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVenda = (Integer) modeloTabela.getValueAt(linha, 0);
        String dataStr = (String) modeloTabela.getValueAt(linha, 1);
        String clienteStr = (String) modeloTabela.getValueAt(linha, 2);
        String totalStr = (String) modeloTabela.getValueAt(linha, 4);

        List<ItemDetalhe> itens = vendaDAO.listarItensPorVenda(idVenda);

        exibirDialogDetalhes(idVenda, dataStr, clienteStr, totalStr, itens);
    }

    private void exibirDialogDetalhes(int idVenda, String dataStr, String clienteStr, String totalStr, List<ItemDetalhe> itens) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Detalhes da Venda #" + idVenda, true);
        dialog.setLayout(new BorderLayout(0, 12));
        dialog.getContentPane().setBackground(COR_FUNDO);

        // Topo do diálogo
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(COR_PRIMARIA);
        painelTopo.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblTit = new JLabel("Venda #" + idVenda + " – " + clienteStr);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Data: " + dataStr);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(240, 220, 250));

        painelTopo.add(lblTit, BorderLayout.WEST);
        painelTopo.add(lblSub, BorderLayout.EAST);
        dialog.add(painelTopo, BorderLayout.NORTH);

        // Tabela de itens
        String[] colunas = {"#", "Produto", "Categoria", "Tamanho", "Cor", "Preço Unit.", "Qtd.", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        int idx = 1;
        for (ItemDetalhe it : itens) {
            model.addRow(new Object[]{
                idx++,
                it.getProdutoNome(),
                it.getCategoria(),
                it.getTamanho() != null ? it.getTamanho() : "-",
                it.getCor() != null ? it.getCor() : "-",
                fmtMoeda.format(it.getPrecoUnitario()),
                it.getQuantidade(),
                fmtMoeda.format(it.getSubtotal())
            });
        }

        JTable tabItens = new JTable(model);
        tabItens.setFont(FONTE_TABELA);
        tabItens.setRowHeight(26);
        tabItens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabItens.getTableHeader().setBackground(new Color(245, 235, 252));
        tabItens.getTableHeader().setForeground(COR_PRIMARIA);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        tabItens.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabItens.getColumnModel().getColumn(0).setCellRenderer(centro);
        tabItens.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabItens.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabItens.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabItens.getColumnModel().getColumn(3).setCellRenderer(centro);
        tabItens.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabItens.getColumnModel().getColumn(4).setCellRenderer(centro);
        tabItens.getColumnModel().getColumn(5).setPreferredWidth(85);
        tabItens.getColumnModel().getColumn(5).setCellRenderer(direita);
        tabItens.getColumnModel().getColumn(6).setPreferredWidth(50);
        tabItens.getColumnModel().getColumn(6).setCellRenderer(centro);
        tabItens.getColumnModel().getColumn(7).setPreferredWidth(90);
        tabItens.getColumnModel().getColumn(7).setCellRenderer(direita);

        JScrollPane scrollItens = new JScrollPane(tabItens);
        scrollItens.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        dialog.add(scrollItens, BorderLayout.CENTER);

        // Rodapé do diálogo com o total e botão fechar
        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setOpaque(false);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(10, 18, 14, 18));

        JLabel lblTotal = new JLabel("Total da Venda: " + totalStr);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(COR_DESTAQUE);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setFont(FONTE_BOTAO);
        btnFechar.setPreferredSize(new Dimension(100, 34));
        btnFechar.addActionListener(e -> dialog.dispose());

        painelRodape.add(lblTotal, BorderLayout.WEST);
        painelRodape.add(btnFechar, BorderLayout.EAST);
        dialog.add(painelRodape, BorderLayout.SOUTH);

        dialog.setSize(680, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ── Borda com sombra suave ───────────────────────────────────────────────
    private static class ShadowBorder extends AbstractBorder {
        private final int   raio;
        private final Color cor;

        ShadowBorder(int raio, Color cor) {
            this.raio = raio;
            this.cor  = cor;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(raio, raio, raio + 2, raio + 2);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < raio; i++) {
                int alpha = (int) (60 * Math.exp(-0.4 * i));
                g2.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), Math.max(0, alpha)));
                g2.draw(new RoundRectangle2D.Float(x + i, y + i, w - i * 2 - 1, h - i * 2 - 1, 16, 16));
            }
            g2.dispose();
        }
    }
}
