package com.beatriz.voigelle.model;

public class ItemVenda {
    private int idItem;
    private Produto produto;  
    private int quantidade;
    private double subtotal;   

    public ItemVenda() {
    }

    // Construtor já calcula o subtotal automaticamente ao criar o item
    public ItemVenda(int idItem, Produto produto, int quantidade) {
        this.idItem = idItem;
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = produto.getPreco() * quantidade;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public Produto getProduto() {
        return produto;
    }

   
 
    
    public void setProduto(Produto produto) {
        this.produto = produto;
        this.subtotal = produto.getPreco() * this.quantidade;
    }

    public int getQuantidade() {
        return quantidade;
    }


    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        if (produto != null) {
            this.subtotal = produto.getPreco() * quantidade;
        }
    }

    
    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return "Item -> Produto: " + produto.getNome()
                + " | Qtd: " + quantidade
                + " | Subtotal: R$ " + subtotal;
    }

}
