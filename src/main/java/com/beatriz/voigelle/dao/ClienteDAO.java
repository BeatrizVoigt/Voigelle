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

// Classe responsável por realizar operações com os clientes no banco de dados.
public class ClienteDAO {

    // Construtor da classe
    public ClienteDAO() {
        // Verifica se a tabela cliente existe e, caso não exista, cria a tabela.
        criarTabelaSeNaoExistir();
    }

    // Método responsável por criar a tabela cliente caso ela ainda não exista.
    public void criarTabelaSeNaoExistir() {
        
        // Comando SQL para criar a tabela cliente com suas respectivas colunas.
        String sql = "CREATE TABLE IF NOT EXISTS cliente ("
                + "id_cliente SERIAL PRIMARY KEY, "
                + "nome VARCHAR(100) NOT NULL, "
                + "cpf VARCHAR(20), "
                + "telefone VARCHAR(30), "
                + "email VARCHAR(100), "
                + "endereco VARCHAR(255)"
                + ")";

         // Tenta estabelecer a conexão com o banco e criar um comando SQL.
        try (Connection con = Banco.conectar();
             Statement stmt = con.createStatement()) {

            // Executa o comando SQL para criar a tabela, caso ela não exista.
            stmt.executeUpdate(sql);

            // Garante que as colunas cpf e endereco existam caso a tabela já existisse
            stmt.executeUpdate("ALTER TABLE cliente ADD COLUMN IF NOT EXISTS cpf VARCHAR(20)");
            stmt.executeUpdate("ALTER TABLE cliente ADD COLUMN IF NOT EXISTS endereco VARCHAR(255)");

            // Mensagem de erro se caso não for possivel criar a tabela.
        } catch (SQLException e) {
            System.err.println("Erro ao verificar/criar tabela cliente: " + e.getMessage());
        }
    }

    // Cadastra um novo cliente no banco de dados.
    public boolean inserir(Cliente cliente) {
        
        // Comando SQL para inserir os dados do cliente na tabela.
        // Os pontos de interrogação representam os valores que serão preenchidos posteriormente.
        String sql = "INSERT INTO cliente (nome, cpf, telefone, email, endereco) "
                   + "VALUES (?, ?, ?, ?, ?)";

        // Abre a conexão com o banco e prepara o comando SQL.
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(
                     // RETURN_GENERATED_KEYS permite recuperar o ID gerado automaticamente.
                     sql, Statement.RETURN_GENERATED_KEYS)) {

             // Preenche os ponto de interrogação.
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getEndereco());

            // Executa o comando SQL e informa quantas linhas foram afetadas.
            int linhas = stmt.executeUpdate();
            
             // Verifica se pelo menos um cliente foi inserido.
            if (linhas > 0) {
                
                // Recupera o ID gerado automaticamente pelo banco de dados.
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    
                    // Verifica se existe um ID retornado pelo banco.
                    if (rs.next()) {
                        // Guarda o ID gerado dentro do objeto cliente.
                        cliente.setId(rs.getInt(1));
                    }
                }
                // Retorna true indicando que o cadastro foi realizado com sucesso.
                return true;
            }

           // Captura possíveis erros durante a inserção dos dados.
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + e.getMessage());
        }
        // Retorna false indicando que a inserção não foi realizada.
        return false;
    }

    // Atualiza os dados de um cliente já cadastrado.
    public boolean atualizar(Cliente cliente) {
        // Comando SQL para modificar os dados de um cliente específico.
        String sql = "UPDATE cliente "
                   + "SET nome = ?, cpf = ?, telefone = ?, email = ?, endereco = ? "
                   + "WHERE id_cliente = ?";

         // Abre a conexão com o banco e prepara o comando SQL.
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getEndereco());
            stmt.setInt(6, cliente.getId());

            // Executa a atualização e retorna true se alguma linha foi alterada.
            return stmt.executeUpdate() > 0;
        
            // Captura possíveis erros
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
        
        // Retorna false caso a atualização não tenha sido realizada.
        return false;
    }

    // Exclui um cliente usando seu ID.
    public boolean excluir(int id) {
         // Comando SQL para excluir um cliente de acordo com seu ID.
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

         // Abre a conexão com o banco e prepara o comando SQL.
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Preenche o ponto de interrogação com o ID do cliente que será excluído.
            stmt.setInt(1, id);
            
            // Executa a exclusão e retorna true se algum registro foi removido.
            return stmt.executeUpdate() > 0;

          // Captura possíveis erros durante a exclusão.
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
        }
         // Retorna false caso nenhum cliente tenha sido excluído.
        return false;
    }

    // Busca todos os clientes cadastrados e retorna uma lista.
    public List<Cliente> listarTodos() {
        
        // Cria uma lista vazia que armazenará os clientes encontrados.
        List<Cliente> lista = new ArrayList<>();

        // Comando SQL para selecionar os dados de todos os clientes.
        // ORDER BY nome organiza os resultados em ordem alfabética.
        String sql = "SELECT id_cliente, nome, cpf, telefone, email, endereco "
                   + "FROM cliente ORDER BY nome";

         // Abre a conexão, prepara o comando e executa a consulta SQL.
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

             // Percorre todos os registros retornados pela consulta.
            while (rs.next()) {
                
                 // Cria um objeto Cliente com os dados encontrados no banco.
                // rs. significa Recuperar
                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getString("email"),
                        rs.getString("endereco")
                );
                 // Adiciona o cliente encontrado dentro da lista.
                lista.add(cliente);
            }

          // Captura possíveis erros durante a consulta.
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }
        
        // Retorna a lista contendo os clientes encontrados.
        return lista;
    }

    // Busca um cliente específico pelo ID.
    public Cliente buscarPorId(int id) {
        
        // Comando SQL para selecionar um cliente específico pelo ID.
        String sql = "SELECT id_cliente, nome, cpf, telefone, email, endereco "
                   + "FROM cliente WHERE id_cliente = ?";

        // Inicializa o cliente como null, indicando que o cliente foi encontrado ainda.
        Cliente cliente = null;

        // Abre a conexão com o banco e prepara o comando SQL.
        try (Connection con = Banco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Preenche o ponto de interrogação com o ID informado.
            stmt.setInt(1, id);

              // Executa a consulta e armazena o resultado.
            try (ResultSet rs = stmt.executeQuery()) {
                
                 // Verifica se foi encontrado algum registro.
                if (rs.next()) {
                    // Cria um objeto Cliente com os dados recuperados do banco.
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

          // Captura possíveis erros durante a busca.
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
        }

        // Retorna o objeto Cliente encontrado ou null caso não exista.
        return cliente;
    }
}
