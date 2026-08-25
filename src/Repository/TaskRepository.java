package Repository;

import Exceptions.AdminException;
import Exceptions.PersonException;
import Model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {
    private final Map<UUID, Task> tasks;

    public TaskRepository() {
        tasks = new ConcurrentHashMap<>();
    }

    public void addTask(Task task) {
        if (task == null)
            throw new IllegalArgumentException("You are trying to add a task that object is null.");
        tasks.put(task.getId(), task);
    }

    public void deleteTask(Task task) {
        if (task == null)
            throw new IllegalArgumentException("You are trying to delete a task that object is null.");
        tasks.remove(task.getId());
    }

    public int getCount() { return tasks.size(); }

    public List<Task> getTasksByName(String text) throws PersonException{
        if (text == null)
            throw new IllegalArgumentException("Name of task cannot be null.");
        if (text.isBlank())
            throw new PersonException("Field of task name cannot be empty.", text);
        List<Task> t = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getName().contains(text))
                t.add(task);
        }
        return t;
    }

    public Task getTaskById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        return tasks.get(id);
    }

    public List<Task> getTasksByCreator(Person p) throws PersonException {
        if (p == null)
            throw new IllegalArgumentException("Object of person cannot be null");
        List<Task> crTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (p.equals(task.getCreator()))
                crTasks.add(task);
        }
        return crTasks;
    }

    public List<Task> getTasksByExecutor(Person p) {
        if (p == null)
            throw new IllegalArgumentException("Object of person cannot be null");
        List<Task> exTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (p.equals(task.getExecutor()))
                exTasks.add(task);
        }
        return exTasks;
    }

    public List<Task> getTasksByPriority(Priority pr) {
        if (pr == null)
            throw new IllegalArgumentException("Priority of the task cannot be null.");
        List<Task> prTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getPriority() == pr)
                prTasks.add(task);
        }
        return prTasks;
    }

    public List<Task> getTasksByStatus(Status st) {
        if (st == null)
            throw new IllegalArgumentException("Status of the task cannot be null.");
        List<Task> stTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getStatus() == st)
                stTasks.add(task);
        }
        return stTasks;
    }

    public List<Task> getAll() { return new ArrayList<>(tasks.values()); }

    public boolean existsById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        return tasks.containsKey(id);
    }

    public List<Task> getTasksCreatedBetween(Instant start, Instant finish) {
        if (start == null || finish == null)
            throw new IllegalArgumentException("Object of LocalDateTime cannot be null.");
        if (start.compareTo(finish) > 0)
            throw new IllegalArgumentException("Start time cannot be after finish time.");
        List<Task> ts = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (start.compareTo(task.getCreatedAt()) <= 0 && finish.compareTo(task.getCreatedAt()) >= 0)
                ts.add(task);
        }
        return ts;
    }

    public List<Task> getTasksUpdatedBetween(Instant start, Instant finish) {
        if (start == null || finish == null)
            throw new IllegalArgumentException("Object of LocalDateTime cannot be null.");
        if (start.compareTo(finish) > 0)
            throw new IllegalArgumentException("Start time cannot be after finish time.");
        List<Task> ts = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (start.compareTo(task.getUpdatedAt()) <= 0 && finish.compareTo(task.getUpdatedAt()) >= 0)
                ts.add(task);
        }
        return ts;
    }

    public boolean existsTasksByCreator(Person person) {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        for (Task task : tasks.values()) {
            if (person.equals(task.getCreator()))
                return true;
        }
        return false;
    }

    public boolean existsTasksByExecutor(Person person) {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        for (Task task : tasks.values()) {
            if (person.equals(task.getExecutor()))
                return true;
        }
        return false;
    }

    public int getCountOfTasksInProcessByCreator(Person person) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (person.getRole() != Role.ADMIN)
            throw new PersonException("The current person cannot create tasks.", person.toString());
        int count = 0;
        for (Task temp : tasks.values()) {
            if (person.equals(temp.getCreator()) && temp.getStatus() == Status.INPROCESS)
                count++;
        }
        return count;
    }

    public int getCountOfDoneTasksByCreator(Person person) throws PersonException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        if (person.getRole() != Role.ADMIN)
            throw new PersonException("The current person cannot create tasks.", person.toString());
        int count = 0;
        for (Task temp : tasks.values()) {
            if (person.equals(temp.getCreator()) && temp.getStatus() == Status.DONE)
                count++;
        }
        return count;
    }

    public int getCountOfTasksInProcessByExecutor(Person person) {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        int count = 0;
        for (Task temp : tasks.values()) {
            if (person.equals(temp.getExecutor()) && temp.getStatus() == Status.INPROCESS)
                count++;
        }
        return count;
    }

    public int getCountOfDoneTasksByExecutor(Person person) {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        int count = 0;
        for (Task temp : tasks.values()) {
            if (person.equals(temp.getExecutor()) && temp.getStatus() == Status.DONE)
                count++;
        }
        return count;
    }

    public int getCountOfTasksToBeDone() {
        int count = 0;
        for (Task temp : tasks.values()) {
            if (temp.getStatus() == Status.INPROCESS)
                count++;
        }
        return count;
    }
}
