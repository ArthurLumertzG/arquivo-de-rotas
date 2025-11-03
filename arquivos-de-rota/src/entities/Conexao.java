package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conexao {

    public static final List<Conexao> listaConexoes = new ArrayList<>();
    private Integer noDeOrigem;
    private Integer noDeDestino;
    private Integer pesoDaConexao;

    public Conexao(Integer noDeOrigem, Integer noDeDestino, Integer pesoDaConexao) {
        this.noDeOrigem = noDeOrigem;
        this.noDeDestino = noDeDestino;
        this.pesoDaConexao = pesoDaConexao;

        listaConexoes.add(this);
    }

    public Integer getNoOrigem() {
        return noDeOrigem;
    }

    public void setNoOrigem(Integer noDeOrigem) {
        this.noDeOrigem = noDeOrigem;
    }

    public Integer getNoDestino() {
        return noDeDestino;
    }

    public void setNoDestino(Integer noDeDestino) {
        this.noDeDestino = noDeDestino;
    }

    public Integer getPesoConexao() {
        return pesoDaConexao;
    }

    public void setPesoConexao(Integer pesoDaConexao) {
        this.pesoDaConexao = pesoDaConexao;
    }
}
