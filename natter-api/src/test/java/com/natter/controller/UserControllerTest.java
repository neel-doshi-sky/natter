package com.natter.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.natter.dto.GetResponseDto;
import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.model.template.UserToDisplay;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.service.AuthService;
import com.natter.service.user.UserService;
import java.util.ArrayList;
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
public class UserControllerTest {

  @Mock
  UserService userService;

  @Mock
  AuthService authService;

  @InjectMocks
  UserController userController;

  OAuth2User
      oAuth2User = new DefaultOAuth2User(new ArrayList<>(),
      Map.of("sub", "115826771724477311086", "name", "Neel Doshi"), "name");

  String authId = oAuth2User.getAttribute("sub");


  @Test
  public void whenFollowUserById_validIdPassed_returnSuccess() {
    String id = "123";
    ResponseDto responseDto = new ResponseDto();
    responseDto.setStatus(HttpStatus.OK);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(authId);
    when(userService.followOrUnfollowUserById(authId, id, true)).thenReturn(responseDto);
    ResponseEntity<ResponseDto> response = userController.followUserById(oAuth2User, id);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenFollowUserById_invalidIdPassed_returnNotFound() {
    String id = "123";
    ResponseDto responseDto = new ResponseDto();
    responseDto.setStatus(HttpStatus.NOT_FOUND);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(authId);
    when(userService.followOrUnfollowUserById(authId, id, true)).thenReturn(responseDto);
    ResponseEntity<ResponseDto> response = userController.followUserById(oAuth2User, id);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(404, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenFollowUserById_nullIdPassed_returnNotFound() {

    ResponseDto responseDto = new ResponseDto();
    responseDto.setStatus(HttpStatus.BAD_REQUEST);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(authId);
    when(userService.followOrUnfollowUserById(authId, null, true)).thenReturn(responseDto);
    ResponseEntity<ResponseDto> response = userController.followUserById(oAuth2User, null);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(400, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenUnfollowUserById_validIdPassed_returnSuccess() {
    String id = "123";
    ResponseDto responseDto = new ResponseDto();
    responseDto.setStatus(HttpStatus.OK);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(authId);
    when(userService.followOrUnfollowUserById(authId, id, false)).thenReturn(responseDto);
    ResponseEntity<ResponseDto> response = userController.unfollowUserById(oAuth2User, id);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenUnfollowUserById_invalidIdPassed_returnNotFound() {
    String id = "123";
    ResponseDto responseDto = new ResponseDto();
    responseDto.setStatus(HttpStatus.NOT_FOUND);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(authId);
    when(userService.followOrUnfollowUserById(authId, id, false)).thenReturn(responseDto);
    ResponseEntity<ResponseDto> response = userController.unfollowUserById(oAuth2User, id);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(404, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenUnfollowUserById_nullIdPassed_returnNotFound() {

    ResponseDto responseDto = new ResponseDto();
    responseDto.setStatus(HttpStatus.BAD_REQUEST);
    when(authService.getUserIdFromAuth(oAuth2User)).thenReturn(authId);
    when(userService.followOrUnfollowUserById(authId, null, false)).thenReturn(responseDto);
    ResponseEntity<ResponseDto> response = userController.unfollowUserById(oAuth2User, null);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(400, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenGetFollowersForUserId_idIsValid_returnSuccess() {
    ResponseListDto<UserFollowersFollowing> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(List.of(new UserFollowersFollowing(), new UserFollowersFollowing(),
        new UserFollowersFollowing()));
    serviceResponse.setStatus(HttpStatus.OK);
    when(userService.getFollowersForUserId(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<UserFollowersFollowing>> response =
        userController.getFollowersForUserId(oAuth2User, "123");

    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(serviceResponse.getList().size(), response.getBody().getList().size())
    );
  }

  @Test
  public void whenGetFollowersForUserId_idIsInvalid_returnErrorNotFound() {
    ResponseListDto<UserFollowersFollowing> serviceResponse = new ResponseListDto<>();
    serviceResponse.setStatus(HttpStatus.NOT_FOUND);
    when(userService.getFollowersForUserId(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<UserFollowersFollowing>> response =
        userController.getFollowersForUserId(oAuth2User, "123");

    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(404, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenGetFollowersForUserId_idIsNull_returnSuccess_verifyAuthIdUsed() {
    ResponseListDto<UserFollowersFollowing> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(List.of(new UserFollowersFollowing(), new UserFollowersFollowing(),
        new UserFollowersFollowing()));
    serviceResponse.setStatus(HttpStatus.OK);
    when(authService.getUserIdFromAuth(any())).thenReturn(authId);
    when(userService.getFollowersForUserId(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<UserFollowersFollowing>> response =
        userController.getFollowersForUserId(oAuth2User, null);

    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(serviceResponse.getList().size(), response.getBody().getList().size())
    );
    verify(authService).getUserIdFromAuth(any());
  }

  @Test
  public void whenGetFollowingForUserId_idIsValid_returnSuccess() {
    ResponseListDto<UserFollowersFollowing> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(List.of(new UserFollowersFollowing(), new UserFollowersFollowing(),
        new UserFollowersFollowing()));
    serviceResponse.setStatus(HttpStatus.OK);
    when(userService.getFollowingForUserId(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<UserFollowersFollowing>> response =
        userController.getFollowingForUserId(oAuth2User, "123");

    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(serviceResponse.getList().size(), response.getBody().getList().size())
    );
  }

  @Test
  public void whenGetFollowingForUserId_idIsInvalid_returnErrorNotFound() {
    ResponseListDto<UserFollowersFollowing> serviceResponse = new ResponseListDto<>();
    serviceResponse.setStatus(HttpStatus.NOT_FOUND);
    when(userService.getFollowingForUserId(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<UserFollowersFollowing>> response =
        userController.getFollowingForUserId(oAuth2User, "123");

    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(404, response.getStatusCodeValue())
    );
  }

  @Test
  public void whenGetFollowingForUserId_idIsNull_returnSuccess_verifyAuthIdUsed() {
    ResponseListDto<UserFollowersFollowing> serviceResponse = new ResponseListDto<>();
    serviceResponse.setList(List.of(new UserFollowersFollowing(), new UserFollowersFollowing(),
        new UserFollowersFollowing()));
    serviceResponse.setStatus(HttpStatus.OK);
    when(authService.getUserIdFromAuth(any())).thenReturn(authId);
    when(userService.getFollowingForUserId(any())).thenReturn(serviceResponse);
    ResponseEntity<ResponseListDto<UserFollowersFollowing>> response =
        userController.getFollowingForUserId(oAuth2User, null);

    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getList()),
        () -> assertEquals(serviceResponse.getList().size(), response.getBody().getList().size())
    );
    verify(authService).getUserIdFromAuth(any());
  }

  @Test
  public void whenGetUserById_validIdPassed_returnUserWithSuccess() {
    UserToDisplay userToDisplay = new UserToDisplay("123");
    GetResponseDto<UserToDisplay> serviceResponse = new GetResponseDto<>();
    serviceResponse.setStatus(HttpStatus.OK);
    serviceResponse.setResponseObject(userToDisplay);
    when(userService.getUserById(any(), any())).thenReturn(serviceResponse);
    ResponseEntity<GetResponseDto<UserToDisplay>> response =
        userController.getUserById(oAuth2User, "123");
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getResponseObject()),
        () -> assertEquals("123", response.getBody().getResponseObject().getId())
    );

  }

  @Test
  public void whenGetUserById_nullIdPassed_UseAuthId_returnUserWithSuccess() {
    UserToDisplay userToDisplay = new UserToDisplay(authId);
    GetResponseDto<UserToDisplay> serviceResponse = new GetResponseDto<>();
    serviceResponse.setStatus(HttpStatus.OK);
    serviceResponse.setResponseObject(userToDisplay);
    when(authService.getUserIdFromAuth(any())).thenReturn(authId);
    when(userService.getUserById(any(), any())).thenReturn(serviceResponse);
    ResponseEntity<GetResponseDto<UserToDisplay>> response =
        userController.getUserById(oAuth2User, null);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(200, response.getStatusCodeValue()),
        () -> assertNotNull(response.getBody().getResponseObject()),
        () -> assertEquals(authId, response.getBody().getResponseObject().getId())
    );
    verify(authService).getUserIdFromAuth(any());
  }

  @Test
  public void whenGetUserById_invalidIdPassed_returnError() {
    GetResponseDto<UserToDisplay> serviceResponse = new GetResponseDto<>();
    serviceResponse.setStatus(HttpStatus.NOT_FOUND);
    when(userService.getUserById(any(), any())).thenReturn(serviceResponse);
    ResponseEntity<GetResponseDto<UserToDisplay>> response =
        userController.getUserById(oAuth2User, "3434");
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.getBody()),
        () -> assertEquals(404, response.getStatusCodeValue())
    );
  }
}
