package com.natter.service.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.natter.dao.UserDao;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserFollowersFollowingRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateFollowerServiceTest {

  @Mock
  UserDao userDao;

  @Mock
  UserFollowersFollowingRepository userFollowersFollowingRepository;

  @InjectMocks
  UpdateFollowerService updateFollowerService;

  UserFollowersFollowing baseUser = new UserFollowersFollowing("123", 5, 5);

  @Test
  public void whenUpdateFollowerCount_databaseErrorOccurs_throwDatabaseErrorException() {
    when(userFollowersFollowingRepository.save(any())).thenThrow(IllegalArgumentException.class);
    assertThrows(DatabaseErrorException.class,
        () -> updateFollowerService.updateFollowerCount(baseUser, true));
  }

  @Test
  public void whenUpdateFollowingCount_databaseErrorOccurs_throwDatabaseErrorException() {
    when(userFollowersFollowingRepository.save(any())).thenThrow(IllegalArgumentException.class);
    assertThrows(DatabaseErrorException.class,
        () -> updateFollowerService.updateFollowingCount(baseUser, true));
  }

  @Test
  public void whenUnfollowUser_unableToFindCurrentUser_databaseErrorOccurs_throwDatabaseErrorException() {
    doThrow(NoSuchElementException.class).when(userFollowersFollowingRepository)
        .findById(eq("123"));
    assertThrows(DatabaseErrorException.class,
        () -> updateFollowerService.unfollowUser("123", "124"));
  }

  @Test
  public void whenUnfollowUser_unableToFindUserToUnfollow_databaseErrorOccurs_throwDatabaseErrorException() {
    when(userFollowersFollowingRepository.findById(eq("123"))).thenReturn(
        Optional.of(new UserFollowersFollowing()));
    doThrow(NoSuchElementException.class).when(userFollowersFollowingRepository)
        .findById(eq("124"));
    assertThrows(DatabaseErrorException.class,
        () -> updateFollowerService.unfollowUser("123", "124"));
  }

  @Test
  public void whenUnfollowUser_success_noExceptionThrown() {
    when(userFollowersFollowingRepository.findById(eq("123"))).thenReturn(
        Optional.of(new UserFollowersFollowing()));
    when(userFollowersFollowingRepository.findById(eq("124"))).thenReturn(
        Optional.of(new UserFollowersFollowing()));
    assertDoesNotThrow(() -> updateFollowerService.unfollowUser("123", "124"));
  }

  @Test
  public void whenFollowUser_unableToFindCurrentUser_databaseErrorOccurs_throwDatabaseErrorException() {
    doThrow(NoSuchElementException.class).when(userFollowersFollowingRepository)
        .findById(eq("123"));
    assertThrows(DatabaseErrorException.class,
        () -> updateFollowerService.followUser("123", "124"));
  }

  @Test
  public void whenFollowUser_unableToFindUserToUnfollow_databaseErrorOccurs_throwDatabaseErrorException() {
    when(userFollowersFollowingRepository.findById(eq("123"))).thenReturn(
        Optional.of(new UserFollowersFollowing()));
    doThrow(NoSuchElementException.class).when(userFollowersFollowingRepository)
        .findById(eq("124"));
    assertThrows(DatabaseErrorException.class,
        () -> updateFollowerService.followUser("123", "124"));
  }

  @Test
  public void whenFollowUser_success_noExceptionThrown() {
    when(userFollowersFollowingRepository.findById(eq("123"))).thenReturn(
        Optional.of(new UserFollowersFollowing()));
    when(userFollowersFollowingRepository.findById(eq("124"))).thenReturn(
        Optional.of(new UserFollowersFollowing()));
    assertDoesNotThrow(() -> updateFollowerService.followUser("123", "124"));
  }

}
