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

    public Person(String login, String password, String phoneNumber, String email) {
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

    public Person(String login, String password, String phoneNumber, String email, String secretWord) {
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

    private void validateLogin(String login) {
        if (login == null || login.isBlank())
            throw new IllegalArgumentException("Login cannot be empty.");
        if (login.length() < 3 || login.length() > 30)
            throw new ExepUncorrect("Login must be between 3 and 30 characters long.", login);
        char[] cs = login.toCharArray();
        int cnt = 0;
        for (int i = 0; i < login.length(); i++) {
            if (Character.isWhitespace(cs[i])) cnt++;
            if (cnt >= 3) throw new ExepUncorrect("Login can contain up to 2 spaces.", login);
        }
    }

    public void setLogin(String login) {
        if (this.login.equals(login))
            throw new ExepExist("You are already using this login.", login);
        validateLogin(login);
        this.login = login;
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be empty.");
        if (password.length() < 8 || password.length() > 80)
            throw new ExepUncorrect("Password must be between 8 and 80 characters long.", password);
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
            throw new ExepUncorrect("Password must contain digits, upper and lower case characters.", password);
    }

    public void setPassword(String password) {
        if (this.password.equals(password))
            throw new ExepExist("You are already using this password.", password);
        validatePassword(password);
        this.password = password;
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number cannot be empty.");
        if (phoneNumber.length() != 11 && phoneNumber.length() != 12)
            throw new ExepUncorrect("Phone number is uncorrect.", phoneNumber);
        if (!(phoneNumber.charAt(0) == '+' && phoneNumber.charAt(1) == '7' && phoneNumber.length() == 12) &&
                !(phoneNumber.charAt(0) == '8' && phoneNumber.length() == 11))
            throw new ExepUncorrect("Phone number is uncorrect.", phoneNumber);
        char[] cs = phoneNumber.toCharArray();
        for (int i = 1; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(cs[i]))
                throw new ExepUncorrect("Phone number must contain only digits.", phoneNumber);
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (this.phoneNumber.equals(phoneNumber))
            throw new ExepExist("You are already using this phone number.", phoneNumber);
        validatePhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be empty.");
        int ind = email.indexOf("@");
        if (ind == -1)
            throw new ExepUncorrect("Email must be format user@example.com.", email);
        if (email.indexOf("@", ind + 1) != -1)
            throw new ExepUncorrect("Email must be format user@example.com.", email);
        int indDomen = email.lastIndexOf(".");
        if (ind == 0 || ind + 2 > indDomen || indDomen == email.length() - 1)
            throw new ExepUncorrect("Email must be format user@example.com.", email);
        char[] cs = email.toCharArray();
        for (int i = 0; i < email.length(); i++) {
            if (Character.isWhitespace(cs[i]))
                throw new ExepUncorrect("Email must not contain spaces.", email);
        }
    }

    public void setEmail(String email) {
        if (this.email.equals(email))
            throw new ExepExist("You are already using this email.", email);
        validateEmail(email);
        this.email = email;
    }

    private void validateSecretWord(String secretWord) {
        if (secretWord == null || secretWord.isBlank())
            throw new IllegalArgumentException("Secret word cannot be empty.");
        if (secretWord.length() < 3 || secretWord.length() > 80)
            throw new ExepUncorrect("Secret word must be between 3 and 80 characters long.", secretWord);
    }

    public void setSecretWord(String secretWord) {
        if (this.secretWord.equals(secretWord))
            throw new ExepExist("You are already using this secret word.", secretWord);
        validateSecretWord(secretWord);
        this.secretWord = secretWord;
    }

    public void setRole(Role r) {
        if (r == null) throw new IllegalArgumentException("Role of person cannot be null.");
        if (this.role.equals(r))
            throw new ExepExist("You are already using this role of person.", r.toString());
        this.role = r;
    }

    @Override
    public String toString() {
        return "_Person_\nId: " + getID() + ", name: " + getLogin() + ", email: "
                + getEmail() + ", phone number: " + getPhoneNumber() + ", role: " + getRole();
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
