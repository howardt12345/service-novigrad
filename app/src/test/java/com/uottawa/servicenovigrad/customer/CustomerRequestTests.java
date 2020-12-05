package com.uottawa.servicenovigrad.customer;

import com.uottawa.servicenovigrad.branch.ServiceRequest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class CustomerRequestTests {
    @Test
    public void CustomerRequestTest(){


        ArrayList<String> info = new ArrayList<>(Arrays.asList("1st time", "testinfo", "Bob"));
        Date date = new Date(2020, 4, 25, 5, 0);
        ServiceRequest sr = new ServiceRequest("testID", "123456", "654321", "12345", "John", "Branch1", "Health Card", info, date, false, false);

        assertEquals("testID", sr.getId());
        assertEquals("123456", sr.getCustomerId());
        assertEquals("654321", sr.getBranchId());
        assertEquals("12345", sr.getServiceId());
        assertEquals("John", sr.getCustomerName());
        assertEquals("Branch1", sr.getBranchName());
        assertEquals("Health Card", sr.getServiceName());
        assertEquals(info, sr.getInfo());
        assertEquals(date, sr.getScheduledTime());
        assertEquals(false, sr.isApproved());
        assertEquals(false, sr.isResponded());
    }
    @Test
    public void TwoCustomerRequestsTest(){


        ArrayList<String> info = new ArrayList<>(Arrays.asList("1st time", "testinfo", "Bob"));
        Date date = new Date(2020, 4, 25, 5, 0);
        ServiceRequest sr = new ServiceRequest("testID", "123456", "654321", "12345", "John", "Branch1", "Health Card", info, date, false, false);

        ArrayList<String> info2 = new ArrayList<>(Arrays.asList("Canada", "testinfo2", "Sam"));
        Date date2 = new Date(2020, 4, 25, 6, 30);
        ServiceRequest sr2 = new ServiceRequest("customID", "123456", "654321", "123457", "John", "Branch1", "Driver's License", info2, date2, false, false);

        assertEquals("testID", sr.getId());
        assertEquals("123456", sr.getCustomerId());
        assertEquals("654321", sr.getBranchId());
        assertEquals("12345", sr.getServiceId());
        assertEquals("John", sr.getCustomerName());
        assertEquals("Branch1", sr.getBranchName());
        assertEquals("Health Card", sr.getServiceName());
        assertEquals(info, sr.getInfo());
        assertEquals(date, sr.getScheduledTime());
        assertEquals(false, sr.isApproved());
        assertEquals(false, sr.isResponded());
        assertEquals("customID", sr2.getId());
        assertEquals("123456", sr2.getCustomerId());
        assertEquals("654321", sr2.getBranchId());
        assertEquals("123457", sr2.getServiceId());
        assertEquals("John", sr2.getCustomerName());
        assertEquals("Branch1", sr2.getBranchName());
        assertEquals("Driver's License", sr2.getServiceName());
        assertEquals(info2, sr2.getInfo());
        assertEquals(date2, sr2.getScheduledTime());
        assertEquals(false, sr2.isApproved());
        assertEquals(false, sr2.isResponded());
    }
    @Test
    public void ChangingRequest(){

        ServiceRequest sr = new ServiceRequest();

        sr.setBranchId("5616");
        sr.setCustomerId("12345");
        sr.setServiceId("43134");
        assertEquals("5616", sr.getBranchId());
        assertEquals("12345", sr.getCustomerId());
        assertEquals("43134", sr.getServiceId());
    }

    @Test
    public void ChangingRequestMultiple(){

        ServiceRequest sr = new ServiceRequest();

        sr.setBranchId("5616");
        sr.setBranchId("5432");
        sr.setBranchId("9001");
        sr.setBranchId("4521");
        assertEquals("4521", sr.getBranchId());

        sr.setBranchId("9001");
        assertEquals("9001", sr.getBranchId());

    }
    @Test
    public void IdenticalRequestsTest(){

        ArrayList<String> info = new ArrayList<>(Arrays.asList("1st time", "testinfo", "Bob"));
        Date date = new Date(2020, 4, 25, 5, 0);
        ServiceRequest sr = new ServiceRequest("testID", "123456", "654321", "12345", "John", "Branch1", "Health Card", info, date, false, false);

        ServiceRequest sr2 = sr;

        sr2.setServiceId("6543221");
        sr2.setBranchId("123456");

        assertEquals("testID", sr2.getId());
        assertEquals("123456", sr2.getCustomerId());
        assertEquals("123456", sr.getBranchId());
        assertEquals("123456", sr2.getBranchId());
        assertEquals("6543221", sr.getServiceId());
        assertEquals("6543221", sr2.getServiceId());
        assertEquals("John", sr2.getCustomerName());
        assertEquals("Branch1", sr2.getBranchName());
        assertEquals("Health Card", sr2.getServiceName());
        assertEquals(info, sr2.getInfo());
        assertEquals(date, sr2.getScheduledTime());
        assertEquals(false, sr2.isApproved());
        assertEquals(false, sr2.isResponded());



    }

    @Test
    public void GetAllInfoTest(){

        ArrayList<String> info = new ArrayList<>(Arrays.asList("1st time", "testinfo", "Bob"));
        Date date = new Date(2020, 4, 25, 5, 0);
        ServiceRequest sr = new ServiceRequest("testID", "123456", "654321", "12345", "John", "Branch1", "Health Card", info, date, false, false);


        assertEquals("Branch Name: Branch1\nCustomer Name: John\nFor Service: Health Card\nScheduled Time: Tue May 25 05:00:00 EDT 3920\n\nInfo Provided: \n > 1st time\n > testinfo\n > Bob\n\nResponded: no", sr.getRequestInfo());

    }
    @Test
    public void GetSideInfoTest(){

        ArrayList<String> info = new ArrayList<>(Arrays.asList("1st time", "testinfo", "Bob"));
        Date date = new Date(2020, 4, 25, 5, 0);
        ServiceRequest sr = new ServiceRequest("testID", "123456", "654321", "12345", "John", "Branch1", "Health Card", info, date, false, false);

        assertEquals("To Branch1", sr.getCustomerSideTitle() );
        assertEquals("Request to Branch1 for Health Card on Tue May 25 05:00:00 EDT 3920",sr.getCustomerSideDesc());
        assertEquals( "From John", sr.getBranchSideTitle());
        assertEquals("Request by John for Health Card on Tue May 25 05:00:00 EDT 3920", sr.getBranchSideDesc());

    }


    @Test
    public void ApprovingRequests(){

        ArrayList<String> info = new ArrayList<>(Arrays.asList("1st time", "testinfo", "Bob"));
        Date date = new Date(2020, 4, 25, 5, 0);
        ServiceRequest sr = new ServiceRequest("testID", "123456", "654321", "12345", "John", "Branch1", "Health Card", info, date, true, true);

        //Assuming the branch responded and approved the request:
        assertEquals(true, sr.isApproved());
        assertEquals(true, sr.isResponded());

    }







}
