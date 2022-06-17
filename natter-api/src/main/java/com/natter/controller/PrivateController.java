package com.natter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrivateController {

  @GetMapping("/private/status")
  public ResponseEntity<String> status(){
    return new ResponseEntity<>("ok", HttpStatus.OK);
  }

}
