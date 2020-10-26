package com.uottawa.servicenovigrad.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

public class UserControllerTest {

    FirebaseFirestore mockFireStore;
    FirebaseAuth mockAuth;

    @Before
    public void setup() {
        mockFireStore = Mockito.mock(FirebaseFirestore.class);
        mockAuth = Mockito.mock(FirebaseAuth.class);
    }

    @Test
    public void getUserAccount() {
        UserAccount account = new AdminAccount("uid123");
        UserController.initialize(account, mockFireStore, mockAuth);
        assertEquals(account, UserController.getInstance().getUserAccount());
    }

    @Test
    public void initialize() {
        UserAccount account = new AdminAccount("uid123");
        UserController.initialize(account, mockFireStore, mockAuth);
        assertEquals(account, UserController.getInstance().getUserAccount());
    }

    @Test
    public void getInstance() {
        UserAccount account = new AdminAccount("uid123");
        UserController.initialize(account, mockFireStore, mockAuth);
        assertNotEquals(null, UserController.getInstance());
    }
}