package utils;

import entities.Config;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**
     * Read All Lines of file
     *
     * @param filePath The name of the file to read
     * @return All lines of file
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public static List<String> readAllLines(String filePath) throws IOException {
        List<String> rows = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String r;
            while ((r = reader.readLine()) != null) {
                rows.add(r);
            }
        }
        return rows;
    }

    /**
     * Validate the file config.txt
     *
     * @param configPath Path of config.txt
     * @return The error or null
     * @throws IOException Signals thta o I/O of dome dort has occurred
     */
    public static String validateConfig(String pasta, String sucesso, String erro, boolean automatico) throws IOException {

        if (pasta == null || sucesso == null || erro == null) {
            return "Todos os campos devem ser preenchidos!";
        }

        if (pasta.trim().isEmpty() || sucesso.trim().isEmpty() || erro.trim().isEmpty()) {
            return "Os campos não podem estar vazios!";
        }

        if (!Files.exists(Path.of(pasta))) {
            try {
                Files.createDirectories(Path.of(pasta));
            } catch (Exception e) {
                return "Falha ao criar o diretório " + pasta + ": " + e.getMessage();
            }

        }

        if (!Files.exists(Path.of(sucesso))) {
            try {
                Files.createDirectories(Path.of(sucesso));
            } catch (Exception e) {
                return "Falha ao criar o diretório " + sucesso + ": " + e.getMessage();
            }

        }

        if (!Files.exists(Path.of(erro))) {
            try {
                Files.createDirectories(Path.of(erro));
            } catch (Exception e) {
                return "Falha ao criar o diretório " + erro + ": " + e.getMessage();
            }

        }

        return null;
    }

    public static String validateRotaFile(List<String> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            return "Arquivo rota está em branco.";
        }

        List<String> nonEmpty = new ArrayList<>();
        for (String linha : linhas) {
            if (linha != null && !linha.trim().isEmpty()) nonEmpty.add(linha.trim());
        }

        if (nonEmpty.isEmpty()) {
            return "Arquivo rota está em branco.";
        }

        String header = nonEmpty.get(0);

        Pattern headerPattern = Pattern.compile("^00(\\d{2})(\\d{1,5})$");
        Matcher mHeader = headerPattern.matcher(header);

        if (!mHeader.matches()) {
            return "Mensagem: Header inválido. Formato esperado: 00NNSP (ex: 001200500).";
        }

        int headerNosTotais;
        int headerSomaPesos;

        try {
            headerNosTotais = Integer.parseInt(mHeader.group(1));
            headerSomaPesos = Integer.parseInt(mHeader.group(2));
        } catch (NumberFormatException e) {
            return "Mensagem: Número totais de nós inválido.";
        }

        List<String> conexoes = new ArrayList<>();
        List<String> pesos = new ArrayList<>();
        String trailer = null;

        for (int i = 1; i < nonEmpty.size(); i++) {
            String linha = nonEmpty.get(i);
            if (linha.startsWith("01")) {
                Pattern p01 = Pattern.compile("^01(\\d{1,2})=(\\d{1,2})$");
                Matcher m01 = p01.matcher(linha);

                if (!m01.matches()) {
                    return "Mensagem: Resumo de conexões inválido. Linha: '" + linha + "'";
                }
                conexoes.add(linha);
            } else if (linha.startsWith("02")) {
                Pattern p02 = Pattern.compile("^02(\\d{1,2});(\\d{1,2})=(\\d{1,4})$");
                Matcher m02 = p02.matcher(linha);
                if (!m02.matches()) {
                    return "Mensagem: Resumo dos pesos inválido. Linha: '" + linha + "'";
                }

                int peso = Integer.parseInt(m02.group(3));
                if (peso > 9999) {
                    return "Mensagem: Peso da aresta excede 9999.";
                }
                pesos.add(linha);
            } else if (linha.startsWith("09")) {
                trailer = linha;
            } else {
                return "Mensagem: Identificador de bloco inválido na linha: '" + linha + "'";
            }
        }

        if (trailer == null) {
            return "Mensagem: Trailer (linha 09) não encontrada.";
        }

        Pattern trailerPattern = Pattern.compile("^09RC=(\\d{1,2});RP=(\\d{1,2});(\\d{1,5})$");
        Matcher mTrailer = trailerPattern.matcher(trailer);
        if (!mTrailer.matches()) {
            return "Mensagem: Trailer inválido. Formato esperado: 09RC=NN;RP=NN;P";
        }

        int resumoConexoes = Integer.parseInt(mTrailer.group(1));
        int resumoPesos = Integer.parseInt(mTrailer.group(2));
        int trailerSomaPesos = Integer.parseInt(mTrailer.group(3));

        if (resumoConexoes != conexoes.size()) {
            return "Mensagem: Número de linhas do Resumo de Conexões inválido. Resumo Conexoes=" + resumoConexoes + " mas encontrou " + conexoes.size() + ".";
        }

        if (resumoPesos != pesos.size()) {
            return "Mensagem: Número de linhas do Resumo dos Pesos inválido. RP=" + resumoPesos + " mas encontrou " + pesos.size() + ".";
        }

        int somaPesosCalculada = 0;

        Pattern p02extract = Pattern.compile("^02(\\d{1,2});(\\d{1,2})=(\\d{1,4})$");

        for (String peso : pesos) {
            Matcher mt = p02extract.matcher(peso);

            if (mt.matches()) {
                int pesoEncontrado = Integer.parseInt(mt.group(3));
                somaPesosCalculada += pesoEncontrado;

            } else {
                return "Mensagem: Linha de peso com formato inválido durante soma.";
            }
        }

        if (somaPesosCalculada != trailerSomaPesos) {
            return "Mensagem: Soma dos pesos difere do trailer (Valor do Registro HEADER = NN e Soma dos Pesos = " + somaPesosCalculada + ").";
        }

        if (somaPesosCalculada != headerSomaPesos) {
            return "Mensagem: Soma dos pesos difere da soma informada no HEADER.";
        }

        return null;
    }

    /*
    public static void antigoMain() throws IOException {

        String finalCaminhoProcessados = caminhoProcessados;
        String finalCaminhoNaoProcessados = caminhoNaoProcessados;
        String finalCaminhoRotas = caminhoRotas;

        int quantityOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorThreads = Executors.newFixedThreadPool(quantityOfThreads);

        for (int i = 1; i <= 10; i++) {
            int rotaIndex = i;

            executorThreads.submit(() -> {
                try {
                    Path origemPath = Paths.get(finalCaminhoRotas + rotaIndex + ".txt");
                    if (Files.exists(origemPath) && Files.isRegularFile(origemPath)) {
                        List<String> linhasRota = readAllLines(origemPath.toString());

                        // valida o arquivo por completo (header, 01, 02, trailer e somas (conexoes e pesos))
                        String validaRota = validateRotaFile(linhasRota);

                        if (validaRota != null) {
                            // validação falhou, mover para Não Processado

                            Path destinoNaoProcessado = Paths.get(finalCaminhoNaoProcessados + "/rota" + rotaIndex + ".txt");
                            Files.move(origemPath, destinoNaoProcessado);
                            System.out.println("Arquivo rota" + rotaIndex + ".txt inválido. Motivo: " + validaRota);
                        } else {
                            // tudo certo, mover para Processado
                            Path destinoProcessado = Paths.get(finalCaminhoProcessados + "/rota" + rotaIndex + ".txt");
                            Files.move(origemPath, destinoProcessado);
                            System.out.println("Rota " + rotaIndex + " processada com sucesso!");
                        }
                    } else {
                        // arquivo não existe ou não está correto, mover (se existir) para Não Processado
                        Path origemErro = Paths.get(finalCaminhoRotas + rotaIndex + ".txt");
                        if (Files.exists(origemErro)) {
                            Path destinoNaoProcessado = Paths.get(finalCaminhoNaoProcessados + "/rota" + rotaIndex + ".txt");
                            Files.move(origemErro, destinoNaoProcessado);
                        }
                    }
                } catch (Exception e) {
                    try {
                        Path origemErro = Paths.get(finalCaminhoRotas + rotaIndex + ".txt");
                        Path destinoNaoProcessado = Paths.get(finalCaminhoNaoProcessados + "/rota" + rotaIndex + ".txt");
                        if (Files.exists(origemErro)) {
                            Files.move(origemErro, destinoNaoProcessado);
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.out.println("Arquivo rota" + rotaIndex + ".txt inválido.");
                }
            });
        }
        executorThreads.shutdown();
    }*/

    public static String arquivoConfig(String configPath) throws IOException {

        List<String> linhasConfig = new ArrayList<String>();

        if (!Files.exists(Path.of(configPath))) {
            return "Arquivo config.txt não encontrado em -> " + configPath;
        }

        linhasConfig = Utils.readAllLines(configPath);
        List<String> linhasValidas = new ArrayList<String>();

        for (String linha : linhasConfig) {
            if (linha.contains("@") && !linha.contains("=")) {
                return "Há um erro de digitação em config.txt";
            }

            if (!linha.trim().equals("") && !linha.trim().isEmpty()) {
                linhasValidas.add(linha);
            }
        }

        if (linhasValidas.isEmpty()) {
            return "Arquivo config.txt está em branco";
        }

        if (linhasValidas.size() < 4) {
            return "Há linha(s) faltando em config.txt";
        }

        for (String linha : linhasConfig) {
            if (linha.startsWith("Rotas=")) {
                String caminhoRotas = linha.substring("Rotas=".length()).trim();
                Path pathRotas = Path.of(caminhoRotas);
                if (!Files.exists(pathRotas)) {
                    try {
                        Files.createDirectories(pathRotas);
                        return "Pasta Rotas criada: " + caminhoRotas;
                    } catch (IOException e) {
                        System.err.println("Erro ao criar pasta Rotas: " + e.getMessage());
                    }
                }
            }

            if (linha.startsWith("Processado=")) {
                String caminhoProcessado = linha.substring("Processado=".length()).trim();
                Path pathProcessado = Path.of(caminhoProcessado);
                if (!Files.exists(pathProcessado)) {
                    try {
                        Files.createDirectories(pathProcessado);
                        return "Pasta Processado criada: " + caminhoProcessado;
                    } catch (IOException e) {
                        System.err.println("Erro ao criar pasta Processado: " + e.getMessage());
                    }
                }
            }

            if (linha.startsWith("NaoProcessado=")) {
                String caminhoNaoProcessado = linha.substring("NaoProcessado=".length()).trim();
                Path pathNaoProcessado = Path.of(caminhoNaoProcessado);
                if (!Files.exists(pathNaoProcessado)) {
                    try {
                        Files.createDirectories(pathNaoProcessado);
                        return "Pasta NaoProcessado criada: " + caminhoNaoProcessado;
                    } catch (IOException e) {
                        System.err.println("Erro ao criar pasta NaoProcessado: " + e.getMessage());
                    }
                }
            }

            if (linha.startsWith("Automatico=")) {
                String valorAutomatico = linha.substring("Automatico=".length()).trim();
                boolean automatico = valorAutomatico.equalsIgnoreCase("Sim");
                return "Modo automático: " + (automatico ? "Sim" : "Não");
                // Você pode armazenar este valor em uma variável da classe ou usar conforme necessário
            }
        }

        return null;
    }


    public static String procuraConfig () throws IOException {

        String resposta = null;
        String config = "C:\\Configuracoes\\config";
        Path caminhoConfig = Paths.get(config + ".txt");

        if (Files.exists(caminhoConfig)) {

            validateConfig("C:\\Rotas", "C:\\Teste\\Processado", "C:\\Teste\\NaoProcessado", true);

            resposta = arquivoConfig(String.valueOf(caminhoConfig));
            return "Configuração pré-definida encontrada!\nPode ser alterado em " + caminhoConfig;
        }
        return null;
    }

    public static boolean criaConfig (String pasta, String sucesso, String erro, boolean rotaAutomatica) throws IOException {

        if ( validateConfig(pasta, sucesso, erro, rotaAutomatica) == null ) {
            new Config(pasta, sucesso, erro, rotaAutomatica);
            return true;
        }

        return false;
    }

    public static boolean validaNoCidade (Integer no, String cidade) {



        return true;
    }


    public static void buscarRota(JTextField txtBuscar) throws IOException {
        String caminhoRota = txtBuscar.toString();
        List<String> linhasRota = readAllLines(caminhoRota);

        if ( validateRotaFile(linhasRota) == null ) {

        }
    }

    /*
    private void adicionarRota(Integer CodigoOrigem, String CidadeOrigem, Integer CodigoDestino, String CidadeDestino, Integer KM) {

        validaNoCidade(CodigoOrigem, CidadeOrigem);
        validaNoCidade(CodigoDestino, CidadeDestino);

        if (codOrigem.isEmpty() || cidOrigem.isEmpty() ||
                codDestino.isEmpty() || cidDestino.isEmpty() || km.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos antes de adicionar!",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) table.getModel();
        model.addRow(new Object[]{codOrigem, cidOrigem, codDestino, cidDestino, km});

        txtCodigoOrigem.setText("");
        txtCidadeOrigem.setText("");
        txtCodigoDestino.setText("");
        txtCidadeDestino.setText("");
        txtKM.setText("");
    }*/

    public static String buscarDiretorio() {
        return null;
    }
}
