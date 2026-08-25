package Service;

import Exceptions.*;
import Model.*;
import Repository.PersonRepository;
import Repository.TaskRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskService {
    private final TaskRepository taskRep;
    private final PersonRepository personRep;

    public TaskService(TaskRepository taskRep, PersonRepository personRep) {
        if (taskRep == null || personRep == null)
            throw new IllegalArgumentException("Task service can not be create.");
        this.taskRep = taskRep;
        this.personRep = personRep;
    }

    public synchronized void create(String name, String description, Person creator, Priority priority) throws PersonException{
        if (creator.getRole() != Role.ADMIN)
            throw new PersonException("Current person cannot create the task.", creator.toString());
        if (taskRep.getCountOfTasksInProcessByCreator(creator) >= 3)
            throw new PersonException("Person cannot create more that 3 tasks.", creator.toString());
        Task task;
        if (description == null)
            task = new Task(name, creator, priority);
        else
            task = new Task(name, description, creator, priority);
        taskRep.addTask(task);
    }

    public synchronized void delete(Person person, Task task) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("The current person cannot delete this task.", person.toString());
        taskRep.deleteTask(task);
    }

    public List<Task> getTasksByCreator(Person person) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (person.getRole() != Role.ADMIN)
            throw new PersonException("The current person cannot be the creator.", person.toString());
        return taskRep.getTasksByCreator(person);
    }

    public List<Task> getTasksByExecutor(Person person) { return taskRep.getTasksByExecutor(person); }

    public List<Task> getTasksByName(String name) throws PersonException { return taskRep.getTasksByName(name); }

    public List<Task> getTasksByPriority(Priority priority) { return taskRep.getTasksByPriority(priority); }

    public List<Task> getTasksByStatus(Status status) { return taskRep.getTasksByStatus(status); }

    public String[] getAllNames() {
        String[] allNames = new String[taskRep.getCount()];
        int cnt = 0;
        for (Task t : taskRep.getAll()) {
            if (cnt == allNames.length)
                throw new IllegalArgumentException("The length of the array is greater than the number of people.");
            allNames[cnt] = t.getName();
            cnt++;
        }
        return allNames;
    }

    public List<Task> getTasksCreatedBetween(Instant start, Instant finish) {
        return taskRep.getTasksCreatedBetween(start, finish);
    }

    public List<Task> getTasksUpdatedBetween(Instant start, Instant finish) {
        return taskRep.getTasksUpdatedBetween(start, finish);
    }

    public int getCount() {
        return taskRep.getCount();
    }

    public void changeName(Person person, Task task, String newName) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("This person cannot change the task.", person.toString());
        if (newName.equals(task.getName()))
            throw new PersonException("You are already using this name.", newName);
        task.setName(newName);
    }

    public void changeDescription(Person person, Task task, String newDescription) throws PersonException{
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (newDescription == null)
            throw new IllegalArgumentException("Description of task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("This person cannot change the task.", person.toString());
        if (newDescription.equals(task.getDescription()))
            throw new PersonException("You are already using this description.", newDescription);
        task.setDescription(newDescription);
    }

    public void changePriority(Person person, Task task, Priority priority) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("This person cannot change the task.", person.toString());
        if (task.getPriority() == priority)
            throw new PersonException("You are already using this priority.", priority.toString());
        task.setPriority(priority);
    }

    public void changeStatus(Person person, Task task, Status status) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("This person cannot change the task.", person.toString());
        if (task.getStatus() == status)
            throw new PersonException("You are already using this status.", status.toString());
        if (status == Status.NEEDSTODO && task.getExecutor() != null && task.getStatus() != Status.DONE)
            throw new PersonException("This task still has an executor.", task.toString());
        if (status == Status.INPROCESS && task.getExecutor() == null && task.getStatus() != Status.DONE)
            throw new PersonException("This task has no executor.", task.toString());
        if (task.getStatus() == Status.DONE)
            throw new PersonException("The status of a completed task cannot be changed.", task.toString());
        task.setStatus(status);
    }

    public synchronized void removeExecutor(Person person, Task task) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()) && !person.equals(task.getExecutor()))
            throw new PersonException("This person cannot remove the executor.", person.toString());
        if (task.getExecutor() == null)
            throw new PersonException("This task has no executor.", task.toString());
        task.setExecutor(null);
        task.setStatus(Status.NEEDSTODO);
    }

    public Task getTaskByNumber(List<Task> tasks, int number) throws PersonException {
        if (tasks == null)
            throw new IllegalArgumentException("Set of tasks cannot be null.");
        if (tasks.isEmpty())
            throw new PersonException("Set of tasks is empty.", tasks.toString());
        if (number <= 0 || number > tasks.size())
            throw new PersonException("Please, enter the correct number of task.", Integer.toString(number));
        return tasks.get(number - 1);
    }

    public synchronized void addExecutor(Person person, Task task) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (taskRep.getCountOfTasksInProcessByExecutor(person) >= 3)
            throw new PersonException("Person cannot execute more that 3 tasks.", person.toString());
        if (task.getExecutor() != null)
            throw new PersonException("This task already has an executor.", task.toString());
        task.setExecutor(person);
        task.setStatus(Status.INPROCESS);
    }
}
