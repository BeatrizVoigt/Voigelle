package com.beatriz.voigelle.dao;

import com.beatriz.voigelle.config.Banco;
import com.beatriz.voigelle.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

     // Ao criar um UsuarioDAO, verifica se a tabela precisa ser criada.
    public UsuarioDAO() {
        criarTabelaSeNaoExistir();
    }

     // Cria a tabela de usuários caso ela ainda não exista.
    public void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS usuario ("
                + "id SERIAL PRIMARY KEY, "
                + "nome VARCHAR(100) NOT NULL, "
                + "usuario VARCHAR(50) NOT NULL UNIQUE, "
                + "senha VARCHAR(100) NOT NULL"
                + ")";

        try (Connection con = Banco.conectar();
             Statement stmt = con.createStatement()) {
            
             // Executa o comando que cria a tabela.
            stmt.executeUpdate(sql);

            // Verifica quantos usuários existem na tabela.
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario")) {
                
                // Se a tabela estiver vazia, cadastra um usuário administrador.
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO usuario (nome, usuario, senha) VALUES ('Administrador', 'admin', '1234')");
                }
            }
           // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao verificar/criar tabela usuario: " + e.getMessage());
        }
    }

    // Cadastra um novo usuário e informa se o cadastro deu certo.
    public boolean cadastrar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, usuario, senha) VALUES (?, ?, ?)";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Preenche os campos com os dados do usuário.
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getSenha());

            // Executa o cadastro e verifica quantas linhas foram afetadas.
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                
                // Recupera o ID gerado automaticamente pelo banco.
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                
                // Retorna true porque o cadastro foi realizado.
                return true;
            }
            
            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
        
        // Retorna false se o cadastro não for realizado.
        return false;
    }

    // Verifica se o nome de usuário e a senha estão corretos.
    public boolean autenticar(String usuario, String senha) {
        String sql = "SELECT id, nome FROM usuario WHERE usuario = ? AND senha = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

             // Preenche os parâmetros usados na consulta.
            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            // Executa a consulta e verifica se encontrou um usuário.
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
        }
        // Retorna false se não encontrar o usuário ou ocorrer um erro.
        return false;
    }

      // Verifica se um nome de usuário já está cadastrado.
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT id FROM usuario WHERE LOWER(usuario) = LOWER(?)";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Informa o nome de usuário que será pesquisado.
            stmt.setString(1, usuario);

             // Retorna true se encontrar um usuário com esse nome.
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao checar existência de usuário: " + e.getMessage());
        }
        
        // Retorna false se não encontrar o usuário ou ocorrer um erro.
        return false;
    }

     // Busca todos os usuários cadastrados e retorna uma lista.
    public List<Usuario> listar() {
        
        // Cria uma lista vazia para armazenar os usuários.
        List<Usuario> lista = new ArrayList<>();
        
        // Busca os usuários sem consultar suas senhas.
        String sql = "SELECT id, nome, usuario FROM usuario ORDER BY id";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Percorre todos os usuários encontrados.
            while (rs.next()) {
                
                // Cria um objeto Usuario para guardar os dados.
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setUsuario(rs.getString("usuario"));
                // Adiciona o usuário à lista.
                lista.add(u);
            }
            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
         // Retorna a lista de usuários.
        return lista;
    }

    // Exclui um usuário usando seu ID.
    public boolean excluir(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

             // Define o ID do usuário que será excluído.
            stmt.setInt(1, id);
            
             // Retorna true se pelo menos um registro for excluído.
            return stmt.executeUpdate() > 0;
            
            // Mensagem de erro.
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
        }
          // Retorna false se a exclusão falhar.
        return false;
    }
}
