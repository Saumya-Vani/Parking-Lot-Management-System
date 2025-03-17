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

public class ParkingManager {

	private AVLTree tree;

	public ParkingManager() {
		this.tree = new AVLTree();
	}

	public AVLNode loadParkingSlotsFromFile(String filePath, AVLTree avlTree) {
		AVLNode root = null;

		try (FileInputStream file = new FileInputStream(new File(filePath));
				Workbook workbook = new XSSFWorkbook(file)) {

			Sheet sheet = workbook.getSheetAt(0);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Skip header

				int slotNumber = row.getCell(0) != null ? (int) row.getCell(0).getNumericCellValue() : -1;
	            String licenseNumber = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
	            String entryTimeStr = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
	            boolean availability = row.getCell(3) != null && row.getCell(3).getBooleanCellValue();
	            boolean reservations = row.getCell(4) != null && row.getCell(4).getBooleanCellValue();

	            if (slotNumber == -1) continue;
	            
				LocalDateTime entryTime = entryTimeStr.isEmpty() ? null : LocalDateTime.parse(entryTimeStr, formatter);
				Car car = licenseNumber.isEmpty() ? null : new Car(licenseNumber, entryTime);

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

			// Write data
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

	public void displaySlots(AVLNode root) {
		tree.displaySlots(root);
	}

	public int findNearestAvailableSlot(AVLNode root) {
		return tree.findNearestAvailableSlot(root);
	}

	public void displayParkingStatus(AVLNode root) {
		tree.printParkingStatus(root);
	}
	
	public AVLNode releaseOldCars(AVLNode node, int hoursLimit) {
		return tree.releaseOldCars(node, hoursLimit);
	}

	public void displaySlotDetails(AVLNode root, int slotNum) {
		tree.displaySlotDetails(root, slotNum);
	}
	
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
	
	public void displayParkingStatistics(AVLNode node) {
	    int totalSlots = countTotalSlots(node);
	    int occupiedSlots = countOccupiedSlots(node);
	    int availableSlots = totalSlots - occupiedSlots;

	    System.out.println("\n--- Parking Statistics ---");
	    System.out.println("Total Slots: " + totalSlots);
	    System.out.println("Occupied Slots: " + occupiedSlots);
	    System.out.println("Available Slots: " + availableSlots);
	}

	private int countTotalSlots(AVLNode node) {
	    if (node == null) return 0;
	    return 1 + countTotalSlots(node.getLeftChild()) + countTotalSlots(node.getRightChild());
	}

	private int countOccupiedSlots(AVLNode node) {
	    if (node == null) return 0;
	    return (node.isAvailable() ? 0 : 1) + countOccupiedSlots(node.getLeftChild()) + countOccupiedSlots(node.getRightChild());
	}
}