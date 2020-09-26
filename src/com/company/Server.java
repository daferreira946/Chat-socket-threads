package com.company;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private static ArrayList<BufferedWriter>clients;
    private static ArrayList<String>clientsName;
    private String name;
    private final Socket connection;
    private BufferedReader bufferedReader;

    public Server (Socket connection){

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
            clients = new ArrayList<>();

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

            clients.add(bufferedWriter);
            name = message = bufferedReader.readLine();
            clientsName.add(name);

            while(!"sair".equalsIgnoreCase(message) && message != null) {

                message = bufferedReader.readLine();
                sendToAll(bufferedWriter, message);
                System.out.println(message);

            }

        } catch (Exception $e) {
            $e.printStackTrace();
        }
    }

    public void sendToAll(BufferedWriter bufferedWriterOut, String message) throws IOException {

        BufferedWriter bufferedWriterOutStatement;

        for (BufferedWriter bufferedWriter : clients) {

            bufferedWriterOutStatement = bufferedWriter;

            if (!(bufferedWriterOut == bufferedWriterOutStatement)) {

                bufferedWriter.write(name + "->" + message + "\r\n");
                bufferedWriter.flush();

            }
        }

    }
}
