package view;

import javax.swing.*;
import java.awt.*;

// Tela Principal com Menu
public class MainView extends JFrame {

    public MainView() {
        createComponents();
    }

    private void createComponents() {
        setTitle("Sistema de Rotas de Arquivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Criar barra de menu
        JMenuBar menuBar = new JMenuBar();

        // Menu Arquivo
        JMenu menuOpcoes = new JMenu("Opções");

        JMenuItem itemVisivel = new JMenuItem("Visível");
        itemVisivel.addActionListener(e -> {
            abrirVisivel();
        });

        JMenuItem itemConfiguracoes = new JMenuItem("Configurações");
        itemConfiguracoes.addActionListener( e -> {
            abrirConfiguracoes();
        });

        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente sair?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuOpcoes.add(itemVisivel);
        menuOpcoes.add(itemConfiguracoes);
        menuOpcoes.add(itemSair);

        menuBar.add(menuOpcoes);
        setJMenuBar(menuBar);

        // Painel central
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Sistema de Rotas de Arquivos", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(70, 90, 140));

        mainPanel.add(lblWelcome, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        add(mainPanel);
    }

    private void abrirConfiguracoes() {
        ConfigView configView = new ConfigView();
        configView.setVisible(true);
    }

    private void abrirVisivel() {
        VisivelView visivelView = new VisivelView();
        visivelView.setVisible(true);
    }


}
