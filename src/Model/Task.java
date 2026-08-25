package Model;

import Exceptions.*;

import java.time.Instant;
import java.util.*;

public class Task {
    private final UUID id;
    private String name;
    private String description;
    private Status status;
    private Priority priority;
    private Person executor;
    private final Person creator;
    private final Instant createdAt;
    private Instant updatedAt;

    public Task(String name, Person creator, Priority priority) throws PersonException {
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
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    public Task(String name, String description, Person creator, Priority priority) throws PersonException {
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

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    private void validateName(String name) throws PersonException {
        if (name == null)
            throw new IllegalArgumentException("The name of the task cannot be null.");
        if (name.isBlank())
            throw new PersonException("The name of the task cannot be empty.", name);
        if (name.length() < 3 || name.length() > 30)
            throw new PersonException("The name of the task must be between 3 and 30 characters long.", name);
        char[] cs = name.toCharArray();
        int cnt = 0;
        for (int i = 0; i < name.length(); i++) {
            if (Character.isWhitespace(cs[i])) cnt++;
            if (cnt >= 7)
                throw new PersonException("The name of the task can contain up to 6 spaces.", name);
        }
    }

    public void setName(String name) throws PersonException {
        validateName(name);
        this.name = name;
        setUpdatedAt();
    }

    private void validateDescription(String description) throws PersonException {
        if (description == null) return;
        if (description.length() > 350)
            throw new PersonException("Description must be no more than 350 characters.", description);
    }

    public void setDescription(String description) throws PersonException {
        validateDescription(description);
        this.description = description;
        setUpdatedAt();
    }

    public void setStatus(Status st) {
        if (st == null)
            throw new IllegalArgumentException("The status of the task cannot be null.");
        this.status = st;
        setUpdatedAt();
    }

    public void setPriority(Priority pr) {
        if (pr == null)
            throw new IllegalArgumentException("The priority of the task cannot be null.");
        this.priority = pr;
        setUpdatedAt();
    }

    public void setExecutor(Person executor) {
        if (Objects.equals(this.executor, executor))
            throw new IllegalArgumentException("The executor already is null.");
        this.executor = executor;
        setUpdatedAt();
    }

    private void setUpdatedAt() { updatedAt = Instant.now(); }

    @Override
    public String toString() {
        return "_Task_\nTitle: " + getName() + ", \ndescription: " + (getDescription() == null ? "no" : getDescription())
                + ",\ncreator: "
        + creator.getLogin() + ", \nexecutor: " + (executor == null ? "not assigned" : executor.getLogin())
                + ", \npriority: " + getPriority()
        + ", \nstatus: " + getStatus() + ",\ncreated at " + getCreatedAt() + ", \nupdated at " + getUpdatedAt();
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
