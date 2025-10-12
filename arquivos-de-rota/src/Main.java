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

public class Main {
    public static void main(String[] args) throws IOException {

        String caminhoProcessados = null;
        String caminhoNaoProcessados = null;
        String caminhoRotas = "C:/Teste/rota";
        List<String> linhasConfig = new ArrayList<String>();
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
                        System.out.println("Diretório " + caminhoPasta + " criado com sucesso!");
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
                    List<String> linhasRota = null;
                    if (Files.exists(Path.of(caminhoRotas + rotaIndex + ".txt")) && Files.isRegularFile(Path.of(caminhoRotas + rotaIndex + ".txt"))) {
                        linhasRota = FileUtils.readAllLines(caminhoRotas + rotaIndex + ".txt");

                        Grafo grafo = new Grafo();

                        linhasRota.forEach((linha) -> {
                            if (linha.isEmpty()) {
                                return;
                            }
                            if (linha.startsWith("00")) {
                                Integer nosTotaisDoGrafo = Integer.parseInt(linha.substring(2, 4));
                                Integer somaPesosArestas = Integer.parseInt(linha.substring(4));

                                if (somaPesosArestas > 99999) {
                                    throw new Error();
                                }

                                grafo.setNosTotaisDoGrafo(nosTotaisDoGrafo);
                                grafo.setSomaPesosDasArestas(somaPesosArestas);
                            }

                            if (linha.startsWith("01")) {
                                String[] nosOrigemAteDestino = (linha.substring(2, 7)).split("=");

                                Integer nosOrigem = Integer.parseInt(nosOrigemAteDestino[0].trim());
                                Integer nosDestino = Integer.parseInt(nosOrigemAteDestino[1].trim());

                                Conexao conexao = new Conexao();
                                conexao.setNoDeOrigem(nosOrigem);
                                conexao.setNoDeDestino(nosDestino);

                                grafo.addConexoes(conexao);
                            }

                            if (linha.startsWith("02")) {
                                String[] nosOrigemAteDestino = ((linha.substring(2, 7)).trim()).split("=");

                                Integer nosOrigem = Integer.parseInt(nosOrigemAteDestino[0].trim());
                                Integer nosDestino = Integer.parseInt(nosOrigemAteDestino[1].trim());
                                Integer pesoDaAresta = Integer.parseInt(linha.substring(8).trim());

                                if (pesoDaAresta > 9999) {
                                    throw new Error();
                                }

                                Conexao[] grafoConexoes = grafo.getConexoes();

                                for (Conexao conexao : grafoConexoes) {
                                    if (conexao.getNoDeOrigem().equals(nosOrigem) && conexao.getNoDeDestino().equals(nosDestino)) {
                                        conexao.setPesoDaConexao(pesoDaAresta);
                                        break;
                                    }
                                }
                            }

                            if (linha.startsWith("09")) {
                                String linhasConexexoesPesos = linha.substring(2, 13);

                                String[] linhasConexoesPesosDivididos = linhasConexexoesPesos.split(";");

                                String quantidadeDeLinhasConexeos = linhasConexoesPesosDivididos[0].split("=")[1].trim();
                                String quantidadeDeLinhasPesos = linhasConexoesPesosDivididos[1].split("=")[1].trim();

                                String somaPesosTodosNos = linhasConexexoesPesos.substring(14);

                                if (Integer.parseInt(quantidadeDeLinhasConexeos) != grafo.getConexoes().length ||
                                        Integer.parseInt(quantidadeDeLinhasPesos) != grafo.getConexoes().length) {
                                    throw new Error();
                                }

                                if (Integer.parseInt(somaPesosTodosNos) != grafo.getSomaPesosDasArestas()) {
                                    throw new Error();
                                }
                            }
                        });

                        System.out.println("Rota " + rotaIndex + " processada com sucesso!");
                        Path origem = Paths.get(caminhoRotas + rotaIndex + ".txt");
                        Path destinoProcessado = Paths.get(finalCaminhoProcessados + "/rota" + rotaIndex + ".txt");
                        Files.move(origem, destinoProcessado);

                    } else {
                        Path origemErro = Paths.get(caminhoRotas + rotaIndex + ".txt");
                        Path destinoNaoProcessado = Paths.get(finalCaminhoNaoProcessados + "/rota" + rotaIndex + ".txt");
                        Files.move(origemErro, destinoNaoProcessado);
                    }
                } catch (Exception e) {
                    try {
                        Path origemErro = Paths.get(caminhoRotas + rotaIndex + ".txt");
                        Path destinoNaoProcessado = Paths.get(finalCaminhoNaoProcessados + "/rota" + rotaIndex + ".txt");
                        Files.move(origemErro, destinoNaoProcessado);
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