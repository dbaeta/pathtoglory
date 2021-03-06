package org.academiadecodigo.balboas;

import org.academiadecodigo.balboas.persistance.ConnectionManager;
import org.academiadecodigo.balboas.services.JdbcUserService;
import org.academiadecodigo.balboas.services.MockUserService;
import org.academiadecodigo.balboas.services.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Daniel Baeta on 23/11/17.
 */
public class Server {

    private ServerSocket serverSocket;
    private ExecutorService service;
    private int portNumber = 32324;


    Server() {

        //Using MockUserService, instead of JdbcUserService
        UserService userService = new MockUserService();
        MessageProtocol.setService(userService);
        init();
    }

    public static void main(String[] args) {

        Server server = new Server();

        while (true) {
            server.acceptConnections();
        }
    }


    private void init() {

        try {
            serverSocket = new ServerSocket(portNumber);
            service = Executors.newCachedThreadPool();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void acceptConnections() {

        try {
            Socket socket = serverSocket.accept();
            System.out.println("Connection established");
            ServerWorker serverWorker = new ServerWorker(socket);
            MessageProtocol.addServerWorkers(serverWorker);
            service.submit(serverWorker);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public class ServerWorker implements Runnable {

        private String playerName;

        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String name;
        private int ID;


        ServerWorker(Socket socket) {
            this.socket = socket;
            init();
        }

        private void init() {

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getMessage(String message) {

            if (message.startsWith(String.valueOf(MessageProtocol.MOVE))) {
                MessageProtocol.decode(message, this);
                return;
            }

            String answer = MessageProtocol.decode(message,this);
            sendMessage(answer);
        }

        public void sendMessage(String message) {
            System.out.println("Send message from server");
            writer.println(message);
        }

        @Override
        public void run() {

            String message = "";

            try {
                while (true) {
                    message = reader.readLine();
                    getMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("socket closed");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void setName(String name) {
            this.name = name;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }

        public String getName() {
            return name;
        }
    }
}
