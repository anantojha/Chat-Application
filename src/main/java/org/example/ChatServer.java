package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Serializable
{
    private static final long serialVersionUID = 1L;
    ServerSocket ss;
    Server[] clientServer = new Server[2];
    ChatClient[] clients = new ChatClient[2];
    int numClients;


    public static void main( String[] args ) throws Exception {
        ChatServer cs = new ChatServer(false);
        cs.acceptConnections();
        cs.startChat();
        cs.chatLoop();
        System.out.println("Chat Over");
        System.exit(1);
    }

    public ChatServer(boolean test) {
        if (!test){
            System.out.println("Starting chat server");
        }

        numClients = 0;
        for (int i = 0; i < clients.length; i++) {
            clients[i] = new ChatClient(" ");
        }

        if(!test) {
            try {
                ss = new ServerSocket(9010);
            } catch (IOException ex) {
                System.out.println("Server Failed to open");
            }
        }
    }

    public void acceptConnections() throws ClassNotFoundException {
        try {
            System.out.println("Waiting for clients...");
            while (numClients < 2) {
                Socket s = ss.accept();
                numClients++;

                Server server = new Server(s, numClients);
                server.dOut.writeInt(server.clientId);
                server.dOut.flush();

                ChatClient in = (ChatClient) server.dIn.readObject();
                System.out.println("Player " + server.clientId + " ~ " + in.name + " ~ has joined");
                clients[server.clientId - 1] = in;
                clientServer[numClients - 1] = server;
            }
            System.out.println("Two clients have joined the chat");

        } catch (IOException ex) {
            System.out.println("Could not connect both clients");
        }
    }

    public void chatLoop() {
        try {
            clientServer[0].sendClients(clients);
            clientServer[1].sendClients(clients);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startChat(){
        for (int i = 0; i < clientServer.length; i++) {
            Thread t = new Thread(clientServer[i]);
            t.start();
        }
    }

    public class Server implements Runnable {
        private Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;
        private int clientId;

        public Server(Socket s, int playerid) {
            socket = s;
            clientId = playerid;
            try {
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                System.out.println("Server Connection failed");
            }
        }

        public void run() {
            try {
                while (true) {
                }

            } catch (Exception ex) {
                {
                    System.out.println("Run failed");
                    ex.printStackTrace();
                }
            }
        }

        public void sendClients(ChatClient[] cl) {
            try {
                for (ChatClient c : cl) {
                    dOut.writeObject(c);
                    dOut.flush();
                    dOut.reset();
                }

            } catch (IOException ex) {
                System.out.println("Clients not sent");
                ex.printStackTrace();
            }
        }
    }
}
