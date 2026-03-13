package com.hallsymphony;

import com.hallsymphony.gui.login.LoginFrame;

public class Main {
    public static void main(String[] args) {
        LoginFrame.run();
    }
}

    private static void handleRegistration(UserService userService) {
        System.out.println("\n=== Customer Registration ===");
        System.out.print("Full name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Address: ");
        String address = scanner.nextLine().trim();

        Customer customer = new Customer("U-" + UUID.randomUUID(), name, email, password, "ACTIVE", phone, address, LocalDate.now());
        userService.registerCustomer(customer);
        System.out.println("Registration successful. You can now login.");
    }

    private static void handleLogin(UserService userService, HallService hallService, BookingService bookingService,
                                    PaymentService paymentService, IssueService issueService) {
        System.out.println("\n=== Login ===");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userService.authenticateUser(email, password);
        if (user == null) {
            System.out.println("Invalid credentials or account blocked.");
            return;
        }

        if (user instanceof Administrator) {
            adminMenu((Administrator) user, userService);
        } else if (user instanceof Scheduler) {
            schedulerMenu((Scheduler) user, hallService);
        } else if (user instanceof Manager) {
            managerMenu((Manager) user, bookingService, paymentService, issueService);
        } else if (user instanceof Customer) {
            customerMenu((Customer) user, hallService, bookingService, paymentService, issueService);
        } else {
            System.out.println("Unknown user role.");
        }
    }

    private static void adminMenu(Administrator admin, UserService userService) {
        while (true) {
            System.out.println("\n=== Administrator Menu ===");
            System.out.println("1) View all users");
            System.out.println("2) Block user");
            System.out.println("0) Logout");
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> {
                    List<User> users = userService.getAllUsers();
                    System.out.println("\n--- Users ---");
                    for (User u : users) {
                        System.out.printf("%s | %s | %s | %s\n", u.getUserId(), u.getFullName(), u.getEmail(), u.getStatus());
                    }
                }
                case "2" -> {
                    System.out.print("Enter user ID to block: ");
                    String userId = scanner.nextLine().trim();
                    userService.blockUser(userId);
                    System.out.println("User blocked (if existed).");
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void schedulerMenu(Scheduler scheduler, HallService hallService) {
        while (true) {
            System.out.println("\n=== Scheduler Menu ===");
            System.out.println("1) View all halls");
            System.out.println("2) Add hall");
            System.out.println("3) Update hall status");
            System.out.println("4) Delete hall");
            System.out.println("0) Logout");
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> {
                    List<Hall> halls = hallService.getAllHalls();
                    System.out.println("\n--- Halls ---");
                    for (Hall hall : halls) {
                        System.out.printf("%s | %s | %s | %d | %.2f | %s\n",
                                hall.getHallId(), hall.getHallName(), hall.getClass().getSimpleName(), hall.getCapacity(), hall.getRatePerHour(), hall.getStatus());
                    }
                }
                case "2" -> {
                    System.out.print("Hall type (AUDITORIUM/BANQUET/MEETING): ");
                    String type = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Hall name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Capacity: ");
                    int capacity = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Rate per hour: ");
                    double rate = Double.parseDouble(scanner.nextLine().trim());
                    Hall hall;
                    if (type.startsWith("AUD")) {
                        hall = new Auditorium("H-" + UUID.randomUUID(), name, capacity, rate, "AVAILABLE");
                    } else if (type.startsWith("BAN")) {
                        hall = new BanquetHall("H-" + UUID.randomUUID(), name, capacity, rate, "AVAILABLE");
                    } else {
                        hall = new MeetingRoom("H-" + UUID.randomUUID(), name, capacity, rate, "AVAILABLE");
                    }
                    hallService.addHall(hall);
                    System.out.println("Hall added.");
                }
                case "3" -> {
                    System.out.print("Enter Hall ID: ");
                    String hallId = scanner.nextLine().trim();
                    System.out.print("New status (AVAILABLE/UNAVAILABLE): ");
                    String status = scanner.nextLine().trim().toUpperCase();
                    List<Hall> halls = hallService.getAllHalls();
                    for (Hall hall : halls) {
                        if (hall.getHallId().equals(hallId)) {
                            hall.updateStatus(status);
                            hallService.updateHall(hall);
                            System.out.println("Hall status updated.");
                            break;
                        }
                    }
                }
                case "4" -> {
                    System.out.print("Enter Hall ID to delete: ");
                    String hallId = scanner.nextLine().trim();
                    hallService.deleteHall(hallId);
                    System.out.println("Hall deleted (if existed).\n");
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void managerMenu(Manager manager, BookingService bookingService, PaymentService paymentService,
                                    IssueService issueService) {
        while (true) {
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1) View sales summary");
            System.out.println("2) View issues");
            System.out.println("3) Update issue status");
            System.out.println("0) Logout");
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> {
                    double total = paymentService.getAllPayments().stream().mapToDouble(Payment::getAmount).sum();
                    System.out.println("Total sales: RM " + total);
                }
                case "2" -> {
                    System.out.println("\n--- Issues ---");
                    issueService.getAllIssues().forEach(i -> System.out.printf("%s | %s | %s | %s\n", i.getIssueId(), i.getBookingId(), i.getDescription(), i.getIssueStatus()));
                }
                case "3" -> {
                    System.out.print("Issue ID: ");
                    String issueId = scanner.nextLine().trim();
                    System.out.print("New status (IN_PROGRESS/DONE/CLOSED/CANCELLED): ");
                    String status = scanner.nextLine().trim().toUpperCase();
                    try {
                        issueService.updateIssueStatus(issueId, IssueStatus.valueOf(status));
                        System.out.println("Issue status updated.");
                    } catch (Exception e) {
                        System.out.println("Invalid status.");
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void customerMenu(Customer customer, HallService hallService, BookingService bookingService,
                                     PaymentService paymentService, IssueService issueService) {
        while (true) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1) View available halls");
            System.out.println("2) Make a booking");
            System.out.println("3) View my bookings");
            System.out.println("4) Cancel a booking");
            System.out.println("5) Raise an issue");
            System.out.println("0) Logout");
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> {
                    System.out.println("\n--- Available Halls ---");
                    hallService.getAvailableHalls().forEach(h -> System.out.printf("%s | %s | %s | RM %.2f\n",
                            h.getHallId(), h.getHallName(), h.getClass().getSimpleName(), h.getRatePerHour()));
                }
                case "2" -> {
                    System.out.print("Enter hall ID: ");
                    String hallId = scanner.nextLine().trim();
                    System.out.print("Booking date (YYYY-MM-DD): ");
                    LocalDate date = LocalDate.parse(scanner.nextLine().trim());
                    System.out.print("Start time (HH:MM): ");
                    LocalTime start = LocalTime.parse(scanner.nextLine().trim());
                    System.out.print("End time (HH:MM): ");
                    LocalTime end = LocalTime.parse(scanner.nextLine().trim());

                    Optional<Hall> hall = hallService.getAllHalls().stream().filter(h -> h.getHallId().equals(hallId)).findFirst();
                    if (hall.isEmpty()) {
                        System.out.println("Hall not found.");
                        break;
                    }

                    if (!bookingService.validateBookingDate(date)) {
                        System.out.println("Booking date is outside allowed range.");
                        break;
                    }

                    LocalTime open = LocalTime.of(8, 0);
                    LocalTime close = LocalTime.of(18, 0);
                    if (start.isBefore(open) || end.isAfter(close) || !end.isAfter(start)) {
                        System.out.println("Booking time must be between 08:00 and 18:00 and end after start.");
                        break;
                    }

                    long hours = java.time.Duration.between(start, end).toHours();
                    double amount = hall.get().getRatePerHour() * hours;
                    Booking booking = new Booking("B-" + UUID.randomUUID(), customer.getUserId(), hallId, date, start, end,
                            amount, BookingStatus.PENDING);
                    Booking result = bookingService.createBooking(booking);
                    if (result == null) {
                        System.out.println("Unable to create booking: hall is already booked for the selected slot.");
                    } else {
                        System.out.println("Booking created (pending). Amount: RM " + amount);
                    }
                }
                case "3" -> {
                    System.out.println("\n--- My Bookings ---");
                    bookingService.getBookingsForCustomer(customer.getUserId()).forEach(b ->
                            System.out.printf("%s | %s | %s | %s | %s | RM %.2f | %s\n", b.getBookingId(), b.getHallId(),
                                    b.getBookingDate(), b.getStartTime(), b.getEndTime(), b.getTotalAmount(), b.getBookingStatus()));
                }
                case "4" -> {
                    System.out.print("Enter booking ID to cancel: ");
                    String bookingId = scanner.nextLine().trim();
                    boolean success = bookingService.cancelBooking(bookingId);
                    if (success) {
                        System.out.println("Booking cancelled.");
                    } else {
                        System.out.println("Unable to cancel booking (it may not exist, is already cancelled, or is within 3 days of the event).");
                    }
                }
                case "5" -> {
                    System.out.print("Enter booking ID for issue: ");
                    String bookingId = scanner.nextLine().trim();
                    System.out.print("Describe the issue: ");
                    String desc = scanner.nextLine().trim();
                    issueService.raiseIssue(new com.hallsymphony.model.issue.Issue("I-" + UUID.randomUUID(), bookingId, desc, LocalDate.now(), com.hallsymphony.model.issue.IssueStatus.IN_PROGRESS));
                    System.out.println("Issue registered.");
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
