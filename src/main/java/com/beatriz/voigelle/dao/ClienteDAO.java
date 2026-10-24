package com.beatriz.voigelle.dao;

import com.beatriz.voigelle.config.Banco;
import com.beatriz.voigelle.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public ClienteDAO() {
        criarTabelaSeNaoExistir();
    }

    public void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS cliente ("
                + "id_cliente SERIAL PRIMARY KEY, "
                + "nome VARCHAR(100) NOT NULL, "
                + "cpf VARCHAR(20), "
                + "telefone VARCHAR(30), "
                + "email VARCHAR(100), "
                + "endereco VARCHAR(255)"
                + ")";

        try (Connection con = Banco.conectar();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);

            // Garante que as colunas cpf e endereco existam caso a tabela já existisse
            stmt.executeUpdate("ALTER TABLE cliente ADD COLUMN IF NOT EXISTS cpf VARCHAR(20)");
            stmt.executeUpdate("ALTER TABLE cliente ADD COLUMN IF NOT EXISTS endereco VARCHAR(255)");

        } catch (SQLException e) {
            System.err.println("Erro ao verificar/criar tabela cliente: " + e.getMessage());
        }
    }

    // Cadastra um novo cliente no banco de dados.
    public boolean inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, email, endereco) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getEndereco());

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + e.getMessage());
        }
        return false;
    }

    // Atualiza os dados de um cliente já cadastrado.
    public boolean atualizar(Cliente cliente) {
        String sql = "UPDATE cliente "
                   + "SET nome = ?, cpf = ?, telefone = ?, email = ?, endereco = ? "
                   + "WHERE id_cliente = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getEndereco());
            stmt.setInt(6, cliente.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
        return false;
    }

    // Exclui um cliente usando seu ID.
    public boolean excluir(int id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
        }
        return false;
    }

    // Busca todos os clientes cadastrados e retorna uma lista.
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nome, cpf, telefone, email, endereco "
                   + "FROM cliente ORDER BY nome";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("endereco")
                );
                lista.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }

        return lista;
    }

    // Busca um cliente específico pelo ID.
    public Cliente buscarPorId(int id) {
        String sql = "SELECT id_cliente, nome, cpf, telefone, email, endereco "
                   + "FROM cliente WHERE id_cliente = ?";

        Cliente cliente = null;

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("telefone"),
                            rs.getString("email"),
                            rs.getString("endereco")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
        }

        return cliente;
    }
}
