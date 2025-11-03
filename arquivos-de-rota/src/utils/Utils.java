package utils;

import entities.Conexao;
import entities.Config;
import entities.HashNoCidade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static entities.Conexao.listaConexoes;
import static entities.HashNoCidade.criaNoCidade;
import static entities.HashNoCidade.mapaNoCidade;
import static view.VisivelView.getTabela;

public class Utils {

    /**
     * Read All Lines of file
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

        List<Integer> nos = new ArrayList<>();

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
                int no1 = Integer.parseInt(m01.group(1));
                int no2 = Integer.parseInt(m01.group(2));

                if (!nos.contains(no1)) {
                    nos.add(no1);
                }
                if (!nos.contains(no2)) {
                    nos.add(no2);
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
                // também registre os nós referenciados nas linhas 02
                int noOrig = Integer.parseInt(m02.group(1));
                int noDest = Integer.parseInt(m02.group(2));
                if (!nos.contains(noOrig)) nos.add(noOrig);
                if (!nos.contains(noDest)) nos.add(noDest);

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

        for (String pesoLinha : pesos) {
            Matcher mt = p02extract.matcher(pesoLinha);
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

        /* ---------- AQUI: popula as estruturas do programa ---------- */
        // cria nós no mapa (se não existirem) usando seu utilitário criaNoCidade
        for (Integer no : nos) {
            criaNoCidade(no, " -\t-"); // sua função já cria o par no->cidade no hashmap
        }

        // a partir das linhas 02, criar objetos Conexao e adicioná-los à lista global,
        // evitando duplicatas
        for (String pesoLinha : pesos) {
            Matcher mt = p02extract.matcher(pesoLinha);
            if (mt.matches()) {
                int origem = Integer.parseInt(mt.group(1));
                int destino = Integer.parseInt(mt.group(2));
                int peso = Integer.parseInt(mt.group(3));

                // checar duplicata (assumindo equals por origem-destino-peso)
                boolean existe = false;
                for (Conexao c : listaConexoes) {
                    if (c.getNoOrigem().equals(origem) && c.getNoDestino().equals(destino) && c.getPesoConexao().equals(peso)) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    listaConexoes.add(new Conexao(origem, destino, peso));
                }
            }
        }

        return null;
    }


    public static String lerArquivoConfig(String configPath) throws IOException {

        if (!Files.exists(Path.of(configPath))) {
            return "Arquivo config.txt não encontrado em -> " + configPath;
        }

        List<String> linhasConfig = readAllLines(configPath);
        List<String> linhasValidas = new ArrayList<>();

        for (String linha : linhasConfig) {
            if (linha.contains("@") && !linha.contains("=")) {
                return "Há um erro de digitação em config.txt";
            }
            if (!linha.trim().isEmpty()) {
                linhasValidas.add(linha);
            }
        }

        if (linhasValidas.isEmpty()) {
            return "Arquivo config.txt está em branco";
        }

        if (linhasValidas.size() < 4) {
            return "Há linha(s) faltando em config.txt";
        }

        String caminhoRotas = "";
        String caminhoProcessado = "";
        String caminhoNaoProcessado = "";
        boolean automatico = false;

        System.out.println("=== Lendo config.txt ===");
        for (String linha : linhasConfig) {
            System.out.println("Linha: " + linha);

            if (linha.startsWith("Rotas=")) {
                caminhoRotas = linha.substring("Rotas=".length()).trim();
                System.out.println("  → Rotas: " + caminhoRotas);
            } else if (linha.startsWith("Processado=")) {
                caminhoProcessado = linha.substring("Processado=".length()).trim();
                System.out.println("  → Processado: " + caminhoProcessado);
            } else if (linha.startsWith("NaoProcessado=")) {
                caminhoNaoProcessado = linha.substring("NaoProcessado=".length()).trim();
                System.out.println("  → NaoProcessado: " + caminhoNaoProcessado);
            } else if (linha.startsWith("Automatico=")) {
                String valorAutomatico = linha.substring("Automatico=".length()).trim();
                automatico = valorAutomatico.equalsIgnoreCase("Sim");
                System.out.println("  → Automatico: " + automatico);
            }
        }
        System.out.println("========================");

        // Validar se todos os caminhos foram preenchidos
        if (caminhoRotas.isEmpty() || caminhoProcessado.isEmpty() || caminhoNaoProcessado.isEmpty()) {
            return "Erro: Algum caminho não foi configurado corretamente no config.txt";
        }

        // Criar diretórios se não existirem
        try {
            Files.createDirectories(Path.of(caminhoRotas));
            Files.createDirectories(Path.of(caminhoProcessado));
            Files.createDirectories(Path.of(caminhoNaoProcessado));
        } catch (Exception e) {
            return "Erro ao criar diretórios: " + e.getMessage();
        }

        // IMPORTANTE: Inicializar Config AQUI
        Config.iniciaConfig(caminhoRotas, caminhoProcessado, caminhoNaoProcessado, automatico);
        System.out.println("Config inicializado com sucesso!");

        return null; // Sucesso
    }

    public static String procuraConfig() throws IOException {
        String config = "C:\\Configuracoes\\config.txt";
        Path caminhoConfig = Paths.get(config);

        if (Files.exists(caminhoConfig)) {
            // Ler e inicializar o Config
            String resultado = lerArquivoConfig(String.valueOf(caminhoConfig));

            // Se houve erro na leitura, retornar o erro
            if (resultado != null) {
                return resultado;
            }

            // Config inicializado com sucesso
            return "Configuração pré-definida encontrada!\nPode ser alterado em " + caminhoConfig;
        }
        return null;
    }

    public static boolean criaConfig(String pasta, String sucesso, String erro, boolean rotaAutomatica) throws IOException {

        // Validar primeiro
        String validacao = validateConfig(pasta, sucesso, erro, rotaAutomatica);
        if (validacao != null) {
            return false;
        }

        String caminhoConfig = "C:\\Configuracoes\\config.txt";
        Path pathConfig = Path.of("C:\\Configuracoes");

        if (!Files.exists(pathConfig)) {
            Files.createDirectories(pathConfig);
        }

        // CORREÇÃO: Adicionar os prefixos corretos
        String[] linhas = {
                "Rotas=" + pasta,
                "Processado=" + sucesso,
                "NaoProcessado=" + erro,
                rotaAutomatica ? "Automatico=Sim" : "Automatico=Nao"
        };

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoConfig, false))) {
            for (String linha : linhas) {
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            return false;
        }

        // Inicializar Config
        Config.iniciaConfig(pasta, sucesso, erro, rotaAutomatica);
        return true;
    }

    public static boolean validaNoCidade(Integer no, String cidade) {
        return true;
    }

    public static void buscarRota(JTextField txtBuscar) throws IOException {
        String caminhoRota = txtBuscar.getText();
        List<String> linhasRota = readAllLines(caminhoRota);

        if (validateRotaFile(linhasRota) == null) {
            // Processar rota válida
        }
    }

    public static String buscarDiretorio(String caminhoDiretorio) {

        if (!Files.exists(Path.of(caminhoDiretorio))) {
            return "Pasta não encontrada!";
        }

        System.out.println("Processando diretório: " + caminhoDiretorio);

        Config config;
        try {
            config = Config.getConfig();
        } catch (IllegalStateException e) {
            return "Erro: Configuração não foi inicializada. Configure o sistema primeiro!";
        }

        String finalCaminhoProcessados = config.getSucesso();
        String finalCaminhoNaoProcessados = config.getErro();

        // Debug: Imprimir os caminhos
        System.out.println("Caminho Processados: " + finalCaminhoProcessados);
        System.out.println("Caminho Não Processados: " + finalCaminhoNaoProcessados);

        // VALIDAÇÃO: Verificar se os caminhos não são nulos ou vazios
        if (finalCaminhoProcessados == null || finalCaminhoProcessados.trim().isEmpty()) {
            return "Erro: Caminho de arquivos processados não configurado!";
        }

        if (finalCaminhoNaoProcessados == null || finalCaminhoNaoProcessados.trim().isEmpty()) {
            return "Erro: Caminho de arquivos não processados não configurado!";
        }

        int quantityOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorThreads = Executors.newFixedThreadPool(quantityOfThreads);

        for (int i = 1; i <= 100; i++) {
            int rotaIndex = i;

            executorThreads.submit(() -> {
                try {
                    Path origemPath = Paths.get(caminhoDiretorio, "rota" + rotaIndex + ".txt");

                    if (Files.exists(origemPath) && Files.isRegularFile(origemPath)) {
                        List<String> linhasRota = readAllLines(origemPath.toString());
                        String validaRota = validateRotaFile(linhasRota);

                        if (validaRota != null) {
                            // Validação falhou - mover para NÃO PROCESSADO
                            Path destinoNaoProcessado = Paths.get(finalCaminhoNaoProcessados, "rota" + rotaIndex + ".txt");

                            // IMPORTANTE: Criar diretório se não existir
                            if (destinoNaoProcessado.getParent() != null) {
                                Files.createDirectories(destinoNaoProcessado.getParent());
                            }

                            Files.move(origemPath, destinoNaoProcessado);
                            System.out.println("✗ Arquivo rota" + rotaIndex + ".txt inválido. Motivo: " + validaRota);
                        } else {
                            // Validação OK - mover para PROCESSADO
                            Path destinoProcessado = Paths.get(finalCaminhoProcessados, "rota" + rotaIndex + ".txt");

                            // IMPORTANTE: Criar diretório se não existir
                            if (destinoProcessado.getParent() != null) {
                                Files.createDirectories(destinoProcessado.getParent());
                            }

                            Files.move(origemPath, destinoProcessado);
                            System.out.println("✓ Rota " + rotaIndex + " processada com sucesso!");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar rota" + rotaIndex + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executorThreads.shutdown();
        try {
            // Aumentado timeout para 30 segundos
            if (!executorThreads.awaitTermination(30, TimeUnit.SECONDS)) {
                executorThreads.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorThreads.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return "Processamento concluído!";
    }

    public static void criarRotaNN(String StrNoOrigem, String cidadeOrigem, String StrNoDestino, String cidadeDestino,
                                   String StrKM) throws IOException {

        Integer noOrigem = Integer.parseInt(StrNoOrigem);
        Integer noDestino = Integer.parseInt(StrNoDestino);
        Integer KM = Integer.parseInt(StrKM);

        criaNoCidade(noOrigem, cidadeOrigem);
        criaNoCidade(noDestino, cidadeDestino);

        String pastaRotas = Config.getPasta();
        String pastaSucesso = Config.getConfig().getSucesso();
        String pastaErro = Config.getConfig().getSucesso();

        int contador = 1;
        while (Files.exists(Paths.get(pastaRotas, "rota" + contador + ".txt")) ||
                Files.exists(Paths.get(pastaSucesso, "rota" + contador + ".txt")) ||
                Files.exists(Paths.get(pastaErro, "rota" + contador + ".txt"))
        ) {
            contador++;
        }

        int totalNos = mapaNoCidade.size();
        String header = String.format("00%02d%05d", totalNos, KM);

        String conexao = String.format("01%s=%s", StrNoOrigem, StrNoDestino);

        String resumoPeso = String.format("02%s;%s=%s", noOrigem, noDestino, StrKM);

        String pesoFormatado = String.format("%04d", KM);
        String trailer = String.format("09RC=%02d;RP=%02d;%05d", 1, 1, KM);

        String conteudo = String.join("\n",
                header,
                conexao,
                resumoPeso,
                trailer
        ) + "\n";

        Path caminhoArquivo = Paths.get(pastaRotas, "rota" + contador + ".txt");
        Files.writeString(caminhoArquivo, conteudo);

        System.out.println("Rota salva em: " + caminhoArquivo);
    }

    public static void adicionarTabela(JTable tabela,
                                       JTextField campoCodigoOrigem,
                                       JTextField campoCidadeOrigem,
                                       JTextField campoCodigoDestino,
                                       JTextField campoCidadeDestino,
                                       JTextField campoKm) {

        String codigoOrigem = campoCodigoOrigem.getText().trim();
        String cidadeOrigem = campoCidadeOrigem.getText().trim();
        String codigoDestino = campoCodigoDestino.getText().trim();
        String cidadeDestino = campoCidadeDestino.getText().trim();
        String distancia = campoKm.getText().trim();

        if (codigoOrigem.isEmpty() || cidadeOrigem.isEmpty() ||
                codigoDestino.isEmpty() || cidadeDestino.isEmpty() || distancia.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos antes de adicionar na tabela!");
            return;
        }

        Integer noOrigem = Integer.parseInt(codigoOrigem);
        Integer noDestino = Integer.parseInt(codigoDestino);
        Integer KM = Integer.parseInt(distancia);

        new Conexao(noOrigem, noDestino, KM);

        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
        modelo.addRow(new Object[]{codigoOrigem, cidadeOrigem, codigoDestino, cidadeDestino, distancia});

        campoCodigoOrigem.setText("");
        campoCidadeOrigem.setText("");
        campoCodigoDestino.setText("");
        campoCidadeDestino.setText("");
        campoKm.setText("");

        campoCodigoOrigem.requestFocus();
    }


    public static boolean lerAutomatico() {

        Config config = Config.getConfig();

        return config.isAutomatico();

    }

    public static void mantemLendoPastaRotas() {

        Timer temporizador = new Timer();

        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                buscarDiretorio(Config.getPasta());
                preencherTabela(getTabela());
                System.out.println("Lendo pasta " + Config.getPasta());
            }
        };

        temporizador.scheduleAtFixedRate(tarefa, 0, 10000);
    }

    public static void preencherTabela(JTable tabela) {
        /*
        if (mapaNoCidade.isEmpty() || listaConexoes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há dados para exibir na tabela!");
            return;
        }*/


        System.out.println("➡️ Preenchendo tabela...");

        if (tabela == null) {
            System.out.println("❌ A tabela é nula!");
            return;
        }

        System.out.println("✔️ Tabela OK");
        System.out.println("listaConexoes tamanho: " + listaConexoes.size());
        System.out.println("mapaNoCidade tamanho: " + mapaNoCidade.size());


        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
        modelo.setRowCount(0);

        for (Map.Entry<Integer, String> entrada : mapaNoCidade.entrySet()) {
            Integer noOrigem = entrada.getKey();
            String cidadeOrigem = entrada.getValue();

            for (Conexao conexao : listaConexoes) {
                if (conexao.getNoOrigem().equals(noOrigem)) {
                    Integer noDestino = conexao.getNoDestino();
                    String cidadeDestino = mapaNoCidade.getOrDefault(noDestino, "-");
                    Integer peso = conexao.getPesoConexao();

                    modelo.addRow(new Object[]{
                            noOrigem,
                            cidadeOrigem,
                            noDestino,
                            cidadeDestino,
                            peso
                    });
                }
            }
        }

    }

}