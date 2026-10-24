package com.beatriz.voigelle.dao;

import com.beatriz.voigelle.config.Banco;
import com.beatriz.voigelle.model.Contato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {

    // Salva um novo contato no banco de dados.
    public void salvar(Contato contato) {

        // Comando SQL para inserir os dados do contato.
        String sql = "INSERT INTO contato (nome, telefone, email) "
                + "VALUES (?, ?, ?)";

        // Abre a conexão e prepara o comando SQL.
        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Preenche os campos com os dados do contato.
            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getTelefone());
            ps.setString(3, contato.getEmail());

            // Executa o cadastro no banco de dados.
            ps.executeUpdate();

            // Mensagem de erro
        } catch (SQLException ex) {
            System.err.println("Erro ao salvar contato: " + ex.getMessage());
        }
    }

    // Busca todos os contatos cadastrados e retorna uma lista.
    public List<Contato> listar() {

        // Cria uma lista vazia para armazenar os contatos.
        List<Contato> contatos = new ArrayList<>();

        // Busca os contatos e organiza os resultados pelo nome.
        String sql = "SELECT id, nome, telefone, email "
                + "FROM contato ORDER BY nome";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Percorre os resultados da consulta.
            while (rs.next()) {

                // Obtém o ID e o nome do contato atual
                int id = rs.getInt("id");
                String nome = rs.getString("nome");

                // Obtém o telefone do contato.
                String telefone = rs.getString("telefone");

                // Se o telefone estiver vazio no banco (NULL),
                // define o valor como "0".
                if (telefone == null) {
                    telefone = "0";
                }

                // Obtém o e-mail do contato.
                String email = rs.getString("email");

                // Cria um objeto Contato com os dados encontrados.
                Contato contato = new Contato(
                        id,
                        nome,
                        telefone,
                        email
                );

                // Adiciona o contato à lista.
                contatos.add(contato);
            }

            // Mensagem de erro.
        } catch (SQLException ex) {
            System.err.println("Erro ao listar contatos: " + ex.getMessage());
        }

        // Retorna a lista de contatos encontrados.
        return contatos;
    }
    
    // Atualiza os dados de um contato e informa se houve alteração.
    public boolean atualizar(Contato contato) {

        // Verifica se o nome foi preenchido e não contém apenas espaços.
        boolean temNome = contato.getNome() != null
                && !contato.getNome().trim().isEmpty();

        String sql;

        // Se houver nome, atualiza nome, telefone e e-mail.
        if (temNome) {

            sql = "UPDATE contato "
                    + "SET nome = ?, telefone = ?, email = ? "
                    + "WHERE id = ?";

          // Se não houver nome, atualiza somente telefone e e-mail.
        } else {

            sql = "UPDATE contato "
                    + "SET telefone = ?, email = ? "
                    + "WHERE id = ?";
        }

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Controla a posição dos parâmetros do comando SQL.
            int idx = 1;

            // Só atualiza o nome se ele tiver sido preenchido.
            if (temNome) {
                ps.setString(idx++, contato.getNome());
            }

            // Preenche os parâmetros de telefone e e-mail.
            ps.setString(idx++, contato.getTelefone());
            ps.setString(idx++, contato.getEmail());
            
             // Informa o ID do contato que será atualizado.
            ps.setInt(idx, contato.getId());

            // Retorna true(VERDADEIRO) se pelo menos um registro foi alterado.
            return ps.executeUpdate() > 0;

           // Mensagem de erro.
        } catch (SQLException ex) {
            System.err.println("Erro ao atualizar contato: " + ex.getMessage());
        }

        // Retorna false se ocorrer um erro.
        return false;
    }

    // Exclui um contato usando seu ID.
    public boolean excluir(int id) {

        String sql = "DELETE FROM contato WHERE id = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Define o ID do contato que será excluído.
            ps.setInt(1, id);

            // Retorna true se pelo menos um registro foi excluído.
            return ps.executeUpdate() > 0;

            // Mensagem de erro.
        } catch (SQLException ex) {
            System.err.println("Erro ao excluir contato: " + ex.getMessage());
        }

        // Retorna false se ocorrer um erro.
        return false;
    }

     // Busca um contato específico pelo ID.
    public Contato buscarPorId(int id) {

        String sql = "SELECT id, nome, telefone, email "
                + "FROM contato WHERE id = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Define o ID que será pesquisado.
            ps.setInt(1, id);

            // Executa a consulta e recebe os resultados.
            try (ResultSet rs = ps.executeQuery()) {

                // Verifica se o contato foi encontrado.
                if (rs.next()) {

                     // Obtém o nome do contato.
                    String nome = rs.getString("nome");

                    // Obtém o telefone do contato.
                    String telefone = rs.getString("telefone");

                    // Se o telefone for NULL, utiliza "0".
                    if (telefone == null) {
                        telefone = "0";
                    }

                    // Obtém o e-mail do contato.
                    String email = rs.getString("email");

                    // Retorna o objeto com os dados encontrados.
                    return new Contato(
                            id,
                            nome,
                            telefone,
                            email
                    );
                }
            }

            // Mensagem de erro.
        } catch (SQLException ex) {
            System.err.println(
                    "Erro ao buscar contato por ID: "
                    + ex.getMessage()
            );
        }

        // Retorna null se nenhum contato for encontrado ou ocorrer um erro.
        return null;
    }
}
