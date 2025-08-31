import java.io.*;
import java.util.*;

// Room Class
class Room {
    private int roomNumber;
    private String category;
    private boolean isBooked;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = false;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public boolean isBooked() { return isBooked; }

    public void bookRoom() { isBooked = true; }
    public void cancelBooking() { isBooked = false; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - " + (isBooked ? "Booked" : "Available");
    }
}

// Reservation Class
class Reservation {
    private String customerName;
    private Room room;
    private double amountPaid;

    public Reservation(String customerName, Room room, double amountPaid) {
        this.customerName = customerName;
        this.room = room;
        this.amountPaid = amountPaid;
    }

    public Room getRoom() { return room; }
    public String getCustomerName() { return customerName; }

    @Override
    public String toString() {
        return "Reservation [Customer: " + customerName + ", Room: " + room.getRoomNumber() +
                " (" + room.getCategory() + "), Paid: $" + amountPaid + "]";
    }
}

// Main System Class
public class HotelReservationSystem {
    private static List<Room> rooms = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static final String FILE_NAME = "reservations.txt";

    public static void main(String[] args) {
        loadRooms();
        loadReservations();

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Make a Reservation");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View All Reservations");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewAvailableRooms();
                case 2 -> makeReservation(sc);
                case 3 -> cancelReservation(sc);
                case 4 -> viewReservations();
                case 5 -> saveReservations();
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 5);
    }

    // Load predefined rooms
    private static void loadRooms() {
        rooms.add(new Room(101, "Standard"));
        rooms.add(new Room(102, "Standard"));
        rooms.add(new Room(201, "Deluxe"));
        rooms.add(new Room(202, "Deluxe"));
        rooms.add(new Room(301, "Suite"));
    }

    // Show available rooms
    private static void viewAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        for (Room room : rooms) {
            if (!room.isBooked()) {
                System.out.println(room);
            }
        }
    }

    // Make reservation
    private static void makeReservation(Scanner sc) {
        viewAvailableRooms();
        System.out.print("Enter Room Number: ");
        int roomNumber = sc.nextInt();
        sc.nextLine();

        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber && !room.isBooked()) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Room not available!");
            return;
        }

        System.out.print("Enter Your Name: ");
        String name = sc.nextLine();

        double price = switch (selectedRoom.getCategory()) {
            case "Standard" -> 100;
            case "Deluxe" -> 200;
            case "Suite" -> 300;
            default -> 0;
        };

        System.out.println("Payment Simulation: $" + price + " charged successfully!");
        selectedRoom.bookRoom();
        Reservation reservation = new Reservation(name, selectedRoom, price);
        reservations.add(reservation);

        System.out.println("Reservation Successful!\n" + reservation);
    }

    // Cancel reservation
    private static void cancelReservation(Scanner sc) {
        System.out.print("Enter Your Name: ");
        String name = sc.nextLine();

        Reservation toCancel = null;
        for (Reservation res : reservations) {
            if (res.getCustomerName().equalsIgnoreCase(name)) {
                toCancel = res;
                break;
            }
        }

        if (toCancel != null) {
            toCancel.getRoom().cancelBooking();
            reservations.remove(toCancel);
            System.out.println("Reservation canceled successfully!");
        } else {
            System.out.println("No reservation found under this name.");
        }
    }

    // View all reservations
    private static void viewReservations() {
        System.out.println("\n--- All Reservations ---");
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (Reservation res : reservations) {
                System.out.println(res);
            }
        }
    }

    // File I/O: Save reservations
    private static void saveReservations() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Reservation res : reservations) {
                writer.println(res.getCustomerName() + "," + res.getRoom().getRoomNumber() + "," + res.getRoom().getCategory());
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    // File I/O: Load reservations
    private static void loadReservations() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",");
                String name = data[0];
                int roomNumber = Integer.parseInt(data[1]);
                String category = data[2];

                for (Room room : rooms) {
                    if (room.getRoomNumber() == roomNumber) {
                        room.bookRoom();
                        reservations.add(new Reservation(name, room, 0)); // price not stored
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading reservations: " + e.getMessage());
        }
    }
}
