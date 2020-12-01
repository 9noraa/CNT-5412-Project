import java.io.*;
import java.net.*;
import java.util.*;

public class mainUser {
	private String Username;
	private String hostname;
    private int port;
    private int user_count;
    private Set<String> userNames = new HashSet<>();
    private Set<userList> userThreads = new HashSet<>();
 
    //Constructor
    public mainUser(int port) {
        this.port = port;
        this.hostname = "localhost";
        user_count = 0;
    }
 
    //Run creates socket to accept users
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            addUser();
            //Socket socket = serverSocket.accept();
            //userList newUser = new userList(socket, this);
            
            //System.out.println("Waiting for user to connect to port: " + port);
 
            while (true) {
            	//Accept new users
                Socket socket = serverSocket.accept();
                user_count++;
                System.out.println("New user connected");
 
                userList newUser = new userList(socket, this);
                userThreads.add(newUser);
                newUser.start();
 
            }
 
        } catch (IOException ex) {
            System.out.println("Error in port: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }
 
        int port = Integer.parseInt(args[0]);
 
        mainUser hostUser = new mainUser(port);
        //Scanner input = new Scanner(System.in);
        //System.out.println("Enter your username: ");
        //String username = input.nextLine();
        
        //input.close();
        //hostUser.addUser();
        hostUser.run();
        //hostUser.addUser();
    }
 
    //Write to JTextArea
    void broadcast(String message, userList excludeUser) {
        for (userList aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }
 
    //Add newly connected user
    void addUserName(String userName) {
        userNames.add(userName);
    }
 
    //Remove user from list
    void removeUser(String userName, userList aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }
 
    public Set<String> getUserNames() {
        return this.userNames;
    }
 
    
    public boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
    
    //Get the number of users on server (max should be 2)
    public int getUserCount() {
    	return user_count;
    }
    
    //Add a new user 
    public void addUser() {
    	connectingUser host = new connectingUser(hostname, port);
    	host.run();
    }
}