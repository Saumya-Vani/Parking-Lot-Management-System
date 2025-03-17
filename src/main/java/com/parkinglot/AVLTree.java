package com.parkinglot;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AVLTree {

	public AVLNode insert(AVLNode node, int slotNumber, Car carDetails) {

		if (node == null) {
			return new AVLNode(slotNumber, carDetails);
		}

		if (node.getSlotNumber() < slotNumber) {
			node.setRightChild(insert(node.getRightChild(), slotNumber, carDetails));
		} else if (node.getSlotNumber() > slotNumber) {
			node.setLeftChild(insert(node.getLeftChild(), slotNumber, carDetails));
		} else
			return node;

		node.updateHeight();
		int balanceFactor = getBalanceFactor(node);

		if (balanceFactor > 1) {
			if (node.getLeftChild() != null && slotNumber < node.getLeftChild().getSlotNumber()) {
				return rightRotate(node); // Left-Left Case
			}
			if (node.getLeftChild() != null && slotNumber > node.getLeftChild().getSlotNumber()) {
				node.setLeftChild(leftRotate(node.getLeftChild()));
				return rightRotate(node); // Left-Right Case
			}
		}
		if (balanceFactor < -1) {
			if (node.getRightChild() != null && slotNumber > node.getRightChild().getSlotNumber()) {
				return leftRotate(node); // Right-Right Case
			}
			if (node.getRightChild() != null && slotNumber < node.getRightChild().getSlotNumber()) {
				node.setRightChild(rightRotate(node.getRightChild()));
				return leftRotate(node); // Right-Left Case
			}
		}

		return node;
	}

	public AVLNode search(AVLNode node, int slotNumber) {
		if (node == null || slotNumber == node.getSlotNumber()) {
			return node;
		}
		if (slotNumber < node.getSlotNumber()) {
			return search(node.getLeftChild(), slotNumber);
		}
		return search(node.getRightChild(), slotNumber);

	}

	public void displaySlotDetails(AVLNode root, int slotNumber) {
		AVLNode slot = search(root, slotNumber);
		if (slot == null) {
			System.out.println("Slot " + slotNumber + " not present in the parking lot!");
		} else {
			System.out.println("\nDetails of the Slot");
			System.out.println("Slot Number: " + slot.getSlotNumber());
			System.out.println("Availability: " + (slot.isAvailable() ? "Available" : "Occupied"));
			System.out.println("Reservation: " + (slot.isReserved() ? "Reserved" : "Unreserved"));
			if (slot.getCarDetails() != null) {
				System.out.println("Car License: " + slot.getCarDetails().getLicenseNumber());
				System.out.println("Entry Time: " + slot.getCarDetails().getEntryTime());
			} else {
				System.out.println("No car parked in this slot.");
			}
		}
	}

	public int getHeight(AVLNode node) {
		return node == null ? 0 : node.getHeight();
	}

	public int getBalanceFactor(AVLNode node) {
		if (node == null) {
			return 0;
		}
		return getHeight(node.getLeftChild()) - getHeight(node.getRightChild());
	}

	public AVLNode leftRotate(AVLNode node) {
		AVLNode mid = node.getRightChild();
		node.setRightChild(mid.getLeftChild());
		mid.setLeftChild(node);

		node.updateHeight();
		mid.updateHeight();
		return mid;
	}

	public AVLNode rightRotate(AVLNode node) {
		AVLNode mid = node.getLeftChild();
		node.setLeftChild(mid.getRightChild());
		mid.setRightChild(node);

		node.updateHeight();
		mid.updateHeight();
		return mid;
	}

	public AVLNode leftRightRotate(AVLNode node) {
		node.setLeftChild(leftRotate(node.getLeftChild()));
		return rightRotate(node);
	}

	public AVLNode rightLeftRotate(AVLNode node) {
		node.setRightChild(rightRotate(node.getRightChild()));
		return leftRotate(node);
	}

	public void printOrderTraversal(AVLNode node) {

		if (node == null)
			return;
		printOrderTraversal(node.getLeftChild());
		System.out.println(node.toString());
		printOrderTraversal(node.getRightChild());
	}

	public int findNearestAvailableSlot(AVLNode node) {
		if (node == null)
			return -1;

		int left = findNearestAvailableSlot(node.getLeftChild());
		if (left != -1)
			return left; // Return the first available slot

		if (node.isAvailable())
			return node.getSlotNumber();

		return findNearestAvailableSlot(node.getRightChild());
	}

	public AVLNode updateAvailability(AVLNode node, int slotNumber, boolean status) {
		if (node == null) {
			System.out.println("Slot " + slotNumber + " not found!");
			return null;
		}
		if (slotNumber < node.getSlotNumber()) {
			node.setLeftChild(updateAvailability(node.getLeftChild(), slotNumber, status));
		} else if (slotNumber > node.getSlotNumber()) {
			node.setRightChild(updateAvailability(node.getRightChild(), slotNumber, status));
		} else {
			node.setAvailable(status);
			// System.out.println("Slot " + slotNumber + " is now " + (status ? "available"
			// : "occupied"));
		}
		return node;
	}

	public void displaySlots(AVLNode node) {
		List<Integer> availableSlots = new ArrayList<>();
		List<Integer> occupiedSlots = new ArrayList<>();
		List<Integer> reservedSlots = new ArrayList<>();
		collectSlots(node, availableSlots, occupiedSlots, reservedSlots);

		System.out.println("\nAvailable Slots: " + availableSlots);
		System.out.println("Occupied Slots: " + occupiedSlots);
		System.out.println("Reserved Slots: " + reservedSlots);
	}

	private void collectSlots(AVLNode node, List<Integer> availableSlots, List<Integer> occupiedSlots,
			List<Integer> reservedSlots) {
		if (node == null)
			return;

		collectSlots(node.getLeftChild(), availableSlots, occupiedSlots, reservedSlots);

		if (node.isAvailable() && !node.isReserved()) {
			availableSlots.add(node.getSlotNumber());
		} else if (node.isAvailable() && node.isReserved()) {
			reservedSlots.add(node.getSlotNumber());
		} else {
			occupiedSlots.add(node.getSlotNumber());
		}

		collectSlots(node.getRightChild(), availableSlots, occupiedSlots, reservedSlots);
	}

	public AVLNode releaseOldCars(AVLNode node, int hoursLimit) {
		if (node == null)
			return node;

		node.setLeftChild(releaseOldCars(node.getLeftChild(), hoursLimit));

		if (!node.isAvailable() && node.getCarDetails() != null) {
			long parkedDuration = ChronoUnit.HOURS.between(node.getCarDetails().getEntryTime(), LocalDateTime.now());

			if (parkedDuration >= hoursLimit) {
				node.setAvailable(true);
				node.setCarDetails(null);
				System.out.println("Slot " + node.getSlotNumber() + " is now available (Car stayed over " + hoursLimit
						+ " hours).");
			}
		}

		node.setRightChild(releaseOldCars(node.getRightChild(), hoursLimit));
		return node;
	}

	public void printParkingStatus(AVLNode node) {
		if (node == null)
			return;

		printParkingStatus(node.getLeftChild());

		if (node.isAvailable() && !node.isReserved()) {
			System.out.println("Slot Number: " + node.getSlotNumber() + " | Status: Available");
		} else if (node.isReserved() && node.isAvailable()) {
			System.out.println("Slot Number: " + node.getSlotNumber() + " | Status: Reserved");
		} else {
			System.out.println("Slot Number: " + node.getSlotNumber() + " | Status: Occupied | Car: "
					+ node.getCarDetails().getLicenseNumber() + " | Entry Time: "
					+ node.getCarDetails().getEntryTime());
		}

		printParkingStatus(node.getRightChild());
	}

	public AVLNode updateReservation(AVLNode node, int slotNumber, boolean status) {
		if (node == null) {
			System.out.println("Slot " + slotNumber + " not found!");
			return null;
		}
		if (slotNumber < node.getSlotNumber()) {
			node.setLeftChild(updateReservation(node.getLeftChild(), slotNumber, status));
		} else if (slotNumber > node.getSlotNumber()) {
			node.setRightChild(updateReservation(node.getRightChild(), slotNumber, status));
		} else {
			node.setReserved(status);
		}
		return node;
	}

}
