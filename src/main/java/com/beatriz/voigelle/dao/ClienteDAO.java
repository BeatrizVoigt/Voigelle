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

    public void inserir(Cliente cliente) {

        String sql = "INSERT INTO cliente (nome, cpf, telefone, email) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir cliente: " + e.getMessage());
        }
    }

    public void atualizar(Cliente cliente) {

        String sql = "UPDATE cliente "
                   + "SET nome = ?, cpf = ?, telefone = ?, email = ? "
                   + "WHERE id_cliente = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setInt(5, cliente.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    public void excluir(int id) {

        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    public List<Cliente> listarTodos() {

        List<Cliente> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nome, cpf, telefone, email "
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
                        rs.getString("email")
                );

                lista.add(cliente);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }

        return lista;
    }

    public Cliente buscarPorId(int id) {

        String sql = "SELECT id_cliente, nome, cpf, telefone, email "
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
                            rs.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }

        return cliente;
    }
}
