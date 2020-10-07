package com.uottawa.servicenovigrad.user;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserControllerTest {

    @Test
    public void getUserAccount() {
        UserAccount account = new AdminAccount("uid123");
        UserController.initialize(account);
        assertEquals(account, UserController.getInstance().getUserAccount());
    }

    @Test
    public void initialize() {
        UserAccount account = new AdminAccount("uid123");
        UserController.initialize(account);
        assertEquals(account, UserController.getInstance().getUserAccount());
    }

    @Test
    public void getInstance() {
        UserAccount account = new AdminAccount("uid123");
        UserController.initialize(account);
        assertNotEquals(null, UserController.getInstance());
    }
}