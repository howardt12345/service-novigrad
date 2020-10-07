package com.uottawa.servicenovigrad.user;

import org.junit.Test;

import static org.junit.Assert.*;

public class AdminAccountTest {
    @Test
    public void adminAccountTest() {
        UserAccount account = new AdminAccount("uid123");
        assertEquals("admin", account.getName());
        assertEquals("admin", account.getEmail());
        assertEquals("admin", account.getRole());
        assertEquals("uid123", account.getUID());
    }
}