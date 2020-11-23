package com.uottawa.servicenovigrad.branch;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BranchEditTest {

    @Test
    public void BranchEditTest(){
        ArrayList<String> services = new ArrayList<>(Arrays.asList("Driver's License'", "Health Card"));
        ArrayList<String> days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        Branch branch = new Branch("id", "Branch 6", "25 Peru Street", "8193286770",services, days ,9, 0, 21, 0, 8.5);

        branch.setAddress("96 Inline Street");
        branch.setName("Branch 7");
        branch.setPhoneNumber("8190002345");
        services.add("Photo ID");
        branch.setServices(services);
        days.add("Sunday");
        branch.setOpenDays(days);
        branch.setOpeningHour(8);
        branch.setOpeningMinute(30);
        branch.setClosingHour(22);
        branch.setClosingMinute(30);


        assertEquals("96 Inline Street", branch.getAddress());
        assertEquals("Branch 7", branch.getName());
        assertEquals("8190002345", branch.getPhoneNumber());
        assertEquals(services, branch.getServices());
        assertEquals(days, branch.getOpenDays());
        assertEquals(8, branch.getOpeningHour());
        assertEquals(30, branch.getOpeningMinute());
        assertEquals(22, branch.getClosingHour());
        assertEquals(30, branch.getClosingMinute());
    }
}
