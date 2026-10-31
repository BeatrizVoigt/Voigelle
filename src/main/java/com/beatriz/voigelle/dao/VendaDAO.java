package com.beatriz.voigelle.dao;

import com.beatriz.voigelle.config.Banco;
import com.beatriz.voigelle.model.ItemVenda;
import com.beatriz.voigelle.model.Venda;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * DAO para gerenciamento de Vendas e Itens de Venda no banco de dados.
 */
public class VendaDAO {

    // Construtor da classe
    public VendaDAO() {
         // Cria as tabelas automaticamente quando o objeto é criado
        criarTabelasSeNaoExistirem();
    }

    /**
     * Garante que as tabelas 'venda' e 'item_venda' existam no banco.
     */
    public void criarTabelasSeNaoExistirem() {
        String sqlVenda = "CREATE TABLE IF NOT EXISTS venda ("
                + "id_venda SERIAL PRIMARY KEY, " //Identificador único da venda
                + "data_venda DATE NOT NULL, " // Data em que a venda foi realizada
                + "valor_total NUMERIC(10, 2) NOT NULL, " // Valor total da venda
                + "cliente_id INTEGER REFERENCES cliente(id_cliente)" // Relacionamento com cliente
                + ")";

        // Comando SQL para criar a tabela de itens da venda
        String sqlItem = "CREATE TABLE IF NOT EXISTS item_venda (" 
                + "id_item SERIAL PRIMARY KEY, " // Identificador único 
                + "venda_id INTEGER NOT NULL REFERENCES venda(id_venda), " 
                + "produto_id INTEGER NOT NULL REFERENCES produto(id_produto), "
                + "quantidade INTEGER NOT NULL, "
                + "subtotal NUMERIC(10, 2) NOT NULL"
                + ")";

         // Abre a conexão com o banco e cria um comando SQL
        try (Connection con = Banco.conectar();
             Statement stmt = con.createStatement()) {
             // Verifica se a conexão existe
            if (con != null) {
                 // Executa o comando para criar a tabela venda
                stmt.executeUpdate(sqlVenda);
                // Executa o comando para criar a tabela item_venda
                stmt.executeUpdate(sqlItem);
            }
            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao verificar/criar tabelas de venda: " + e.getMessage());
        }
    }

    /**
     * Registra uma nova venda, seus itens e dá baixa automática no estoque.
     * Toda a operação é executada dentro de uma única transação SQL.
     *
     * @param venda Objeto contendo os dados da venda, cliente e lista de itens
     * @return true se a venda foi gravada com sucesso, false caso contrário
     */
    public boolean inserir(Venda venda) {
        // Verifica se a venda recebida não está vazia
        if (venda == null) return false;
          // Calcula o valor total com base nos itens da venda
        venda.calcularValorTotal();

         // Comando SQL para inserir uma nova venda
         // Comando SQL para inserir os produtos vendidos
        // Comando SQL para diminuir a quantidade no estoque
        String sqlVenda = "INSERT INTO venda (data_venda, valor_total, cliente_id) VALUES (?, ?, ?)";
        String sqlItem  = "INSERT INTO item_venda (venda_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)";
        String sqlBaixaEstoque = "UPDATE produto SET quantidade = quantidade - ? WHERE id_produto = ?";

         // Cria uma variável para armazenar a conexão
        Connection con = null;
        
        try {
            con = Banco.conectar();// Conecta ao banco de dados
            if (con == null) {  // Verifica se a conexão foi realizada
                System.err.println("Conexão com o banco de dados nula ao lançar venda.");
                return false;
            }
            
            // Desativa o modo automático para controlar a transação manualmente
            con.setAutoCommit(false); // Transação iniciada

            // 1. Grava o cabeçalho da venda
            // Prepara o comando SQL para cadastrar a venda
            try (PreparedStatement stmtVenda = con.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setDate(1, Date.valueOf(venda.getDataVenda())); //Define a data da venda
                stmtVenda.setDouble(2, venda.getValorTotal()); // Define o valor total da venda

                  // Verifica se existe um cliente associado à venda
                if (venda.getCliente() != null && venda.getCliente().getId() > 0) {
                    stmtVenda.setInt(3, venda.getCliente().getId());  // Informa o ID do cliente
                } else {
                    stmtVenda.setNull(3, Types.INTEGER); // Caso não exista cliente, grava um valor nulo
                }

                 // Executa o cadastro da venda
                 // Verifica se alguma linha foi cadastrada
                int linhasVenda = stmtVenda.executeUpdate();
                if (linhasVenda == 0) {
                    con.rollback(); // Cancela a operação
                    return false;
                }

                 // Recupera o ID gerado automaticamente pelo banco
                try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                    if (rs.next()) { // Verifica se existe um ID retornado
                        venda.setIdVenda(rs.getInt(1));  // Guarda o ID na venda
                    }
                }
            }

            // 2. Grava cada item da venda e atualiza o estoque
            try (PreparedStatement stmtItem = con.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtEstoque = con.prepareStatement(sqlBaixaEstoque)) {

                // Percorre todos os itens que fazem parte da venda
                // Define
                for (ItemVenda item : venda.getItens()) {
                    stmtItem.setInt(1, venda.getIdVenda());
                    stmtItem.setInt(2, item.getProduto().getIdProduto());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getSubtotal());
                    stmtItem.executeUpdate();  // Executa o cadastro do item

                     // Recupera o ID gerado para o item cadastrado
                    try (ResultSet rsItem = stmtItem.getGeneratedKeys()) {
                        if (rsItem.next()) { // Verifica se o ID foi retornado
                            item.setIdItem(rsItem.getInt(1)); // Guarda o ID no objeto ItemVenda
                        }
                    }

                    // Baixa do estoque do produto
                    
                    // Define a quantidade que será retirada do estoque
                    stmtEstoque.setInt(1, item.getQuantidade());
                    // Define qual produto terá seu estoque atualizado
                    stmtEstoque.setInt(2, item.getProduto().getIdProduto());
                     // Executa a atualização do estoque
                    stmtEstoque.executeUpdate();
                }
            }

            con.commit();   // Confirma todas as operações no banco de dados
            return true;

            // Mensagem de erro
        } catch (SQLException e) {
            System.err.println("Erro ao salvar venda no banco: " + e.getMessage());
            if (con != null) {  // Verifica se existe uma conexão aberta
                try {
                    con.rollback(); // Desfaz as operações realizadas na transação
                } catch (SQLException ex) {
                    System.err.println("Erro ao efetuar rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally { 
            if (con != null) {  // Verifica se a conexão ainda existe
                try {
                    con.setAutoCommit(true);   // Ativa novamente o modo automático
                    con.close();  // Fecha a conexão com o banco
                } catch (SQLException ignored) {}  // Ignora erros ao fechar a conexão
            }
        }
    }

    /**
     * Retorna a listagem de vendas com dados consolidados para o relatório.
     */
    
    // Método responsável por listar as vendas para o relatório
    public java.util.List<RelatorioVenda> listarRelatorioVendas() {
        // Cria uma lista vazia para armazenar as vendas
        java.util.List<RelatorioVenda> lista = new java.util.ArrayList<>();
        
        // Consulta os dados das vendas, clientes e itens
        String sql = "SELECT v.id_venda, v.data_venda, "
                + "COALESCE(NULLIF(SUM(iv.subtotal), 0), v.valor_total, 0) AS valor_total, "
                + "c.nome AS cliente_nome, "
                + "COALESCE(SUM(iv.quantidade), 0) AS total_itens "
                + "FROM venda v "
                + "LEFT JOIN cliente c ON v.cliente_id = c.id_cliente "
                + "LEFT JOIN item_venda iv ON v.id_venda = iv.venda_id "
                + "GROUP BY v.id_venda, v.data_venda, v.valor_total, c.nome "
                + "ORDER BY v.id_venda DESC";

        // Tenta conectar ao banco e executar a consulta
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

              // Percorre todos os resultados encontrados
            while (rs.next()) {
                Date d = rs.getDate("data_venda");  // Recupera a data da venda
                // Converte a data para LocalDate
                java.time.LocalDate data = (d != null) ? d.toLocalDate() : java.time.LocalDate.now();

                // Cria um objeto com as informações do relatório
                RelatorioVenda rv = new RelatorioVenda(
                        rs.getInt("id_venda"),
                        data,
                        rs.getDouble("valor_total"),
                        rs.getString("cliente_nome"),
                        rs.getInt("total_itens")
                );
                lista.add(rv);   // Adiciona a venda na lista
            }

            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao listar relatório de vendas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Retorna a lista detalhada de produtos vendidos em uma venda específica.
     */
    public java.util.List<ItemDetalhe> listarItensPorVenda(int idVenda) {
        java.util.List<ItemDetalhe> lista = new java.util.ArrayList<>();
        String sql = "SELECT p.nome, p.categoria, p.tamanho, p.cor, p.preco, iv.quantidade, iv.subtotal "
                + "FROM item_venda iv "
                + "JOIN produto p ON iv.produto_id = p.id_produto "
                + "WHERE iv.venda_id = ? "
                + "ORDER BY iv.id_item";

         // Conecta ao banco e prepara a consulta
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);
            try (ResultSet rs = stmt.executeQuery()) {
                
                 // Percorre os produtos encontrados
                while (rs.next()) {
                    
                      // Cria um objeto com os detalhes de cada produto
                    ItemDetalhe item = new ItemDetalhe(
                            rs.getString("nome"),// Nome do produto
                            rs.getString("categoria"),
                            rs.getString("tamanho"),
                            rs.getString("cor"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade"), // Quantidade vendida
                            rs.getDouble("subtotal") // Subtotal do produto
                    );
                    lista.add(item);  // Adiciona o produto à lista
                }
            }

            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens da venda #" + idVenda + ": " + e.getMessage());
        }

        return lista;
    }

    // ── Classes auxiliares para Relatórios ────────────────────────────────────

    // Classe que representa os dados de uma venda no relatório
    public static class RelatorioVenda {
        
          // Declara os atributos da classe
        private final int idVenda;
        private final java.time.LocalDate dataVenda;
        private final double valorTotal;
        private final String clienteNome;
        private final int totalItens;

         // Construtor que recebe os dados da venda
        public RelatorioVenda(int idVenda, java.time.LocalDate dataVenda, double valorTotal, String clienteNome, int totalItens) {
            this.idVenda = idVenda;  // Armazena o ID da venda
            this.dataVenda = dataVenda;  // Armazena a data da venda
            this.valorTotal = valorTotal; // Armazena o valor total
            
            // Se não houver nome de cliente, utiliza Consumidor Final
            this.clienteNome = (clienteNome != null && !clienteNome.trim().isEmpty()) ? clienteNome : "Consumidor Final";
            this.totalItens = totalItens; // Armazena a quantidade total de itens
        }

     
        public int getIdVenda() { return idVenda; }// Método que retorna o ID da venda
        public java.time.LocalDate getDataVenda() { return dataVenda; } // Método que retorna a data da venda
        public double getValorTotal() { return valorTotal; }  // Método que retorna o valor total
        public String getClienteNome() { return clienteNome; } // Método que retorna o nome do cliente
        public int getTotalItens() { return totalItens; }  // Método que retorna a quantidade de itens
    }

    // Classe auxiliar que representa os detalhes de cada produto vendido
    public static class ItemDetalhe {
         // Atributos que armazenam as informações do produto
        private final String produtoNome;
        private final String categoria;
        private final String tamanho;
        private final String cor;
        private final double precoUnitario;
        private final int quantidade;
        private final double subtotal;

        // Construtor que recebe as informações do produto
        public ItemDetalhe(String produtoNome, String categoria, String tamanho, String cor, double precoUnitario, int quantidade, double subtotal) {
            // Armazena dados
            this.produtoNome = produtoNome;
            this.categoria = categoria;
            this.tamanho = tamanho;
            this.cor = cor;
            this.precoUnitario = precoUnitario;
            this.quantidade = quantidade;
            this.subtotal = subtotal;
        }

          // Retorna dados
        public String getProdutoNome() { return produtoNome; }
        public String getCategoria() { return categoria; }
        public String getTamanho() { return tamanho; }
        public String getCor() { return cor; }
        public double getPrecoUnitario() { return precoUnitario; }
        public int getQuantidade() { return quantidade; }
        public double getSubtotal() { return subtotal; }
    }
}
