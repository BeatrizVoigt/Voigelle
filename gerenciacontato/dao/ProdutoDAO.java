package gerenciacontato.dao;

import gerenciacontato.config.Banco;
import gerenciacontato.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto produto) {

        String sql = "INSERT INTO produto "
                + "(nome, categoria, tamanho, cor, preco, quantidade) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setString(3, produto.getTamanho());
            stmt.setString(4, produto.getCor());
            stmt.setDouble(5, produto.getPreco());
            stmt.setInt(6, produto.getQuantidade());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    produto.setIdProduto(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir produto: " + e.getMessage());
        }
    }

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

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria());
            stmt.setString(3, produto.getTamanho());
            stmt.setString(4, produto.getCor());
            stmt.setDouble(5, produto.getPreco());
            stmt.setInt(6, produto.getQuantidade());
            stmt.setInt(7, produto.getIdProduto());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void excluir(int id) {

        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir produto: " + e.getMessage());
        }
    }

    public List<Produto> listarTodos() {

        List<Produto> lista = new ArrayList<>();

        String sql = "SELECT id_produto, nome, categoria, tamanho, "
                + "cor, preco, quantidade "
                + "FROM produto ORDER BY nome";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Produto produto = new Produto(
                        rs.getInt("id_produto"),
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getString("tamanho"),
                        rs.getString("cor"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade")
                );

                lista.add(produto);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

        return lista;
    }

    public Produto buscarPorId(int id) {

        String sql = "SELECT id_produto, nome, categoria, tamanho, "
                + "cor, preco, quantidade "
                + "FROM produto WHERE id_produto = ?";

        Produto produto = null;

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

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

        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        }

        return produto;
    }
}
