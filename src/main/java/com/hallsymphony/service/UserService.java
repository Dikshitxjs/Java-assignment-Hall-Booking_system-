package com.hallsymphony.service;

import com.hallsymphony.model.user.Administrator;
import com.hallsymphony.model.user.Customer;
import com.hallsymphony.model.user.Manager;
import com.hallsymphony.model.user.Scheduler;
import com.hallsymphony.model.user.User;
import com.hallsymphony.util.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Path USER_FILE = Paths.get("data", "users.txt");

    public UserService() {
        ensureDataFiles();
    }

    private void ensureDataFiles() {
        try {
            if (Files.notExists(USER_FILE.getParent())) {
                Files.createDirectories(USER_FILE.getParent());
            }

            if (Files.notExists(USER_FILE)) {
                Files.write(USER_FILE, List.of("# User data file"));
            }

            // Ensure at least one user of each role exists for easy login
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            long dataLines = lines.stream().filter(l -> l != null && !l.isBlank() && !l.startsWith("#")).count();
            if (dataLines == 0) {
                registerCustomer(new Customer("U-CUST-1", "John Doe", "customer@hall.com", "cust123", "ACTIVE",
                        "0123456789", "123 Main St", LocalDate.now()));
                appendUser(new Administrator("U-ADMIN-1", "Admin", "admin@hall.com", "admin123", "ACTIVE",
                        "S-ADMIN-1", "ADMIN", LocalDate.now()));
                appendUser(new Scheduler("U-SCHED-1", "Scheduler", "scheduler@hall.com", "sched123", "ACTIVE",
                        "S-SCHED-1", "SCHEDULER", LocalDate.now()));
                appendUser(new Manager("U-MGR-1", "Manager", "manager@hall.com", "mgr123", "ACTIVE",
                        "S-MGR-1", "MANAGER", LocalDate.now()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<User> parseUser(String line) {
        if (line == null || line.isBlank() || line.startsWith("#")) {
            return Optional.empty();
        }

        String[] parts = line.split("\\|");
        if (parts.length < 6) {
            return Optional.empty();
        }

        String userId = parts[0].trim();
        String userType = parts[1].trim().toUpperCase();
        String fullName = parts[2].trim();
        String email = parts[3].trim();
        String password = parts[4].trim();
        String status = parts[5].trim();

        switch (userType) {
            case "CUSTOMER": {
                String phone = parts.length > 6 ? parts[6].trim() : "";
                String address = parts.length > 7 ? parts[7].trim() : "";
                LocalDate registrationDate = parts.length > 8 && !parts[8].isEmpty()
                        ? LocalDate.parse(parts[8].trim())
                        : LocalDate.now();
                return Optional.of(new Customer(userId, fullName, email, password, status, phone, address, registrationDate));
            }
            case "ADMIN":
            case "SCHEDULER":
            case "MANAGER": {
                String staffId = parts.length > 6 ? parts[6].trim() : userId;
                String role = parts.length > 7 ? parts[7].trim() : userType;
                LocalDate joinedDate = parts.length > 8 && !parts[8].isEmpty()
                        ? LocalDate.parse(parts[8].trim())
                        : LocalDate.now();
                if ("ADMIN".equals(userType)) {
                    return Optional.of(new Administrator(userId, fullName, email, password, status, staffId, role, joinedDate));
                } else if ("SCHEDULER".equals(userType)) {
                    return Optional.of(new Scheduler(userId, fullName, email, password, status, staffId, role, joinedDate));
                } else {
                    return Optional.of(new Manager(userId, fullName, email, password, status, staffId, role, joinedDate));
                }
            }
            default:
                return Optional.empty();
        }
    }

    private String userToLine(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUserId()).append("|");
        if (user instanceof Customer) {
            Customer c = (Customer) user;
            sb.append("CUSTOMER").append("|")
                    .append(c.getFullName()).append("|")
                    .append(c.getEmail()).append("|")
                    .append(c.getPassword()).append("|")
                    .append(c.getStatus()).append("|")
                    .append(c.getPhoneNumber()).append("|")
                    .append(c.getAddress()).append("|")
                    .append(c.getRegistrationDate());
        } else if (user instanceof Administrator) {
            Administrator a = (Administrator) user;
            sb.append("ADMIN").append("|")
                    .append(a.getFullName()).append("|")
                    .append(a.getEmail()).append("|")
                    .append(a.getPassword()).append("|")
                    .append(a.getStatus()).append("|")
                    .append(a.getStaffId()).append("|")
                    .append(a.getRole()).append("|")
                    .append(a.getJoinedDate());
        } else if (user instanceof Scheduler) {
            Scheduler s = (Scheduler) user;
            sb.append("SCHEDULER").append("|")
                    .append(s.getFullName()).append("|")
                    .append(s.getEmail()).append("|")
                    .append(s.getPassword()).append("|")
                    .append(s.getStatus()).append("|")
                    .append(s.getStaffId()).append("|")
                    .append(s.getRole()).append("|")
                    .append(s.getJoinedDate());
        } else if (user instanceof Manager) {
            Manager m = (Manager) user;
            sb.append("MANAGER").append("|")
                    .append(m.getFullName()).append("|")
                    .append(m.getEmail()).append("|")
                    .append(m.getPassword()).append("|")
                    .append(m.getStatus()).append("|")
                    .append(m.getStaffId()).append("|")
                    .append(m.getRole()).append("|")
                    .append(m.getJoinedDate());
        } else {
            // fallback
            sb.append("UNKNOWN").append("|")
                    .append(user.getFullName()).append("|")
                    .append(user.getEmail()).append("|")
                    .append(user.getPassword()).append("|")
                    .append(user.getStatus());
        }
        return sb.toString();
    }

    private void appendUser(User user) {
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            lines.add(userToLine(user));
            FileHandler.writeToFile(USER_FILE, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String email, String password) {
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            for (String line : lines) {
                Optional<User> opt = parseUser(line);
                if (opt.isPresent()) {
                    User user = opt.get();
                    if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
                            return null;
                        }
                        return user;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            for (String line : lines) {
                parseUser(line).ifPresent(users::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void registerCustomer(Customer customer) {
        customer = new Customer(
                customer.getUserId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getStatus(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getRegistrationDate() == null ? LocalDate.now() : customer.getRegistrationDate()
        );
        appendUser(customer);
    }

    public void blockUser(String userId) {
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Optional<User> opt = parseUser(line);
                if (opt.isPresent() && opt.get().getUserId().equals(userId)) {
                    User user = opt.get();
                    // Create a new instance with blocked status while preserving user type
                    User blockedUser;
                    if (user instanceof Customer) {
                        Customer c = (Customer) user;
                        blockedUser = new Customer(c.getUserId(), c.getFullName(), c.getEmail(), c.getPassword(), "BLOCKED",
                                c.getPhoneNumber(), c.getAddress(), c.getRegistrationDate());
                    } else if (user instanceof Administrator) {
                        Administrator a = (Administrator) user;
                        blockedUser = new Administrator(a.getUserId(), a.getFullName(), a.getEmail(), a.getPassword(), "BLOCKED",
                                a.getStaffId(), a.getRole(), a.getJoinedDate());
                    } else if (user instanceof Scheduler) {
                        Scheduler s = (Scheduler) user;
                        blockedUser = new Scheduler(s.getUserId(), s.getFullName(), s.getEmail(), s.getPassword(), "BLOCKED",
                                s.getStaffId(), s.getRole(), s.getJoinedDate());
                    } else if (user instanceof Manager) {
                        Manager m = (Manager) user;
                        blockedUser = new Manager(m.getUserId(), m.getFullName(), m.getEmail(), m.getPassword(), "BLOCKED",
                                m.getStaffId(), m.getRole(), m.getJoinedDate());
                    } else {
                        blockedUser = user;
                        blockedUser.setStatus("BLOCKED");
                    }
                    lines.set(i, userToLine(blockedUser));
                    FileHandler.writeToFile(USER_FILE, lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmailExists(String email) {
        List<User> allUsers = getAllUsers();
        for (User user : allUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public void updateCustomerProfile(Customer customer) {
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Optional<User> opt = parseUser(line);
                if (opt.isPresent() && opt.get().getUserId().equals(customer.getUserId())) {
                    lines.set(i, userToLine(customer));
                    FileHandler.writeToFile(USER_FILE, lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Scheduler> getAllSchedulers() {
        List<Scheduler> schedulers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (user instanceof Scheduler) {
                schedulers.add((Scheduler) user);
            }
        }
        return schedulers;
    }

    public void addScheduler(Scheduler scheduler) {
        appendUser(scheduler);
    }

    public void updateScheduler(Scheduler scheduler) {
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            for (int i = 0; i < lines.size(); i++) {
                Optional<User> opt = parseUser(lines.get(i));
                if (opt.isPresent() && opt.get().getUserId().equals(scheduler.getUserId())) {
                    lines.set(i, userToLine(scheduler));
                    FileHandler.writeToFile(USER_FILE, lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteScheduler(String staffId) {
        try {
            List<String> lines = FileHandler.readFromFile(USER_FILE);
            for (int i = 0; i < lines.size(); i++) {
                Optional<User> opt = parseUser(lines.get(i));
                if (opt.isPresent() && opt.get().getUserId().equals(staffId) && opt.get() instanceof Scheduler) {
                    lines.remove(i);
                    FileHandler.writeToFile(USER_FILE, lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Customer findCustomerByEmail(String email) {
        List<User> allUsers = getAllUsers();
        for (User user : allUsers) {
            if (user instanceof Customer && user.getEmail().equalsIgnoreCase(email)) {
                return (Customer) user;
            }
        }
        return null;
    }
}
