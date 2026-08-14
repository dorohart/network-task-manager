package Repository;

import Exceptions.PersonException;
import Model.*;
import java.util.*;

public class PersonRepository {
    private final Map<UUID, Person> people;

    public PersonRepository() {
        people = new HashMap<>();
    }

    public boolean existsByLogin(String suggestedLogin) {
        if (suggestedLogin == null)
            throw new IllegalArgumentException("Login cannot be null.");
        for (Person p : people.values()) {
            if (p.getLogin().equals(suggestedLogin))
                return true;
        }
        return false;
    }

    public boolean existsByPhoneNumber(String suggestedPhone) {
        if (suggestedPhone == null)
            throw new IllegalArgumentException("Phone number cannot be null.");
        for (Person p : people.values()) {
            if (p.getPhoneNumber().equals(suggestedPhone))
                return true;
        }
        return false;
    }

     public boolean existsByEmail(String suggestedEmail) {
        if (suggestedEmail == null)
            throw new IllegalArgumentException("Email cannot be null.");
        for (Person p : people.values()) {
            if (p.getEmail().equals(suggestedEmail))
                return true;
        }
        return false;
     }

     public void addPerson(Person p) {
        if (p == null)
            throw new IllegalArgumentException("You are trying to add a user that object is null.");
        people.put(p.getID(), p);
     }

     public void deletePerson(Person p) {
         if (p == null)
             throw new IllegalArgumentException("You are trying to delete a user that object is null.");
        people.remove(p.getID());
     }

     public void deletePersonByLogin(String login) throws PersonException {
        Person p = this.getPersonByLogin(login);
        if (p != null)
            people.remove(p.getID());
        else
            throw new PersonException("Person with this login not found.", login);
     }

     public int getCountOfThisRole(Role role) {
        if (role == null)
            throw new IllegalArgumentException("Role cannot be null.");
        int count = 0;
        for (Person p : people.values()) {
            if (p.getRole() == role)
                count++;
        }
        return count;
     }

     public int getCountOfPeople() { return people.size(); }

    public Person getPersonByLogin(String nameOfPerson) {
        if (nameOfPerson == null)
            throw new IllegalArgumentException("Login cannot be null.");
        for (Person p : people.values()) {
            if (p.getLogin().equals(nameOfPerson))
                return p;
        }
        return null;
    }

    public Person getPersonById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        return people.get(id);
    }

    public List<Person> getAll() { return new ArrayList<>(people.values()); }

    public boolean existsById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        return people.containsKey(id);
    }

    public int getCount() {
        return people.size();
    }
}
