package com.natter.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class PrivateControllerTest {

  PrivateController privateController = new PrivateController();

  @Test
  public void testStatusEndpointReturnsOk(){
    ResponseEntity<String> response = privateController.status();
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals("ok", response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue())
    );
  }
}
