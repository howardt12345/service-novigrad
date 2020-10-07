package com.uottawa.servicenovigrad.user;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeAccountTest {
    @Test
    public void employeeAccountTest() {
        UserAccount account = new EmployeeAccount("test", "test@test.com", "uid123");
        assertEquals("test", account.getName());
        assertEquals("test@test.com", account.getEmail());
        assertEquals("employee", account.getRole());
        assertEquals("uid123", account.getUID());
    }
}