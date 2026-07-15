package Model;

import Exceptions.*;
import Repository.User_repository;

import java.util.*;

public class User {
    private final UUID id;
    private String login;
    private String password;
    private final Role role;
    private String phoneNumber;
    private final String secretWord;

    public User(String login, String password, Role role, String phoneNumber, String secretWord) {
        if (login == null || login.isEmpty())
            throw new NullPointerException("couldn't register, login can not be null");
        if (password == null || password.isEmpty())
            throw new NullPointerException("couldn't register, password can not be null");
        if (role == null)
            throw new NullPointerException("couldn't register, role can not be null");
        if (secretWord == null)
            throw new NullPointerException("couldn't register, secret word can not be null");
        if (phoneNumber == null)
            throw new NullPointerException("couldn't register, phone number can not be null");
        if (!(phoneNumber.charAt(0) == '+' && phoneNumber.charAt(1) == '7' && phoneNumber.length() == 12) &&
                !(phoneNumber.charAt(0) == '8' && phoneNumber.length() == 11))
            throw new ExepUncorrect("this save word not correct", phoneNumber);
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.secretWord = secretWord;
        System.out.println("registration was successful");
    }

    public UUID getID() { return this.id; }

    public String getLogin() { return  this.login; }

    public String getPassword() { return this.password; }

    public Role getRole() { return this.role; }

    public String getPhoneNumber() { return this.phoneNumber; }

    public String getSecretWord() { return secretWord; }

    public void setLogin(String login, User_repository u_r) {
        if (login == null)
            throw new NullPointerException("line of login is null");
        if (u_r.existByLogin(login))
            throw new ExepExist("this login is busy", login);
        Scanner scan = new Scanner(System.in);
        System.out.println("please, enter your save word... ");
        String input = scan.nextLine();
        scan.close();
        if (!input.equals(this.secretWord))
            throw new ExepUncorrect("this save word not correct", input);
        this.login = login;
        System.out.println("update was successful");
    }

    public void setPassword(String password) {
        if (password == null)
            throw new NullPointerException("line of password is null");
        Scanner scan = new Scanner(System.in);
        System.out.println("please, enter your save word... ");
        String input = scan.nextLine();
        scan.close();
        if (!input.equals(this.secretWord))
            throw new ExepUncorrect("this save word not correct", input);
        this.password = password;
        System.out.println("update was successful");
    }

    public void setPhoneNumber(String phoneNumber, User_repository u_r) {
        if (phoneNumber == null)
            throw new NullPointerException("line of phone number is null");
        if (u_r.existByPhone(phoneNumber))
            throw new ExepExist("account with this phone number already exist", phoneNumber);
        if (!(phoneNumber.charAt(0) == '+' && phoneNumber.charAt(1) == '7' && phoneNumber.length() == 12) &&
                !(phoneNumber.charAt(0) == '8' && phoneNumber.length() == 11))
            throw new ExepUncorrect("this save word not correct", phoneNumber);
        Scanner scan = new Scanner(System.in);
        System.out.println("please, enter your save word... ");
        String input = scan.nextLine();
        scan.close();
        if (!input.equals(this.secretWord))
            throw new ExepUncorrect("this save word not correct", input);
        this.phoneNumber = phoneNumber;
        System.out.println("update was successful");
    }
}
