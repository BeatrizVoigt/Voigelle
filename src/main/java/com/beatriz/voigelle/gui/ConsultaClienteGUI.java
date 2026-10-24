package com.beatriz.voigelle.gui;

import com.beatriz.voigelle.dao.ClienteDAO;
import com.beatriz.voigelle.model.Cliente;
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
 * Tela de Consulta de Clientes – Loja Voigelle
 *
 * Focada exclusivamente na busca, visualização e acompanhamento de clientes,
 * com indicadores estatísticos, filtros dinâmicos e opções de edição/exclusão.
 *
 * @author avrsoftware
 */
public class ConsultaClienteGUI extends JInternalFrame {

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
    private static final Color COR_SELECAO       = new Color(230, 180, 240);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FONTE_TITULO    = new Font("Segoe UI Light", Font.BOLD, 24);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONTE_CARD_NUM  = new Font("Segoe UI Light", Font.BOLD, 22);
    private static final Font FONTE_CARD_LBL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONTE_TABELA    = new Font("Segoe UI", Font.PLAIN, 13);

    // ── DAO e Componentes ─────────────────────────────────────────────────────
    private ClienteDAO        clienteDAO;
    private DefaultTableModel modeloTabela;
    private JTable            tabela;
    private JTextField        txtBusca;

    // Cards numéricos de resumo
    private JLabel lblTotalClientes;
    private JLabel lblTotalComEndereco;
    private JLabel lblTotalComTelefone;
    private JLabel lblTotalComEmail;

    /**
     * Cria e inicializa a tela de consulta de clientes.
     */
    public ConsultaClienteGUI() {
        super("Consulta de Clientes", true, true, true, true);
        this.clienteDAO = new ClienteDAO();
        initComponents();
        carregarDados(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Construção da interface
    // ─────────────────────────────────────────────────────────────────────────

    private void initComponents() {
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(COR_FUNDO);
        setContentPane(raiz);

        // Cabeçalho com banner roxo degradê
        raiz.add(criarCabecalho(), BorderLayout.NORTH);

        // Centro: cards de resumo + tabela de clientes
        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(14, 20, 20, 20));

        centro.add(criarPainelCards(),  BorderLayout.NORTH);
        centro.add(criarPainelTabela(), BorderLayout.CENTER);

        raiz.add(centro, BorderLayout.CENTER);

        setPreferredSize(new Dimension(940, 580));
        pack();
    }

    /** Cabeçalho degradê com título, subtítulo, barra de busca e botões. */
    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel() {
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
        cabecalho.setLayout(new BorderLayout(0, 0));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        // Linha superior: Título e subtítulo
        JPanel linhaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaTitulo.setOpaque(false);

        JLabel icone = new JLabel("👥  ");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        icone.setForeground(Color.WHITE);
        linhaTitulo.add(icone);

        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Consulta de Clientes");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        titulos.add(lblTitulo);

        JLabel lblSub = new JLabel("Pesquise, visualize detalhes e gerencie a base de clientes cadastrados");
        lblSub.setFont(FONTE_SUBTITULO);
        lblSub.setForeground(new Color(255, 220, 255));
        titulos.add(lblSub);

        linhaTitulo.add(titulos);
        cabecalho.add(linhaTitulo, BorderLayout.NORTH);

        // Linha inferior: busca em tempo real + botões de ação
        JPanel linhaAcoes = new JPanel(new BorderLayout(12, 0));
        linhaAcoes.setOpaque(false);
        linhaAcoes.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        // Campo de busca
        JPanel painelBusca = new JPanel(new BorderLayout(8, 0));
        painelBusca.setOpaque(false);

        JLabel lblLupa = new JLabel("🔍");
        lblLupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblLupa.setForeground(Color.WHITE);
        painelBusca.add(lblLupa, BorderLayout.WEST);

        txtBusca = new JTextField();
        txtBusca.setFont(FONTE_CAMPO);
        txtBusca.setForeground(COR_TEXTO_CAMPO);
        txtBusca.setPreferredSize(new Dimension(340, 34));
        txtBusca.setBackground(new Color(255, 252, 255));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 180, 230), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        txtBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome, CPF ou cidade…");
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e)  { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        painelBusca.add(txtBusca, BorderLayout.CENTER);
        linhaAcoes.add(painelBusca, BorderLayout.WEST);

        // Botões no topo
        JPanel painelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBtns.setOpaque(false);

        JButton btnAtualizar = criarBotaoBanner("↺  Atualizar", new Color(255, 255, 255, 45));
        btnAtualizar.addActionListener(e -> {
            txtBusca.setText("");
            carregarDados(null);
        });
        painelBtns.add(btnAtualizar);

        JButton btnNovo = criarBotaoBanner("＋ Novo Cliente", new Color(255, 255, 255, 75));
        btnNovo.addActionListener(e -> abrirNovoCadastro());
        painelBtns.add(btnNovo);

        linhaAcoes.add(painelBtns, BorderLayout.CENTER);
        cabecalho.add(linhaAcoes, BorderLayout.SOUTH);

        return cabecalho;
    }

    /** Painel de cards com estatísticas da base de clientes. */
    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 12, 0));
        painel.setOpaque(false);

        lblTotalClientes     = new JLabel("–", SwingConstants.CENTER);
        lblTotalComEndereco  = new JLabel("–", SwingConstants.CENTER);
        lblTotalComTelefone  = new JLabel("–", SwingConstants.CENTER);
        lblTotalComEmail     = new JLabel("–", SwingConstants.CENTER);

        painel.add(criarCard("👥 Total de Clientes", lblTotalClientes,    COR_PRIMARIA));
        painel.add(criarCard("📍 Com Endereço",     lblTotalComEndereco, new Color(30, 150, 80)));
        painel.add(criarCard("📞 Com Telefone",     lblTotalComTelefone, new Color(60, 130, 200)));
        painel.add(criarCard("✉️ Com E-mail",       lblTotalComEmail,    new Color(200, 120, 0)));

        return painel;
    }

    private JPanel criarCard(String titulo, JLabel lblValor, Color corDestaque) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_PAINEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(corDestaque);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 4, 0, 0));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(5, new Color(100, 0, 100, 25)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(FONTE_CARD_LBL);
        lblTitulo.setForeground(new Color(120, 80, 130));
        g.gridy = 0; g.insets = new Insets(4, 0, 4, 0);
        card.add(lblTitulo, g);

        lblValor.setFont(FONTE_CARD_NUM);
        lblValor.setForeground(corDestaque);
        g.gridy = 1; g.insets = new Insets(0, 0, 2, 0);
        card.add(lblValor, g);

        return card;
    }

    /** Painel da tabela com botões de ação e rodapé. */
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
        painel.setBorder(new ShadowBorder(6, new Color(100, 0, 100, 25)));

        // Barra superior de ações sobre a seleção
        JPanel barraAcoes = new JPanel(new BorderLayout(10, 0));
        barraAcoes.setOpaque(false);
        barraAcoes.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));

        JLabel lblLista = new JLabel("Resultados da Consulta");
        lblLista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLista.setForeground(COR_PRIMARIA);
        barraAcoes.add(lblLista, BorderLayout.WEST);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoesAcao.setOpaque(false);

        JButton btnEditar = criarBotaoAcao("✏ Editar Selecionado", new Color(238, 222, 248), COR_PRIMARIA);
        btnEditar.addActionListener(e -> editarSelecionado());
        painelBotoesAcao.add(btnEditar);

        JButton btnExcluir = criarBotaoAcao("🗑 Excluir Selecionado", new Color(235, 70, 90), Color.WHITE);
        btnExcluir.addActionListener(e -> excluirSelecionado());
        painelBotoesAcao.add(btnExcluir);

        barraAcoes.add(painelBotoesAcao, BorderLayout.EAST);
        painel.add(barraAcoes, BorderLayout.NORTH);

        // Modelo da tabela
        String[] colunas = {"ID", "Nome Completo", "CPF", "Telefone", "E-mail", "Endereço"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
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

        estilizarTabela();

        // Duplo clique na linha abre para edição
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSelecionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_PAINEL);
        painel.add(scroll, BorderLayout.CENTER);

        // Rodapé
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COR_CARD_BORDA),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        JLabel lblDica = new JLabel("Dica: Dê duplo clique em qualquer linha para abrir a edição do cliente.");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(new Color(150, 100, 160));
        rodape.add(lblDica, BorderLayout.WEST);

        JLabel lblContador = new JLabel();
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblContador.setForeground(COR_PRIMARIA);
        rodape.add(lblContador, BorderLayout.EAST);

        modeloTabela.addTableModelListener(e -> {
            int n = modeloTabela.getRowCount();
            lblContador.setText(n + " cliente" + (n != 1 ? "s" : "") + " exibido" + (n != 1 ? "s" : ""));
        });

        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private void estilizarTabela() {
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(32);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(COR_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO);
        tabela.setFocusable(false);
        tabela.getTableHeader().setReorderingAllowed(false);

        // Cabeçalho da tabela
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
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return lbl;
            }
        });

        // Alinhamento das colunas
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centralizado); // ID
        tabela.getColumnModel().getColumn(2).setCellRenderer(centralizado); // CPF
        tabela.getColumnModel().getColumn(3).setCellRenderer(centralizado); // Telefone

        // Larguras
        int[] larguras = {45, 170, 110, 110, 170, 200};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Carregamento de Dados e Ações
    // ─────────────────────────────────────────────────────────────────────────

    public void carregarDados(String filtro) {
        modeloTabela.setRowCount(0);
        List<Cliente> clientes = clienteDAO.listarTodos();

        int total       = 0;
        int comEndereco = 0;
        int comTelefone = 0;
        int comEmail    = 0;

        for (Cliente c : clientes) {
            String nome     = c.getNome() != null ? c.getNome() : "";
            String cpf      = c.getCpf() != null ? c.getCpf() : "";
            String telefone = c.getTelefone() != null ? c.getTelefone() : "";
            String email    = c.getEmail() != null ? c.getEmail() : "";
            String endereco = c.getEndereco() != null ? c.getEndereco() : "";

            if (!endereco.trim().isEmpty()) comEndereco++;
            if (!telefone.trim().isEmpty()) comTelefone++;
            if (!email.trim().isEmpty())    comEmail++;

            // Filtro
            if (filtro != null && !filtro.trim().isEmpty()) {
                String f = filtro.toLowerCase();
                boolean match = nome.toLowerCase().contains(f)
                        || cpf.toLowerCase().contains(f)
                        || email.toLowerCase().contains(f)
                        || endereco.toLowerCase().contains(f);
                if (!match) continue;
            }

            modeloTabela.addRow(new Object[]{
                c.getId(),
                nome,
                cpf.isEmpty() ? "–" : cpf,
                telefone.isEmpty() ? "–" : telefone,
                email.isEmpty() ? "–" : email,
                endereco.isEmpty() ? "–" : endereco
            });
            total++;
        }

        lblTotalClientes.setText(String.valueOf(clientes.size()));
        lblTotalComEndereco.setText(String.valueOf(comEndereco));
        lblTotalComTelefone.setText(String.valueOf(comTelefone));
        lblTotalComEmail.setText(String.valueOf(comEmail));
    }

    private void filtrar() {
        carregarDados(txtBusca.getText().trim());
    }

    private void abrirNovoCadastro() {
        abrirTelaGerencia(null);
    }

    private void editarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na tabela para editar!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
        Cliente c = clienteDAO.buscarPorId(id);
        if (c != null) {
            abrirTelaGerencia(c);
        }
    }

    private void abrirTelaGerencia(Cliente c) {
        JDesktopPane desktop = getDesktopPane();
        if (desktop != null) {
            // Procura se já existe a tela de cadastro aberta
            for (JInternalFrame f : desktop.getAllFrames()) {
                if (f instanceof GerenciaClienteGUI) {
                    GerenciaClienteGUI g = (GerenciaClienteGUI) f;
                    if (c != null) {
                        g.carregarCliente(c);
                    }
                    g.setVisible(true);
                    g.toFront();
                    try {
                        g.setSelected(true);
                    } catch (java.beans.PropertyVetoException ignored) {
                    }
                    return;
                }
            }
            // Não existe: cria nova
            GerenciaClienteGUI gerencia = new GerenciaClienteGUI(c);
            desktop.add(gerencia);
            gerencia.setVisible(true);
            try {
                gerencia.setSelected(true);
            } catch (java.beans.PropertyVetoException ignored) {
            }
        }
    }

    private void excluirSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na tabela para excluir!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
        String nome = modeloTabela.getValueAt(linha, 1).toString();

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o cliente \"" + nome + "\"?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            if (clienteDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this,
                        "Cliente excluído com sucesso!",
                        "Confirmação", JOptionPane.INFORMATION_MESSAGE);
                carregarDados(txtBusca.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir cliente no banco de dados!",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers de botões
    // ─────────────────────────────────────────────────────────────────────────

    private JButton criarBotaoBanner(String texto, Color fundo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(255, 255, 255, 100) : fundo;
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
        btn.setPreferredSize(new Dimension(140, 34));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return btn;
    }

    private JButton criarBotaoAcao(String texto, Color bgCor, Color textoCor) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? bgCor.darker() : bgCor;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONTE_BOTAO);
        btn.setForeground(textoCor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 32));
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
}
