package org.example;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AppTest 
{
    /* Example of asserting true */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    /* Example of asserting false */
    @Test
    public void shouldAnswerWithFalse()
    {
        assertFalse( false );
    }

    /* Simulate one client connecting and sending a message */
    @Test
    public void sendTestMessage() throws InterruptedException {

        //create server and start it in its own thread
        ChatServer chatSrv = new ChatServer();
        Thread srvThread = new Thread(() -> {
            try {
                chatSrv.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //create one client thread (make more of these to simulate more clients)
        Thread cliThread = new Thread(() -> {
            ChatClient chatCli = new ChatClient();

            //simulate user input - this must be set BEFORE function call
            String inString = "A\n";
            ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
            System.setIn(in);

            String name = chatCli.askUserName();

            //ensure there is a newline to simulate return key
            inString = "Hello\nexit.\n";
            in = new ByteArrayInputStream((inString).getBytes());
            System.setIn(in);

            chatCli.startClientChat(name);

            //we are done sending messages, so stop the server
            try {
                chatSrv.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //start threads
        srvThread.start();
        TimeUnit.SECONDS.sleep(1);
        cliThread.start();
        srvThread.join();
        cliThread.join();

        //example of asserting two things equal to each other
        assertEquals("(A) message :  Hello", chatSrv.threads.get(0).messages.get(1));
    }
}
