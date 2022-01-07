package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/*
    A class to handle the client portion of the app
 */
public class ChatClient implements Serializable {

    public static void main(String[] args) {
        ChatClient chatCli = new ChatClient();
        chatCli.startClientChat(chatCli.askUserName());
    }

    /* Asks user for their name and returns it */
    public String askUserName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        String name = scanner.nextLine();
        return name;
    }

    /* Waits for client to enter a message, then sends it to server */
    public void startClientChat(String clientName){
        Scanner scanner = new Scanner(System.in);
        if (!clientName.equals("exit.")) {
            try (Socket socket = new Socket("localhost", 10008)) {
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                String userInput;

                ClientRunnable clientRun = new ClientRunnable(socket);
                Thread clientThread = new Thread(clientRun);
                clientThread.start();

                output.println(clientName + " has joined.");

                while (true) {
                    String message = ("(" + clientName + ")" + " message : ");
                    userInput = scanner.nextLine();
                    output.println(message + " " + userInput);
                    if (userInput.equals("exit.")) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception occurred in client main: " + e);
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
