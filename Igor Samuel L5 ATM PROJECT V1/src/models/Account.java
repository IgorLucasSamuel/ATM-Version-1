package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a user's bank account
 */
public class Account {
    private String accountNumber;
    private String pin;
    private double balance;
    private List<Transaction> transactionHistory;

    // No-args constructor for Gson (prevents reflection issues)
    public Account() {
        this.transactionHistory = new ArrayList<>();
    }

    public Account(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account Created", initialBalance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        addTransaction("Withdrawal", -amount);
        return true;
    }

    public void changePin(String newPin) {
        this.pin = newPin;
    }

    private void addTransaction(String type, double amount) {
        transactionHistory.add(new Transaction(type, amount, balance));
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public void setTransactionHistory(List<Transaction> transactions) {
        this.transactionHistory = transactions;
    }
}