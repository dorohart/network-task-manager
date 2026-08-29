package ClientHandler;

import Model.*;
import Service.*;
import Exceptions.*;

import java.io.*;
import java.net.*;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Command {
    private final PersonService personServ;
    private final TaskService taskServ;
    private Person currentPerson;
    private Task selectedTask;
    private List<Task> setOfTasks;

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
                String[] combo = input.split(" ", 3);
                try {
                    SetOfCommand cmnd = Command.parse(combo[0]);
                    if (cmnd != SetOfCommand.select_task)
                        setOfTasks = null;
                    if (cmnd != SetOfCommand.delete_task && cmnd != SetOfCommand.change_task_name && cmnd != SetOfCommand.change_task_description
                            && cmnd != SetOfCommand.change_task_priority && cmnd != SetOfCommand.change_task_status
                            && cmnd != SetOfCommand.execute_task && cmnd != SetOfCommand.drop_task && cmnd != SetOfCommand.remove_executor
                            && cmnd != SetOfCommand.task_info)
                        selectedTask = null;
                    switch (cmnd) {
                        case help -> getCommands();
                        case exit -> throw new ExitException("Goodbye!");
                        case register -> register();
                        case login -> login();
                        case logout -> logout();
                        case delete_account -> deleteAccount();
                        case change_login -> changeLogin();
                        case change_password -> changePassword();
                        case change_phone_number -> changePhoneNumber();
                        case change_email -> changeEmail();
                        case change_secret_word -> changeSecretWord();
                        case my_info -> myInfo();
                        case search_user -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Login is required.", input);
                            searchUser(combo[1]);
                        }
                        case list_users -> listUser();
                        case user_count -> userCount();
                        case delete_user -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Login is required.", input);
                            deleteUser(combo[1]);
                        }
                        case make_admin -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Login is required.", input);
                            makeAdmin(combo[1]);
                        }
                        case create_task -> createTask();
                        case delete_task -> deleteTask();
                        case my_created_tasks -> getCreatedTasks();
                        case my_executed_tasks -> getExecutedTasks();
                        case select_task -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Number is required.", input);
                            selectTask(combo[1]);
                        }
                        case change_task_name -> changeTaskName();
                        case change_task_description -> changeTaskDescription();
                        case change_task_priority -> changeTaskPriority();
                        case change_task_status -> changeTaskStatus();
                        case execute_task -> executeTask();
                        case remove_executor -> removeExecutor();
                        case drop_task -> dropTask();
                        case search_task_by_creator -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Login is required.", input);
                            searchTaskByCreator(combo[1]);
                        }
                        case search_task_by_executor -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Login is required.", input);
                            searchTaskByExecutor(combo[1]);
                        }
                        case search_task_by_name -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Name is required.", input);
                            searchTaskByName(combo[1]);
                        }
                        case search_task_by_priority -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Priority is required.", input);
                            searchTaskByPriority(combo[1]);
                        }
                        case search_task_by_status -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Status is required.", input);
                            searchTaskByStatus(combo[1]);
                        }
                        case list_tasks -> listTask();
                        case task_count -> taskCount();
                        case sort_task_created_between -> {
                            if (combo.length < 3 || combo[1].isBlank() || combo[2].isBlank()) 
                                throw new PersonException("Params is required.", "time params");
                            sortTaskCreatedBetween(combo[1], combo[2]);
                        }
                        case sort_task_updated_between -> {
                            if (combo.length < 3 || combo[1].isBlank() || combo[2].isBlank())
                                throw new PersonException("Params is required.", "time params");
                            sortTaskUpdatedBetween(combo[1], combo[2]);
                        }
                        case user_info -> {
                            if (combo.length < 2 || combo[1].isBlank())
                                throw new PersonException("Login is required.", input);
                            userInfo(combo[1]);
                        }
                        case task_info -> taskInfo();
                    }
                } catch (PersonException | AdminException | ExistUserException e) {
                    out.write(e.getMessage());
                    out.newLine();
                    out.flush();
                }
                catch (ExitException e) {
                    out.write(e.getMessage());
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

    private String readLine() throws IOException, ExitException {
        String input = in.readLine();
        if (input == null)
            throw new ExitException("");
        return input;
    }

    private static SetOfCommand parse(String str) throws PersonException {
        if (str == null)
            throw new IllegalArgumentException("Input data cannot be null.");
        int ind = 0;
        for (SetOfCommand s : SetOfCommand.values()) {
            if (s.name().equals(str))
                return s;
        }
        throw new PersonException("Unknown command. Type 'help' to see available commands.", str);
    }

    private void requireLogin() throws PersonException {
        if (currentPerson == null)
            throw new PersonException("You must login first.", "current person");
        if (!personServ.existsById(currentPerson.getID())) {
            currentPerson = null;
            selectedTask = null;
            setOfTasks = null;
            throw new PersonException("Your account has been deleted.", "");
        }
    }

    private void requireTask() throws PersonException {
        if (selectedTask == null)
            throw new PersonException("No task was selected.", "selected task");
    }

    private void getCommands() throws IOException {
        for (SetOfCommand s : SetOfCommand.values()) {
            if (s == SetOfCommand.delete_user || s == SetOfCommand.make_admin || s == SetOfCommand.search_user
                    || s == SetOfCommand.search_task_by_creator || s == SetOfCommand.search_task_by_executor
                    || s == SetOfCommand.search_task_by_name || s == SetOfCommand.search_task_by_status
                    || s == SetOfCommand.search_task_by_priority || s == SetOfCommand.user_info)
                out.write(s.name() + " <name>");
            else if (s == SetOfCommand.select_task)
                out.write(s.name() + " <number>");
            else if (s == SetOfCommand.sort_task_created_between || s == SetOfCommand.sort_task_updated_between)
                out.write(s.name() + " <start_time> <finish_time> (2026-08-22T12:34:56.123456789Z format)");
            else
                out.write(s.name());
            out.newLine();
        }
        out.flush();
    }

    private void register() throws IOException, ExitException {
        out.write("Enter your login: ");
        out.newLine();
        out.flush();
        String login = readLine();

        out.write("Enter your password: ");
        out.newLine();
        out.flush();
        String password = readLine();

        out.write("Enter your phone number: ");
        out.newLine();
        out.flush();
        String phone = readLine();

        out.write("Enter your email: ");
        out.newLine();
        out.flush();
        String email = readLine();

        out.write("Do you want to enter the secret word? (l/y) ");
        out.newLine();
        out.flush();
        String ly = readLine();
        while (!ly.equals("l") && !ly.equals("y")) {
            out.write("Unknown command. Type 'exit' to exit the program.\nDo you want to enter the secret word? (l/y) ");
            out.newLine();
            out.flush();
            ly = readLine();
            if (ly.equals("exit"))
                throw new ExitException("Goodbye!");
        }
        if (ly.equals("l")) {
            out.write("Enter your secret word: ");
            out.newLine();
            out.flush();
            String secretWord = readLine();
            try {
                personServ.register(login, password, phone, email, secretWord);
                out.write("Success!");
                out.newLine();
                out.flush();
            } catch (ExistUserException | PersonException e) {
                out.write("Registration failed. " + e.getMessage());
                out.newLine();
                out.flush();
            }
        }
        else {
            try {
                personServ.register(login, password, phone, email, null);
                out.write("Success!");
                out.newLine();
                out.flush();
            }
            catch (ExistUserException | PersonException e) {
                out.write("Registration failed. " + e.getMessage());
                out.newLine();
                out.flush();
            }
        }
    }

    private void login() throws IOException, ExitException, PersonException {
        out.write("Enter your login: ");
        out.newLine();
        out.flush();
        String login = readLine();

        out.write("Enter your password: ");
        out.newLine();
        out.flush();
        String password = readLine();

        try {
            personServ.login(login, password);
            currentPerson = personServ.getPersonByLogin(login);
            out.write("Success!");
            out.newLine();
            out.flush();
        }
        catch (PersonException e) {
            out.write("Login failed. " + e.getMessage());
            out.newLine();
            out.flush();
        }
    }

    private void logout() throws IOException, PersonException {
        requireLogin();
        currentPerson = null;
        selectedTask = null;
        setOfTasks = null;
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void deleteAccount() throws IOException, PersonException, AdminException {
        requireLogin();
        personServ.delete(currentPerson);
        currentPerson = null;
        selectedTask = null;
        setOfTasks = null;
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeLogin() throws IOException, PersonException, ExistUserException, ExitException {
        requireLogin();

        out.write("Enter your new login: ");
        out.newLine();
        out.flush();
        String login = readLine();

        out.write("Enter your password to confirm: ");
        out.newLine();
        out.flush();
        String password = readLine();

        personServ.changeLogin(currentPerson, login, password);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changePassword() throws IOException, PersonException, ExistUserException, ExitException {
        requireLogin();

        out.write("Enter your new password: ");
        out.newLine();
        out.flush();
        String password = readLine();

        out.write("Enter secret code to confirm: ");
        out.newLine();
        out.flush();
        String code = readLine();

        personServ.changePassword(currentPerson, password, code);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changePhoneNumber() throws IOException, PersonException, ExistUserException, ExitException {
        requireLogin();

        out.write("Enter your new phone number: ");
        out.newLine();
        out.flush();
        String phoneNumber = readLine();

        out.write("Enter secret code to confirm: ");
        out.newLine();
        out.flush();
        String code = readLine();

        personServ.changePhoneNumber(currentPerson, phoneNumber, code);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeEmail() throws IOException, PersonException, ExistUserException, ExitException {
        requireLogin();

        out.write("Enter your new email: ");
        out.newLine();
        out.flush();
        String email = readLine();

        out.write("Enter secret code to confirm: ");
        out.newLine();
        out.flush();
        String code = readLine();

        personServ.changeEmail(currentPerson, email, code);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeSecretWord() throws IOException, PersonException, ExistUserException, ExitException {
        requireLogin();

        out.write("Enter your new secret word: ");
        out.newLine();
        out.flush();
        String secretWord = readLine();

        out.write("Enter secret code to confirm: ");
        out.newLine();
        out.flush();
        String code = readLine();

        personServ.changeSecretWord(currentPerson, secretWord, code);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void myInfo() throws IOException, PersonException {
        requireLogin();
        out.write(currentPerson.toString());
        out.newLine();
        out.flush();
    }

    private void searchUser(String name) throws IOException, PersonException {
        requireLogin();
        out.write(personServ.getPersonByLogin(name).toString());
        out.newLine();
        out.flush();
    }

    private void listUser()  throws IOException, PersonException {
        requireLogin();
        String[] logins = personServ.getAllLogins();
        for (int i = 0; i < logins.length; i++) {
            out.write(logins[i]);
            out.newLine();
        }
        out.flush();
    }

    private void userCount() throws  IOException, PersonException {
        requireLogin();
        out.write("We are " + personServ.getCount() + " people.");
        out.newLine();
        out.flush();
    }

    private void deleteUser(String name) throws IOException, PersonException {
        requireLogin();
        personServ.deleteOther(currentPerson, name);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void makeAdmin(String name) throws IOException, PersonException, AdminException {
        requireLogin();
        personServ.makeAdmin(currentPerson, name);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void createTask() throws IOException, PersonException, ExitException {
        requireLogin();

        out.write("Enter task name: ");
        out.newLine();
        out.flush();
        String name = readLine();

        out.write("Do you want to enter the task description? (l/y) ");
        out.newLine();
        out.flush();
        String ly = readLine();
        String description = null;
        while (!ly.equals("l") && !ly.equals("y")) {
            out.write("Unknown command. Type 'exit' to exit the program.\nDo you want to enter the task description? (l/y) ");
            out.newLine();
            out.flush();
            ly = readLine();
            if (ly.equals("exit"))
                throw new ExitException("Goodbye!");
        }
        if (ly.equals("l")) {
            out.write("Enter task description: ");
            out.newLine();
            out.flush();
            description = readLine();
        }
        out.write("Enter the priority for your task: low/medium/high ");
        String priority = readLine();
        while (!priority.equals("low") && !priority.equals("medium") && !priority.equals("high")) {
            out.write("Unknown command. Type 'exit' to exit the program.\nEnter the priority for your task: low/medium/high ");
            out.newLine();
            out.flush();
            priority = readLine();
            if (priority.equals("exit"))
                throw new ExitException("Goodbye!");
        }
        if (priority.equals("low"))
            taskServ.create(name, description, currentPerson, Priority.LOW);
        else if (priority.equals("medium"))
            taskServ.create(name, description, currentPerson, Priority.MEDIUM);
        else if (priority.equals("high"))
            taskServ.create(name, description, currentPerson, Priority.HIGH);
        else
            throw new IllegalArgumentException("Enum exception.");
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void deleteTask() throws IOException, PersonException {
        requireLogin();
        requireTask();
        taskServ.delete(currentPerson, selectedTask);
        selectedTask = null;
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void getCreatedTasks() throws IOException, PersonException {
        requireLogin();
        List<Task> tasks = taskServ.getTasksByCreator(currentPerson);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void getExecutedTasks() throws IOException, PersonException {
        requireLogin();
        List<Task> tasks = taskServ.getTasksByExecutor(currentPerson);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void selectTask(String number) throws IOException, PersonException {
        requireLogin();
        if (setOfTasks == null)
            throw new PersonException("No task list selected. Search or get tasks first.", "select_task");
        int numb = -1;
        try {
            numb = Integer.parseInt(number);
        }
        catch (NumberFormatException e) {
            throw new PersonException("Please, enter the number of task.", number);
        }
        selectedTask = taskServ.getTaskByNumber(setOfTasks, numb);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeTaskName() throws IOException, PersonException, ExitException {
        requireLogin();
        requireTask();

        out.write("Enter the new name of task: ");
        out.newLine();
        out.flush();
        String name = readLine();

        taskServ.changeName(currentPerson, selectedTask, name);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeTaskDescription() throws IOException, PersonException, ExitException {
        requireLogin();
        requireTask();

        out.write("Enter the new description of task: ");
        out.newLine();
        out.flush();
        String description = readLine();

        taskServ.changeDescription(currentPerson, selectedTask, description);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeTaskStatus() throws IOException, PersonException, ExitException {
        requireLogin();
        requireTask();

        out.write("Enter the new status of task: needstodo/inprocess/done ");
        out.newLine();
        out.flush();
        String status = readLine();

        while (!status.equals("needstodo") && !status.equals("inprocess") && !status.equals("done")) {
            out.write("Unknown command. Type 'exit' to exit the program.\nEnter the status for your task: needstodo/inprocess/done ");
            out.newLine();
            out.flush();
            status = readLine();
            if (status.equals("exit"))
                throw new ExitException("Goodbye!");
        }
        if (status.equals("needstodo"))
            taskServ.changeStatus(currentPerson, selectedTask, Status.NEEDSTODO);
        else if (status.equals("inprocess"))
            taskServ.changeStatus(currentPerson, selectedTask, Status.INPROCESS);
        else if (status.equals("done"))
            taskServ.changeStatus(currentPerson, selectedTask, Status.DONE);
        else
            throw new IllegalArgumentException("Enum exception.");
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void changeTaskPriority() throws IOException, PersonException, ExitException {
        requireLogin();
        requireTask();

        out.write("Enter the new priority of task: ");
        out.newLine();
        out.flush();
        String priority = readLine();

        while (!priority.equals("low") && !priority.equals("medium") && !priority.equals("high")) {
            out.write("Unknown command. Type 'exit' to exit the program.\nEnter the priority for your task: low/medium/high ");
            out.newLine();
            out.flush();
            priority = readLine();
            if (priority.equals("exit"))
                throw new ExitException("Goodbye!");
        }
        if (priority.equals("low"))
            taskServ.changePriority(currentPerson, selectedTask, Priority.LOW);
        else if (priority.equals("medium"))
            taskServ.changePriority(currentPerson, selectedTask, Priority.MEDIUM);
        else if (priority.equals("high"))
            taskServ.changePriority(currentPerson, selectedTask, Priority.HIGH);
        else
            throw new IllegalArgumentException("Enum exception.");
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void executeTask() throws IOException, PersonException {
        requireLogin();
        requireTask();
        taskServ.addExecutor(currentPerson, selectedTask);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void removeExecutor() throws IOException, PersonException {
        requireLogin();
        requireTask();
        if (selectedTask.getExecutor().equals(currentPerson))
            throw new PersonException("This task is for removing other persons. Enter 'drop_task' to remove yourself.", "drop_task");
        taskServ.removeExecutor(currentPerson, selectedTask);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void dropTask() throws IOException, PersonException {
        requireLogin();
        requireTask();
        if (!selectedTask.getExecutor().equals(currentPerson))
            throw new PersonException("This task is for removing yourself. Enter 'remove_executor' to remove the executor.", "remove_executor");
        taskServ.removeExecutor(currentPerson, selectedTask);
        out.write("Success!");
        out.newLine();
        out.flush();
    }

    private void searchTaskByCreator(String login) throws IOException, PersonException {  //by kseniya
        requireLogin();
        Person p = personServ.getPersonByLogin(login);
        List<Task> tasks = taskServ.getTasksByCreator(p);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void searchTaskByExecutor(String login) throws IOException, PersonException {
        requireLogin();
        Person p = personServ.getPersonByLogin(login);
        List<Task> tasks = taskServ.getTasksByExecutor(p);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void searchTaskByName(String name) throws IOException, PersonException {
        requireLogin();
        List<Task> tasks = taskServ.getTasksByName(name);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void searchTaskByPriority(String priority) throws IOException, PersonException {
        List<Task> tasks;
        requireLogin();
        if (priority.equals("low"))
            tasks = taskServ.getTasksByPriority(Priority.LOW);
        else if (priority.equals("medium"))
            tasks = taskServ.getTasksByPriority(Priority.MEDIUM);
        else if (priority.equals("high"))
            tasks = taskServ.getTasksByPriority(Priority.HIGH);
        else
            throw new PersonException("Unknown priority. Enter the priority for this command: low/medium/high ", priority);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void searchTaskByStatus(String status) throws IOException, PersonException {
        List<Task> tasks;
        requireLogin();
        if (status.equals("needstodo"))
            tasks = taskServ.getTasksByStatus(Status.NEEDSTODO);
        else if (status.equals("inprocess"))
            tasks = taskServ.getTasksByStatus(Status.INPROCESS);
        else if (status.equals("done"))
            tasks = taskServ.getTasksByStatus(Status.DONE);
        else
            throw new PersonException("Unknown status. Enter the status for this command: needstodo/inprocess/done ", status);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void sortTaskCreatedBetween(String start, String finish) throws IOException, PersonException {
        // 2026-08-22T12:34:56.123456789Z
        requireLogin();
        Instant st, fin;
        try {
            st = Instant.parse(start);
            fin = Instant.parse(finish);
        }
        catch (DateTimeParseException e) {
            throw new PersonException("Please, enter correct format of date.", "uncorrect date");
        }
        List<Task> tasks = taskServ.getTasksCreatedBetween(st, fin);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void sortTaskUpdatedBetween(String start, String finish) throws IOException, PersonException {
        // 2026-08-22T12:34:56.123456789Z
        requireLogin();
        Instant st, fin;
        try {
            st = Instant.parse(start);
            fin = Instant.parse(finish);
        }
        catch (DateTimeParseException e) {
            throw new PersonException("Please, enter correct format of date.", "uncorrect date");
        }
        List<Task> tasks = taskServ.getTasksUpdatedBetween(st, fin);
        if (tasks.size() == 0)
            throw new PersonException("No task available to select.", "no tasks");
        for (Task t : tasks) {
            out.write(t.getName() + ", created: " + t.getCreatedAt());
            out.newLine();
        }
        out.flush();
        setOfTasks = tasks;
    }

    private void listTask()  throws IOException, PersonException {
        requireLogin();
        String[] names = taskServ.getAllNames();
        if (names.length == 0)
            out.write("No tasks.");
        else {
            for (int i = 0; i < names.length; i++) {
                out.write(names[i]);
                out.newLine();
            }
        }
        out.flush();
    }

    private void taskCount() throws  IOException, PersonException {
        requireLogin();
        out.write(taskServ.getCount() + " tasks created!");
        out.newLine();
        out.flush();
    }

    private void userInfo(String login) throws IOException, PersonException {
        requireLogin();
        out.write(personServ.otherToString(login));
        out.newLine();
        out.flush();
    }

    private void taskInfo() throws IOException, PersonException {
        requireLogin();
        requireTask();
        out.write(selectedTask.toString());
        out.newLine();
        out.flush();
    }
 }
