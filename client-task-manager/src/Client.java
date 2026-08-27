import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    private static final int port = 12345;
    public static void main(String[] args) {
        try (Socket client = new Socket("192.168.1.58", 12345);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
             BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream())))
        {
            Scanner scan = new Scanner(System.in);
            Thread reader = new Thread(() -> {
                try {
                    String message;
                    while ((message = br.readLine()) != null) {
                        System.out.println(message);
                    }
                }
                catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            reader.start();
            while (true) {
                bw.write(scan.nextLine());
                bw.newLine();
                bw.flush();
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Unknown host Exception.");
        }
        catch (IOException e) {
            System.out.println("IOException.");
            e.printStackTrace();
        }
    }
}