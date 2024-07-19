package school.faang.user_service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListPartitionerTest {

    private ListPartitioner listPartitioner;

    @BeforeEach
    public void setUp() {
        listPartitioner = new ListPartitioner();
    }

    @Test
    public void testPartitionList_evenDivision() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<List<Integer>> partitions = listPartitioner.partitionList(numbers, 5);

        assertEquals(5, partitions.size(), "Expected 5 partitions");
        assertEquals(partitions.get(0), List.of(1, 2), "First partition should contain [1, 2]");
        assertEquals(partitions.get(1), List.of(3, 4), "Second partition should contain [3, 4]");
        assertEquals(partitions.get(2), List.of(5, 6), "Third partition should contain [5, 6]");
        assertEquals(partitions.get(3), List.of(7, 8), "Fourth partition should contain [7, 8]");
        assertEquals(partitions.get(4), List.of(9, 10), "Fifth partition should contain [9, 10]");
    }

    @Test
    public void testPartitionList_unevenDivision() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7);

        List<List<Integer>> partitions = listPartitioner.partitionList(numbers, 3);

        assertEquals(3, partitions.size(), "Expected 3 partitions");
        assertEquals(partitions.get(0), List.of(1, 2, 3), "First partition should contain [1, 2, 3]");
        assertEquals(partitions.get(1), List.of(4, 5, 6), "Second partition should contain [4, 5, 6]");
        assertEquals(partitions.get(2), List.of(7), "Third partition should contain [7]");
    }

    @Test
    public void testPartitionList_emptyList() {
        List<Integer> numbers = new ArrayList<>();

        List<List<Integer>> partitions = listPartitioner.partitionList(numbers, 3);

        assertTrue(partitions.isEmpty(), "Expected empty list of partitions");
    }

    @Test
    public void testPartitionList_morePartitionsThanElements() {
        List<Integer> numbers = List.of(1, 2, 3);

        List<List<Integer>> partitions = listPartitioner.partitionList(numbers, 5);

        assertEquals(3, partitions.size(), "Expected 3 partitions");
        assertEquals(partitions.get(0), List.of(1), "First partition should contain [1]");
        assertEquals(partitions.get(1), List.of(2), "Second partition should contain [2]");
        assertEquals(partitions.get(2), List.of(3), "Third partition should contain [3]");
    }

    @Test
    public void testPartitionList_negativePartitions() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        List<List<Integer>> partitions = listPartitioner.partitionList(numbers, -1);

        assertEquals(1, partitions.size(), "Expected 1 partition");
        assertEquals(partitions.get(0), numbers, "Partition should contain all elements");
    }

    @Test
    public void testPartitionList_zeroPartitions() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<List<Integer>> partitions = listPartitioner.partitionList(numbers, 0);

        assertEquals(1, partitions.size(), "Expected 1 partition");
        assertEquals(partitions.get(0), numbers, "Partition should contain all elements");
    }
}
