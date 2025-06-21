import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserProcessor {
    private static final int MAX_USERS = 100;
    private static final int MAX_LINE = 256;

    static class User {
        private int id;
        private String name;
        private int age;
        private double balance;

        public User(int id, String name, int age, double balance) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.balance = balance;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public double getBalance() { return balance; }
    }

    public static void processUserFile(String filename) {
        List<User> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null && users.size() < MAX_USERS) {
                String[] tokens = line.split(",");
                if (tokens.length != 4) continue;

                try {
                    int id = Integer.parseInt(tokens[0].trim());
                    String name = tokens[1].trim();
                    int age = Integer.parseInt(tokens[2].trim());
                    double balance = Double.parseDouble(tokens[3].trim());

                    if (age <= 0 || balance < 0) {
                        System.err.printf("Invalid data for user ID %d. Skipping.%n", id);
                        continue;
                    }

                    users.add(new User(id, name, age, balance));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing user data: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.printf("Error: Could not open file %s%n", filename);
            return;
        }

        System.out.printf("Processed %d users successfully.%n", users.size());

        // Display users with balance > 1000
        System.out.println("Users with balance > 1000:");
        users.stream()
            .filter(user -> user.getBalance() > 1000.0)
            .forEach(user -> System.out.printf("ID: %d, Name: %s, Age: %d, Balance: %.2f%n",
                    user.getId(), user.getName(), user.getAge(), user.getBalance()));

        // Calculate average age
        double averageAge = users.stream()
            .mapToInt(User::getAge)
            .average()
            .orElse(0.0);
        System.out.printf("Average age of users: %.2f%n", averageAge);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please provide a filename as an argument");
            return;
        }
        processUserFile(args[0]);
    }
} 