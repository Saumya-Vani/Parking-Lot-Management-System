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

	            if (slotNumber == -1) continue; // Skip invalid slot numbers
	            
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
		String formattedDate = node.getCarDetails() != null && node.getCarDetails().getEntryTime() != null
				? node.getCarDetails().getEntryTime().format(formatter)
				: "";
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
}
