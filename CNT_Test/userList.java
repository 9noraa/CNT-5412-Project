import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.awt.event.*; 
import java.awt.*; 
import javax.swing.*;

public class userList extends Thread {
    private Socket socket;
    private mainUser hostUser;
    private PrintWriter writer;
    private Encryption ENC;
    private SecretKeySpec SharedKey;
    private String encMessage;
    private String decMessage;
    
    private static JFrame f;
    private static JTextArea jt;
    private JPanel p;
 
    public userList(Socket socket, mainUser hostUser) {
    	f = new JFrame("Chat");
    	jt = new JTextArea(30, 30); 
    	  
        p = new JPanel();
        p.add(jt);
        f.add(p);
        
        f.setSize(500, 500); 
        f.show();
    	
        this.socket = socket;
        this.hostUser = hostUser;
        ENC = new Encryption(); 
        try {
			SharedKey = ENC.KeyExchange();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
 
            printUsers();
 
            String userName = reader.readLine();
            hostUser.addUserName(userName);
 
            String serverMessage = "New user connected: " + userName;
            hostUser.broadcast(serverMessage, this);
 
            String clientMessage;
 
            do {
            	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss");
            	LocalDateTime now = LocalDateTime.now();
            	
                clientMessage = reader.readLine();
                try {
					encMessage = ENC.encrypt(SharedKey, clientMessage);
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
                serverMessage = dtf.format(now) + " [" + userName + "]: " + clientMessage;
                hostUser.broadcast(serverMessage, this);
 
            } while (!clientMessage.equals("exit"));
 
            hostUser.removeUser(userName, this);
            socket.close();
 
            serverMessage = userName + " has quitted.";
            hostUser.broadcast(serverMessage, this);
 
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (hostUser.hasUsers()) {
            writer.println("You are connected to user: " + hostUser.getUserNames());
        } else {
            //writer.println("No other users connected");
        }
    }
 
    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        //writer.println(message);
    	jt.append(message + "\n");
    }
}