package com.beatriz.voigelle;

import com.beatriz.voigelle.dao.ContatoDAO;
import com.beatriz.voigelle.model.Contato;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbTest {

    @Test
    public void testCrudContato() {
        ContatoDAO dao = new ContatoDAO();

        Contato c = new Contato("Teste Antigravity", "11999999999", "teste@example.com");
        dao.salvar(c);

        List<Contato> contatos = dao.listar();
        assertNotNull(contatos);
        assertTrue(!contatos.isEmpty());

        Contato inserido = null;
        for (Contato item : contatos) {
            if ("Teste Antigravity".equals(item.getNome())) {
                inserido = item;
                break;
            }
        }
        assertNotNull(inserido);

        boolean excluido = dao.excluir(inserido.getId());
        assertTrue(excluido);
        System.out.println("CRUD de Contato testado com sucesso!");
    }

    @Test
    public void testListTables() throws Exception {
        try (java.sql.Connection con = com.beatriz.voigelle.config.Banco.conectar();
             java.sql.ResultSet rs = con.getMetaData().getTables(null, "public", "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                System.out.println("TABELA ENCONTRADA: " + rs.getString("TABLE_NAME"));
            }
        }
    }

    @Test
    public void testCreateUsuarioTable() throws Exception {
        try (java.sql.Connection con = com.beatriz.voigelle.config.Banco.conectar();
             java.sql.Statement stmt = con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuario (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "usuario VARCHAR(50) NOT NULL UNIQUE, " +
                    "senha VARCHAR(100) NOT NULL)");
            System.out.println("Tabela usuario verificada/criada com sucesso!");
        }
    }

    @Test
    public void testCrudUsuario() {
        com.beatriz.voigelle.dao.UsuarioDAO dao = new com.beatriz.voigelle.dao.UsuarioDAO();
        String testUser = "teste_user_" + System.currentTimeMillis();
        com.beatriz.voigelle.model.Usuario u = new com.beatriz.voigelle.model.Usuario("Usuario Teste", testUser, "senha123");

        boolean cadastrou = dao.cadastrar(u);
        assertTrue(cadastrou, "Deveria cadastrar usuario com sucesso");

        boolean autenticou = dao.autenticar(testUser, "senha123");
        assertTrue(autenticou, "Deveria autenticar com credenciais corretas");

        boolean autenticouErrado = dao.autenticar(testUser, "senhaErrada");
        assertTrue(!autenticouErrado, "Nao deveria autenticar com senha incorreta");

        boolean excluiu = dao.excluir(u.getId());
        assertTrue(excluiu, "Deveria excluir usuario de teste com sucesso");
        System.out.println("CRUD de Usuario testado com sucesso!");
    }
}
