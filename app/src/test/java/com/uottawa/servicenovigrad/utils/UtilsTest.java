package com.uottawa.servicenovigrad.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void isEmailValid() {
        assertEquals(true, Utils.isEmailValid("test@test.com"));
        assertEquals(false, Utils.isEmailValid("notanemail.com"));
        assertEquals(false, Utils.isEmailValid("alsonotanemail#com"));
    }
}