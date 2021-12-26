package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    int clientId = 0;
    Chat chat = new Chat();
    Client clientConnection;
    ChatClient[] clients = new ChatClient[2];

    public ChatClient(String n) {
        name = n;
    }

    public ChatClient getClient() {
        return this;
    }

    public String getName() { return  name; }

    public void connectToClient() {
        clientConnection = new Client();
    }

    public void initializeClients() {
        for (int i = 0; i < 2; i++) {
            clients[i] = new ChatClient(" ");
        }
    }

    public void startChat() {
        clients = clientConnection.receiveClients();
        chat.printClients(clients);
    }

    public class Client {
        Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;

        public Client() {
            try {
                socket = new Socket("localhost", 9010);
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());

                clientId = dIn.readInt();

                System.out.println("Connected as " + clientId);
                sendClient();

            } catch (IOException ex) {
                System.out.println("Client failed to open");
            }
        }

        public void sendClient() {
            try {
                dOut.writeObject(getClient());
                dOut.flush();
                dOut.reset();
            } catch (IOException ex) {
                System.out.println("Player not sent");
                ex.printStackTrace();
            }
        }

        public ChatClient[] receiveClients() {
            ChatClient[] cl = new ChatClient[2];
            try {
                for(int i=0; i < 2; i++)
                {
                    cl[i] = (ChatClient) dIn.readObject();
                }
            } catch (IOException e) {
                System.out.println("clients not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                e.printStackTrace();
            }
            return cl;
        }
    }

    public static void main(String args[]) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("What is your name ? ");
        String name = myObj.next();
        ChatClient cc = new ChatClient(name);
        cc.initializeClients();
        cc.connectToClient();
        cc.startChat();
        myObj.close();
    }
}
