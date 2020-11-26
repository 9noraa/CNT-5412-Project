import java.io.*;
import java.net.*;
import java.util.*;

public class userList extends Thread {
    private Socket socket;
    private mainUser hostUser;
    private PrintWriter writer;
 
    public userList(Socket socket, mainUser hostUser) {
        this.socket = socket;
        this.hostUser = hostUser;
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
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                hostUser.broadcast(serverMessage, this);
 
            } while (!clientMessage.equals("bye"));
 
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
            writer.println("No other users connected");
        }
    }
 
    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}