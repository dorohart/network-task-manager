package Model;

import java.util.*;

public class Task {
    private Status status;
    private Priority priority;
    private final UUID id;
    private String name;
    private String description;
    private User designer;
    private final User creater;

    public Task(String name, String description, User creater, Priority priority) {
        if (name == null)
            throw new NullPointerException("couldn't register this task, name can not be null");
        if (priority == null)
            throw new NullPointerException("couldn't register this task, priority can not be null");
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.creater = creater;
        this.status = Status.NEEDSTODO;
        this.priority = priority;
        System.out.println("registration of your task was successful");
    }

    public Status getStatus() { return this.status; }

    public Priority getPriority() { return this.priority; }

    public UUID getId() { return this.id; }

    public String getName() { return this.name; }

    public String getDescription() { return this.description; }

    public User getDesigner() { return this.designer; }

    public User getCreater() { return this.creater; }

    public void setStatus(Status status) {
        if (status == null)
            throw new NullPointerException("status of task can not be null");
        this.status = status;
    }

    public void setPriority(Priority priority) {
        if (priority == null)
            throw new NullPointerException("priority of task can not be null");
        this.priority = priority;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("name can not be null or empty");
        this.name = name;
    }

    public void setDescription(String description) { this.description = description; }

    public void setDesigner(User designer) { this.designer = designer; }
}
