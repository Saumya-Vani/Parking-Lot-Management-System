package com.parkinglot;

import java.util.Scanner;

/**
 * Main class for the Parking Lot Management System. This class provides a
 * command-line interface (CLI) for users to interact with the system, perform
 * various parking operations, and manage parking slots.
 */
public class ParkingLot {
	public static void main(String[] args) {
		// Create instances of ParkingManager and AVLTree
		ParkingManager manager = new ParkingManager();
		AVLTree avlTree = new AVLTree();
		AVLNode root = null;
		String filePath = "src/main/resources/parking_lot_data.xlsx"; // File path for parking slot data

		// Load parking slots from the Excel file to initialize the system
		root = manager.loadParkingSlotsFromFile(filePath, avlTree);

		Scanner scanner = new Scanner(System.in);

		// Main loop for user interaction
		while (true) {
			// Display menu options
			System.out.println("\nPARKING LOT MANAGEMENT");
			System.out.println("1. Park a Car");
			System.out.println("2. Remove a Car");
			System.out.println("3. Display Slot Details");
			System.out.println("4. Find Nearest Available Slot");
			System.out.println("5. Show Parking Status");
			System.out.println("6. Show Slots Availability");
			System.out.println("7. Show Parking Statistics");
			System.out.println("8. Remove Cars Parked for Too Long");
			System.out.println("9. Reserve a Parking Slot");
			System.out.println("10. Calculate Parking Fee");
			System.out.println("11. Save & Exit");
			System.out.print("Enter your choice: ");

			// Get user input
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline character

			// Process user input using a switch case
			switch (choice) {
			case 1:
				// Park a car by assigning it to the nearest available slot
				System.out.print("Enter Car License Number: ");
				String license = scanner.nextLine();
				root = manager.assignParkingSlot(root, license);
				break;
			case 2:
				// Remove a parked car and free the slot
				System.out.print("Enter Slot Number to Free: ");
				int slotNumber = scanner.nextInt();
				root = manager.freeSlot(root, slotNumber);
				break;
			case 3:
				// Display details of a specific parking slot
				System.out.print("Enter the Slot Number: ");
				int slotNum = scanner.nextInt();
				manager.displaySlotDetails(root, slotNum);
				break;
			case 4:
				// Find the nearest available parking slot
				int nearestSlot = manager.findNearestAvailableSlot(root);
				System.out.println("Nearest Available Slot: " + (nearestSlot == -1 ? "None" : nearestSlot));
				break;
			case 5:
				// Show the status of all parking slots
				manager.displayParkingStatus(root);
				break;
			case 6:
				// Show the list of available, occupied, and reserved slots
				manager.displaySlots(root);
				break;
			case 7:
				// Show statistics of the parking lot
				manager.displayParkingStatistics(root);
				break;
			case 8:
				// Remove cars that have been parked for too long
				System.out.print("Enter the number of hours to check for old cars: ");
				int hoursLimit = scanner.nextInt();
				root = manager.releaseOldCars(root, hoursLimit);
				break;
			case 9:
				// Reserve a specific parking slot
				System.out.print("Enter Slot Number to Reserve: ");
				int reserveSlot = scanner.nextInt();
				root = manager.reserveSlot(root, reserveSlot);
				break;
			case 10:
				// Calculate the parking fee based on the duration of parking
				System.out.print("Enter Slot Number: ");
				int feeSlot = scanner.nextInt();
				System.out.print("Enter Hourly Rate: ");
				double rate = scanner.nextDouble();
				manager.calculateParkingFee(root, feeSlot, rate);
				break;
			case 11:
				// Save the parking lot data and exit the program
				manager.saveParkingSlotsToFile(filePath, root);
				System.out.println("Exiting...");
				scanner.close(); // Close scanner before exiting
				return;
			default:
				// Handle invalid input
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}
}
