package com.uottawa.servicenovigrad.service;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;


public class ServiceTest {

    @Test
    public void ServiceTest() {
        ArrayList<String> forms = new ArrayList<>(Arrays.asList("Passport renewal form"));
        ArrayList<String> documents = new ArrayList<>(Arrays.asList("Birth Certificate", "Expired Passport", "Photo ID"));
        Service service = new Service("01", "Passport Renewal", "Service to allow residents to renew their passports", forms, documents, 75);
        assertEquals("01", service.getId());
        assertEquals("Passport Renewal", service.getName());
        assertEquals("Service to allow residents to renew their passports", service.getDesc());
        assertEquals(forms, service.getForms());
        assertEquals(documents, service.getDocuments());
        assertEquals(75, service.getPrice());

        service.setPrice(85);
        service.setId("00");
        service.setDesc("Service to allow residents to update their passports");
        service.setName("Passport update");
        assertEquals("00", service.getId());
        assertEquals(85, service.getPrice());
        assertEquals("Service to allow residents to update their passports", service.getDesc());

        assertEquals("Service to allow residents to update their passports\n" +
                "\nInformation required: \n" + " > Passport renewal form\n" + "\nDocuments required: \n"
                + " > Birth Certificate\n" + " > Expired Passport\n" + " > Photo ID\n"
                + "\nPrice: $85\n", service.getInfo());
    }
}
