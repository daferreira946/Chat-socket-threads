package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private final JTextArea text;
    private final JTextField textMessage;
    private final JButton buttonSend;
    private final JButton buttonQuit;
    private Socket socket;
    private OutputStream outputStream;
    private Writer outputStreamWriter;
    private BufferedWriter bufferedWriter;
    private final JTextField textIP;
    private final JTextField textPort;
    private final JTextField textName;

    public Client() {

        JLabel labelMessageVerification = new JLabel("Verificar!");
        textIP = new JTextField("127.0.0.1");
        textPort = new JTextField("12345");
        textName = new JTextField("Cliente");

        Object[] texts = {labelMessageVerification, textIP, textPort, textName};

        JOptionPane.showMessageDialog(null, texts);

        JPanel panelContent = new JPanel();

        text = new JTextArea(10,20);
        text.setEditable(false);
        text.setBackground(new Color(240, 240,240));

        textMessage = new JTextField(20);

        JLabel labelHistory = new JLabel("Histórico");
        JLabel labelMessage = new JLabel("Mensagem");

        buttonSend = new JButton("Enviar");
        buttonSend.setToolTipText("Enviar Mensagem");
        buttonQuit = new JButton("Sair");
        buttonQuit.setToolTipText("Sair do chat");

        buttonSend.addActionListener(this);
        buttonQuit.addActionListener(this);

        buttonSend.addKeyListener(this);
        textMessage.addKeyListener(this);

        JScrollPane scrollPane = new JScrollPane(text);
        text.setLineWrap(true);

        panelContent.add(labelHistory);
        panelContent.add(scrollPane);
        panelContent.add(labelMessage);
        panelContent.add(textMessage);
        panelContent.add(buttonQuit);
        panelContent.add(buttonSend);

        panelContent.setBackground(Color.LIGHT_GRAY);
        text.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        textMessage.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));

        setTitle(textName.getText());
        setContentPane(panelContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(300,400);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public static void main(String[] args) throws IOException{

        Client client = new Client();
        client.connect();
        client.listener();

    }

    public void connect() throws IOException{

        socket = new Socket(textIP.getText(), Integer.parseInt(textPort.getText()));
        outputStream = socket.getOutputStream();
        outputStreamWriter = new OutputStreamWriter(outputStream);

        bufferedWriter = new BufferedWriter(outputStreamWriter);
        bufferedWriter.write(textName.getText() + "\r\n");
        bufferedWriter.flush();

    }

    public void sendMessage(String message) throws IOException {

        if (message.equalsIgnoreCase("Sair")) {

            bufferedWriter.write("Desconectado \r\n");
            text.append("Desconectado \r\n");

        } else {

            bufferedWriter.write(message + "\r\n");
            text.append(message + "\r\n");

        }

        bufferedWriter.flush();
        textMessage.setText("");

    }

    public void listener() throws IOException {

        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String message = "";

        while (!"Sair".equalsIgnoreCase(message)){

            if (bufferedReader.ready()) {

                message = bufferedReader.readLine();
                if (message.equalsIgnoreCase("sair")) {

                    text.append("Servidor caiu... \r\n");

                } else {

                    text.append(message + "\r\n");

                }


            }

        }

    }

    public void quit() throws IOException {

        sendMessage("Sair");
        bufferedWriter.close();
        outputStreamWriter.close();
        outputStream.close();
        socket.close();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getActionCommand().equals(buttonSend.getActionCommand())) {
                sendMessage(textMessage.getText());
            } else {
                if (e.getActionCommand().equals(buttonQuit.getActionCommand())) {
                    quit();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                sendMessage(textMessage.getText());
            }catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
