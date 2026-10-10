package gerenciacontato.model;

public class Produto {
    private int idProduto;
    private String nome;
    private String categoria;
    private String tamanho;
    private String cor;
    private double preco;
    private int quantidade;

   
    public Produto() {
    }

    // Construtor 
    public Produto(int idProduto, String nome, String categoria, String tamanho,
                    String cor, double preco, int quantidade) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.cor = cor;
        this.preco = preco;
        this.quantidade = quantidade;
    }

  
    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

   
    @Override
    public String toString() {
        return "Produto -> Id: " + idProduto
                + " | Nome: " + nome
                + " | Categoria: " + categoria
                + " | Tamanho: " + tamanho
                + " | Cor: " + cor
                + " | Preco: R$ " + preco
                + " | Qtd: " + quantidade;
    }

}
