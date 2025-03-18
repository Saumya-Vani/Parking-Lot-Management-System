package com.parkinglot;

import java.time.LocalDateTime;

/**
 * Represents a car parked in the parking lot.
 * Stores the car's license number and entry time for tracking.
 */
public class Car {

	private String licenseNumber; // Car's license plate number
	private LocalDateTime entryTime; // Time when the car entered the parking lot

	/**
	 * Constructor to initialize a Car object.
	 * 
	 * @param licenseNumber The license plate number of the car.
	 * @param entryTime     The time when the car entered the parking lot.
	 */
	public Car(String licenseNumber, LocalDateTime entryTime) {
		this.licenseNumber = licenseNumber;
		this.entryTime = entryTime;
	}

	/**
	 * Retrieves the license number of the car.
	 * 
	 * @return The car's license plate number.
	 */
	public String getLicenseNumber() {
		return licenseNumber;
	}

	/**
	 * Updates the license number of the car.
	 * 
	 * @param licenseNumber The new license plate number to be set.
	 */
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	/**
	 * Retrieves the entry time of the car.
	 * 
	 * @return The entry time of the car when it was parked.
	 */
	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	/**
	 * Updates the entry time of the car.
	 * 
	 * @param entryTime The new entry time to be set.
	 */
	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	/**
	 * Returns a string representation of the car, displaying its license number
	 * and entry time.
	 * 
	 * @return A formatted string with the car details.
	 */
	@Override
	public String toString() {
		return "Car [License: " + licenseNumber + ", Entry Time: " + entryTime + "]";
	}
}
