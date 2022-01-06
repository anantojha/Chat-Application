package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatServer implements Serializable
{
    public static void main(String[] args) {
        ArrayList<ServerThread> threads = new ArrayList<>();
        try (ServerSocket ss = new ServerSocket(10008)){
            while (true) {
                Socket socket = ss.accept();
                ServerThread serverThread = new ServerThread(socket, threads);
                threads.add(serverThread);
                serverThread.start();
            }
        } catch (Exception e) {
            System.out.println("Server Connection Error: " + Arrays.toString(e.getStackTrace()));
        }
    }

    public static class ServerThread extends Thread {
        private Socket socket;
        private ArrayList<ServerThread> threadList;
        private PrintWriter output;
        BufferedReader input;

        public ServerThread(Socket socket, ArrayList<ServerThread> threads) {
            this.socket = socket;
            this.threadList = threads;
        }

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                ChatLoop();
            } catch (Exception e) {
                System.out.println("Error occured " + Arrays.toString(e.getStackTrace()));
            }
        }

        private void ChatLoop() throws IOException {
            while (true) {
                String outputString = input.readLine();

                if (outputString == null)
                    break;

                printToALlClients(outputString);
                System.out.println("Server received: " + outputString);
            }
        }

        private void printToALlClients(String outputString) {
            for (ServerThread sT : threadList) {
                sT.output.println(outputString);
            }

        }
    }
}
