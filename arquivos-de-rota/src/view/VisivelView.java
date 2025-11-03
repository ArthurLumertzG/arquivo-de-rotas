package view;

import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class VisivelView extends JFrame {

    private static JTable tabela;

    public VisivelView() {
        createComponents();
    }

    private void createComponents() {
        setTitle("Dijkstra - Busca por melhor caminho");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 630);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(235, 240, 245));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 180), 2));

        JLabel lblTitulo = new JLabel("Dijkstra - Busca por melhor caminho");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(70, 70, 70));
        lblTitulo.setBounds(220, 20, 400, 25);
        mainPanel.add(lblTitulo);

        JLabel lblBuscar = new JLabel("Buscar");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBuscar.setBounds(85, 70, 60, 25);
        mainPanel.add(lblBuscar);

        JTextField txfBuscar = new JTextField();
        txfBuscar.setBounds(140, 70, 305, 26);
        txfBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfBuscar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(txfBuscar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(455, 70, 90, 26);
        btnBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBackground(new Color(240, 240, 240));
        btnBuscar.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
        btnBuscar.addActionListener(e -> Utils.buscarDiretorio(txfBuscar.getText()));
        mainPanel.add(btnBuscar);

        JButton btnEscolher = new JButton("Escolher");
        btnEscolher.setBounds(565, 70, 90, 26);
        btnEscolher.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEscolher.setFocusPainted(false);
        btnEscolher.setBackground(new Color(240, 240, 240));
        btnEscolher.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
        btnEscolher.addActionListener(e -> {
            JFileChooser fcEscolher = new JFileChooser();
            fcEscolher.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int retorno = fcEscolher.showOpenDialog(this);

            if (retorno == JFileChooser.APPROVE_OPTION) {
                File pastaSelecionada = fcEscolher.getSelectedFile();
                txfBuscar.setText(pastaSelecionada.getAbsolutePath());
                System.out.println("Pasta selecionada: " + pastaSelecionada.getAbsolutePath());
            } else {
                System.out.println("Nenhuma pasta foi selecionada.");
            }
        });
        mainPanel.add(btnEscolher);

        JLabel lblCodigoOrigem = new JLabel("Código:");
        lblCodigoOrigem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCodigoOrigem.setBounds(85, 115, 60, 25);
        mainPanel.add(lblCodigoOrigem);

        JTextField txfCodigoOrigem = new JTextField();
        txfCodigoOrigem.setBounds(140, 115, 75, 26);
        txfCodigoOrigem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfCodigoOrigem.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(txfCodigoOrigem);

        JLabel lblCidadeOrigem = new JLabel("Cidade:");
        lblCidadeOrigem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCidadeOrigem.setBounds(238, 115, 60, 25);
        mainPanel.add(lblCidadeOrigem);

        JTextField txfCidadeOrigem = new JTextField();
        txfCidadeOrigem.setBounds(293, 115, 152, 26);
        txfCidadeOrigem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfCidadeOrigem.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(txfCidadeOrigem);

        JLabel lblOrigem = new JLabel("(ORIGEM)");
        lblOrigem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOrigem.setForeground(new Color(120, 120, 120));
        lblOrigem.setBounds(455, 115, 80, 25);
        mainPanel.add(lblOrigem);

        JLabel lblCodigoDestino = new JLabel("Código:");
        lblCodigoDestino.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCodigoDestino.setBounds(85, 155, 60, 25);
        mainPanel.add(lblCodigoDestino);

        JTextField txfCodigoDestino = new JTextField();
        txfCodigoDestino.setBounds(140, 155, 75, 26);
        txfCodigoDestino.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfCodigoDestino.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(txfCodigoDestino);

        JLabel lblCidadeDestino = new JLabel("Cidade:");
        lblCidadeDestino.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCidadeDestino.setBounds(238, 155, 60, 25);
        mainPanel.add(lblCidadeDestino);

        JTextField txfCidadeDestino = new JTextField();
        txfCidadeDestino.setBounds(293, 155, 152, 26);
        txfCidadeDestino.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfCidadeDestino.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(txfCidadeDestino);

        JLabel lblDestino = new JLabel("(DESTINO)");
        lblDestino.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDestino.setForeground(new Color(120, 120, 120));
        lblDestino.setBounds(455, 155, 80, 25);
        mainPanel.add(lblDestino);

        JLabel lblKM = new JLabel("KM:");
        lblKM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblKM.setBounds(105, 195, 40, 25);
        mainPanel.add(lblKM);

        JTextField txfKM = new JTextField();
        txfKM.setBounds(140, 195, 75, 26);
        txfKM.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfKM.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(txfKM);

        String[] colunas = {"Código Origem", "Cidade Origem", "Código Destino", "Cidade Destino", "Distância"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(colunas, 0);

        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(22);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setBackground(new Color(230, 235, 240));
        tabela.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBounds(45, 240, 620, 300);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        mainPanel.add(scrollPane);

        JButton btnAdicionar = new JButton("+");
        btnAdicionar.setBounds(620, 195, 45, 26);
        btnAdicionar.setFont(new Font("Arial", Font.BOLD, 16));
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.setBackground(new Color(240, 240, 240));
        btnAdicionar.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
        btnAdicionar.addActionListener( e -> {
            try {
                Utils.criarRotaNN(txfCodigoOrigem.getText(), txfCidadeOrigem.getText(), txfCodigoDestino.getText(),
                        txfCidadeDestino.getText(), txfKM.getText());
                Utils.adicionarTabela(tabela,
                        txfCodigoOrigem,
                        txfCidadeOrigem,
                        txfCodigoDestino,
                        txfCidadeDestino,
                        txfKM);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        mainPanel.add(btnAdicionar);

        JButton btnSalvar = new JButton("SALVAR");
        btnSalvar.setBounds(445, 555, 105, 30);
        btnSalvar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBackground(new Color(240, 240, 240));
        btnSalvar.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
       // btnSalvar.addActionListener(e -> );
        mainPanel.add(btnSalvar);

        JButton btnProcessar = new JButton("PROCESSAR");
        btnProcessar.setBounds(560, 555, 105, 30);
        btnProcessar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnProcessar.setFocusPainted(false);
        btnProcessar.setBackground(new Color(240, 240, 240));
        btnProcessar.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 1));
        //btnProcessar.addActionListener(e -> );
        mainPanel.add(btnProcessar);

        add(mainPanel);
    }

    public static JTable getTabela() {
        return tabela;
    }
}