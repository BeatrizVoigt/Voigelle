package com.beatriz.voigelle.model;

public class Cliente extends Pessoa {

    public Cliente() {
        super();
    }

    public Cliente(int id, String nome, String cpf, String telefone, String email) {
        super(id, nome, cpf, telefone, email);
    }

    public Cliente(int id, String nome, String cpf, String telefone, String email, String endereco) {
        super(id, nome, cpf, telefone, email, endereco);
    }

    // POLIMORFISMO: Cliente tem seu PRÓPRIO toString(), diferente do de Pessoa
    @Override
    public String toString() {
        return "Cliente -> " + super.toString()
                + " | Telefone: " + getTelefone()
                + " | Email: " + getEmail()
                + " | Endereço: " + getEndereco();
    }
}
