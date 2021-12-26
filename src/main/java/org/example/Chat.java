package org.example;

import java.io.Serializable;

public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    public Chat(){ }

    public void printClients(ChatClient[] arr){
        for (int i = 0; i < arr.length; i++){
            if (arr[1] != null){
                System.out.println("Name:\s" + arr[i].name + "\t\tId:\s" + arr[i].clientId);
            }
        }
    }
}
