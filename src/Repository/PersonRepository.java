package Repository;

import Exceptions.PersonException;
import Model.*;

import java.sql.*;
import java.util.*;
import java.time.*;

public class PersonRepository {
    private final static String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=Network-task-manager-db;"
                    + "encrypt=false;user=_YOUR_LOGIN_;password=_YOUR_PASSWORD_";

    public PersonRepository() {}

    public boolean existsByLogin(String suggestedLogin) throws SQLException {
        if (suggestedLogin == null)
            throw new IllegalArgumentException("Login cannot be null.");
        String sql = "select * from Person where Login_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, suggestedLogin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByPhoneNumber(String suggestedPhone) throws SQLException {
        if (suggestedPhone == null)
            throw new IllegalArgumentException("Phone number cannot be null.");
        String sql = "select * from Person where Phone_number = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, suggestedPhone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

     public boolean existsByEmail(String suggestedEmail) throws SQLException {
        if (suggestedEmail == null)
            throw new IllegalArgumentException("Email cannot be null.");
        String sql = "select * from Person where Email = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, suggestedEmail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
     }

     public void addPerson(Person p) throws SQLException {
        if (p == null)
            throw new IllegalArgumentException("You are trying to add a user that object is null.");
        String sql = "insert into Person (Id_of_person, Login_of_person, Password_of_person, Phone_number,"
                + "Email, Secret_word, Role_of_person, Registered_at) values (?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, p.getID());
            ps.setString(2, p.getLogin());
            ps.setString(3, p.getPassword());
            ps.setString(4, p.getPhoneNumber());
            ps.setString(5, p.getEmail());
            ps.setString(6, p.getSecretWord());
            ps.setString(7, p.getRole().name());
            ps.setObject(8, p.getRegisteredAt().atZone(ZoneOffset.UTC).toLocalDateTime());
            ps.executeUpdate();
        }
     }

     public void deletePerson(Person p) throws SQLException {
         if (p == null)
             throw new IllegalArgumentException("You are trying to delete a user that object is null.");
        String sql = "delete from Person where Id_of_person = ?;";
         try (Connection connection = DriverManager.getConnection(URL);
              PreparedStatement ps = connection.prepareStatement(sql))
         {
             ps.setObject(1, p.getID());
             ps.executeUpdate();
         }
     }

     public int getCountOfThisRole(Role role) throws SQLException {
        if (role == null)
            throw new IllegalArgumentException("Role cannot be null.");
        String roleStr = role.name();
        String sql = "select count(*) from Person where Role_of_person = ?;";
         try (Connection connection = DriverManager.getConnection(URL);
              PreparedStatement ps = connection.prepareStatement(sql))
         {
             ps.setString(1, roleStr);
             try (ResultSet rs = ps.executeQuery()) {
                 rs.next();
                 return rs.getInt(1);
             }
         }
     }

     public int getCountOfPeople() throws SQLException {
         String sql = "select count(*) from Person;";
         try (Connection connection = DriverManager.getConnection(URL);
              PreparedStatement ps = connection.prepareStatement(sql))
         {
             try (ResultSet rs = ps.executeQuery()) {
                 rs.next();
                 return rs.getInt(1);
             }
         }
     }

    public Person getPersonByLogin(String nameOfPerson) throws SQLException, PersonException {
        if (nameOfPerson == null)
            throw new IllegalArgumentException("Login cannot be null.");
        String sql = "select * from Person where Login_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, nameOfPerson);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return getPersonFromDataBase(rs);
            }
        }
    }

    public Person getPersonById(UUID id) throws SQLException, PersonException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        String sql = "select * from Person where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return getPersonFromDataBase(rs);
            }
        }
    }

    public List<Person> getAll() throws SQLException, PersonException {
        String sql = "select * from Person;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            List<Person> people = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    people.add(getPersonFromDataBase(rs));
                }
                return people;
            }
        }
    }

    public boolean existsById(UUID id) throws SQLException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        String sql = "select * from Person where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Role parseRole(String str) {
        if (str.equals("ADMIN"))
            return Role.ADMIN;
        else if (str.equals("USER"))
            return Role.USER;
        else
            throw new IllegalArgumentException("This role not found.");
    }

    private Person getPersonFromDataBase(ResultSet rs) throws SQLException, PersonException{
        UUID id = rs.getObject("Id_of_person", UUID.class);
        String login = rs.getString("Login_of_person");
        String password = rs.getString("Password_of_person");
        String phoneNumber = rs.getString("Phone_number");
        String email = rs.getString("Email");
        String secretWord = rs.getString("Secret_word");
        Role role = parseRole(rs.getString("Role_of_person"));
        LocalDateTime dateTime = rs.getObject("Registered_at", LocalDateTime.class);
        Instant registeredAt = dateTime.toInstant(ZoneOffset.UTC);
        return new Person(id, login, password, phoneNumber, email, secretWord, role, registeredAt);
    }

    public void updateLogin(Person person, String newLogin) throws PersonException, SQLException {
        person.validateLogin(newLogin);
        String sql = "update Person set Login_of_person = ? where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, newLogin);
            ps.setObject(2, person.getID());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Person was not updated.");
        }
        person.setLogin(newLogin);
    }

    public void updatePassword(Person person, String newPassword) throws PersonException, SQLException {
        person.validatePassword(newPassword);
        String sql = "update Person set Password_of_person = ? where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, newPassword);
            ps.setObject(2, person.getID());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Person was not updated.");
        }
        person.setPassword(newPassword);
    }

    public void updatePhoneNumber(Person person, String newPhoneNumber) throws PersonException, SQLException {
        person.validatePhoneNumber(newPhoneNumber);
        String sql = "update Person set Phone_number = ? where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, newPhoneNumber);
            ps.setObject(2, person.getID());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Person was not updated.");
        }
        person.setPhoneNumber(newPhoneNumber);
    }

    public void updateEmail(Person person, String newEmail) throws PersonException, SQLException {
        person.validateEmail(newEmail);
        String sql = "update Person set Email = ? where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, newEmail);
            ps.setObject(2, person.getID());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Person was not updated.");
        }
        person.setEmail(newEmail);
    }

    public void updateSecretWord(Person person, String newSecretWord) throws PersonException, SQLException {
        person.validateSecretWord(newSecretWord);
        String sql = "update Person set Secret_word = ? where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, newSecretWord);
            ps.setObject(2, person.getID());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Person was not updated.");
        }
        person.setSecretWord(newSecretWord);
    }

    public void updateRole(Person person) throws SQLException {
        String sql = "update Person set Role_of_person = 'ADMIN' where Id_of_person = ?;";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setObject(1, person.getID());
            int updated = ps.executeUpdate();
            if (updated != 1)
                throw new SQLException("Person was not updated.");
        }
        person.setRole(Role.ADMIN);
    }
}
