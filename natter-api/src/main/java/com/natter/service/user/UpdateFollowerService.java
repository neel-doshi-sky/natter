package com.natter.service.user;

import com.natter.dao.UserDao;
import com.natter.exception.DatabaseErrorException;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserFollowersFollowingRepository;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateFollowerService {

  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  private final UserDao userDao;

  /**
   * Method to increment or decrement the follower count for a given user
   *
   * @param user      the user to update
   * @param increment the operation, if true then will increment, else decrement
   */
  public void updateFollowerCount(@NonNull final UserFollowersFollowing user,
                                  final boolean increment) throws DatabaseErrorException {
    int newFollowerCount = increment ? user.getFollowers() + 1 : user.getFollowers() - 1;
    user.setFollowers(newFollowerCount);
    try {
      userFollowersFollowingRepository.save(user);
    } catch (IllegalArgumentException e) {
      log.error("error updating follower count for user id: " + user.getId() + " increment: " +
          increment + " with exception " + e);
      throw new DatabaseErrorException(e.getMessage());
    }


  }

  /**
   * Method to increment or decrement the following count for a given user
   *
   * @param user      the user to update
   * @param increment the operation, if true then will increment, else decrement
   */
  public void updateFollowingCount(@NonNull final UserFollowersFollowing user,
                                   final boolean increment)
      throws DatabaseErrorException {
    int newFollowingCount = increment ? user.getFollowing() + 1 : user.getFollowing() - 1;
    user.setFollowing(newFollowingCount);
    try {
      userFollowersFollowingRepository.save(user);
    } catch (IllegalArgumentException e) {
      log.error("error updating following count for user id: " + user.getId() + " increment: " +
          increment + " with exception " + e);
      throw new DatabaseErrorException(e.getMessage());
    }

  }

  /**
   * Method to update relevant tables when following a user
   *
   * @param currentUserId  the id of the current user
   * @param userToFollowId the id of the user to follow
   */
  void followUser(@NonNull final String currentUserId, @NonNull final String userToFollowId)
      throws DatabaseErrorException {
    try {
      UserFollowersFollowing currentUser =
          userFollowersFollowingRepository.findById(currentUserId).orElseThrow();
      UserFollowersFollowing userToFollow =
          userFollowersFollowingRepository.findById(userToFollowId).orElseThrow();
      userDao.addUserToFollowerList(currentUserId, userToFollowId);
      updateFollowerCount(userToFollow, true);
      userDao.addUserToFollowingList(currentUserId, userToFollowId);
      updateFollowingCount(currentUser, true);
    } catch (NoSuchElementException e) {
      log.error("unable to find user with error: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }

  }

  /**
   * Method to update tables when unfollowing a user
   *
   * @param currentUserId    the id of the current user
   * @param userToUnfollowId the id of the user that is unfollowed
   */
  void unfollowUser(@NonNull final String currentUserId, @NonNull final String userToUnfollowId)
      throws DatabaseErrorException {
    try {
      UserFollowersFollowing currentUser =
          userFollowersFollowingRepository.findById(currentUserId).orElseThrow();
      UserFollowersFollowing userToUnfollow =
          userFollowersFollowingRepository.findById(userToUnfollowId).orElseThrow();
      userDao.removeUserFromFollowerList(currentUserId, userToUnfollowId);
      updateFollowerCount(userToUnfollow, false);
      userDao.removeUserFromFollowingList(currentUserId, userToUnfollowId);
      updateFollowingCount(currentUser, false);
    } catch (NoSuchElementException e) {
      log.error("unable to find user with error: " + e);
      throw new DatabaseErrorException(e.getMessage());
    }

  }


}
