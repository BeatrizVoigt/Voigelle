package com.beatriz.voigelle.model;

import com.beatriz.voigelle.model.Cliente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int idVenda;
    private LocalDate dataVenda;
    private double valorTotal;
    private Cliente cliente;         
    private List<ItemVenda> itens;  

 
    // e define a data como o dia de hoje
    public Venda() {
        this.itens = new ArrayList<>();
        this.dataVenda = LocalDate.now();
    }

    public Venda(int idVenda, Cliente cliente) {
        this.idVenda = idVenda;
        this.cliente = cliente;
        this.itens = new ArrayList<>();
        this.dataVenda = LocalDate.now();
        this.valorTotal = 0.0;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

   
    // o valor total é sempre CALCULADO, nunca digitado à mão
    public double getValorTotal() {
        return valorTotal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    // Adiciona um item na venda e já recalcula o valor total
    public void adicionarItem(ItemVenda item) {
        this.itens.add(item);
        calcularValorTotal();
    }

    // Remove um item da venda e recalcula o valor total
    public void removerItem(ItemVenda item) {
        this.itens.remove(item);
        calcularValorTotal();
    }

   
    // Percorre todos os itens e soma os subtotais
    private void calcularValorTotal() {
        double total = 0.0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
        this.valorTotal = total;
    }

    @Override
    public String toString() {
        return "Venda -> Id: " + idVenda
                + " | Data: " + dataVenda
                + " | Cliente: " + cliente.getNome()
                + " | Total: R$ " + valorTotal;
    }

}
