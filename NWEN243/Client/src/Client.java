import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.IOException;
import java.net.Socket;

public class Client {
  public static final String IP = "A-Load-Balancer-1657037942.us-east-1.elb.amazonaws.com";
  public static final int PORT = 5000;

  public static void main(String[] args) {
    System.out.println("Connecting to " + IP + ":" + PORT);
    for (int i = 0; i < 40; i++) {
      try {
        Socket clientSock = new Socket(IP, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
        System.out.println(in.readLine());

        in.close();
        clientSock.close();
      } catch(IOException e){System.out.println(e);}
    }
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
  }
}
