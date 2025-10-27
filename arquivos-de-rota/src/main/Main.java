package main;

import utils.Utils;
import view.ConfigView;
import view.MainView;

import javax.swing.*;
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
        SwingUtilities.invokeLater(() -> {

            try {
                if (Utils.procuraConfig() != null){
                    MainView mainView = new MainView();
                    mainView.setVisible(true);

                    String resposta = Utils.procuraConfig();
                    JOptionPane.showMessageDialog(mainView, resposta);
                } else {
                    MainView mainView = new MainView();
                    mainView.setVisible(true);
                    ConfigView configView = new ConfigView();
                    configView.setVisible(true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}