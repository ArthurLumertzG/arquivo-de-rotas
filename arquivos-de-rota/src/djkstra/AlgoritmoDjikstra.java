package djkstra;

import entities.Conexao;
import view.MainView;

import javax.swing.*;
import java.util.*;

public class AlgoritmoDjikstra {

    private static final int INDEFINIDO = -1;

    public static ResultadoCaminho calcular(List<Conexao> listaOriginal, int noOrigem, int noDestino) {
        // Cria uma cópia da lista original para evitar ConcurrentModificationException
        List<Conexao> listaConexoes = new ArrayList<>(listaOriginal);

        // Inicialização de estruturas
        Set<Integer> naoVisitados = new HashSet<>();
        Map<Integer, Integer> custo = new HashMap<>();
        Map<Integer, Integer> antecessor = new HashMap<>();

        // Inicializa os custos
        for (Conexao c : listaConexoes) {
            naoVisitados.add(c.getNoOrigem());
            naoVisitados.add(c.getNoDestino());
        }

        if (!naoVisitados.contains(noOrigem)) {
            JOptionPane.showMessageDialog(null, "Escolha um nó existente!");
            throw new Error ("Nó de origem não existe no grafo.");
        }

        if (!naoVisitados.contains(noDestino)) {
            JOptionPane.showMessageDialog(null, "Escolha um nó existente!");
            throw new Error ("Nó de destino não existe no grafo.");
        }

        for (Integer no : naoVisitados) {
            custo.put(no, Integer.MAX_VALUE);
            antecessor.put(no, -1);
        }

        custo.put(noOrigem, 0);

        while (!naoVisitados.isEmpty()) {
            int atual = getMaisProximo(custo, naoVisitados);
            naoVisitados.remove(atual);

            for (Conexao vizinho : getVizinhos(listaConexoes, atual)) {
                int outro = (vizinho.getNoOrigem() == atual) ? vizinho.getNoDestino() : vizinho.getNoOrigem();
                int custoNovo = custo.get(atual) + vizinho.getPesoConexao();

                if (custoNovo < custo.get(outro)) {
                    custo.put(outro, custoNovo);
                    antecessor.put(outro, atual);
                }
            }

            if (atual == noDestino)
                break;
        }

        List<Integer> caminho = new ArrayList<>();
        Integer passo = noDestino;

        // reconstrói o caminho apenas se o destino for alcançável
        if (antecessor.containsKey(noDestino) || noDestino == noOrigem) {
            while (passo != -1 && antecessor.containsKey(passo)) {
                caminho.add(passo);
                passo = antecessor.get(passo);
            }

            // só adiciona o nó de origem se ainda não estiver na lista
            if (!caminho.contains(noOrigem)) {
                caminho.add(noOrigem);
            }

            Collections.reverse(caminho);
        } else {
            caminho.add(noOrigem);
        }

        return new ResultadoCaminho(caminho, custo.get(noDestino));
    }

    // Função auxiliar segura
    public static List<Conexao> getVizinhos(List<Conexao> listaOriginal, int noOrigem) {
        // Usa uma cópia local — nunca a original
        List<Conexao> lista = new ArrayList<>(listaOriginal);
        List<Conexao> vizinhos = new ArrayList<>();

        for (Conexao c : lista) {
            if (c.getNoOrigem() == noOrigem || c.getNoDestino() == noOrigem) {
                vizinhos.add(c);
            }
        }
        return vizinhos;
    }

    // Busca o nó mais próximo não visitado
    private static int getMaisProximo(Map<Integer, Integer> custo, Set<Integer> naoVisitados) {
        int min = Integer.MAX_VALUE;
        int maisProximo = -1;
        for (Integer no : naoVisitados) {
            if (custo.get(no) < min) {
                min = custo.get(no);
                maisProximo = no;
            }
        }
        return maisProximo;
    }

    private static List<Integer> reconstruirCaminho(Map<Integer, Integer> antecessor, int destino) {
        List<Integer> caminho = new ArrayList<>();
        Integer atual = destino;
        while (atual != null && atual != INDEFINIDO) {
            caminho.add(atual);
            atual = antecessor.get(atual);
        }
        Collections.reverse(caminho);
        return caminho;
    }

    public static class ResultadoCaminho {
        private final List<Integer> caminho;
        private final int custoTotal;

        public ResultadoCaminho(List<Integer> caminho, int custoTotal) {
            this.caminho = caminho;
            this.custoTotal = custoTotal;
        }

        public List<Integer> getCaminho() {
            return caminho;
        }

        public int getCustoTotal() {
            return custoTotal;
        }
    }
}
