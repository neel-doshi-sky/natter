package com.natter.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.dto.CreateResponseDto;
import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.model.natter.NatterByAuthor;
import com.natter.model.natter.NatterById;
import com.natter.model.natter.NatterCreateRequest;
import com.natter.service.AuthService;
import com.natter.service.natter.NatterService;
import com.natter.service.natter.NatterServiceTestHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
public class NatterControllerTest {

  @Mock
  NatterService natterService;

  @Mock
  AuthService authService;

  @InjectMocks
  NatterController natterController;

  NatterServiceTestHelper natterServiceTestHelper = new NatterServiceTestHelper();

  OAuth2User
      oAuth2User = new DefaultOAuth2User(new ArrayList<>(), Map.of("sub","115826771724477311086", "name", "Neel Doshi"), "name");


  @Test
  public void whenListNattersForUserId_validUserIdPassed_returnNattersBelongingToThatUser(){
    List<NatterByAuthor> listOfNatters = natterServiceTestHelper.getListOfNatters();
    ResponseListDto<NatterByAuthor> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(listOfNatters);
    serviceResponse.setStatus(HttpStatus.OK);
    when(natterService.getNattersForUser(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<NatterByAuthor>> response = natterController.listNattersForUserId(oAuth2User, "123");
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(listOfNatters.size(), response.getBody().getList().size())
    );
  }

  @Test
  public void whenListNattersForUserId_noUserIdPassed_returnNattersBelongingToOauthUser(){
    List<NatterByAuthor> listOfNatters = natterServiceTestHelper.getListOfNatters();
    ResponseListDto<NatterByAuthor> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(listOfNatters);
    serviceResponse.setStatus(HttpStatus.OK);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(oAuth2User.getAttribute("sub"));
    when(natterService.getNattersForUser(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<NatterByAuthor>> response = natterController.listNattersForUserId(oAuth2User, null);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(listOfNatters.size(), response.getBody().getList().size())
    );
    verify(authService).getUserIdFromAuth(oAuth2User);
    verify(natterService).getNattersForUser(oAuth2User.getAttribute("sub"));
  }

  @Test
  public void whenListAllNatters_returnAllNatters(){
    List<NatterByAuthor> listOfNatters = natterServiceTestHelper.getListOfNatters();
    ResponseListDto<NatterByAuthor> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(listOfNatters);
    serviceResponse.setStatus(HttpStatus.OK);
    when(natterService.getAllNatters()).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<NatterByAuthor>> response = natterController.listAllNatters(oAuth2User);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(listOfNatters.size(), response.getBody().getList().size())
    );
  }

  @Test
  public void whenCreateNatter_validBodyPassed_returnSuccess(){
    NatterCreateRequest natterCreateRequest = new NatterCreateRequest();
    CreateResponseDto<NatterById> serviceResponse = new CreateResponseDto<>();
    serviceResponse.setCreated(new NatterById());
    serviceResponse.setStatus(HttpStatus.OK);
    when(natterService.create(natterCreateRequest, oAuth2User)).thenReturn(serviceResponse);
    ResponseEntity<CreateResponseDto<NatterById>> response = natterController.create(oAuth2User, natterCreateRequest);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody()),
        () -> assertNotNull(response.getBody().getCreated())
    );
  }

  @Test
  public void whenCreateNatter_nullBodyPassed_returnBadRequest(){
    CreateResponseDto<NatterById> serviceResponse = new CreateResponseDto<>();
    serviceResponse.setStatus(HttpStatus.BAD_REQUEST);
    when(natterService.create(any(), any())).thenReturn(serviceResponse);
    ResponseEntity<CreateResponseDto<NatterById>> response = natterController.create(oAuth2User, null);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(400, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody()),
        () -> assertNull(response.getBody().getCreated())
    );
  }

  @Test
  public void whenCreateNatter_inValidBodyPassed_returnBadRequest(){
    CreateResponseDto<NatterById> serviceResponse = new CreateResponseDto<>();
    serviceResponse.setStatus(HttpStatus.BAD_REQUEST);
    when(natterService.create(any(), any())).thenReturn(serviceResponse);
    ResponseEntity<CreateResponseDto<NatterById>> response = natterController.create(oAuth2User, new NatterCreateRequest());
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(400, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody()),
        () -> assertNull(response.getBody().getCreated())
    );
  }

  @Test
  public void whenDeleteNatter_validIdPassed_returnSuccess(){
    ResponseDto serviceResponse = new ResponseDto();
    serviceResponse.setStatus(HttpStatus.OK);
    when(natterService.delete(any(), any())).thenReturn(serviceResponse);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(oAuth2User.getAttribute("sub"));
    ResponseEntity<ResponseDto> response = natterController.delete(oAuth2User, "123");
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void whenDeleteNatter_unauthorisedIdPassed_returnForbidden(){
    ResponseDto serviceResponse = new ResponseDto();
    serviceResponse.setStatus(HttpStatus.FORBIDDEN);
    when(natterService.delete(any(), any())).thenReturn(serviceResponse);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(oAuth2User.getAttribute("sub"));
    ResponseEntity<ResponseDto> response = natterController.delete(oAuth2User, "123");
    assertEquals(403, response.getStatusCodeValue());
  }

  @Test
  public void whenDeleteNatter_IdThatDoesNotExistPassed_returnSuccess(){
    ResponseDto serviceResponse = new ResponseDto();
    serviceResponse.setStatus(HttpStatus.OK);
    when(natterService.delete(any(), any())).thenReturn(serviceResponse);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(oAuth2User.getAttribute("sub"));
    ResponseEntity<ResponseDto> response = natterController.delete(oAuth2User, "123");
    assertEquals(200, response.getStatusCodeValue());
  }

}
