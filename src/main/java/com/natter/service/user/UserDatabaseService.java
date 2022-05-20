package com.natter.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDatabaseService {

  private final UpdateFollowerService updateFollowerService;

  /**
   * Method to update relevant tables when following a user
   *
   * @param currentUserId  the id of the current user
   * @param userToFollowId the id of the user to follow
   */
  void updateFollowersForFollow(String currentUserId, String userToFollowId) {
    updateFollowerService.addUserToFollowerList(currentUserId, userToFollowId);
    updateFollowerService.addUserToFollowingList(currentUserId, userToFollowId);

  }

  /**
   * Method to update tables when unfollowing a user
   *
   * @param currentUserId    the id of the current user
   * @param userToUnfollowId the id of the user that is unfollowed
   */
  void updateFollowersForUnfollow(String currentUserId, String userToUnfollowId) {
    updateFollowerService.removeUserFromFollowerList(currentUserId, userToUnfollowId);
    updateFollowerService.removeUserFromFollowingList(currentUserId, userToUnfollowId);
  }
}
