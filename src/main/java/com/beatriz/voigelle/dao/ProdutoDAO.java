package com.beatriz.voigelle.dao;

import com.beatriz.voigelle.config.Banco;
import com.beatriz.voigelle.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // Cadastra um novo produto no banco de dados.
    public void inserir(Produto produto) {

        // Comando SQL para inserir os dados do produto.
        String sql = "INSERT INTO produto "
                + "(nome, categoria, tamanho, cor, preco, quantidade) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        // Abre a conexão e prepara o comando para inserir o produto.
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

             // Preenche os campos com os dados do produto.
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setString(3, produto.getTamanho());
            stmt.setString(4, produto.getCor());
            stmt.setDouble(5, produto.getPreco());
            stmt.setInt(6, produto.getQuantidade());

            // Executa o cadastro no banco de dados.
            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente pelo banco.
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setIdProduto(rs.getInt(1));
                }
            }

           // Mensagem de erro
        } catch (SQLException e) {
            System.out.println("Erro ao inserir produto: " + e.getMessage());
        }
    }

    // Atualiza os dados de um produto já cadastrado.
    public void atualizar(Produto produto) {

        String sql = "UPDATE produto SET "
                + "nome = ?, "
                + "categoria = ?, "
                + "tamanho = ?, "
                + "cor = ?, "
                + "preco = ?, "
                + "quantidade = ? "
                + "WHERE id_produto = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Informa os novos dados do produto.
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setString(3, produto.getTamanho());
            stmt.setString(4, produto.getCor());
            stmt.setDouble(5, produto.getPreco());
            stmt.setInt(6, produto.getQuantidade());
            
             // Define o ID do produto que será atualizado.
            stmt.setInt(7, produto.getIdProduto());

             // Executa a atualização no banco.
            stmt.executeUpdate();

            // Mensagem de erro.
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

     // Exclui um produto usando seu ID.
    public void excluir(int id) {

        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

             // Informa o ID do produto que será excluído.
            stmt.setInt(1, id);

            // Executa a exclusão no banco de dados.
            stmt.executeUpdate();

            // Mensagem de erro.
        } catch (SQLException e) {
            System.out.println("Erro ao excluir produto: " + e.getMessage());
        }
    }

     // Busca todos os produtos e retorna uma lista.
    public List<Produto> listarTodos() {

        // Cria uma lista vazia para armazenar os produtos encontrados.
        List<Produto> lista = new ArrayList<>();

         // Busca os produtos e organiza os resultados pelo nome.
        String sql = "SELECT id_produto, nome, categoria, tamanho, "
                + "cor, preco, quantidade "
                + "FROM produto ORDER BY nome";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Percorre todos os produtos encontrados na consulta.
            while (rs.next()) {

                // Cria um objeto Produto com os dados do registro atual.
                Produto produto = new Produto(
                        rs.getInt("id_produto"),
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getString("tamanho"),
                        rs.getString("cor"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade")
                );

                 // Adiciona o produto à lista.
                lista.add(produto);
            }

            // Mensagem de erro.
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

         // Retorna a lista de produtos encontrados.
        return lista;
    }

     // Busca um produto específico pelo ID.
    public Produto buscarPorId(int id) {

        String sql = "SELECT id_produto, nome, categoria, tamanho, "
                + "cor, preco, quantidade "
                + "FROM produto WHERE id_produto = ?";

        // Inicializa a variável sem um produto encontrado.
        Produto produto = null;

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
             // Define o ID que será pesquisado.
            stmt.setInt(1, id);

            // Executa a consulta e recebe os resultados.
            try (ResultSet rs = stmt.executeQuery()) {

                // Verifica se o produto foi encontrado.
                if (rs.next()) {

                    // Cria um objeto Produto com os dados encontrados.
                    produto = new Produto(
                            rs.getInt("id_produto"),
                            rs.getString("nome"),
                            rs.getString("categoria"),
                            rs.getString("tamanho"),
                            rs.getString("cor"),
                            rs.getDouble("preco"),
                            rs.getInt("quantidade")
                    );
                }
            }

            // Mensagem de erro.
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }
        
        // Retorna o produto encontrado ou null se não existir.
        return produto;
    }
}
 