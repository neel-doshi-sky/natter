package com.natter.controller;

import com.natter.service.AuthService;
import com.natter.service.natter.NatterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NatterControllerTest {

  @Mock
  NatterService natterService;

  @Mock
  AuthService authService;

  @InjectMocks
  NatterController natterController;


}
