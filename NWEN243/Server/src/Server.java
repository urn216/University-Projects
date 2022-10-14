import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

  public static void main(String[] args) {
    doThing();
  }

  private static void doThing() {
    ServerSocket sock;
    System.out.println("Creating new Server!");
    try {
      sock = new ServerSocket(5000);
      while (true) {
        new ClientHandler(sock.accept()).start();
      }
    } catch(IOException e){System.out.println(e);}
    System.out.println("Server closed unexpectedly!");
    System.out.println("Press Enter to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
  }
}

class ClientHandler extends Thread {
  Socket clientSock;

  public ClientHandler(Socket clientSock) {
    this.clientSock = clientSock;
  }

  public void run() {
    try {
      String ip = (InetAddress.getLocalHost().getHostAddress());
      PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true);
      System.out.println("Got a sucker!");
      out.println(ip + " says: " + getQuote());
      out.close();
      clientSock.close();
    } catch(IOException e){System.out.println(e);}
  }

  private String getQuote() {
    String[] quotes = {
      "Wow, you're so cool!",
      "[Insert inspiration here]",
      "I bet you could achieve that task if you put your mind to it for once!",
      "Fold your laundry!",
      "[Something funny]",
      "Someone smart probably said something about missing basketball shots or something...",
      "Hang in there!"
    };
    return quotes[(int)(Math.random()*quotes.length)];
  }
}
