package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class representing a transaction record
 */
public class Transaction {
    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    // No-args constructor for Gson (prevents reflection issues)
    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %-20s | Amount: €%8.2f | Balance: €%8.2f",
                timestamp.format(formatter), type, amount, balanceAfter);
    }
}