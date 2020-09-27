package com.company;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {
    private static Map<String,BufferedWriter> clients;
    private final Socket connection;
    private BufferedReader bufferedReader;

    public Server(Socket connection){

        this.connection = connection;

        try {

            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

        } catch (IOException $e) {
            $e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        try {

            JLabel labelMessage = new JLabel("Porta do Servidor:");
            JTextField textFieldPort = new JTextField("12345");
            Object[] texts = {labelMessage, textFieldPort};

            JOptionPane.showMessageDialog(null, texts);

            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(textFieldPort.getText()));
            clients = new HashMap<>();

            JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + textFieldPort.getText());

            //noinspection InfiniteLoopStatement
            while (true) {

                System.out.println("Aguardando conexão...");
                Socket connection = serverSocket.accept();

                System.out.println("Cliente conectado...");
                Thread thread = new Server(connection);

                thread.start();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {

        try {

            String message;

            OutputStream outputStream = this.connection.getOutputStream();
            Writer outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            String name = message = bufferedReader.readLine();
            int i = 1;

            for (Map.Entry<String, BufferedWriter> entry : clients.entrySet()) {

                if (name.equals(entry.getKey())) {
                    name = String.format(name + "%d", i);
                }

                i++;
            }

            clients.put(name,bufferedWriter);


            while(!"sair".equalsIgnoreCase(message) && message != null) {

                message = bufferedReader.readLine();

                if (message.matches("^/.+/.+")) {
                    String[] compost = message.split("/");
                    String privateMessage = compost[2];
                    String privateRecepient = compost[1];

                    sendToOne(bufferedWriter, privateMessage, privateRecepient);

                } else {

                    sendToAll(bufferedWriter, message);
                    System.out.println(message);

                }

            }

        } catch (Exception $e) {
            $e.printStackTrace();
        }
    }

    public void sendToAll(BufferedWriter bufferedWriterOut, String message) throws IOException {

        BufferedWriter bufferedWriterOutStatement;
        String name = "";

        for (Map.Entry<String, BufferedWriter> entry : clients.entrySet()) {

            if (bufferedWriterOut == entry.getValue()) {
                name = entry.getKey();
            }

        }

        for (Map.Entry<String, BufferedWriter> entry : clients.entrySet()) {

            bufferedWriterOutStatement = entry.getValue();

            if (!(bufferedWriterOut == bufferedWriterOutStatement)) {

                entry.getValue().write( name + "->" + message + "\r\n");
                entry.getValue().flush();

            }
        }

    }

    public void sendToOne(BufferedWriter bufferedWriterOut, String message, String recipient) throws IOException {

        String name = "";

        for (Map.Entry<String, BufferedWriter> entry : clients.entrySet()) {

            if (bufferedWriterOut == entry.getValue()) {
                name = entry.getKey();
            }

        }

        for (Map.Entry<String, BufferedWriter> entry : clients.entrySet()) {

            if (entry.getKey().equalsIgnoreCase(recipient)) {

                entry.getValue().write(name + "/privado/->" + message + "\r\n");
                entry.getValue().flush();

            }
        }

    }


}
