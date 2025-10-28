package entities;

public class Conexao {

    private Integer noDeOrigem;
    private Integer noDeDestino;
    private Integer pesoDaConexao;

    public Conexao(Integer noDeOrigem, Integer noDeDestino, Integer pesoDaConexao) {
        this.noDeOrigem = noDeOrigem;
        this.noDeDestino = noDeDestino;
        this.pesoDaConexao = pesoDaConexao;
    }

    public Integer getNoDeOrigem() {
        return noDeOrigem;
    }

    public void setNoDeOrigem(Integer noDeOrigem) {
        this.noDeOrigem = noDeOrigem;
    }

    public Integer getNoDeDestino() {
        return noDeDestino;
    }

    public void setNoDeDestino(Integer noDeDestino) {
        this.noDeDestino = noDeDestino;
    }

    public Integer getPesoDaConexao() {
        return pesoDaConexao;
    }

    public void setPesoDaConexao(Integer pesoDaConexao) {
        this.pesoDaConexao = pesoDaConexao;
    }
}
