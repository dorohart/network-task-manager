package Exceptions;

public class PersonException extends Exception {
    private String symbols;

    public PersonException(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }

    public String getSymbols() { return symbols; }
}
