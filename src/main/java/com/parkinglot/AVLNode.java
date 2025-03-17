package com.parkinglot;

public class AVLNode {

	private int slotNumber;
	private boolean available;
	private Car carDetails;
	private int height;
	private boolean reserved;
	private AVLNode leftChild;
	private AVLNode rightChild;

	public AVLNode(int slotNumber, Car carDetails) {
		this.slotNumber = slotNumber;
		this.available = true;
		this.reserved = false;
		this.carDetails = carDetails;
		this.height = 1;
		this.leftChild = null;
		this.rightChild = null;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotnumber) {
		this.slotNumber = slotnumber;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean availability) {
		this.available = availability;
	}

	public Car getCarDetails() {
		return carDetails;
	}

	public void setCarDetails(Car carDetails) {
		this.carDetails = carDetails;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

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

	@Override
	public String toString() {
		return "Slot: " + slotNumber + " | Available: " + available + " | Reserved: " + reserved
				+ (carDetails != null ? " | Car: " + carDetails.toString() : "");
	}

	public void updateHeight() {
		this.height = 1 + Math.max(this.leftChild == null ? 0 : this.leftChild.getHeight(),
				this.rightChild == null ? 0 : this.rightChild.getHeight());
	}

}
