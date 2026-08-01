package Repository;

import Model.*;
import java.util.*;

public class PersonRepository {
    private final Map<UUID, Person> people;

    public PersonRepository() {
        people = new HashMap<>();
    }

    public boolean existsByLogin(String suggestedLogin) {
        for (Person p : people.values()) {
            if (p.getLogin().equals(suggestedLogin))
                return true;
        }
        return false;
    }

    public boolean existsByPhoneNumber(String suggestedPhone) {
        for (Person p : people.values()) {
            if (p.getPhoneNumber().equals(suggestedPhone))
                return true;
        }
        return false;
    }

     public boolean existsByEmail(String suggestedEmail) {
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
    //
     public void deletePersonByLogin(String login) {
        if (login == null)
            throw new IllegalArgumentException("You are trying to delete a user that login is null.");
        people.remove(searchPersonByLogin(login).getID());
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

    public Person searchPersonByLogin(String nameOfPerson) {
        for (Person p : people.values()) {
            if (p.getLogin().equals(nameOfPerson))
                return p;
        }
        return null;
    }

    public Person searchPersonById(UUID id) {
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
}
