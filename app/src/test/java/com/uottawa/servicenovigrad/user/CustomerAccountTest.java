package com.uottawa.servicenovigrad.user;

import org.junit.Test;

import static org.junit.Assert.*;

public class CustomerAccountTest {
    @Test
    public void customerAccountTest() {
        UserAccount account = new CustomerAccount("test", "test@test.com", "uid123");
        assertEquals("test", account.getName());
        assertEquals("test@test.com", account.getEmail());
        assertEquals("customer", account.getRole());
        assertEquals("uid123", account.getUID());
    }
}