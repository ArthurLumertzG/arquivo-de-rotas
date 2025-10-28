package entities;

public class Config {

    private static Config config;

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

    public static Config iniciaConfig(String pasta, String sucesso, String erro, boolean automatico) {
        config = new Config(pasta, sucesso, erro, automatico);
        return config;
    }

    public static Config getConfig() {
        if (config == null) {
            throw new IllegalStateException("Config não foi inicializada. Use iniciaConfig primeiro.");
        }
        return config;
    }

    public static String getPasta() {
        return config.pasta;
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
