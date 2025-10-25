package entities;

public class Config {

    private String pasta;
    private String sucesso;
    private String erro;
    private boolean automatico;

    public Config(String pasta, String sucesso, String erro, boolean automatico) {
        this.pasta = pasta;
        this.sucesso = sucesso;
        this.erro = erro;
        this.automatico = automatico;
    }

    public String getPasta() {
        return pasta;
    }

    public void setPasta(String pasta) {
        this.pasta = pasta;
    }

    public String getSucesso() {
        return sucesso;
    }

    public void setSucesso(String sucesso) {
        this.sucesso = sucesso;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public boolean isAutomatico() {
        return automatico;
    }

    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }
}
