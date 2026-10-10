package com.beatriz.voigelle.model;

public class Pessoa {
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;

    // Construtor vazio 
    public Pessoa() {
    }

    // Construtor completo, recebe todos os dados de uma vez

    public Pessoa(int id, String nome, String cpf, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }

    // Getters e Setters:
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

  
    // cada subclasse  pode mudar esse comportamento.
    @Override
    public String toString() {
        return "Id: " + id + " | Nome: " + nome + " | CPF: " + cpf;
    }
}   

