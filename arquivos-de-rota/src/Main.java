import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
                    } catch ( Exception e ){
                        System.out.println("Falha ao criar o diretório" + nomePasta + ": " + e.getMessage());
                    }
                }
            }
        }

        for ( int i = 1; i <= 10; i ++) {
            List<String> linhasRota = null;
            if (Files.exists(Path.of(caminhoRotas + i + ".txt")) && Files.isRegularFile(Path.of(caminhoRotas + i + ".txt"))) {
                linhasRota = FileUtils.readAllLines(caminhoRotas + i + ".txt");
                System.out.println(i);
            } else {
                continue;
                };
        }



    }
}