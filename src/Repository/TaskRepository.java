package Repository;

import Model.*;

import java.time.LocalDateTime;
import java.util.*;

public class TaskRepository {
    private final Map<UUID, Task> tasks;

    public TaskRepository() {
        tasks = new HashMap<>();
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

    public List<Task> searchTasksByName(String text) {
        List<Task> t = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getName().contains(text))
                t.add(task);
        }
        return t;
    }

    public Task searchTaskById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        return tasks.get(id);
    }

    public List<Task> searchTasksByCreator(Person p) {
        if (p == null)
            throw new IllegalArgumentException("Object of person cannot be null");
        List<Task> crTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getCreator().equals(p))
                crTasks.add(task);
        }
        return crTasks;
    }

    public List<Task> searchTasksByExecutor(Person p) {
        if (p == null)
            throw new IllegalArgumentException("Object of person cannot be null");
        List<Task> exTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (Objects.equals(task.getExecutor(), p))
                exTasks.add(task);
        }
        return exTasks;
    }

    public List<Task> searchTasksByPriority(Priority pr) {
        if (pr == null)
            throw new IllegalArgumentException("Priority of the task cannot be null.");
        List<Task> prTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getPriority() == pr)
                prTasks.add(task);
        }
        return prTasks;
    }

    public List<Task> searchTasksByStatus(Status st) {
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

    public List<Task> searchTasksCreatedBetween(LocalDateTime start, LocalDateTime finish) {
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

    public List<Task> searchTasksUpdatedBetween(LocalDateTime start, LocalDateTime finish) {
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
    //
    public boolean existsTasksByCreator(Person person) {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        for (Task task : tasks.values()) {
            if (task.getCreator().equals(person))
                return true;
        }
        return false;
    }
    //
    public boolean existsTasksByExecutor(Person person) {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        for (Task task : tasks.values()) {
            if (task.getExecutor().equals(person))
                return true;
        }
        return false;
    }
}
