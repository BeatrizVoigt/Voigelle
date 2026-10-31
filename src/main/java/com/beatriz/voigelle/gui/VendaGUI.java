package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.ClienteDAO;
import com.beatriz.voigelle.dao.ProdutoDAO;
import com.beatriz.voigelle.dao.VendaDAO;
import com.beatriz.voigelle.model.Cliente;
import com.beatriz.voigelle.model.ItemVenda;
import com.beatriz.voigelle.model.Produto;
import com.beatriz.voigelle.model.Venda;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de Lançamento de Vendas – Loja Voigelle
 *
 * Permite que a funcionária selecione o cliente, adicione produtos ao carrinho,
 * visualize subtotais e valor total em tempo real e confirme a venda com baixa
 * automática no estoque.
 */
public class VendaGUI extends JInternalFrame {

    // ── Paleta de cores ───────────────────────────────────────────────────────
    private static final Color COR_FUNDO        = new Color(250, 245, 255);
    private static final Color COR_PAINEL       = Color.WHITE;
    private static final Color COR_PRIMARIA     = new Color(180, 0, 180);
    private static final Color COR_PRIMARIA_ESC = new Color(140, 0, 140);
    private static final Color COR_DESTAQUE     = new Color(30, 150, 80);
    private static final Color COR_TEXTO        = new Color(50, 50, 60);
    private static final Color COR_TEXTO_CAMPO  = new Color(60, 60, 70);
    private static final Color COR_BORDA_CAMPO  = new Color(200, 180, 210);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO    = new Font("Segoe UI Light", Font.BOLD, 24);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_LABEL     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_TOTAL     = new Font("Segoe UI", Font.BOLD, 22);

    private final NumberFormat fmtMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── DAOs e Estado ──────────────────────────────────────────────────────────
    private final ClienteDAO clienteDAO;
    private final ProdutoDAO produtoDAO;
    private final VendaDAO   vendaDAO;

    private Venda vendaAtual;
    private List<Produto> listaProdutosDisponiveis;

    // ── Componentes Visuais ───────────────────────────────────────────────────
    private JComboBox<ClienteItem> cbCliente;
    private JComboBox<String>      cbPagamento;
    private JComboBox<ProdutoItem> cbProduto;
    private JSpinner               spQuantidade;
    private JLabel                 lblPrecoUnitario;
    private JLabel                 lblEstoqueDisp;
    private JButton                btnAdicionarItem;
    private JButton                btnRemoverItem;
    private JTable                 tabelaItens;
    private DefaultTableModel      modeloTabela;
    private JLabel                 lblValorTotal;
    private JLabel                 lblTotalItens;
    private JButton                btnFinalizar;
    private JButton                btnLimpar;

    public VendaGUI() {
        super("Lançar Venda – Loja Voigelle", true, true, true, true);
        this.clienteDAO = new ClienteDAO();
        this.produtoDAO = new ProdutoDAO();
        this.vendaDAO   = new VendaDAO();
        this.vendaAtual = new Venda();

        initComponents();
        carregarClientes();
        carregarProdutos();
        atualizarTotais();
    }

    private void initComponents() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 14));
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        painelPrincipal.add(criarCabecalho(), BorderLayout.NORTH);
        painelPrincipal.add(criarCorpo(), BorderLayout.CENTER);
        painelPrincipal.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        pack();
        setSize(880, 640);
    }

    // ── Cabeçalho com Título e Data ──────────────────────────────────────────
    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblTitulo = new JLabel("Lançar Venda no Sistema");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(COR_PRIMARIA);

        JLabel lblSub = new JLabel("Registre as peças vendidas, selecione o cliente e finalize a venda");
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(130, 100, 140));

        info.add(lblTitulo);
        info.add(Box.createVerticalStrut(2));
        info.add(lblSub);

        // Badge com a data de hoje
        JPanel badgeData = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        badgeData.setOpaque(false);
        JLabel lblData = new JLabel("📅 Data: " + LocalDate.now().format(fmtData));
        lblData.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblData.setForeground(new Color(90, 70, 110));
        lblData.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        lblData.setBackground(Color.WHITE);
        lblData.setOpaque(true);
        badgeData.add(lblData);

        painel.add(info, BorderLayout.WEST);
        painel.add(badgeData, BorderLayout.EAST);

        return painel;
    }

    // ── Corpo: Seleção de Cliente/Pagamento, Adicionar Produto e Tabela ───────
    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(0, 12));
        corpo.setOpaque(false);

        // Painel superior com Dados do Cliente e Adição de Produtos
        JPanel painelControles = new JPanel(new GridBagLayout());
        painelControles.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 8, 0);
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1.0;

        painelControles.add(criarCardClientePagamento(), g);

        g.gridy = 1;
        g.insets = new Insets(0, 0, 0, 0);
        painelControles.add(criarCardAdicionarProduto(), g);

        corpo.add(painelControles, BorderLayout.NORTH);
        corpo.add(criarCardTabelaItens(), BorderLayout.CENTER);

        return corpo;
    }

    // Card 1: Cliente e Forma de Pagamento
    private JPanel criarCardClientePagamento() {
        JPanel card = criarPainelCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 10);

        // Cliente
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0;
        card.add(criarLabel("Cliente:"), g);

        cbCliente = new JComboBox<>();
        cbCliente.setFont(FONTE_CAMPO);
        cbCliente.setBackground(Color.WHITE);
        cbCliente.setPreferredSize(new Dimension(280, 32));
        g.gridx = 1;
        g.weightx = 0.6;
        card.add(cbCliente, g);

        // Forma de Pagamento
        g.gridx = 2;
        g.weightx = 0;
        card.add(criarLabel("Forma de Pagamento:"), g);

        cbPagamento = new JComboBox<>(new String[]{
            "PIX", "Cartão de Crédito", "Cartão de Débito", "Dinheiro"
        });
        cbPagamento.setFont(FONTE_CAMPO);
        cbPagamento.setBackground(Color.WHITE);
        cbPagamento.setPreferredSize(new Dimension(170, 32));
        g.gridx = 3;
        g.weightx = 0.4;
        card.add(cbPagamento, g);

        return card;
    }

    // Card 2: Selecionar Produto, Quantidade e Botão Adicionar
    private JPanel criarCardAdicionarProduto() {
        JPanel card = criarPainelCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 10);

        // Produto
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0;
        card.add(criarLabel("Produto:"), g);

        cbProduto = new JComboBox<>();
        cbProduto.setFont(FONTE_CAMPO);
        cbProduto.setBackground(Color.WHITE);
        cbProduto.setPreferredSize(new Dimension(280, 32));
        cbProduto.addActionListener(e -> atualizarDetalhesProdutoSelecionado());
        g.gridx = 1;
        g.weightx = 0.5;
        card.add(cbProduto, g);

        // Preço Unitário & Estoque Disp.
        lblPrecoUnitario = new JLabel("Preço: R$ 0,00");
        lblPrecoUnitario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrecoUnitario.setForeground(COR_PRIMARIA);

        lblEstoqueDisp = new JLabel("Estoque: 0");
        lblEstoqueDisp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstoqueDisp.setForeground(new Color(110, 110, 120));

        JPanel painelPrecoEstoque = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelPrecoEstoque.setOpaque(false);
        painelPrecoEstoque.add(lblPrecoUnitario);
        painelPrecoEstoque.add(lblEstoqueDisp);

        g.gridx = 2;
        g.weightx = 0.3;
        card.add(painelPrecoEstoque, g);

        // Quantidade
        g.gridx = 3;
        g.weightx = 0;
        card.add(criarLabel("Qtd:"), g);

        spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spQuantidade.setFont(FONTE_CAMPO);
        spQuantidade.setPreferredSize(new Dimension(65, 32));
        g.gridx = 4;
        g.weightx = 0;
        card.add(spQuantidade, g);

        // Botão Adicionar
        btnAdicionarItem = criarBotaoPrimario("+ Adicionar");
        btnAdicionarItem.setPreferredSize(new Dimension(110, 34));
        btnAdicionarItem.addActionListener(e -> adicionarItemVenda());
        g.gridx = 5;
        g.weightx = 0;
        card.add(btnAdicionarItem, g);

        return card;
    }

    // Card 3: Tabela com os Itens da Venda
    private JPanel criarCardTabelaItens() {
        JPanel card = criarPainelCard();
        card.setLayout(new BorderLayout(0, 8));

        String[] colunas = {"#", "Produto", "Categoria", "Tamanho", "Cor", "Preço Unit.", "Qtd.", "Subtotal"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabelaItens = new JTable(modeloTabela);
        tabelaItens.setFont(FONTE_CAMPO);
        tabelaItens.setRowHeight(28);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaItens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaItens.getTableHeader().setBackground(new Color(245, 235, 252));
        tabelaItens.getTableHeader().setForeground(COR_PRIMARIA);

        // Alinhamento das colunas
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        tabelaItens.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabelaItens.getColumnModel().getColumn(0).setCellRenderer(centro);
        tabelaItens.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabelaItens.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaItens.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabelaItens.getColumnModel().getColumn(3).setCellRenderer(centro);
        tabelaItens.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabelaItens.getColumnModel().getColumn(4).setCellRenderer(centro);
        tabelaItens.getColumnModel().getColumn(5).setPreferredWidth(85);
        tabelaItens.getColumnModel().getColumn(5).setCellRenderer(direita);
        tabelaItens.getColumnModel().getColumn(6).setPreferredWidth(50);
        tabelaItens.getColumnModel().getColumn(6).setCellRenderer(centro);
        tabelaItens.getColumnModel().getColumn(7).setPreferredWidth(95);
        tabelaItens.getColumnModel().getColumn(7).setCellRenderer(direita);

        JScrollPane scroll = new JScrollPane(tabelaItens);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 220, 240)));
        card.add(scroll, BorderLayout.CENTER);

        // Barra inferior com botão para remover item
        JPanel painelAcaoTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelAcaoTabela.setOpaque(false);
        btnRemoverItem = criarBotaoSecundario("🗑  Remover Item");
        btnRemoverItem.setPreferredSize(new Dimension(140, 32));
        btnRemoverItem.addActionListener(e -> removerItemSelecionado());
        painelAcaoTabela.add(btnRemoverItem);

        card.add(painelAcaoTabela, BorderLayout.SOUTH);

        return card;
    }

    // ── Rodapé: Valor Total e Botões de Fechamento ────────────────────────────
    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(14, 0));
        rodape.setOpaque(false);

        // Painel com Total da Venda destacado
        JPanel cardTotal = criarPainelCard();
        cardTotal.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 8));
        cardTotal.setBackground(new Color(253, 248, 255));

        JLabel lblTotalTexto = new JLabel("TOTAL DA VENDA:");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalTexto.setForeground(COR_TEXTO);

        lblValorTotal = new JLabel("R$ 0,00");
        lblValorTotal.setFont(FONTE_TOTAL);
        lblValorTotal.setForeground(COR_DESTAQUE);

        lblTotalItens = new JLabel("(0 peças)");
        lblTotalItens.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalItens.setForeground(new Color(130, 110, 140));

        cardTotal.add(lblTotalTexto);
        cardTotal.add(lblValorTotal);
        cardTotal.add(lblTotalItens);

        // Botões de Ação
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        painelBotoes.setOpaque(false);

        btnLimpar = criarBotaoSecundario("Limpar Venda");
        btnLimpar.setPreferredSize(new Dimension(130, 42));
        btnLimpar.addActionListener(e -> limparVenda());
        painelBotoes.add(btnLimpar);

        btnFinalizar = criarBotaoPrimario("✓  Finalizar Venda");
        btnFinalizar.setPreferredSize(new Dimension(170, 42));
        btnFinalizar.addActionListener(e -> finalizarVenda());
        painelBotoes.add(btnFinalizar);

        rodape.add(cardTotal, BorderLayout.WEST);
        rodape.add(painelBotoes, BorderLayout.EAST);

        return rodape;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lógica de Carregamento e Negócio
    // ─────────────────────────────────────────────────────────────────────────

    private void carregarClientes() {
        cbCliente.removeAllItems();
        // Opção padrão para venda rápida de balcão
        cbCliente.addItem(new ClienteItem(null, "Consumidor Final / Balcão"));

        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            for (Cliente c : clientes) {
                cbCliente.addItem(new ClienteItem(c, c.getNome() + (c.getCpf() != null && !c.getCpf().isEmpty() ? " (" + c.getCpf() + ")" : "")));
            }
        } catch (Exception ex) {
            System.err.println("Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void carregarProdutos() {
        cbProduto.removeAllItems();
        try {
            listaProdutosDisponiveis = produtoDAO.listarTodos();
            for (Produto p : listaProdutosDisponiveis) {
                cbProduto.addItem(new ProdutoItem(p));
            }
            atualizarDetalhesProdutoSelecionado();
        } catch (Exception ex) {
            System.err.println("Erro ao carregar produtos: " + ex.getMessage());
        }
    }

    private void atualizarDetalhesProdutoSelecionado() {
        ProdutoItem item = (ProdutoItem) cbProduto.getSelectedItem();
        if (item != null && item.produto != null) {
            Produto p = item.produto;
            lblPrecoUnitario.setText("Preço: " + fmtMoeda.format(p.getPreco()));
            lblEstoqueDisp.setText("Estoque: " + p.getQuantidade() + " un.");

            if (p.getQuantidade() <= 0) {
                lblEstoqueDisp.setForeground(Color.RED);
                lblEstoqueDisp.setText("Esgotado!");
                btnAdicionarItem.setEnabled(false);
            } else {
                lblEstoqueDisp.setForeground(p.getQuantidade() <= 5 ? new Color(200, 100, 0) : new Color(30, 150, 80));
                btnAdicionarItem.setEnabled(true);
                spQuantidade.setModel(new SpinnerNumberModel(1, 1, p.getQuantidade(), 1));
            }
        } else {
            lblPrecoUnitario.setText("Preço: R$ 0,00");
            lblEstoqueDisp.setText("Estoque: 0");
            btnAdicionarItem.setEnabled(false);
        }
    }

    private void adicionarItemVenda() {
        ProdutoItem selecionado = (ProdutoItem) cbProduto.getSelectedItem();
        if (selecionado == null || selecionado.produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para adicionar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto prod = selecionado.produto;
        int qtdPedida = (Integer) spQuantidade.getValue();

        if (qtdPedida <= 0) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade válida!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica quantidade já adicionada deste produto no carrinho atual
        int qtdJaNoCarrinho = 0;
        ItemVenda itemExistente = null;
        for (ItemVenda iv : vendaAtual.getItens()) {
            if (iv.getProduto().getIdProduto() == prod.getIdProduto()) {
                qtdJaNoCarrinho += iv.getQuantidade();
                itemExistente = iv;
            }
        }

        if (qtdJaNoCarrinho + qtdPedida > prod.getQuantidade()) {
            JOptionPane.showMessageDialog(this,
                    "Quantidade solicitada (" + (qtdJaNoCarrinho + qtdPedida) + ") excede o estoque disponível (" + prod.getQuantidade() + ")!",
                    "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (itemExistente != null) {
            // Aumenta quantidade no item existente
            itemExistente.setQuantidade(itemExistente.getQuantidade() + qtdPedida);
        } else {
            // Cria novo item
            ItemVenda novoItem = new ItemVenda(0, prod, qtdPedida);
            vendaAtual.getItens().add(novoItem);
        }

        recarregarTabelaItens();
        spQuantidade.setValue(1);
    }

    private void removerItemSelecionado() {
        int linha = tabelaItens.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para remover!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        vendaAtual.getItens().remove(linha);
        recarregarTabelaItens();
    }

    private void recarregarTabelaItens() {
        modeloTabela.setRowCount(0);
        int num = 1;
        for (ItemVenda iv : vendaAtual.getItens()) {
            Produto p = iv.getProduto();
            modeloTabela.addRow(new Object[]{
                num++,
                p.getNome(),
                p.getCategoria(),
                p.getTamanho() != null ? p.getTamanho() : "-",
                p.getCor() != null ? p.getCor() : "-",
                fmtMoeda.format(p.getPreco()),
                iv.getQuantidade(),
                fmtMoeda.format(iv.getSubtotal())
            });
        }
        atualizarTotais();
    }

    private void atualizarTotais() {
        vendaAtual.calcularValorTotal();
        double total = vendaAtual.getValorTotal();
        int totalPecas = 0;
        for (ItemVenda iv : vendaAtual.getItens()) {
            totalPecas += iv.getQuantidade();
        }
        lblValorTotal.setText(fmtMoeda.format(total));
        lblTotalItens.setText("(" + totalPecas + (totalPecas == 1 ? " peça)" : " peças)"));
    }

    private void finalizarVenda() {
        if (vendaAtual.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Adicione ao menos um produto para finalizar a venda!",
                    "Carrinho Vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        vendaAtual.calcularValorTotal();
        ClienteItem clienteItem = (ClienteItem) cbCliente.getSelectedItem();
        vendaAtual.setCliente(clienteItem != null ? clienteItem.cliente : null);
        vendaAtual.setDataVenda(LocalDate.now());

        double total = vendaAtual.getValorTotal();

        String formaPagamento = (String) cbPagamento.getSelectedItem();
        String nomeCliente = (clienteItem != null && clienteItem.cliente != null)
                ? clienteItem.cliente.getNome()
                : "Consumidor Final";

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirmar lançamento da venda?\n\n"
                + "Cliente: " + nomeCliente + "\n"
                + "Pagamento: " + formaPagamento + "\n"
                + "Total: " + fmtMoeda.format(total) + "\n"
                + "Peças: " + vendaAtual.getItens().size() + " itens",
                "Confirmar Venda", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        boolean sucesso = vendaDAO.inserir(vendaAtual);

        if (sucesso) {
            JOptionPane.showMessageDialog(this,
                    "✓ Venda #" + vendaAtual.getIdVenda() + " registrada com sucesso!\n"
                    + "Total: " + fmtMoeda.format(total) + "\n"
                    + "Estoque atualizado automaticamente.",
                    "Venda Realizada", JOptionPane.INFORMATION_MESSAGE);

            limparVenda();
            carregarProdutos(); // Recarrega os estoques atualizados dos produtos
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar a venda no banco de dados!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparVenda() {
        this.vendaAtual = new Venda();
        if (cbCliente.getItemCount() > 0) cbCliente.setSelectedIndex(0);
        if (cbPagamento.getItemCount() > 0) cbPagamento.setSelectedIndex(0);
        spQuantidade.setValue(1);
        recarregarTabelaItens();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers Visuais
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel criarPainelCard() {
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
        card.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(4, new Color(150, 0, 150, 20)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        return card;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_TEXTO);
        return label;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() || getModel().isRollover()
                        ? COR_PRIMARIA_ESC : COR_PRIMARIA);
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
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setForeground(COR_PRIMARIA);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return btn;
    }

    // ── Wrappers para JComboBox ──────────────────────────────────────────────

    private static class ClienteItem {
        final Cliente cliente;
        final String  label;

        ClienteItem(Cliente cliente, String label) {
            this.cliente = cliente;
            this.label   = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class ProdutoItem {
        final Produto produto;

        ProdutoItem(Produto produto) {
            this.produto = produto;
        }

        @Override
        public String toString() {
            if (produto == null) return "-";
            String tam = produto.getTamanho() != null ? " (" + produto.getTamanho() + ")" : "";
            return produto.getNome() + tam + " - R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", produto.getPreco());
        }
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
