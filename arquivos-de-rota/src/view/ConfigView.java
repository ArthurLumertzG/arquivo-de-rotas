package view;

import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ConfigView extends JFrame {

    private JTextField txfPasta;
    private JTextField txfSucesso;
    private JTextField txfErro;
    private JCheckBox chkRotaAutomatica;
    private JButton btnSalvar;

    public ConfigView() {
        createComponents();
    }

    private void createComponents() {
        setTitle("Configurações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 240, 255));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 180), 2));

        JLabel lblIcon = new JLabel(new ImageIcon());
        lblIcon.setBounds(20, 15, 32, 32);
        mainPanel.add(lblIcon);

        JLabel lblTitulo = new JLabel("Configurações");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(70, 70, 70));
        lblTitulo.setBounds(245, 20, 200, 25);
        mainPanel.add(lblTitulo);

        JLabel lblPasta = new JLabel("Pasta:");
        lblPasta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPasta.setForeground(new Color(70, 70, 70));
        lblPasta.setBounds(150, 70, 150, 25);
        mainPanel.add(lblPasta);

        txfPasta = new JTextField();
        txfPasta.setBounds(220, 70, 215, 30);
        txfPasta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfPasta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        mainPanel.add(txfPasta);

        JLabel lblSucesso = new JLabel("Sucesso:");
        lblSucesso.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSucesso.setForeground(new Color(70, 70, 70));
        lblSucesso.setBounds(150, 120, 150, 25);
        mainPanel.add(lblSucesso);

        txfSucesso = new JTextField();
        txfSucesso.setBounds(220, 120, 215, 30);
        txfSucesso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfSucesso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        mainPanel.add(txfSucesso);

        JLabel lblErro = new JLabel("Erro:");
        lblErro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblErro.setForeground(new Color(70, 70, 70));
        lblErro.setBounds(150, 170, 150, 25);
        mainPanel.add(lblErro);

        txfErro = new JTextField();
        txfErro.setBounds(220, 170, 215, 30);
        txfErro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txfErro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        mainPanel.add(txfErro);

        chkRotaAutomatica = new JCheckBox("Rota automática");
        chkRotaAutomatica.setBounds(245, 215, 200, 25);
        chkRotaAutomatica.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chkRotaAutomatica.setForeground(new Color(100, 100, 100));
        chkRotaAutomatica.setBackground(new Color(240, 240, 255));
        chkRotaAutomatica.setFocusPainted(false);
        mainPanel.add(chkRotaAutomatica);

        btnSalvar = new JButton("SALVAR");
        btnSalvar.setBounds(240, 245, 140, 35);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setForeground(new Color(70, 90, 140));
        btnSalvar.setBackground(new Color(220, 230, 255));
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 180), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> {
            try {
                if ( Utils.criaConfig( txfPasta.getText(), txfSucesso.getText(), txfErro.getText(), chkRotaAutomatica.isSelected()) ) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Diretórios criados com sucesso!");
                    txfPasta.setText("");
                    txfSucesso.setText("");
                    txfErro.setText("");
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Houve um erro\nOs diretórios não foram criados!");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        mainPanel.add(btnSalvar);

        add(mainPanel);
    }

    private void salvar() {
        String pasta = txfPasta.getText();
        String sucesso = txfSucesso.getText();
        String erro = txfErro.getText();
        boolean rotaAutomatica = chkRotaAutomatica.isSelected();

        JOptionPane.showMessageDialog(this,
                "Configurações salvas!\n" +
                        "Pasta: " + pasta + "\n" +
                        "Sucesso: " + sucesso + "\n" +
                        "Erro: " + erro + "\n" +
                        "Rota automática: " + rotaAutomatica,
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }
}