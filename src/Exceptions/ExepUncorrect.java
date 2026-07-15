package Exceptions;

public class ExepUncorrect extends RuntimeException {
    private String symbols;
    public ExepUncorrect(String message, String symbols) {
        super(message);
        this.symbols = symbols;
    }
}
