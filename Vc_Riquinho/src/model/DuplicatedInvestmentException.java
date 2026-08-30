package model;

public class DuplicatedInvestmentException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicatedInvestmentException() {
        super("This already exists in the client's wallet.");
    }

    public DuplicatedInvestmentException(String message) {
        super(message);
    }
}