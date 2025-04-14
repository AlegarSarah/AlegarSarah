/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ChatApp;
import java.util.*;
import java.util.regex.*;

public class ChatApp {
    private static UserAccount registeredUser = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MessageManager manager = new MessageManager();

        while (true) {
            System.out.println("\n=== Welcome to ChatApp ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                registeredUser = register(scanner);
            } else if (option == 2) {
                if (registeredUser == null) {
                    System.out.println("⚠️ No user registered. Please register first.");
                } else if (login(scanner, registeredUser)) {
                    runChatApp(scanner, manager);
                }
            } else if (option == 3) {
                System.out.println("👋 Goodbye!");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    // --------------------- Registration ----------------------
    public static UserAccount register(Scanner scanner) {
        System.out.println("\n=== Registration ===");

        System.out.print("Enter username (must contain '_' and be ≤5 characters): ");
        String username = scanner.nextLine();
        while (!UserAccount.validateUsername(username)) {
            System.out.print("Invalid. Try again: ");
            username = scanner.nextLine();
        }

        System.out.print("Enter password (min 8 chars, 1 capital, 1 digit, 1 special char): ");
        String password = scanner.nextLine();
        while (!UserAccount.validatePassword(password)) {
            System.out.print("Invalid. Try again: ");
            password = scanner.nextLine();
        }

        System.out.print("Enter SA phone number (+27...): ");
        String phone = scanner.nextLine();
        while (!UserAccount.validatePhoneNumber(phone)) {
            System.out.print("Invalid. Try again: ");
            phone = scanner.nextLine();
        }

        System.out.println("✅ Registered successfully!");
        return new UserAccount(username, password, phone);
    }

    // ---------------------- Login ---------------------------
    public static boolean login(Scanner scanner, UserAccount user) {
        System.out.println("\n=== Login ===");

        System.out.print("Enter username: ");
        String inputUser = scanner.nextLine();
        System.out.print("Enter password: ");
        String inputPass = scanner.nextLine();

        if (user.getUsername().equals(inputUser) && user.getPassword().equals(inputPass)) {
            System.out.println("✅ Login successful!");
            return true;
        } else {
            System.out.println("❌ Incorrect username or password.");
            return false;
        }
    }

    // -------------------- Main Chat Menu ---------------------
    public static void runChatApp(Scanner scanner, MessageManager manager) {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Chat Menu ---");
            System.out.println("1. Add new message");
            System.out.println("2. View all messages");
            System.out.println("3. View sent messages");
            System.out.println("4. Delete message by hash");
            System.out.println("5. Show deleted messages");
            System.out.println("6. Show longest message");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter recipient (+27...): ");
                    String recipient = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String text = scanner.nextLine();
                    System.out.print("Enter flag (sent/received/read/stored): ");
                    String flag = scanner.nextLine();
                    manager.addMessage(recipient, text, flag);
                    break;
                case 2:
                    manager.viewAllMessages();
                    break;
                case 3:
                    manager.viewMessagesByFlag("sent");
                    break;
                case 4:
                    System.out.print("Enter message hash to delete: ");
                    int hash = scanner.nextInt();
                    scanner.nextLine();
                    manager.deleteMessageByHash(hash);
                    break;
                case 5:
                    manager.showDeletedMessages();
                    break;
                case 6:
                    manager.showLongestMessage();
                    break;
                case 7:
                    System.out.println("🔒 Logged out.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}

// --------------------- UserAccount ---------------------
class UserAccount {
    private String username;
    private String password;
    private String phone;

    public UserAccount(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public static boolean validateUsername(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public static boolean validatePassword(String password) {
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[0-9].*") &&
               password.matches(".*[^A-Za-z0-9].*");
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\+27\\d{9}");
    }
}

// ---------------------- Message ------------------------
class Message {
    String recipient;
    String messageText;
    String flag;

    public Message(String recipient, String messageText, String flag) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.flag = flag;
    }

    public void display() {
        System.out.printf("Recipient: %-13s | Message: %-25s | Flag: %s | Hash: %d\n",
                recipient, messageText, flag, this.hashCode());
    }
}

// ------------------- MessageManager --------------------
class MessageManager {
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<Message> deletedMessages = new ArrayList<>();

    public void addMessage(String recipient, String text, String flag) {
        Message m = new Message(recipient, text, flag);
        messages.add(m);
        System.out.println("✅ Message added.");
    }

    public void viewAllMessages() {
        System.out.println("\n--- All Messages ---");
        for (Message m : messages) {
            m.display();
        }
    }

    public void viewMessagesByFlag(String flag) {
        System.out.println("\n--- Messages with Flag: " + flag + " ---");
        for (Message m : messages) {
            if (m.flag.equalsIgnoreCase(flag)) {
                m.display();
            }
        }
    }

    public void deleteMessageByHash(int hash) {
        Iterator<Message> it = messages.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.hashCode() == hash) {
                it.remove();
                deletedMessages.add(m);
                m.flag = "disregard";
                System.out.println("🗑️ Message deleted.");
                return;
            }
        }
        System.out.println("❌ Message not found.");
    }

    public void showDeletedMessages() {
        System.out.println("\n--- Deleted Messages ---");
        for (Message m : deletedMessages) {
            m.display();
        }
    }

    public void showLongestMessage() {
        if (messages.isEmpty()) {
            System.out.println("No messages to evaluate.");
            return;
        }

        Message longest = messages.get(0);
        for (Message m : messages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }

        System.out.println("\n--- Longest Message ---");
        longest.display();
    }
}



    


    

