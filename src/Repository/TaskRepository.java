package Repository;

import Exceptions.AdminException;
import Exceptions.PersonException;
import Model.*;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {
    private final PersonRepository pr;

    private final static String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=Network-task-manager-db;"
                    + "encrypt=false;user=_YOUR_LOGIN_;password=_YOUR_PASSWORD_";

    public TaskRepository() { pr = new PersonRepository(); }

    public void addTask(Task task) throws SQLException {
        if (task == null)
            throw new IllegalArgumentException("You are trying to add a task that object is null.");
        String sql = "insert into Task (Id_of_task, Name_of_task, Description_of_task, Status_of_task, Priority_of_task, Id_of_creator,"
                + " Id_of_executor, Created_at, Updated_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, task.getId());
            ps.setString(2, task.getName());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus().name());
            ps.setString(5, task.getPriority().name());
            ps.setObject(6, task.getCreator().getID());
            if (task.getExecutor() == null)
                ps.setObject(7, null);
            else
                ps.setObject(7, task.getExecutor().getID());
            ps.setObject(8,task.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(9, task.getUpdatedAt().atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.executeUpdate();
        }
    }

    public void deleteTask(Task task) throws SQLException {
        if (task == null)
            throw new IllegalArgumentException("You are trying to delete a task that object is null.");
        String sql = "delete from Task where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql);)
        {
            ps.setObject(1, task.getId());
            ps.executeUpdate();
        }
    }

    public int getCount() throws SQLException {
        String sql = "select count(*) from Task;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public List<Task> getTasksByName(String text) throws PersonException, SQLException {
        if (text == null)
            throw new IllegalArgumentException("Name of task cannot be null.");
        if (text.isBlank())
            throw new PersonException("Field of task name cannot be empty.", text);
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Name_of_task like ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            String searchPattern = "%" + text + "%";
            ps.setString(1, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public Task getTaskById(UUID id) throws SQLException, PersonException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        String sql = "select * from task where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return getTaskFromDataBase(rs);
            }
        }
    }

    public List<Task> getTasksByCreator(Person p) throws PersonException, SQLException {
        if (p == null)
            throw new IllegalArgumentException("Object of person cannot be null");
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Id_of_creator = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, p.getID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public List<Task> getTasksByExecutor(Person p) throws SQLException, PersonException {
        if (p == null)
            throw new IllegalArgumentException("Object of person cannot be null");
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Id_of_executor = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, p.getID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public List<Task> getTasksByPriority(Priority pr) throws SQLException, PersonException {
        if (pr == null)
            throw new IllegalArgumentException("Priority of the task cannot be null.");
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Priority_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, pr.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public List<Task> getTasksByStatus(Status st) throws SQLException, PersonException {
        if (st == null)
            throw new IllegalArgumentException("Status of the task cannot be null.");
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Status_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, st.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public List<Task> getAll() throws SQLException, PersonException {
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public boolean existsById(UUID id) throws SQLException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        String sql = "select * from Task where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Task> getTasksCreatedBetween(Instant start, Instant finish) throws SQLException, PersonException {
        if (start == null || finish == null)
            throw new IllegalArgumentException("Object of LocalDateTime cannot be null.");
        if (start.compareTo(finish) > 0)
            throw new IllegalArgumentException("Start time cannot be after finish time.");
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Created_at between ? and ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, start);
            ps.setObject(2, finish);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public List<Task> getTasksUpdatedBetween(Instant start, Instant finish) throws SQLException, PersonException {
        if (start == null || finish == null)
            throw new IllegalArgumentException("Object of LocalDateTime cannot be null.");
        if (start.compareTo(finish) > 0)
            throw new IllegalArgumentException("Start time cannot be after finish time.");
        List<Task> t = new ArrayList<>();
        String sql = "select * from Task where Updated_at between ? and ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, start.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(2, finish.atZone(ZoneOffset.UTC).toLocalDateTime());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    t.add(getTaskFromDataBase(rs));
                }
                return t;
            }
        }
    }

    public boolean existsTasksByCreator(Person person) throws SQLException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        String sql = "select * from Task where Id_of_creator = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, person.getID());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsTasksByExecutor(Person person) throws SQLException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        String sql = "select * from Task where Id_of_executor = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, person.getID());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int getCountOfTasksInProcessByCreator(Person person) throws SQLException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        String sql = "select count(*) from Task where Id_of_creator = ? and Status_of_task = 'INPROCESS';";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, person.getID());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int getCountOfTasksInProcessByExecutor(Person person) throws SQLException {
        if (person == null)
            throw new IllegalArgumentException("Person cannot be null.");
        String sql = "select count(*) from Task where Id_of_executor = ? and Status_of_task = 'INPROCESS';";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, person.getID());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Status parseStatus(String str) {
        if (str.equals("NEEDSTODO"))
            return Status.NEEDSTODO;
        else if (str.equals("INPROCESS"))
            return Status.INPROCESS;
        else if (str.equals("DONE"))
            return Status.DONE;
        else
            throw new IllegalArgumentException("This status not found.");
    }

    private Priority parsePriority(String str) {
        if (str.equals("LOW"))
            return Priority.LOW;
        else if (str.equals("MEDIUM"))
            return Priority.MEDIUM;
        else if (str.equals("HIGH"))
            return Priority.HIGH;
        else
            throw new IllegalArgumentException("This priority not found.");
    }

    private Task getTaskFromDataBase(ResultSet rs) throws SQLException, PersonException {
        UUID id = rs.getObject("Id_of_task", UUID.class);
        String name = rs.getString("Name_of_task");
        String description = rs.getString("Description_of_task");
        Priority priority = parsePriority(rs.getString("Priority_of_task"));
        Status status = parseStatus(rs.getString("Status_of_task"));
        UUID idOfCreator = rs.getObject("Id_of_creator", UUID.class);
        UUID idOfExecutor = rs.getObject("Id_of_executor", UUID.class);
        Person creator = this.pr.getPersonById(idOfCreator);
        Person executor = null;
        if (idOfExecutor != null)
            executor = this.pr.getPersonById(idOfExecutor);
        LocalDateTime dateTime = rs.getObject("Created_at", LocalDateTime.class);
        Instant createdAt = dateTime.toInstant(ZoneOffset.UTC);
        dateTime = rs.getObject("Updated_at", LocalDateTime.class);
        Instant updatedAt = dateTime.toInstant(ZoneOffset.UTC);
        return new Task(id, name, description, status, priority, creator, executor, createdAt, updatedAt);
    }

    public void updateName(Task task, String newName) throws PersonException, SQLException {
        task.validateName(newName);
        String sql = "update Task set Name_of_task = ?, Updated_at = ? where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            Instant inst = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ps.setString(1, newName);
            ps.setObject(2, inst.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(3, task.getId());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Task was not updated.");
            task.setName(newName);
            task.setUpdatedAt(inst);
        }
    }

    public void updateDescription(Task task, String newDescription) throws PersonException, SQLException {
        task.validateDescription(newDescription);
        String sql = "update Task set Description_of_task = ?, Updated_at = ? where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            Instant inst = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ps.setString(1, newDescription);
            ps.setObject(2, inst.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(3, task.getId());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Task was not updated.");
            task.setDescription(newDescription);
            task.setUpdatedAt(inst);
        }
    }

    public void updateStatus(Task task, Status newStatus) throws SQLException {
        String sql = "update Task set Status_of_task = ?, Updated_at = ? where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            Instant inst = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ps.setString(1, newStatus.name());
            ps.setObject(2, inst.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(3, task.getId());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Task was not updated.");
            task.setStatus(newStatus);
            task.setUpdatedAt(inst);
        }
    }

    public void updatePriority(Task task, Priority newPriority) throws SQLException {
        String sql = "update Task set Priority_of_task = ?, Updated_at = ? where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            Instant inst = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ps.setString(1, newPriority.name());
            ps.setObject(2, inst.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(3, task.getId());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Task was not updated.");
            task.setPriority(newPriority);
            task.setUpdatedAt(inst);
        }
    }

    public void updateExecutor(Task task) throws SQLException {
        String sql = "update Task set Id_of_executor = null, Status_of_task = 'NEEDSTODO', Updated_at = ?  where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            Instant inst = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ps.setObject(1, inst.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(2, task.getId());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Task was not updated.");
            task.setExecutor(null);
            task.setUpdatedAt(inst);
        }
    }

    public void updateExecutor(Task task, Person newExecutor) throws SQLException {
        String sql = "update Task set Id_of_executor = ?, Status_of_task = 'INPROCESS', Updated_at = ?  where Id_of_task = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            Instant inst = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            ps.setObject(1, newExecutor.getID());
            ps.setObject(2, inst.atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.setObject(3, task.getId());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Task was not updated.");
            task.setExecutor(newExecutor);
            task.setUpdatedAt(inst);
        }
    }
}
