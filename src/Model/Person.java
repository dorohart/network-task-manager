package Model;

import Exceptions.*;
import java.util.*;

public class Person {
    private final UUID id;
    private String login;
    private String password;
    private Role role;
    private String phoneNumber;
    private String email;
    private String secretWord;

    public Person(String login, String password, String phoneNumber, String email) throws PersonException {
        validateLogin(login);
        validatePassword(password);
        validatePhoneNumber(phoneNumber);
        validateEmail(email);
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = Role.USER;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Person(String login, String password, String phoneNumber, String email, String secretWord) throws PersonException{
        this(login, password, phoneNumber, email);
        validateSecretWord(secretWord);
        this.secretWord = secretWord;
    }

    public UUID getID() { return this.id; }

    public String getLogin() { return  this.login; }

    public String getPassword() { return this.password; }

    public Role getRole() { return this.role; }

    public String getPhoneNumber() { return this.phoneNumber; }

    public String getSecretWord() { return secretWord; }

    public String getEmail() { return email; }

    private void validateLogin(String login) throws PersonException {
        if (login == null)
            throw new IllegalArgumentException("Login cannot be null.");
        if (login.isBlank())
            throw new PersonException("Login cannot be empty.", login);
        if (login.length() < 3 || login.length() > 30)
            throw new PersonException("Login must be between 3 and 30 characters long.", login);
        char[] cs = login.toCharArray();
        int cnt = 0;
        for (int i = 0; i < login.length(); i++) {
            if (Character.isWhitespace(cs[i])) cnt++;
            if (cnt >= 3) throw new PersonException("Login can contain up to 2 spaces.", login);
        }
    }

    public void setLogin(String login) throws PersonException {
        validateLogin(login);
        this.login = login;
    }

    private void validatePassword(String password) throws PersonException {
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");
        if (password.isBlank())
            throw new PersonException("Password cannot be empty.", password);
        if (password.length() < 8 || password.length() > 80)
            throw new PersonException("Password must be between 8 and 80 characters long.", password);
        char[] cs = password.toCharArray();
        boolean flagUpper = false;
        boolean flagLower = false;
        boolean flagDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(cs[i])) flagUpper = true;
            if (Character.isLowerCase(cs[i])) flagLower = true;
            if (Character.isDigit(cs[i])) flagDigit = true;
        }
        if (!flagUpper || !flagLower || !flagDigit)
            throw new PersonException("Password must contain digits, upper and lower case characters.", password);
    }

    public void setPassword(String password) throws PersonException {
        validatePassword(password);
        this.password = password;
    }

    private void validatePhoneNumber(String phoneNumber) throws PersonException {
        if (phoneNumber == null)
            throw new IllegalArgumentException("Phone number cannot be null.");
        if (phoneNumber.isBlank())
            throw new PersonException("Phone number cannot be empty.", phoneNumber);
        if (phoneNumber.length() != 11 && phoneNumber.length() != 12)
            throw new PersonException("Phone number is uncorrect.", phoneNumber);
        if (!(phoneNumber.charAt(0) == '+' && phoneNumber.charAt(1) == '7' && phoneNumber.length() == 12) &&
                !(phoneNumber.charAt(0) == '8' && phoneNumber.length() == 11))
            throw new PersonException("Phone number is uncorrect.", phoneNumber);
        char[] cs = phoneNumber.toCharArray();
        for (int i = 1; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(cs[i]))
                throw new PersonException("Phone number must contain only digits.", phoneNumber);
        }
    }

    public void setPhoneNumber(String phoneNumber) throws PersonException {
        validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    private void validateEmail(String email) throws PersonException {
        if (email == null)
            throw new IllegalArgumentException("Email cannot be null.");
        if (email.isBlank())
            throw new PersonException("Email cannot be empty.", email);
        int ind = email.indexOf("@");
        if (ind == -1)
            throw new PersonException("Email must be format user@example.com.", email);
        if (email.indexOf("@", ind + 1) != -1)
            throw new PersonException("Email must be format user@example.com.", email);
        int indDomen = email.lastIndexOf(".");
        if (ind == 0 || ind + 2 > indDomen || indDomen == email.length() - 1)
            throw new PersonException("Email must be format user@example.com.", email);
        char[] cs = email.toCharArray();
        for (int i = 0; i < email.length(); i++) {
            if (Character.isWhitespace(cs[i]))
                throw new PersonException("Email must not contain spaces.", email);
        }
    }

    public void setEmail(String email) throws PersonException {
        validateEmail(email);
        this.email = email;
    }

    private void validateSecretWord(String secretWord) throws PersonException {
        if (secretWord == null)
            throw new IllegalArgumentException("Secret word cannot be null.");
        if (secretWord.isBlank())
            throw new PersonException("Secret word cannot be empty.", secretWord);
        if (secretWord.length() < 3 || secretWord.length() > 80)
            throw new PersonException("Secret word must be between 3 and 80 characters long.", secretWord);
    }

    public void setSecretWord(String secretWord) throws PersonException{
        validateSecretWord(secretWord);
        this.secretWord = secretWord;
    }

    public void setRole(Role r) {
        if (r == null)
            throw new IllegalArgumentException("Role of person cannot be null.");
        this.role = r;
    }

    @Override
    public String toString() {
        return "_Person_\nId: " + getID() + ", \nname: " + getLogin() + ", \nemail: "
                + getEmail() + ", \nphone number: " + getPhoneNumber() + ", \nrole: " + getRole();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person && this.getID().equals(((Person) obj).getID()))
            return true;
        return false;
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
