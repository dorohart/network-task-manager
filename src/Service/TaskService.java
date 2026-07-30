package Service;

import Exceptions.*;
import Model.*;
import Repository.TaskRepository;

import java.util.UUID;

public class TaskService {
    private TaskRepository taskRep;
    private PersonService personServ;

    public TaskService(TaskRepository taskRep, PersonService personServ) {
        if (taskRep == null || personServ == null)
            throw new IllegalArgumentException("Task service can not be create.");
        this.taskRep = taskRep;
        this.personServ = personServ;
    }

    public void createTask(String name, String description, Person creator, Priority priority) {
        Task task;
        if (description == null)
            task = new Task(name, creator, priority);
        else
            task = new Task(name, description, creator, priority);
        taskRep.addTask(task);
    }


}
