package com.beatriz.voigelle.model;

// A classe Cliente herda os atributos e métodos da classe Pessoa.
public class Cliente extends Pessoa {

     // Construtor vazio da classe Cliente.
    // O super() chama o construtor vazio da classe Pessoa.
    public Cliente() {
        super();
    }

    // Construtor que recebe cinco parâmetros para cadastrar um cliente.
    // O super() envia esses dados para o construtor da classe Pessoa,
    public Cliente(int id, String nome, String cpf, String telefone, String email) {
        super(id, nome, cpf, telefone, email);
    }

    // Construtor que recebe seis parâmetros, incluindo o endereço.
    // Os dados são enviados para o construtor correspondente da classe Pessoa.
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
