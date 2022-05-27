package com.natter.service.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.natter.dto.ResponseDto;
import com.natter.dto.ResponseListDto;
import com.natter.enums.user.ErrorMessageUserEnum;
import com.natter.enums.user.SuccessMessageUserEnum;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserFollowersFollowingRepository;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  @Mock
  UserRepository userRepository;

  @Mock
  UserFollowersFollowingRepository userFollowersFollowingRepository;

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

  @Test
  public void whenGetFollowers_validIdPassed_returnFollowers_ToUser(){
    List<UserFollowersFollowing> list = List.of(new UserFollowersFollowing("123", "Test", "1", 0, 0),
        new UserFollowersFollowing("1234", "Test", "3", 2, 6),
        new UserFollowersFollowing("1235", "Test", "5", 23, 54),
        new UserFollowersFollowing("1236", "Test", "6", 46, 654),
        new UserFollowersFollowing("1237", "Test", "7", 244, 4));
    when(userRepository.findById(anyString())).thenReturn(Optional.of(new User("123", "Test", "1", "test", Set.of("1", "2", "3", "4", "5"), new HashSet<>(), LocalDateTime.now())));
    when(userFollowersFollowingRepository.findAllById(anySet())).thenReturn(list);
    ResponseListDto<UserFollowersFollowing> responseListDto = userService.getFollowersForUserId("123");
    assertAll(
        () -> assertNotNull(responseListDto),
        () -> assertEquals(0, responseListDto.getErrorMessages().size()),
        () -> assertEquals(5, responseListDto.getList().size())
    );
  }

  @Test
  public void whenGetFollowers_idDoesNotExist_returnError(){
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    ResponseListDto<UserFollowersFollowing> responseListDto = userService.getFollowersForUserId("123");
    assertAll(
        () -> assertNotNull(responseListDto),
        () -> assertEquals(1, responseListDto.getErrorMessages().size()),
        () -> assertEquals(0, responseListDto.getList().size())
    );
  }

  @Test
  public void whenGetFollowing_validIdPassed_returnFollowing_ToUser(){
    List<UserFollowersFollowing> list = List.of(new UserFollowersFollowing("123", "Test", "1", 0, 0),
        new UserFollowersFollowing("1234", "Test", "3", 2, 6),
        new UserFollowersFollowing("1235", "Test", "5", 23, 54),
        new UserFollowersFollowing("1236", "Test", "6", 46, 654),
        new UserFollowersFollowing("1237", "Test", "7", 244, 4));
    when(userRepository.findById(anyString())).thenReturn(Optional.of(new User("123", "Test", "1", "test", new HashSet<>(), Set.of("1", "2", "3", "4", "5"), LocalDateTime.now())));
    when(userFollowersFollowingRepository.findAllById(anySet())).thenReturn(list);
    ResponseListDto<UserFollowersFollowing> responseListDto = userService.getFollowingForUserId("123");
    assertAll(
        () -> assertNotNull(responseListDto),
        () -> assertEquals(0, responseListDto.getErrorMessages().size()),
        () -> assertEquals(5, responseListDto.getList().size())
    );
  }

  @Test
  public void whenGetFollowing_idDoesNotExist_returnError(){
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());
    ResponseListDto<UserFollowersFollowing> responseListDto = userService.getFollowingForUserId("123");
    assertAll(
        () -> assertNotNull(responseListDto),
        () -> assertEquals(1, responseListDto.getErrorMessages().size()),
        () -> assertEquals(0, responseListDto.getList().size())
    );
  }


}