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

The **Parking Lot Management System** provides users with various options to manage parking slots. Below is the menu of available actions:

1. **Park a Car**
   - This option allows users to park a car in the nearest available slot. It will automatically assign a slot to the car and update the availability status.

2. **Remove a Car**
   - Removes a car from a specified parking slot and updates its status to available. The slot will be free for future use.

3. **Display Slot Details**
   - This option shows the details of a specified parking slot, including whether it is available, reserved, or occupied by a car. It also displays the car's license number and entry time if occupied.

4. **Find Nearest Available Slot**
   - Searches for the nearest available parking slot. It will return the slot number of the first available slot it encounters in the parking lot.

5. **Show Parking Status**
   - Displays the current status of all parking slots. This includes whether each slot is available, occupied, or reserved.

6. **Show Slots Availability**
   - Lists all available, occupied, and reserved parking slots separately for better management.

7. **Show Parking Statistics**
   - Provides statistical information about the parking lot, including the total number of slots, number of occupied slots, reserved slots and available slots.

8. **Remove Cars Parked for Too Long**
   - Removes cars that have been parked for a specified number of hours. It checks the parking duration of each car and frees up the slot if the car exceeds the time limit.

9. **Reserve a Parking Slot**
   - Reserves a parking slot for future use. Once a slot is reserved, it will not be available for other cars until the reservation is cleared.

10. **Calculate Parking Fee**
    - This option calculates the parking fee based on the number of hours a car has been parked. The user needs to provide the hourly rate.

11. **Save & Exit**
    - Saves all changes made to the parking lot and exits the system. The parking lot status is saved to a file for future reference.

## **Usage**

- Select an option by entering the corresponding number (e.g., 1 to park a car, 2 to remove a car, etc.).
- The system will guide you through the process and display the updated status after each operation.

### Running Example
```
PARKING LOT MANAGEMENT
1. Park a Car
2. Remove a Car
3. Display Slot Details
4. Find Nearest Available Slot
5. Show Parking Status
6. Show Slots Availability
7. Show Parking Statistics
8. Remove Cars Parked for Too Long
9. Reserve a Parking Slot
10. Calculate Parking Fee
11. Save & Exit
Enter your choice:
```
