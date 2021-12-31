package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer implements Serializable
{
    public static void main(String[] args) {
        ArrayList<ServerThread> threads = new ArrayList<>();
        try (ServerSocket ss = new ServerSocket(10008)){
            while(true) {
                Socket socket = ss.accept();
                ServerThread serverThread = new ServerThread(socket, threads);
                threads.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) {
            System.out.println("Server Connection Error: " + e.getStackTrace());
        }
    }

    public static class ServerThread extends Thread {
        private Socket socket;
        private ArrayList<ServerThread> threadList;
        private PrintWriter output;

        public ServerThread(Socket socket, ArrayList<ServerThread> threads) {
            this.socket = socket;
            this.threadList = threads;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String outputString = input.readLine();

                    if (outputString.equals("exit."))
                        break;

                    printToALlClients(outputString);
                    System.out.println("Server received: " + outputString);
                }
            } catch (Exception e) {
                System.out.println("Error occured " + e.getStackTrace());
            }
        }

        private void printToALlClients(String outputString) {
            for (ServerThread sT : threadList) {
                sT.output.println(outputString);
            }

        }
    }
}
