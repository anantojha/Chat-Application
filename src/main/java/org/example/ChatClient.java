package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Serializable {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        String clientName = scanner.nextLine();
        startClientChat(clientName);
        scanner.close();
    }

    /*listens to messages and sends them to server*/
    public static void startClientChat(String clientName){
        Scanner scanner = new Scanner(System.in);
        if (!clientName.equals("exit.")) {
            try (Socket socket = new Socket("localhost", 10008)) {
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                String userInput;

                ClientRunnable clientRun = new ClientRunnable(socket);
                Thread clientThread = new Thread(clientRun);
                clientThread.start();

                output.println(clientName + " has joined.");

                do {
                    String message = ("(" + clientName + ")" + " message : ");
                    userInput = scanner.nextLine();
                    output.println(message + " " + userInput);
                    if (userInput.equals("exit.")) {
                        break;
                    }
                } while (!userInput.equals("exit."));
            } catch (Exception e) {
                System.out.println("Exception occured in client main: " + e.getStackTrace());
            }
        }
    }

        public static class ClientRunnable implements Runnable {

        private Socket socket;
        private BufferedReader input;

        public ClientRunnable(Socket s) throws IOException {
            this.socket = s;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void closeSocket() throws IOException {
            this.socket.close();
        }

        @Override
        public void run() {

            try {
                while (true) {
                    if (!socket.isClosed()){
                        String response = input.readLine();
                        if (response != null) {
                            System.out.println(response);
                        }
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
