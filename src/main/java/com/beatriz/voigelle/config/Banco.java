package com.beatriz.voigelle.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Banco {

    //Armazena a URL do banco de dados PostgreSQL hospedado na nuvem.
    private static String url = "jdbc:postgresql://ep-falling-scene-acb3glmp-pooler.sa-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_JAnRsHCNVr43&sslmode=require&channelBinding=require";
    private static String user = "postgres";
    private static String senha = "1234";

     // Método responsável por conectar o sistema ao banco de dados.
    public static Connection conectar() {
        Connection con = null;
        
        // Tenta estabelecer a conexão usando a URL configurada.
        try {
            con = DriverManager.getConnection(url);
        
        // Exibe uma mensagem caso a conexão falhe.
        } catch (SQLException ex) {
            System.out.printf("Conexao com banco de dados falhou");
        } 
        
        // Retorna a conexão para ser utilizada em outras partes do sistema.
        return con;
    }
}