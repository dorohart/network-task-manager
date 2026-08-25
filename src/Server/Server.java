package Server;

import ClientHandler.*;
import Service.*;
import Repository.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int port = 12345;
    public static void main(String[] args) {
        try {
            ExecutorService pool = Executors.newCachedThreadPool();
            ServerSocket server = new ServerSocket(port);
            PersonRepository pr = new PersonRepository();
            TaskRepository tr = new TaskRepository();
            PersonService ps = new PersonService(pr, tr);
            TaskService ts = new TaskService(tr, pr);
            while (true) {
                Socket client = server.accept();
                pool.execute(() -> {
                    try (Socket socket = client;
                         BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))
                    {
                        Command command = new Command(ps, ts, socket, br, bw);
                        command.start();
                    }
                    catch (IOException e) {
                        System.out.println("IOException.");
                        e.printStackTrace();
                    }
                });
            }
        }
        catch (IOException e) {
            System.out.println("IOException.");
            e.printStackTrace();
        }
    }
}
