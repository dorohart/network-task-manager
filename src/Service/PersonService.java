package Service;

import Exceptions.*;
import Model.*;
import Repository.PersonRepository;

import java.util.List;
import java.util.UUID;

public class PersonService {
    private PersonRepository personRep;
    private TaskService taskServ;

    public PersonService(PersonRepository personRep, TaskService taskService) {
        if (personRep == null || taskService == null)
            throw new IllegalArgumentException("Person service can not be create.");
        this.personRep = personRep;
        this.taskServ = taskService;
    }

    public void createPerson(String login, String password, String phoneNumber, String email, String secretWord) {
        personRep.existsByLogin(login);
        personRep.existsByPhoneNumber(phoneNumber);
        personRep.existsByEmail(email);
        Person person;
        if (secretWord == null)
            person = new Person(login, password, phoneNumber, email);
        else
            person = new Person(login, password, phoneNumber, email, secretWord);
        if (personRep.getCountOfPeople() == 0)
            person.setRole(Role.ADMIN);
        personRep.addPerson(person);
    }

    /*public void deletePerson(UUID id) {
        Person person = personRep.searchPersonById(id);
        if (person == null)
            throw new NotFoundException("Person with this id not found.", id.toString());

    }*/
}
