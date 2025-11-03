package main;

import utils.Utils;
import view.ConfigView;
import view.MainView;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String configEncontrada = Utils.procuraConfig();

                MainView mainView = new MainView();
                mainView.setVisible(true);

                if (configEncontrada != null) {
                    JOptionPane.showMessageDialog(mainView, configEncontrada);
                    if ( Utils.lerAutomatico() ) {
                        Utils.mantemLendoPastaRotas();
                    }

                } else {
                    ConfigView configView = new ConfigView();
                    configView.setVisible(true);
                    JOptionPane.showMessageDialog(mainView,
                            "Nenhuma configuração encontrada.\nPor favor, configure o sistema.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao carregar configuração: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}