package com.parkinglot;

/**
 * Represents a node in the AVL Tree used for managing parking slots. Each node
 * corresponds to a parking slot and stores its status, car details, and links
 * to left and right child nodes.
 */
public class AVLNode {

	private int slotNumber; // Unique slot number
	private boolean available; // Indicates if the slot is available
	private Car carDetails; // Car parked in the slot (null if empty)
	private int height; // Height of the node for AVL balancing
	private boolean reserved; // Indicates if the slot is reserved
	private AVLNode leftChild; // Left child node in the AVL Tree
	private AVLNode rightChild; // Right child node in the AVL Tree

	/**
	 * Constructor to initialize an AVL Node (parking slot). By default, the slot is
	 * available and not reserved.
	 *
	 * @param slotNumber Unique slot number.
	 * @param carDetails Car parked in the slot (null if empty).
	 */
	public AVLNode(int slotNumber, Car carDetails) {
		this.slotNumber = slotNumber;
		this.available = true;
		this.reserved = false;
		this.carDetails = carDetails;
		this.height = 1; // New node is initially at height 1
		this.leftChild = null;
		this.rightChild = null;
	}

	// Getter and Setter methods for slot number
	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotnumber) {
		this.slotNumber = slotnumber;
	}

	// Getter and Setter methods for availability status
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean availability) {
		this.available = availability;
	}

	// Getter and Setter methods for car details
	public Car getCarDetails() {
		return carDetails;
	}

	public void setCarDetails(Car carDetails) {
		this.carDetails = carDetails;
	}

	// Getter and Setter methods for node height (used in AVL balancing)
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	// Getter and Setter methods for reservation status
	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	// Getter and Setter methods for left and right child nodes
	public AVLNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(AVLNode leftChild) {
		this.leftChild = leftChild;
	}

	public AVLNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(AVLNode rightChild) {
		this.rightChild = rightChild;
	}

	/**
	 * Returns a string representation of the parking slot. Includes slot number,
	 * availability, reservation status, and car details (if any).
	 *
	 * @return String representation of the slot.
	 */
	@Override
	public String toString() {
		return "Slot: " + slotNumber + " | Available: " + available + " | Reserved: " + reserved
				+ (carDetails != null ? " | Car: " + carDetails.toString() : "");
	}

	/**
	 * Updates the height of the node based on its children's heights. Ensures that
	 * AVL tree balancing operations can be performed correctly.
	 */
	public void updateHeight() {
		this.height = 1 + Math.max(this.leftChild == null ? 0 : this.leftChild.getHeight(),
				this.rightChild == null ? 0 : this.rightChild.getHeight());
	}
}
