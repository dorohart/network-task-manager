package Service;

import Exceptions.*;
import Model.*;
import Repository.PersonRepository;
import Repository.TaskRepository;
import java.sql.SQLException;
import java.util.UUID;

public class PersonService {
    private final PersonRepository personRep;
    private final TaskRepository taskRep;

    public PersonService(PersonRepository personRep, TaskRepository taskRep) {
        if (personRep == null || taskRep == null)
            throw new IllegalArgumentException("Person service can not be create.");
        this.personRep = personRep;
        this.taskRep = taskRep;
    }

    public synchronized void register(String login, String password, String phoneNumber, String email, String secretWord)
            throws  ExistUserException, PersonException, SQLException {
        if (personRep.existsByLogin(login))
            throw new ExistUserException("Person with this login already exist.", login);
        if (personRep.existsByPhoneNumber(phoneNumber))
            throw new ExistUserException("Person with this phone number already exist.", login);
        if (personRep.existsByEmail(email))
            throw new ExistUserException("Person with this email already exist.", login);
        Person person;
        if (secretWord == null)
            person = new Person(login, password, phoneNumber, email);
        else
            person = new Person(login, password, phoneNumber, email, secretWord);
        if (personRep.getCountOfPeople() == 0)
            personRep.updateRole(person);
        personRep.addPerson(person);
    }

    public synchronized void delete(Person person) throws AdminException, PersonException, SQLException {
        if (taskRep.existsTasksByCreator(person))
            throw new PersonException("The person did not delete his tasks. Please, delete your tasks.", person.toString());
        if (taskRep.existsTasksByExecutor(person))
            throw new PersonException("The person did not complete his tasks. Please, complete your tasks.", person.toString());
        if (person.getRole() == Role.ADMIN && personRep.getCountOfThisRole(Role.ADMIN) == 1)
            throw new AdminException("The last admin cannot be deleted.", person.toString());
        personRep.deletePerson(person);
    }

    public synchronized void deleteOther(Person person, String login) throws PersonException, SQLException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (person.getRole() != Role.ADMIN)
            throw new PersonException("Current person cannot delete other persons.", person.toString());
        Person other = personRep.getPersonByLogin(login);
        if (other == null)
            throw new PersonException("Person with this login not found.", login);
        if (other.getRole() == Role.ADMIN)
            throw new PersonException("Cannot delete an admin.", personRep.getPersonByLogin(login).toString());
        for (Task task : taskRep.getTasksByExecutor(other)) {
            taskRep.updateExecutor(task);
        }
        personRep.deletePerson(other);
    }

    public synchronized Person login(String login, String password) throws PersonException, SQLException {
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");
        Person person = personRep.getPersonByLogin(login);
        if (person == null)
            throw new PersonException("Input login is incorrect.", login);
        if (!person.getPassword().equals(password))
            throw new PersonException("Input password is incorrect.", password);
        return person;
    }

    public synchronized void changeLogin(Person person, String newLogin, String password) throws ExistUserException, PersonException, SQLException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");
        if (personRep.existsByLogin(newLogin))
            throw new ExistUserException("This login already exists.", newLogin);
        if (!person.getPassword().equals(password))
            throw new PersonException("Passwords do not match.", password);
        personRep.updateLogin(person, newLogin);
    }

    public void changePassword(Person person, String newPassword, String code) throws PersonException, ExistUserException, SQLException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getPassword().equals(newPassword))
            throw new ExistUserException("This password is the same as the old password.", newPassword);
        personRep.updatePassword(person, newPassword);
    }

    public synchronized void changePhoneNumber(Person person, String newPhoneNumber, String code) throws PersonException, ExistUserException, SQLException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (personRep.existsByPhoneNumber(newPhoneNumber))
            throw new ExistUserException("This phone number already exists.", newPhoneNumber);
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getPhoneNumber().equals(newPhoneNumber))
            throw new ExistUserException("This phone number is the same as the old phone number.", newPhoneNumber);
        personRep.updatePhoneNumber(person, newPhoneNumber);
    }

    public synchronized void changeEmail(Person person, String newEmail, String code) throws PersonException, ExistUserException, SQLException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (personRep.existsByEmail(newEmail))
            throw new ExistUserException("This email already exists.", newEmail);
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getEmail().equals(newEmail))
            throw new ExistUserException("This email is the same as the old email.", newEmail);
        personRep.updateEmail(person, newEmail);
    }

    public void changeSecretWord(Person person, String newSecretWord, String code) throws PersonException, ExistUserException, SQLException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getSecretWord().equals(newSecretWord))
            throw new ExistUserException("This secret word is the same as the old secret word.", newSecretWord);
        personRep.updateSecretWord(person, newSecretWord);
    }

    public Person getPersonByLogin(String login) throws PersonException, SQLException {
        if (!personRep.existsByLogin(login))
            throw new PersonException("Person with this login not found.", login);
        return personRep.getPersonByLogin(login);
    }

    public String[] getAllLogins() throws SQLException, PersonException {
        String[] allLogins = new String[personRep.getCountOfPeople()];
        int cnt = 0;
        for (Person p : personRep.getAll()) {
            if (cnt == allLogins.length)
                throw new IllegalArgumentException("The length of the array is greater than the number of people.");
            allLogins[cnt] = p.getLogin();
            cnt++;
        }
        return allLogins;
    }

    public synchronized void makeAdmin(Person currentPerson, String loginOfUser) throws AdminException, PersonException, SQLException {
        if (currentPerson == null)
            throw new IllegalArgumentException("Current person cannot be null.");
        if (currentPerson.getRole() != Role.ADMIN)
            throw new AdminException("The current person cannot be made an admins.", currentPerson.toString());
        Person newAdmin = personRep.getPersonByLogin(loginOfUser);
        if (newAdmin == null)
            throw new PersonException("Person with this login not found.", loginOfUser);
        if (newAdmin.getRole() == Role.ADMIN)
            throw new PersonException("This person is already an admin.", newAdmin.toString());
        personRep.updateRole(newAdmin);
    }

    public int getCount() throws SQLException {
        return personRep.getCountOfPeople();
    }

    public synchronized boolean existsById(UUID id) throws SQLException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        return personRep.existsById(id);
    }

    public String otherToString(String login) throws PersonException, SQLException {
        Person p = this.getPersonByLogin(login);
        return "_Person " + login + "_\nname: " + p.getLogin() + ",\nemail: " + p.getEmail()
                + ",\nphone number: " + p.getPhoneNumber() + ",\nrole: " + p.getRole()
                + ",\nregistered at: " + p.getRegisteredAt();
    }
}
