package Service;

import Exceptions.*;
import Model.*;
import Repository.PersonRepository;
import Repository.TaskRepository;

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

    public void create(String name, String description, Person creator, Priority priority) throws PersonException{
        if (creator.getRole() != Role.ADMIN)
            throw new PersonException("Current person cannot create the task.", creator.toString());
        Task task;
        if (description == null)
            task = new Task(name, creator, priority);
        else
            task = new Task(name, description, creator, priority);
        taskRep.addTask(task);
    }

    public void delete(Person person, Task task) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("The current person cannot delete this task.", person.toString());
        taskRep.deleteTask(task);
    }

    public List<Task> getTasksByCreator(Person person) throws PersonException {
        if (person.getRole() != Role.ADMIN)
            throw new PersonException("The current person cannot be the creator.", person.toString());
        return taskRep.getTasksByCreator(person);
    }

    public List<Task> getTasksByExecutor(Person person) { return taskRep.getTasksByExecutor(person); }

    public List<Task> getTasksByName(String name) throws PersonException { return taskRep.getTasksByName(name); }

    public List<Task> getTasksByPriority(Priority priority) { return taskRep.getTasksByPriority(priority); }

    public List<Task> getTasksByStatus(Status status) { return taskRep.getTasksByStatus(status); }

    public List<Task> getAll() { return taskRep.getAll(); }

    public List<Task> getTasksCreatedBetween(LocalDateTime start, LocalDateTime finish) {
        return taskRep.getTasksCreatedBetween(start, finish);
    }

    public List<Task> getTasksUpdatedBetween(LocalDateTime start, LocalDateTime finish) {
        return taskRep.getTasksUpdatedBetween(start, finish);
    }

    public void changeName(Person person, Task task, String newName) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (newName == null)
            throw new IllegalArgumentException("Name of task cannot be null.");
        if (newName.isBlank())
            throw new PersonException("Field of task name cannot be empty.", newName);
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
        if (priority == null)
            throw new IllegalArgumentException("Priority of task cannot be null.");
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
        if (status == null)
            throw new IllegalArgumentException("Status of task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("This person cannot change the task.", person.toString());
        if (task.getStatus() == status)
            throw new PersonException("You are already using this status.", status.toString());
        task.setStatus(status);
    }

    public void removeExecutor(Person person, Task task) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null.");
        if (!person.equals(task.getCreator()))
            throw new PersonException("This person cannot remove the executor.", person.toString());
        task.setExecutor(null);
    }

    public Task getTaskByNumber(List<Task> tasks, int number) throws PersonException {
        if (tasks == null)
            throw new IllegalArgumentException("Set of tasks cannot be null.");
        if (tasks.size() == 0)
            throw new PersonException("Set of tasts is empty.", tasks.toString());
        if (number <= 0 || number > tasks.size())
            throw new PersonException("Uncorrect number of task.", Integer.toString(number));
        return tasks.get(number - 1);
    }
}
