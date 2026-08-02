package Service;

import Exceptions.*;
import Model.*;
import Repository.PersonRepository;
import Repository.TaskRepository;

public class PersonService {
    private final PersonRepository personRep;
    private final TaskRepository taskRep;

    public PersonService(PersonRepository personRep, TaskRepository taskRep) {
        if (personRep == null || taskRep == null)
            throw new IllegalArgumentException("Person service can not be create.");
        this.personRep = personRep;
        this.taskRep = taskRep;
    }

    public void register(String login, String password, String phoneNumber, String email, String secretWord) throws  ExistUserException {
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
            person.setRole(Role.ADMIN);
        personRep.addPerson(person);
    }

    public void delete(Person person) throws AdminException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (taskRep.existsTasksByCreator(person))
            throw new ExistException("The person did not delete his tasks", person.toString());
        if (taskRep.existsTasksByExecutor(person))
            throw new ExistException("The person did not complete his tasks.", person.toString());
        if (person.getRole() == Role.ADMIN && personRep.getCountOfThisRole(Role.ADMIN) == 1)
            throw new AdminException("The last admin cannot be deleted.", person.toString());
        personRep.deletePerson(person);
    }

    public Person login(String login, String password) throws PersonException {
        if (login == null)
            throw new IllegalArgumentException("Login cannot be null.");
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");
        if (login.isBlank())
            throw new PersonException("Field of login cannot be empty.", login);
        if (password.isBlank())
            throw new PersonException("Field of password cannot be empty.", password);
        Person person = personRep.getPersonByLogin(login);
        if (person == null)
            throw new PersonException("Input login is incorrect.", login);
        if (!person.getPassword().equals(password))
            throw new PersonException("Input password is incorrect.", password);
        return person;
    }

    public void changeLogin(Person person, String newLogin, String password) throws ExistUserException, PersonException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");
        if (newLogin.isBlank())
            throw new PersonException("Field of the new login cannot be empty.", newLogin);
        if (password.isBlank())
            throw new PersonException("Field of password cannot be empty.", password);
        if (personRep.existsByLogin(newLogin))
            throw new ExistUserException("This login already exists.", newLogin);
        if (!person.getPassword().equals(password))
            throw new PersonException("Passwords do not match.", password);
        person.setLogin(newLogin);
    }

    public void changePassword(Person person, String newPassword, String code) throws PersonException {
        if (newPassword == null)
            throw new IllegalArgumentException("Password cannot be null.");
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (newPassword.isBlank())
            throw new PersonException("Field of the new password cannot be empty.", newPassword);
        if (code.isBlank())
            throw new PersonException("Field of code cannot be empty.", code);
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getPassword().equals(newPassword))
            throw new ExistException("This password is the same as the old password.", newPassword);
        person.setPassword(newPassword);
    }

    public void changePhoneNumber(Person person, String newPhoneNumber, String code) throws PersonException, ExistUserException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (newPhoneNumber.isBlank())
            throw new PersonException("Field of the new phone number cannot be empty.", newPhoneNumber);
        if (code.isBlank())
            throw new PersonException("Field of code cannot be empty.", code);
        if (personRep.existsByPhoneNumber(newPhoneNumber))
            throw new ExistUserException("This phone number already exists.", newPhoneNumber);
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getPhoneNumber().equals(newPhoneNumber))
            throw new ExistException("This phone number is the same as the old phone number.", newPhoneNumber);
        person.setPhoneNumber(newPhoneNumber);
    }

    public void changeEmail(Person person, String newEmail, String code) throws PersonException, ExistUserException {
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (newEmail.isBlank())
            throw new PersonException("Field of the new email cannot be empty.", newEmail);
        if (code.isBlank())
            throw new PersonException("Field of code cannot be empty.", code);
        if (personRep.existsByEmail(newEmail))
            throw new ExistUserException("This email already exists.", newEmail);
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getEmail().equals(newEmail))
            throw new ExistException("This email is the same as the old email.", newEmail);
        person.setEmail(newEmail);
    }

    public void changeSecretWord(Person person, String newSecretWord, String code) throws PersonException {
        if (newSecretWord == null)
            throw new IllegalArgumentException("Secret word cannot be null.");
        if (person == null)
            throw new IllegalArgumentException("The current person cannot be null.");
        if (code == null)
            throw new IllegalArgumentException("Code cannot be null.");
        if (newSecretWord.isBlank())
            throw new PersonException("Field of the new secret word cannot be empty.", newSecretWord);
        if (code.isBlank())
            throw new PersonException("Field of code cannot be empty.", code);
        if (!VerificationService.verify(code))
            throw new PersonException("Invalid person code", code);
        if (person.getSecretWord().equals(newSecretWord))
            throw new ExistException("This secret word is the same as the old secret word.", newSecretWord);
        person.setSecretWord(newSecretWord);
    }

    public Person getPersonByLogin(String login) throws PersonException {
        if (login.isBlank())
            throw new PersonException("Field of login cannot be empty.", login);
        if (!personRep.existsByLogin(login))
            throw new PersonException("Person with this login not found.", login);
        return personRep.getPersonByLogin(login);
    }

    public String[] getAllLogins() {
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

    public void makeAdmin(Person currentPerson, String loginOfUser) throws AdminException, PersonException {
        if (currentPerson == null)
            throw new IllegalArgumentException("Current person cannot be null.");
        if (loginOfUser.isBlank())
            throw new PersonException("Field of login cannot be empty.", loginOfUser);
        if (currentPerson.getRole() != Role.ADMIN)
            throw new AdminException("The current person cannot be made an admins.", currentPerson.toString());
        Person newAdmin = personRep.getPersonByLogin(loginOfUser);
        if (newAdmin == null)
            throw new PersonException("Person with this login not found.", loginOfUser);
        if (newAdmin.getRole() == Role.ADMIN)
            throw new PersonException("This person is already an admin.", newAdmin.toString());
        newAdmin.setRole(Role.ADMIN);
    }
}
