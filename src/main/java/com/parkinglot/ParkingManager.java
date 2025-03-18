package com.parkinglot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Manages parking slots using an AVL Tree.
 * Handles loading and saving parking slots from an Excel file, 
 * assigning, reserving, and freeing parking slots.
 */
public class ParkingManager {

	private AVLTree tree; // AVL Tree instance to manage parking slots

	/**
 	 * Constructor to initialize the ParkingManager with an empty AVL Tree.
 	 */
	public ParkingManager() {
		this.tree = new AVLTree();
	}

	/**
 	 * Loads parking slot data from an Excel file and inserts it into the AVL Tree.
 	 *
 	 * @param filePath The path to the Excel file.
 	 * @param avlTree  The AVL Tree instance where data will be loaded.
 	 * @return The root node of the AVL Tree after inserting parking slots.
 	 */
	public AVLNode loadParkingSlotsFromFile(String filePath, AVLTree avlTree) {
		AVLNode root = null;

		try (FileInputStream file = new FileInputStream(new File(filePath));
				Workbook workbook = new XSSFWorkbook(file)) {

			Sheet sheet = workbook.getSheetAt(0);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Skip header row

				int slotNumber = row.getCell(0) != null ? (int) row.getCell(0).getNumericCellValue() : -1;
	            String licenseNumber = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
	            String entryTimeStr = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
	            boolean availability = row.getCell(3) != null && row.getCell(3).getBooleanCellValue();
	            boolean reservations = row.getCell(4) != null && row.getCell(4).getBooleanCellValue();

	            if (slotNumber == -1) continue;// Skip invalid slot numbers
	            
				LocalDateTime entryTime = entryTimeStr.isEmpty() ? null : LocalDateTime.parse(entryTimeStr, formatter);
				Car car = licenseNumber.isEmpty() ? null : new Car(licenseNumber, entryTime);

				// Insert slot into AVL Tree
				root = avlTree.insert(root, slotNumber, car);
				avlTree.updateAvailability(root, slotNumber, availability);
				avlTree.updateReservation(root, slotNumber, reservations);
			}

			System.out.println("Parking slots loaded successfully from " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return root;
	}

	/**
 	 * Saves parking slot data to an Excel file.
 	 *
 	 * @param filePath The path where the Excel file will be saved.
 	 * @param root     The root node of the AVL Tree.
 	 */
	public void saveParkingSlotsToFile(String filePath, AVLNode root) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Parking Slots");

			// Create header row
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Slot Number");
			headerRow.createCell(1).setCellValue("Car License Number");
			headerRow.createCell(2).setCellValue("Entry Time");
			headerRow.createCell(3).setCellValue("Availability");
			headerRow.createCell(4).setCellValue("Reservations");

			// Write slot data to Excel
			saveNodeDataToSheet(sheet, root, 1);

			// Write to file
			try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
				workbook.write(fileOut);
			}

			System.out.println("Parking slots saved successfully to " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
 	 * Recursively saves parking slot data from the AVL Tree to the Excel sheet.
 	 *
 	 * @param sheet    The Excel sheet where data will be written.
 	 * @param node     The current AVL node being processed.
 	 * @param rowIndex The current row index in the sheet.
 	 * @return The next row index after writing data.
 	 */
	private int saveNodeDataToSheet(Sheet sheet, AVLNode node, int rowIndex) {
		if (node == null)
			return rowIndex;

		rowIndex = saveNodeDataToSheet(sheet, node.getLeftChild(), rowIndex);

		Row row = sheet.createRow(rowIndex++);
		row.createCell(0).setCellValue(node.getSlotNumber());
		row.createCell(1).setCellValue(node.getCarDetails() == null ? "" : node.getCarDetails().getLicenseNumber());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formattedDate = "";
		if (node.getCarDetails() != null && node.getCarDetails().getEntryTime() != null) {
		    formattedDate = node.getCarDetails().getEntryTime().format(formatter);
		}
		row.createCell(2).setCellValue(formattedDate);
		row.createCell(3).setCellValue(node.isAvailable());
		row.createCell(4).setCellValue(node.isReserved());
		
		rowIndex = saveNodeDataToSheet(sheet, node.getRightChild(), rowIndex);

		return rowIndex;
	}

	/**
 	 * Assigns a parking slot to a car by finding the nearest available slot.
 	 *
 	 * @param root          The root node of the AVL Tree.
 	 * @param licenseNumber The license plate number of the car to be parked.
 	 * @return The updated AVL Tree root after assignment.
 	 */
	public AVLNode assignParkingSlot(AVLNode root, String licenseNumber) {
		int slotNumber = tree.findNearestAvailableSlot(root);

		if (slotNumber == -1) {
			System.out.println("No available slots.");
			return root;
		}

		Car newCar = new Car(licenseNumber, LocalDateTime.now());
		root = tree.updateAvailability(root, slotNumber, false);
		AVLNode slot = tree.search(root, slotNumber);
		slot.setCarDetails(newCar);

		if (slot != null) {
	        slot.setCarDetails(newCar);
	        // Remove reservation since the car is parked
	        if (slot.isReserved()) {
	            slot.setReserved(false);
	            System.out.println("Reservation cleared for Slot " + slotNumber + ".");
	        }
	        System.out.println("Car " + licenseNumber + " parked at slot " + slotNumber);
	    }

		return root;
	}

	/**
 	 * Frees an occupied parking slot.
 	 *
 	 * @param root       The root node of the AVL Tree.
 	 * @param slotNumber The slot number to be freed.
 	 * @return The updated AVL Tree root after freeing the slot.
 	 */
	public AVLNode freeSlot(AVLNode root, int slotNumber) {
	    AVLNode slot = tree.search(root, slotNumber);
	    
	    if (slot == null) {  
	        System.out.println("Slot " + slotNumber + " is not present in the parking lot.");
	        return root;
	    }
	    
	    if (slot.isAvailable() && !slot.isReserved()) { 
	        System.out.println("Slot " + slotNumber + " is already available.");
	        return root;
	    }

	    slot.setCarDetails(null);
	    root = tree.updateAvailability(root, slotNumber, true);
	    System.out.println("Slot " + slotNumber + " is now available.");
	    
	    return root;
	}

	/**
	 * Displays the available, occupied, and reserved slots in the AVL tree.
	 * 
	 * This method calls the tree's `displaySlots` method to categorize the slots into
	 * available, occupied, and reserved slots, then prints out the categorized slots.
	 * 
	 * @param root The root node of the AVL tree to begin displaying slots from.
	 */
	public void displaySlots(AVLNode root) {
		tree.displaySlots(root);
	}

	/**
	 * Finds the nearest available slot in the AVL tree.
	 * 
	 * This method calls the tree's `findNearestAvailableSlot` method to find and return
	 * the first available slot in the tree. The search is done in an in-order manner.
	 * 
	 * @param root The root node of the AVL tree to start searching for the nearest available slot.
	 * @return The slot number of the nearest available slot. Returns -1 if no available slots are found.
	 */
	public int findNearestAvailableSlot(AVLNode root) {
		return tree.findNearestAvailableSlot(root);
	}

	/**
	 * Prints the parking status of each slot in the AVL tree.
	 * 
	 * This method calls the tree's `printParkingStatus` method to print the status of each slot.
	 * The parking status includes whether the slot is available, reserved, or occupied,
	 * and additional information about the car if the slot is occupied.
	 * 
	 * @param root The root node of the AVL tree to start displaying the parking status from.
	 */
	public void displayParkingStatus(AVLNode root) {
		tree.printParkingStatus(root);
	}
	
	/**
	 * Releases cars that have been parked for longer than a specified time limit.
	 * 
	 * This method recursively traverses the AVL tree and checks each slot to see if a car has
	 * been parked for longer than the given `hoursLimit`. If so, it marks the slot as available.
	 * 
	 * @param node The current node being evaluated.
	 * @param hoursLimit The number of hours beyond which a car is considered to have stayed too long.
	 * @return The updated AVLNode with old cars released as needed.
	 */
	public AVLNode releaseOldCars(AVLNode node, int hoursLimit) {
		return tree.releaseOldCars(node, hoursLimit);
	}

	/**
	 * Displays the details of a specific parking slot.
	 * 
	 * This method calls the tree's `displaySlotDetails` method to display detailed information
	 * about a particular parking slot, including its availability, reservation status, and car details
	 * if the slot is occupied.
	 * 
	 * @param root The root node of the AVL tree.
	 * @param slotNum The slot number whose details need to be displayed.
	 */
	public void displaySlotDetails(AVLNode root, int slotNum) {
		tree.displaySlotDetails(root, slotNum);
	}
	
	/**
	 * Calculates the parking fee for a car parked in a specific slot.
	 * 
	 * This method calculates the total parking fee for a car based on the time it has been parked
	 * and a provided hourly rate. The fee is calculated by determining the number of hours the car
	 * has been parked and multiplying it by the hourly rate.
	 * 
	 * @param root The root node of the AVL tree.
	 * @param slotNumber The slot number where the car is parked.
	 * @param hourlyRate The hourly rate to be applied for parking.
	 * @return The calculated parking fee.
	 */
	public double calculateParkingFee(AVLNode root, int slotNumber, double hourlyRate) {
	    AVLNode slot = tree.search(root, slotNumber);
	    if (slot == null || slot.isAvailable()) {
	        System.out.println("Slot " + slotNumber + " is not occupied.");
	        return 0;
	    }

	    long hoursParked = ChronoUnit.HOURS.between(slot.getCarDetails().getEntryTime(), LocalDateTime.now());
	    double totalFee = hoursParked * hourlyRate;
	    
	    System.out.println("Car " + slot.getCarDetails().getLicenseNumber() + " parked for " + hoursParked + " hours.");
	    System.out.println("Total Fee: EUR " + totalFee);
	    return totalFee;
	}

	/**
 	 * Reserves a parking slot.
 	 *
 	 * @param root       The root node of the AVL Tree.
 	 * @param slotNumber The slot number to reserve.
 	 * @return The updated AVL Tree root after reservation.
 	 */
	public AVLNode reserveSlot(AVLNode root, int slotNumber) {
	    AVLNode slot = tree.search(root, slotNumber);
	    if (slot == null) {
	        System.out.println("Slot " + slotNumber + " not found.");
	        return root;
	    }

	    if (!slot.isAvailable()) {
	        System.out.println("Slot " + slotNumber + " is already occupied.");
	        return root;
	    }
	    slot.setReserved(true);
	    System.out.println("Slot " + slotNumber + " has been reserved.");
	    return root;
	}
	
	/**
 	 * Displays statistics about the parking lot.
 	 */
	public void displayParkingStatistics(AVLNode node) {
	    int totalSlots = countTotalSlots(node);
	    int occupiedSlots = countOccupiedSlots(node);
	    int availableSlots = totalSlots - occupiedSlots;

	    System.out.println("\n--- Parking Statistics ---");
	    System.out.println("Total Slots: " + totalSlots);
	    System.out.println("Occupied Slots: " + occupiedSlots);
	    System.out.println("Available Slots: " + availableSlots);
	}

	/**
	 * Counts the total number of slots in the AVL tree.
	 * 
	 * This method recursively traverses the AVL tree and counts the total number
	 * of slots (nodes) in the tree.
	 * 
	 * @param node The current node being evaluated.
	 * @return The total number of slots in the tree.
	 */
	private int countTotalSlots(AVLNode node) {
	    if (node == null) return 0;
	    return 1 + countTotalSlots(node.getLeftChild()) + countTotalSlots(node.getRightChild());
	}

	/**
	 * Counts the number of occupied slots in the AVL tree.
	 * 
	 * This method recursively traverses the AVL tree and counts the slots that are occupied.
	 * A slot is considered occupied if its availability status is false.
	 * 
	 * @param node The current node being evaluated.
	 * @return The number of occupied slots in the tree.
	 */
	private int countOccupiedSlots(AVLNode node) {
	    if (node == null) return 0;
	    return (node.isAvailable() ? 0 : 1) + countOccupiedSlots(node.getLeftChild()) + countOccupiedSlots(node.getRightChild());
	}
}