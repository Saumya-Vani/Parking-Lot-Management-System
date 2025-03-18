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
	private static AVLNode root_10k;
	private static AVLNode root_100k;
	private static final String FILE_PATH_10K = "src/test/resources/parking_lot_10k.xlsx";
	private static final String FILE_PATH_100K = "src/test/resources/parking_lot_100k.xlsx";

	@BeforeAll
	public static void setup() {
		manager = new ParkingManager();
		avlTree = new AVLTree();
		root_10k = null;
		root_100k = null;

		root_10k = manager.loadParkingSlotsFromFile(FILE_PATH_10K, avlTree);
		root_100k = manager.loadParkingSlotsFromFile(FILE_PATH_100K, avlTree);
	}

	// Test insertion performance for 10k
	@Test
	public void testInsertionPerformance10K() {
		assertTimeout(Duration.ofSeconds(10), () -> {
			for (int i = 10; i <= 50000; i += 5) {
				root_10k = avlTree.insert(root_10k, i, null);
			}
		}, "Insertion of 10K slots took too long!");
	}

	// Test insertion performance for 100k
	@Test
	public void testInsertionPerformance100K() {
		assertTimeout(Duration.ofSeconds(30), () -> {
			for (int i = 10; i <= 500000; i += 5) {
				root_100k = avlTree.insert(root_100k, i, null);
			}
		}, "Insertion of 100K slots took too long!");
	}

	// Test search performance for 10k
	@Test
	public void testSearchPerformance10K() {
		root_10k = avlTree.insert(root_10k, 25000, new Car("CAR25000", LocalDateTime.now()));
		assertTimeout(Duration.ofMillis(500), () -> {
			AVLNode result = avlTree.search(root_10k, 25000);
			assertNotNull(result, "Slot not found!");
			assertEquals(25000, result.getSlotNumber(), "Incorrect slot retrieved!");
		}, "Search for 10K took too long!");
	}

	// Test search performance for 100k
	@Test
	public void testSearchPerformance100K() {
		root_100k = avlTree.insert(root_100k, 25000, new Car("CAR25000", LocalDateTime.now()));
		assertTimeout(Duration.ofMillis(500), () -> {
			AVLNode result = avlTree.search(root_100k, 25000);
			assertNotNull(result, "Slot not found!");
			assertEquals(25000, result.getSlotNumber(), "Incorrect slot retrieved!");
		}, "Search for 100K took too long!");
	}

	// Test nearest available slot performance for 10k
	@Test
	public void testNearestAvailableSlotPerformance10K() {
		assertTimeout(Duration.ofMillis(500), () -> {
			int nearest = avlTree.findNearestAvailableSlot(root_10k);
			assertTrue(nearest > 0, "No available slots found in 10K!");
		}, "Finding the nearest available slot for 10K took too long!");
	}

	// Test nearest available slot performance for 100k
	@Test
	public void testNearestAvailableSlotPerformance100K() {
		assertTimeout(Duration.ofMillis(500), () -> {
			int nearest = avlTree.findNearestAvailableSlot(root_100k);
			assertTrue(nearest > 0, "No available slots found in 100K!");
		}, "Finding the nearest available slot for 100K took too long!");
	}

	// Test free slot performance for 10k
	@Test
	public void testFreeSlotPerformance10K() {
		assertTimeout(Duration.ofSeconds(10), () -> {
			for (int i = 10; i <= 50000; i += 5) {
				root_10k = manager.freeSlot(root_10k, i);
			}
		}, "Freeing 10K slots took too long!");
	}

	// Test free slot performance for 100k
	@Test
	public void testFreeSlotPerformance100K() {
		assertTimeout(Duration.ofSeconds(30), () -> {
			for (int i = 10; i <= 500000; i += 5) {
				root_100k = manager.freeSlot(root_100k, i);
			}
		}, "Freeing 100K slots took too long!");
	}

	// Test reservation performance for 10k
	@Test
	public void testReservationPerformance10K() {
		assertTimeout(Duration.ofSeconds(10), () -> {
			for (int i = 10; i <= 50000; i += 5) {
				root_10k = manager.reserveSlot(root_10k, i);
			}
		}, "Reserving 10K slots took too long!");
	}

	// Test reservation performance for 100k
	@Test
	public void testReservationPerformance100K() {
		assertTimeout(Duration.ofSeconds(30), () -> {
			for (int i = 10; i <= 500000; i += 5) {
				root_100k = manager.reserveSlot(root_100k, i);
			}
		}, "Reserving 100K slots took too long!");
	}

}