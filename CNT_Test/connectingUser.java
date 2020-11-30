import java.net.*;
import java.io.*;

public class connectingUser {
    private String hostname;
    private int port;
    private String userName;
 
    public connectingUser(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
 
    public void run() {
        try {
            Socket socket = new Socket(hostname, port);
 
            System.out.println("Connected to the chat host");
 
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
            
            //Encryption ENC = new Encryption(hostname, port);
            
 
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
 
    }
 
    void setUserName(String userName) {
        this.userName = userName;
    }
 
    String getUserName() {
        return this.userName;
    }
 
 
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
 
        connectingUser user = new connectingUser(hostname, port);
        user.run();
    }
}
