package ClientHandler;

import Model.*;
import Service.*;
import Exceptions.*;

import java.io.*;
import java.net.*;

public class Command {
    private final PersonService personServ;
    private final TaskService taskServ;
    private Person currentPerson;
    private Task selectedTask;

    private final BufferedReader in;
    private final BufferedWriter out;
    private final Socket client;

    public Command(PersonService ps, TaskService ts, Socket client, BufferedReader in, BufferedWriter out) {
        this.personServ = ps;
        this.taskServ = ts;
        this.client = client;
        this.in = in;
        this.out = out;
    }

    public void start() {
        while(true) {
            try {
                String input = in.readLine();
                if (input == null)
                    return;
                try {
                    SetOfCommand cmnd = Command.parse(input);
                    switch (cmnd) {
                        case help ->
                            getCommands();
                        case exit ->
                            throw new ExitException("");
                        case register ->
                            register();
                    }
                } catch (PersonException e) {
                    out.write(e.getMessage());
                    out.newLine();
                    out.flush();
                }
                catch (ExitException e) {
                    out.write("Goodbye!");
                    out.newLine();
                    out.flush();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static SetOfCommand parse(String str) throws PersonException {
        if (str == null)
            throw new IllegalArgumentException("Input data cannot be null.");
        int ind = 0;
        for (SetOfCommand s : SetOfCommand.values()) {
            if (s.name().equals(str))
                return s;
        }
        throw new PersonException("Unknown command. Type 'help' to see available commands.", str);
    }

    public void getCommands() throws IOException {
        for (SetOfCommand s : SetOfCommand.values()) {
            out.write(s.name());
            out.newLine();
        }
        out.flush();
    }

    public void register() throws IOException, ExitException {
        out.write("Enter your login: ");
        out.newLine();
        out.flush();
        String login = in.readLine();

        out.write("Enter your password: ");
        out.newLine();
        out.flush();
        String password = in.readLine();

        out.write("Enter your phone number: ");
        out.newLine();
        out.flush();
        String phone = in.readLine();

        out.write("Enter your email: ");
        out.newLine();
        out.flush();
        String email = in.readLine();

        out.write("Do you want to enter the secret word? (l/y) ");
        out.newLine();
        out.flush();
        String ly = in.readLine();
        while (!ly.equals("l") && !ly.equals("y")) {
            out.write("Unknown command. Type 'exit' to exit the program.\nDo you want to enter the secret word? (l/y) ");
            out.newLine();
            out.flush();
            ly = in.readLine();
            if (ly.equals("exit"))
                throw new ExitException("");
        }
        if (ly.equals("l")) {
            out.write("Enter your secret word: ");
            out.newLine();
            out.flush();
            String secretWord = in.readLine();
            try {
                personServ.register(login, password, phone, email, secretWord);
            } catch (ExistUserException e) {
                out.write("Registration failed. " + e.getMessage());
            }
        }
        else {
            try {
                personServ.register(login, password, phone, email, null);
            } catch (ExistUserException e) {
                out.write("Registration failed. " + e.getMessage());
            }
        }
    }
}
