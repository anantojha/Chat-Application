package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/*
    A class to handle the server portion of the app

    Class variables:
        threads: contains ServerThread objects - one per client that connects
        ss: ServerSocket object
 */
public class ChatServer implements Serializable
{
    ArrayList<ServerThread> threads;
    ServerSocket ss;

    public static void main(String[] args) throws IOException {
        ChatServer chatSrv = new ChatServer();
        chatSrv.start();
    }

    public ChatServer() {
        this.threads = new ArrayList<>();
    }

    /* Starts server and accepts new connections */
    public void start() throws IOException {
        ss = new ServerSocket(10008);
        try {
            while (true) {
                Socket socket = ss.accept();
                ServerThread serverThread = new ServerThread(socket, threads);
                threads.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) { }
    }

    /* Kills all ServerThread sockets and threads, then closes the ServerSocket */
    public void stop() throws IOException {
        for (ServerThread sT : this.threads) {
            sT.socket.close();
            sT.interrupt();
        }
        ss.close();
    }

    public static class ServerThread extends Thread {
        private Socket socket;
        private ArrayList<ServerThread> threadList;
        ArrayList<String> messages;
        private PrintWriter output;
        BufferedReader input;

        public ServerThread(Socket socket, ArrayList<ServerThread> threads) {
            this.socket = socket;
            this.threadList = threads;
            this.messages = new ArrayList<>();
        }

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                ChatLoop();
            } catch (Exception e) {
                System.out.println("Error occurred: " + Arrays.toString(e.getStackTrace()));
            }
        }

        /* Waits for new messages to arrive, and broadcasts to all clients */
        private void ChatLoop() throws IOException {
            while (true) {
                String outputString = input.readLine();

                if (outputString == null)
                    break;

                printToAllClients(outputString);
                System.out.println("Server received: " + outputString);
                this.messages.add(outputString);
            }
        }

        /* Broadcasts incoming message to all clients */
        private void printToAllClients(String outputString) {
            for (ServerThread sT : threadList) {
                sT.output.println(outputString);
            }

        }
    }
}
