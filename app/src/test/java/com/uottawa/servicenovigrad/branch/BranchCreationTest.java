package com.uottawa.servicenovigrad.branch;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


public class BranchCreationTest {
    @Test
    public void BranchCreationTest(){
        ArrayList<String> services = new ArrayList<>(Arrays.asList("Driver's License'", "Health Card"));
        ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        Branch branch = new Branch("id", "Branch 6", "25 Peru Street", "8193286770",services, days ,9, 0, 21, 0, 8.5);
        assertEquals("id", branch.getId());
        assertEquals("Branch 6", branch.getName());
        assertEquals("25 Peru Street", branch.getAddress());
        assertEquals("8193286770", branch.getPhoneNumber());
        assertEquals(services, branch.getServices());
        assertEquals(days, branch.getOpenDays());
        assertEquals(9, branch.getOpeningHour());
        assertEquals(0, branch.getOpeningMinute());
        assertEquals(21, branch.getClosingHour());
        assertEquals(0, branch.getClosingMinute());
        assertEquals(8.5, branch.getRating(), 0);
    }
}