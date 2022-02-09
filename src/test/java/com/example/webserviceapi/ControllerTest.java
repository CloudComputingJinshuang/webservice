package com.example.webserviceapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    Controller testController;
    String expectedString;

    @BeforeEach
    void setUp() {
        testController = new Controller();
        expectedString = "";
    }
    @Test
    void testingAPI() {
        assertEquals(expectedString,testController.testingAPI());
    }
}