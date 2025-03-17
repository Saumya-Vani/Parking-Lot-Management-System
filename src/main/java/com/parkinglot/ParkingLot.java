package com.parkinglot;

import java.util.Scanner;

public class ParkingLot {
	public static void main(String[] args) {
		ParkingManager manager = new ParkingManager();
		AVLTree avlTree = new AVLTree();
		AVLNode root = null;
		String filePath = "src/main/resources/parking_lot_data.xlsx"; // File path

		// Load parking slots from Excel
		root = manager.loadParkingSlotsFromFile(filePath, avlTree);

		Scanner scanner = new Scanner(System.in);

		while (true) {
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

			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				System.out.print("Enter Car License Number: ");
				String license = scanner.nextLine();
				root = manager.assignParkingSlot(root, license);
				break;
			case 2:
				System.out.print("Enter Slot Number to Free: ");
				int slotNumber = scanner.nextInt();
				root = manager.freeSlot(root, slotNumber);
				break;
			case 3:
				System.out.print("Enter the SlotNumber: ");
				int slotNum = scanner.nextInt();
				manager.displaySlotDetails(root, slotNum);
				break;
			case 4:
				int nearestSlot = manager.findNearestAvailableSlot(root);
				System.out.println("Nearest Available Slot: " + (nearestSlot == -1 ? "None" : nearestSlot));
				break;
			case 5:
				manager.displayParkingStatus(root);
				break;
			case 6:
				manager.displaySlots(root);
				break;
			case 7:
				manager.displayParkingStatistics(root);
				break;
			case 8:
				System.out.print("Enter the number of hours to check for old cars: ");
				int hoursLimit = scanner.nextInt();
				root = manager.releaseOldCars(root, hoursLimit);
				break;
			case 9:
				System.out.print("Enter Slot Number to Reserve: ");
				int reserveSlot = scanner.nextInt();
				root = manager.reserveSlot(root, reserveSlot);
				break;
			case 10:
				System.out.print("Enter Slot Number: ");
				int feeSlot = scanner.nextInt();
				System.out.print("Enter Hourly Rate: ");
				double rate = scanner.nextDouble();
				manager.calculateParkingFee(root, feeSlot, rate);
				break;
			case 11:
				manager.saveParkingSlotsToFile(filePath, root);
				System.out.println("Exiting...");
				scanner.close();
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}
}
