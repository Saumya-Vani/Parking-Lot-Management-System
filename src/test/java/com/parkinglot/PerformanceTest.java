package com.parkinglot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PerformanceTest {

	private static ParkingManager manager;
	private static AVLTree avlTree;
	private static AVLNode root;
	private static final String FILE_PATH_10K = "src/test/resources/parking_lot_10k.xlsx";
	private static final String FILE_PATH_100K = "src/test/resources/parking_lot_100k.xlsx";

	@BeforeAll
	public static void setup() {
		manager = new ParkingManager();
		avlTree = new AVLTree();
		root = null;
		
		root = manager.loadParkingSlotsFromFile(FILE_PATH_10K, avlTree); 
		root = manager.loadParkingSlotsFromFile(FILE_PATH_100K, avlTree);
	}

	// Test insertion performance for 10k
	@Test
	public void testInsertionPerformance10K() {
		assertTimeout(Duration.ofSeconds(10), () -> {
			for (int i = 10; i <= 50000; i += 5) {
				root = avlTree.insert(root, i, null);
			}
		}, "Insertion of 10K slots took too long!");
	}

	// Test insertion performance for 100k
	@Test
	public void testInsertionPerformance100K() {
		assertTimeout(Duration.ofSeconds(30), () -> {
			for (int i = 10; i <= 500000; i += 5) {
				root = avlTree.insert(root, i, null);
			}
		}, "Insertion of 100K slots took too long!");
	}

	// Test search performance
	@Test
	public void testSearchPerformance() {
		root = avlTree.insert(root, 25000, new Car("CAR25000", LocalDateTime.now()));
		assertTimeout(Duration.ofMillis(500), () -> {
			AVLNode result = avlTree.search(root, 25000);
			assertNotNull(result, "Slot not found!");
			assertEquals(25000, result.getSlotNumber(), "Incorrect slot retrieved!");
		}, "Search took too long!");
	}

	// Test nearest available slot performance
	@Test
	public void testNearestAvailableSlotPerformance() {
		assertTimeout(Duration.ofMillis(500), () -> {
			int nearest = avlTree.findNearestAvailableSlot(root);
			assertTrue(nearest > 0, "No available slots found!");
		}, "Finding the nearest available slot took too long!");
	}

	// Test free slot performance for 10k
	@Test
	public void testFreeSlotPerformance10K() {
		assertTimeout(Duration.ofSeconds(10), () -> {
			for (int i = 10; i <= 50000; i += 5) {
				root = manager.freeSlot(root, i);
			}
		}, "Freeing 10K slots took too long!");
	}

	// Test free slot performance for 100k
	@Test
	public void testFreeSlotPerformance100K() {
		assertTimeout(Duration.ofSeconds(30), () -> {
			for (int i = 10; i <= 500000; i += 5) {
				root = manager.freeSlot(root, i);
			}
		}, "Freeing 100K slots took too long!");
	}

	// Test reservation performance for 10k
	@Test
	public void testReservationPerformance10K() {
		assertTimeout(Duration.ofSeconds(10), () -> {
			for (int i = 10; i <= 50000; i += 5) {
				root = manager.reserveSlot(root, i);
			}
		}, "Reserving 10K slots took too long!");
	}

	// Test reservation performance for 100k
	@Test
	public void testReservationPerformance100K() {
		assertTimeout(Duration.ofSeconds(30), () -> {
			for (int i = 10; i <= 500000; i += 5) {
				root = manager.reserveSlot(root, i);
			}
		}, "Reserving 100K slots took too long!");
	}

}