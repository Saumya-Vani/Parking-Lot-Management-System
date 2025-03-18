package com.parkinglot;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * AVLTree class represents a self-balancing binary search tree (AVL Tree) 
 * used for managing parking slots efficiently.
 */
public class AVLTree {

	/**
 	 * Inserts a new parking slot into the AVL Tree while maintaining balance.
 	 *
 	 * @param node        Current node in the AVL Tree.
 	 * @param slotNumber  The slot number to be inserted.
 	 * @param carDetails  Car object if a car is parked in the slot, otherwise null.
 	 * @return The updated AVL Tree root node after insertion.
 	 */
	public AVLNode insert(AVLNode node, int slotNumber, Car carDetails) {

		// Base case: If the node is null, create a new AVLNode.
		if (node == null) {
			return new AVLNode(slotNumber, carDetails);
		}
		
		// Standard BST insert operation
		if (node.getSlotNumber() < slotNumber) {
			node.setRightChild(insert(node.getRightChild(), slotNumber, carDetails));
		} else if (node.getSlotNumber() > slotNumber) {
			node.setLeftChild(insert(node.getLeftChild(), slotNumber, carDetails));
		} else
			return node; // Duplicate slot numbers are not allowed.

		// Update the height of the node after insertion
		node.updateHeight();
		
		// Get the balance factor to check if rebalancing is needed
		int balanceFactor = getBalanceFactor(node);

		// Perform rotations if the tree becomes unbalanced
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

	/**
 	 * Searches for a parking slot by slot number in the AVL Tree.
 	 *
 	 * @param node       Current node in the AVL Tree.
 	 * @param slotNumber The slot number to search for.
 	 * @return The AVLNode containing the slot number, or null if not found.
 	 */
	public AVLNode search(AVLNode node, int slotNumber) {
		if (node == null || slotNumber == node.getSlotNumber()) {
			return node;
		}
		if (slotNumber < node.getSlotNumber()) {
			return search(node.getLeftChild(), slotNumber);
		}
		return search(node.getRightChild(), slotNumber);

	}

	/**
 	 * Displays details of a specific parking slot.
 	 *
 	 * @param root       The root node of the AVL Tree.
 	 * @param slotNumber The slot number to display.
 	 */
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

	/**
 	 * Returns the height of a node.
 	 *
 	 * @param node The AVLNode whose height is to be retrieved.
 	 * @return Height of the node or 0 if null.
 	 */
	public int getHeight(AVLNode node) {
		return node == null ? 0 : node.getHeight();
	}

	/**
 	 * Returns the balance factor of a node.
 	 *
 	 * @param node The AVLNode whose balance factor is to be retrieved.
 	 * @return The balance factor of the node.
 	 */
	public int getBalanceFactor(AVLNode node) {
		if (node == null) {
			return 0;
		}
		return getHeight(node.getLeftChild()) - getHeight(node.getRightChild());
	}

	/**
 	 * Performs a left rotation on the given node to balance the AVL Tree.
 	 */
	public AVLNode leftRotate(AVLNode node) {
		AVLNode mid = node.getRightChild();
		node.setRightChild(mid.getLeftChild());
		mid.setLeftChild(node);

		node.updateHeight();
		mid.updateHeight();
		return mid;
	}

	/**
 	 * Performs a right rotation on the given node to balance the AVL Tree.
 	 */
	public AVLNode rightRotate(AVLNode node) {
		AVLNode mid = node.getLeftChild();
		node.setLeftChild(mid.getRightChild());
		mid.setRightChild(node);

		node.updateHeight();
		mid.updateHeight();
		return mid;
	}

	/**
	 * Performs a left-right rotation on the AVL tree.
	 * This is a combination of two rotations:
	 * 1. Left rotation on the left child of the node.
	 * 2. Right rotation on the node itself.
	 * 
	 * This method is used to fix the AVL tree when there is an imbalance caused by
	 * an insertion into the left child’s right subtree (Left-Right case).
	 *
	 * @param node The node to perform the rotation on.
	 * @return The new root node of the subtree after the rotations.
	 */
	public AVLNode leftRightRotate(AVLNode node) {
		node.setLeftChild(leftRotate(node.getLeftChild()));
		return rightRotate(node);
	}

	/**
	 * Performs a right-left rotation on the AVL tree.
	 * This is a combination of two rotations:
	 * 1. Right rotation on the right child of the node.
	 * 2. Left rotation on the node itself.
	 * 
	 * This method is used to fix the AVL tree when there is an imbalance caused by
	 * an insertion into the right child’s left subtree (Right-Left case).
	 *
	 * @param node The node to perform the rotation on.
	 * @return The new root node of the subtree after the rotations.
	 */
	public AVLNode rightLeftRotate(AVLNode node) {
		node.setRightChild(rightRotate(node.getRightChild()));
		return leftRotate(node);
	}

	/**
	 * Performs an in-order traversal of the AVL tree and prints the details of each node.
	 *
	 * @param node The root node of the subtree to traverse.
	 */
	public void printOrderTraversal(AVLNode node) {

		if (node == null)
			return;
		printOrderTraversal(node.getLeftChild());
		System.out.println(node.toString());
		printOrderTraversal(node.getRightChild());
	}

	/**
 	 * Finds the nearest available parking slot.
 	 *
 	 * @param node The root of the AVL Tree.
 	 * @return The nearest available slot number or -1 if no slot is available.
 	 */
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

	/**
 	 * Updates the availability status of a parking slot.
 	 *
 	 * @param node       The root node of the AVL Tree.
 	 * @param slotNumber The slot number to update.
 	 * @param status     New availability status (true = available, false = occupied).
 	 * @return The updated AVLNode after modifying availability.
 	 */
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

	/**
	 * Displays the available, occupied, and reserved parking slots in the AVL tree.
	 * 
	 * This method calls the helper method collectSlots() to collect and categorize
	 * the slots based on their availability and reservation status, then prints 
	 * out the categorized slots.
	 *
	 * @param node The root node of the AVL tree to begin the slot collection.
	 */
	public void displaySlots(AVLNode node) {
		List<Integer> availableSlots = new ArrayList<>();
		List<Integer> occupiedSlots = new ArrayList<>();
		List<Integer> reservedSlots = new ArrayList<>();
		collectSlots(node, availableSlots, occupiedSlots, reservedSlots);

		System.out.println("\nAvailable Slots: " + availableSlots);
		System.out.println("Occupied Slots: " + occupiedSlots);
		System.out.println("Reserved Slots: " + reservedSlots);
	}

	/**
	 * Helper method that categorizes the parking slots into available, occupied, and reserved.
	 * 
	 * This method traverses the AVL tree and adds slot numbers to the respective lists
	 * (available, occupied, or reserved) based on the status of each slot.
	 * 
	 * @param node The current node being evaluated.
	 * @param availableSlots List to store the available slot numbers.
	 * @param occupiedSlots List to store the occupied slot numbers.
	 * @param reservedSlots List to store the reserved slot numbers.
	 */
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

	/**
 	 * Releases cars that have been parked for too long.
 	 *
 	 * @param node       The root of the AVL Tree.
 	 * @param hoursLimit Maximum hours a car can remain parked before removal.
 	 * @return The updated AVL Tree after removing old cars.
 	 */
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

	/**
	 * Prints the parking status of each slot in the AVL tree.
	 * 
	 * This method traverses the AVL tree in an in-order fashion and prints the 
	 * status of each slot, including the car details (if the slot is occupied).
	 * 
	 * @param node The root node of the AVL tree to begin the parking status display.
	 */
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

	/**
	 * Updates the reservation status of a given parking slot.
	 * 
	 * This method searches for the slot with the given slot number and updates 
	 * its reservation status. If the slot is not found, a message is printed.
	 * 
	 * @param node The current node being evaluated.
	 * @param slotNumber The slot number to be updated.
	 * @param status The new reservation status to set for the slot.
	 * @return The updated AVLNode after the reservation status is updated.
	 */
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