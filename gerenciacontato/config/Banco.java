package gerenciacontato.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Banco {

    private static String url = "jdbc:postgresql://ep-falling-scene-acb3glmp-pooler.sa-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_JAnRsHCNVr43&sslmode=require&channelBinding=require";
    private static String user = "postgres";
    private static String senha = "1234";

    public static Connection conectar() {
        Connection con = null;
        
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            System.out.printf("Conexao com banco de dados falhou");
        } 
        
        return con;
    }
}