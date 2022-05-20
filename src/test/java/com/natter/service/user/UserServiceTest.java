package com.natter.service.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.natter.dto.ResponseDto;
import com.natter.enums.user.ErrorMessageUserEnum;
import com.natter.enums.user.SuccessMessageUserEnum;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserInfoRepository userInfoRepository;

  @Mock
  UserDatabaseService userDatabaseService;

  @Test
  public void whenValidId_FollowUser_ReturnSuccessMessage(){
    Optional<UserInfo> userOptional = Optional.of( new UserInfo("1", "test", "user1", "test_user1@gmail.com"));
    when(userInfoRepository.findById(anyString())).thenReturn(userOptional);
    ResponseDto responseDto = userService.followOrUnfollowUserById("123", "1", true);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageUserEnum.FOLLOWED_USER.getMessage(), responseDto.getUserMessages().get(SuccessMessageUserEnum.FOLLOWED_USER.getCode()))
    );

  }

  @Test
  public void whenValidId_UnFollowUser_ReturnSuccessMessage(){
    Optional<UserInfo> userOptional = Optional.of( new UserInfo("1", "test", "user1", "test_user1@gmail.com"));
    when(userInfoRepository.findById(anyString())).thenReturn(userOptional);
    ResponseDto responseDto = userService.followOrUnfollowUserById("123", "1", false);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getUserMessages().size()),
        () -> assertEquals(SuccessMessageUserEnum.UNFOLLOWED_USER.getMessage(), responseDto.getUserMessages().get(SuccessMessageUserEnum.UNFOLLOWED_USER.getCode()))
    );

  }

  @Test
  public void whenInValidId_SkipFollowUser_ReturnErrorMessage(){
    when(userInfoRepository.findById(anyString())).thenReturn(Optional.empty());
    ResponseDto responseDto = userService.followOrUnfollowUserById("123", "1", true);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageUserEnum.USER_NOT_FOUND.getMessage(), responseDto.getErrorMessages().get(ErrorMessageUserEnum.USER_NOT_FOUND.getCode()))
    );

  }

  @Test
  public void whenNullId_SkipFollowUser_ReturnErrorMessage(){
    ResponseDto responseDto = userService.followOrUnfollowUserById("123", null, true);
    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertEquals(1, responseDto.getErrorMessages().size()),
        () -> assertEquals(
            ErrorMessageUserEnum.USER_ID_NULL.getMessage(), responseDto.getErrorMessages().get(ErrorMessageUserEnum.USER_ID_NULL.getCode()))
    );

  }



}