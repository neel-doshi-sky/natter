package com.natter.service.user;

import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserFollowersFollowingRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDatabaseService {

  private final UpdateFollowerService updateFollowerService;

  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  /**
   * Method to update relevant tables when following a user
   *
   * @param currentUserId  the id of the current user
   * @param userToFollowId the id of the user to follow
   */
  void updateFollowersForFollow(String currentUserId, String userToFollowId) throws
      NoSuchElementException {
    UserFollowersFollowing currentUser = userFollowersFollowingRepository.findById(currentUserId).orElseThrow();
    UserFollowersFollowing userToFollow = userFollowersFollowingRepository.findById(userToFollowId).orElseThrow();
    updateFollowerService.addUserToFollowerList(currentUserId, userToFollowId);
    updateFollowerService.updateFollowerCount(userToFollow, true);
    updateFollowerService.addUserToFollowingList(currentUserId, userToFollowId);
    updateFollowerService.updateFollowingCount(currentUser, true);

  }

  /**
   * Method to update tables when unfollowing a user
   *
   * @param currentUserId    the id of the current user
   * @param userToUnfollowId the id of the user that is unfollowed
   */
  void updateFollowersForUnfollow(String currentUserId, String userToUnfollowId) {
    UserFollowersFollowing currentUser = userFollowersFollowingRepository.findById(currentUserId).orElseThrow();
    UserFollowersFollowing userToUnfollow = userFollowersFollowingRepository.findById(userToUnfollowId).orElseThrow();
    updateFollowerService.removeUserFromFollowerList(currentUserId, userToUnfollowId);
    updateFollowerService.updateFollowerCount(userToUnfollow, false);
    updateFollowerService.removeUserFromFollowingList(currentUserId, userToUnfollowId);
    updateFollowerService.updateFollowingCount(currentUser, false);
  }
}
