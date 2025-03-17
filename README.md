# Parking Lot Management System

## Project Overview
The Parking Lot Management System is designed to manage parking slots efficiently. It uses an AVL Tree to store and manage parking slots, allowing for operations like parking a car, removing a car, finding available slots, reserving slots, and releasing old cars. The system is optimized for large datasets, capable of handling up to 100,000 parking slots.

## Key Features
- **Parking Slot Management**: Insert, remove, and update slots based on car availability and reservations.
- **Parking Statistics**: Track and display available, occupied, and reserved slots.
- **Performance Testing**: Supports performance testing on large datasets (up to 100k slots).
- **Fee Calculation**: Calculates parking fees based on hourly rates.
- **Excel Integration**: Load and save parking data to Excel files.
- **CLI Interface**: User-friendly command-line interface to interact with the parking lot system.

## Project Structure

```
ParkingLotManagement
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── parkinglot/
│   │   │           ├── AVLNode.java
│   │   │           ├── AVLTree.java
│   │   │           ├── Car.java
│   │   │           ├── ParkingLot.java
│   │   │           ├── ParkingManager.java
│   │   │           └── ParkingSlot.java
│   │   └── resources/
│   │       └── parking_lot_data.xlsx
│   ├── test/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── parkinglot/
│   │   │           └── PerformanceTest.java
│   │   └── resources/
│   │       ├── parking_lot_100k.xlsx
│   │       └── parking_lot_10k.xlsx
├── target/
├── pom.xml
```

## CLI provided:

- **Park a Car**: Assign a parking slot by entering a car license number.
- **Remove a Car**: Free up a parking slot.
- **Display Slot Details**: View detailed information about a specific slot.
- **Find Nearest Available Slot**: Quickly find the nearest available slot.
- **View Parking Status**: Get a summary of the parking lot's status.
- **Calculate Parking Fee**: Calculate the fee for a parked car based on the hourly rate.
