package system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import models.Account;
import models.BankNote;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles JSON persistence for accounts, stock, and technician credentials
 *
 * IMPORTANT: Includes custom LocalDateTime adapter to avoid Java module access issues
 * This allows Gson to serialize/deserialize Transaction timestamps without reflection
 */
public class PersistenceManager {
    // File paths for storing data
    private static final String DATA_DIR = "data/";
    private static final String ACCOUNTS_FILE = DATA_DIR + "accounts.json";
    private static final String STOCK_FILE = DATA_DIR + "stock.json";
    private static final String TECHNICIANS_FILE = DATA_DIR + "technicians.json";

    private Gson gson;

    /**
     * Constructor: Initialize Gson with pretty printing AND custom LocalDateTime adapter
     * The adapter tells Gson how to convert LocalDateTime to/from JSON strings
     */
    public PersistenceManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Custom adapter
                .create();
        createDataDirectory();
    }

    /**
     * Custom TypeAdapter for LocalDateTime
     * This handles serialization (Java → JSON) and deserialization (JSON → Java)
     * without using reflection, which avoids module access errors
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        /**
         * Serialize: Convert LocalDateTime to JSON string
         * Example: 2026-02-01T14:30:00 → "2026-02-01T14:30:00"
         */
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        /**
         * Deserialize: Convert JSON string to LocalDateTime
         * Example: "2026-02-01T14:30:00" → LocalDateTime object
         */
        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            String dateTimeString = in.nextString();
            return LocalDateTime.parse(dateTimeString, formatter);
        }
    }

    /**
     * Create the data directory if it doesn't exist
     */
    private void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ========== ACCOUNTS PERSISTENCE ==========

    /**
     * Save all user accounts to JSON file
     *
     * @param accounts Map of account numbers to Account objects
     */
    public void saveAccounts(Map<String, Account> accounts) {
        try (Writer writer = new FileWriter(ACCOUNTS_FILE)) {
            gson.toJson(accounts, writer);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    /**
     * Load all user accounts from JSON file
     * UPDATED: Now handles empty or missing files gracefully
     *
     * @return Map of account numbers to Account objects
     */
    public Map<String, Account> loadAccounts() {
        File file = new File(ACCOUNTS_FILE);

        // Check if file exists AND has content
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();  // Return empty map for fresh start
        }

        try (Reader reader = new FileReader(ACCOUNTS_FILE)) {
            Type type = new TypeToken<HashMap<String, Account>>(){}.getType();
            Map<String, Account> accounts = gson.fromJson(reader, type);
            return accounts != null ? accounts : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // ========== STOCK PERSISTENCE ==========

    /**
     * Save ATM stock levels to JSON file
     *
     * @param stock The Stock object to save
     */
    public void saveStock(Stock stock) {
        Map<String, Object> stockData = new HashMap<>();

        // Convert BankNote enum keys to String names for proper JSON serialization
        Map<String, Integer> bankNotesAsStrings = new HashMap<>();
        for (Map.Entry<BankNote, Integer> entry : stock.getAllBankNotes().entrySet()) {
            bankNotesAsStrings.put(entry.getKey().name(), entry.getValue());
        }

        stockData.put("bankNotes", bankNotesAsStrings);
        stockData.put("inkLevel", stock.getInkLevel());
        stockData.put("paperLevel", stock.getPaperLevel());

        try (Writer writer = new FileWriter(STOCK_FILE)) {
            gson.toJson(stockData, writer);
        } catch (IOException e) {
            System.err.println("Error saving stock: " + e.getMessage());
        }
    }

    /**
     * Load ATM stock levels from JSON file
     * UPDATED: Now handles empty or missing files gracefully
     *
     * @param stock The Stock object to update
     */
    public void loadStock(Stock stock) {
        File file = new File(STOCK_FILE);

        // Check if file exists AND has content
        if (!file.exists() || file.length() == 0) {
            return;  // Keep default stock values
        }

        try (Reader reader = new FileReader(STOCK_FILE)) {
            Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
            Map<String, Object> stockData = gson.fromJson(reader, type);

            if (stockData != null) {
                // Load bank notes
                Map<String, Double> bankNotesRaw = (Map<String, Double>) stockData.get("bankNotes");
                if (bankNotesRaw != null) {
                    Map<BankNote, Integer> bankNotes = new HashMap<>();
                    for (Map.Entry<String, Double> entry : bankNotesRaw.entrySet()) {
                        BankNote note = BankNote.valueOf(entry.getKey());
                        bankNotes.put(note, entry.getValue().intValue());
                    }
                    stock.setBankNotes(bankNotes);
                }

                // Load ink and paper
                if (stockData.get("inkLevel") != null) {
                    stock.setInkLevel(((Double) stockData.get("inkLevel")).intValue());
                }
                if (stockData.get("paperLevel") != null) {
                    stock.setPaperLevel(((Double) stockData.get("paperLevel")).intValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading stock: " + e.getMessage());
        }
    }

    // ========== TECHNICIANS PERSISTENCE ==========

    /**
     * Save technician credentials to JSON file
     *
     * @param technicians Map of usernames to passwords
     */
    public void saveTechnicians(Map<String, String> technicians) {
        try (Writer writer = new FileWriter(TECHNICIANS_FILE)) {
            gson.toJson(technicians, writer);
        } catch (IOException e) {
            System.err.println("Error saving technicians: " + e.getMessage());
        }
    }

    /**
     * Load technician credentials from JSON file
     * UPDATED: Now handles empty or missing files gracefully
     *
     * @return Map of usernames to passwords
     */
    public Map<String, String> loadTechnicians() {
        File file = new File(TECHNICIANS_FILE);

        // Check if file exists AND has content
        if (!file.exists() || file.length() == 0) {
            return new HashMap<>();  // Return empty map for fresh start
        }

        try (Reader reader = new FileReader(TECHNICIANS_FILE)) {
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            Map<String, String> technicians = gson.fromJson(reader, type);
            return technicians != null ? technicians : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading technicians: " + e.getMessage());
            return new HashMap<>();
        }
    }
}