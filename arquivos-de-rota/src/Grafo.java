public class Grafo {

    private Integer nosTotaisDoGrafo;
    private Integer somaPesosDasArestas;
    private Conexao[] conexoes;

    public Integer getNosTotaisDoGrafo() {
        return nosTotaisDoGrafo;
    }

    public void setNosTotaisDoGrafo(Integer nosTotaisDoGrafo) {
        this.nosTotaisDoGrafo = nosTotaisDoGrafo;
    }

    public Integer getSomaPesosDasArestas() {
        return somaPesosDasArestas;
    }

    public void setSomaPesosDasArestas(Integer somaPesoDasArestas) {
        this.somaPesosDasArestas = somaPesoDasArestas;
    }

    public Conexao[] getConexoes() {
        return conexoes;
    }

    public void setConexoes(Conexao[] conexoes) {
        this.conexoes = conexoes;
    }

    public void addConexoes(Conexao conexao) {
        if (this.conexoes == null) {
            this.conexoes = new Conexao[]{conexao};
        } else {
            Conexao[] novasConexoes = new Conexao[this.conexoes.length + 1];
            System.arraycopy(this.conexoes, 0, novasConexoes, 0, this.conexoes.length);
            novasConexoes[this.conexoes.length] = conexao;
            this.conexoes = novasConexoes;
        }
    }
}
