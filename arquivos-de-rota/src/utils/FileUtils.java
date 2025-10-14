package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

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
     * Writes lines in the file
     *
     * @param filePath The name of the file to write
     * @param rows     Roes to be writing in the file
     * @throws IOException Signals thta o I/O of some sort has occurred
     */
    public static void writeLines(String filePath, List<String> rows) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String r : rows) {
                writer.write(r);
                writer.newLine();
            }
        }
    }

    /**
     * Writes text on the file
     *
     * @param filePath The name of the file to write
     * @param text     The text to be writing in the file
     * @throws IOException Signals thta o I/O of some sort has occurred
     */
    public static void writeText(String filePath, String text, boolean isAppend) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, isAppend))) {
            writer.write(text);
            if (isAppend) {
                writer.newLine();
            }
        }
    }


    /**
     * Remove the file of disk
     *
     * @param filePath The name of the file to delete
     * @return TRUE if the file has been removed
     * or
     * FALSE if not
     * @throws IOException Signals thta o I/O of dome dort has occurred
     */
    public static boolean deleteFile(String filePath) throws IOException {
        return Files.deleteIfExists(Path.of(filePath));
    }

    /**
     * Validate the file config.txt
     *
     * @param configPath Path of config.txt
     * @return The error or null
     * @throws IOException Signals thta o I/O of dome dort has occurred
     */
    public static String validateConfig(String configPath) throws IOException {

        List<String> linhasConfig = new ArrayList<String>();

        if (!Files.exists(Path.of(configPath))) {
            return "Arquivo config.txt não encontrado em -> " + configPath;
        }

        linhasConfig = FileUtils.readAllLines(configPath);
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

        if (linhasValidas.size() < 2) {
            return "Há linha(s) faltando em config.txt";
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
}
