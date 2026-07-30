package Model;

import Exceptions.ExistException;
import Exceptions.UncorrectException;

import java.time.LocalDateTime;
import java.util.*;

public class Task {
    private final UUID id;
    private String name;
    private String description;
    private Status status;
    private Priority priority;
    private Person executor;
    private final Person creator;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(String name, Person creator, Priority priority) {
        if (creator == null)
            throw new IllegalArgumentException("The creator of the task cannot be null.");
        if (priority == null)
            throw new IllegalArgumentException("The priority of the task cannot be null.");
        validateName(name);
        this.id = UUID.randomUUID();
        this.name = name;
        this.creator = creator;
        this.status = Status.NEEDSTODO;
        this.priority = priority;
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    public Task(String name, String description, Person creator, Priority priority) {
        this(name, creator, priority);
        validateDescription(description);
        this.description = description;
    }

    public Status getStatus() { return this.status; }

    public Priority getPriority() { return this.priority; }

    public UUID getId() { return this.id; }

    public String getName() { return this.name; }

    public String getDescription() { return this.description; }

    public Person getExecutor() { return this.executor; }

    public Person getCreator() { return this.creator; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    private void validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("The name of the task cannot be empty.");
        if (name.length() < 3 || name.length() > 30)
            throw new UncorrectException("The name of the task must be between 3 and 30 characters long.", name);
        char[] cs = name.toCharArray();
        int cnt = 0;
        for (int i = 0; i < name.length(); i++) {
            if (Character.isWhitespace(cs[i])) cnt++;
            if (cnt >= 7) throw new UncorrectException("The name of the task can contain up to 6 spaces.", name);
        }
    }

    public void setName(String name) {
        if (this.name.equals(name))
            throw new ExistException("You are already using this name.", name);
        validateName(name);
        this.name = name;
        setUpdatedAt();
    }

    private void validateDescription(String description) {
        if (description == null) return;
        if (description.length() > 350)
            throw new UncorrectException("Description must be no more than 350 characters.", description);
    }

    public void setDescription(String description) {
        if (this.description.equals(description))
            throw new ExistException("You are already using this description.", description);
        validateDescription(description);
        this.description = description;
        setUpdatedAt();
    }

    public void setStatus(Status st) {
        if (st == null) throw new IllegalArgumentException("The status of the task cannot be null.");
        if (this.status.equals(st))
            throw new ExistException("You are already using this status of the task", st.toString());
        this.status = st;
        setUpdatedAt();
    }

    public void setPriority(Priority pr) {
        if (pr == null) throw new IllegalArgumentException("The priority of the task cannot be null.");
        if (this.priority.equals(pr))
            throw new ExistException("You are already using this priority of the task.", pr.toString());
        this.priority = pr;
        setUpdatedAt();
    }

    public void setExecutor(Person executor) {
        if (executor == null) throw new IllegalArgumentException("The incompletely of the task cannot be null");
        if (Objects.equals(this.executor, executor))
            throw new ExistException("This executor of this task has been changed.", executor.toString());
        this.executor = executor;
        setUpdatedAt();
    }

    private void setUpdatedAt() { updatedAt = LocalDateTime.now(); }

    @Override
    public String toString() {
        return "_Task_\nTitle: " + getName() + ", description: " + getDescription() + ",\ncreator: "
        + creator.getLogin() + ", executor: " + (executor == null ? "not assigned" : executor.getLogin())
                + ", priority: " + getPriority()
        + ",status: " + getStatus() + ",\ncreated at " + getCreatedAt() + ", updated at " + getUpdatedAt();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task && this.getId().equals(((Task) obj).id))
            return true;
        return false;
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
