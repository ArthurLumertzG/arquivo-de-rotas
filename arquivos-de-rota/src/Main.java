import entities.Conexao;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import entities.*;

public class Main {
    public static void main(String[] args) throws IOException {

        String caminhoProcessados = null;
        String caminhoNaoProcessados = null;
        String caminhoRotas = "C:/Teste/rota";
        String caminhoConfig = "C:/Teste/Configuracao/config.txt";
        List<String> linhasConfig = new ArrayList<String>();

        String validaConfig = FileUtils.validateConfig(caminhoConfig);
        if (validaConfig != null) {
            System.out.println("\n" + validaConfig);
            return;
        }
        System.out.println("\nTudo correto com o arquivo config.txt");
        linhasConfig = FileUtils.readAllLines("C:/Teste/Configuracao/config.txt");

        for (String linha : linhasConfig) {
            String[] linhaDividida = linha.split("=");

            if (linhaDividida.length == 2) {
                String nomePasta = linhaDividida[0].trim();
                String caminhoPasta = linhaDividida[1].trim();

                if (nomePasta.equalsIgnoreCase("processado")) {
                    caminhoProcessados = caminhoPasta;
                    Path processadosDiretorio = Paths.get(caminhoPasta);
                    try {
                        File processados = new File(caminhoProcessados);
                        Files.createDirectories(processadosDiretorio);
                        System.out.println("\nDiretório " + caminhoPasta + " criado com sucesso!\n");
                    } catch (Exception e) {
                        System.out.println("Falha ao criar o diretório" + nomePasta + ": " + e.getMessage());
                    }

                } else if (nomePasta.equalsIgnoreCase("não processado")) {
                    caminhoNaoProcessados = caminhoPasta;
                    Path naoProcessadosDiretorio = Paths.get(caminhoPasta);
                    try {
                        File naoProcessados = new File(caminhoNaoProcessados);
                        Files.createDirectories(naoProcessadosDiretorio);
                        System.out.println("Diretório " + caminhoPasta + " criado com sucesso!\n");
                    } catch (Exception e) {
                        System.out.println("Falha ao criar o diretório" + nomePasta + ": " + e.getMessage());
                    }
                }
            }
        }

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
                        List<String> linhasRota = FileUtils.readAllLines(origemPath.toString());

                        // valida o arquivo por completo (header, 01, 02, trailer e somas)
                        String validaRota = FileUtils.validateRotaFile(linhasRota);

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
    }
}