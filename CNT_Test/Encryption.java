import java.util.*;
import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;


public class Encryption implements Serializable{
	private String hostname;
	private int port;
	private int p;
	private int g;
	private int A;
	
	public Encryption(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
	
	public void run() {
		try {
			Socket users = new Socket(hostname, port);
			
		} catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
		
		
	}
	
	public static void main(String[] args) {
		

	}

}
