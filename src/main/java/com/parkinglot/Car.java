package com.parkinglot;

import java.time.LocalDateTime;

public class Car {

	private String licenseNumber;
	private LocalDateTime entryTime;

	public Car(String licenseNumber, LocalDateTime entryTime) {
		this.licenseNumber = licenseNumber;
		this.entryTime = entryTime;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	@Override
	public String toString() {
		return "Car [License: " + licenseNumber + ", Entry Time: " + entryTime + "]";
	}
}
