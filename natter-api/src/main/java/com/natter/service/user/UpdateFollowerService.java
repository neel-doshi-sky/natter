package com.natter.service.user;

import com.datastax.oss.driver.api.core.CqlSession;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserFollowersFollowingRepository;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateFollowerService {

  private final CqlSession session;

  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  /**
   * Add user to follower list
   *
   * @param followerId     the id of the follower
   * @param userIdToFollow the id of the user to follow
   */
  public void addUserToFollowerList(@NonNull final String followerId,
                                    @NonNull final String userIdToFollow) {
    String query =
        String.format("UPDATE user SET followers = followers + {'%s'} WHERE id = '%s';", followerId,
            userIdToFollow);
    session.execute(query);
  }

  /**
   * Add user to following list
   *
   * @param followerId     the follower id
   * @param userIdToFollow the id of the user to follow
   */
  public void addUserToFollowingList(@NonNull final String followerId,
                                     @NonNull final String userIdToFollow) {
    String query = String.format("UPDATE user SET following = following + {'%s'} WHERE id = '%s';",
        userIdToFollow, followerId);
    session.execute(query);
  }

  /**
   * Remove follower from follower list
   *
   * @param followerId     the follower to remove
   * @param userIdToFollow the user to remove the follower for
   */
  public void removeUserFromFollowerList(@NonNull final String followerId,
                                         @NonNull final String userIdToFollow) {
    String query =
        String.format("UPDATE user SET followers = followers - {'%s'} WHERE id = '%s';", followerId,
            userIdToFollow);
    session.execute(query);
  }


  /**
   * Remove follower from following
   *
   * @param followerId     the follower to remove
   * @param userIdToFollow the user to remove the follower for
   */
  public void removeUserFromFollowingList(@NonNull final String followerId,
                                          @NonNull final String userIdToFollow) {
    String query = String.format("UPDATE user SET following = following - {'%s'} WHERE id = '%s';",
        userIdToFollow, followerId);
    session.execute(query);
  }

  /**
   * Method to increment or decrement the follower count for a given user
   *
   * @param user      the user to update
   * @param increment the operation, if true then will increment, else decrement
   */
  public void updateFollowerCount(UserFollowersFollowing user, boolean increment) {
    int newFollowerCount = increment ? user.getFollowers() + 1 : user.getFollowers() - 1;
    user.setFollowers(newFollowerCount);
    userFollowersFollowingRepository.save(user);

  }

  /**
   * Method to increment or decrement the following count for a given user
   *
   * @param user      the user to update
   * @param increment the operation, if true then will increment, else decrement
   */
  public void updateFollowingCount(UserFollowersFollowing user, boolean increment) {
    int newFollowingCount = increment ? user.getFollowing() + 1 : user.getFollowing() - 1;
    user.setFollowing(newFollowingCount);
    userFollowersFollowingRepository.save(user);
  }
  /**
   * Method to update relevant tables when following a user
   *
   * @param currentUserId  the id of the current user
   * @param userToFollowId the id of the user to follow
   */
  void followUser(String currentUserId, String userToFollowId) throws
      NoSuchElementException {
    UserFollowersFollowing currentUser = userFollowersFollowingRepository.findById(currentUserId).orElseThrow();
    UserFollowersFollowing userToFollow = userFollowersFollowingRepository.findById(userToFollowId).orElseThrow();
    addUserToFollowerList(currentUserId, userToFollowId);
    updateFollowerCount(userToFollow, true);
    addUserToFollowingList(currentUserId, userToFollowId);
    updateFollowingCount(currentUser, true);

  }

  /**
   * Method to update tables when unfollowing a user
   *
   * @param currentUserId    the id of the current user
   * @param userToUnfollowId the id of the user that is unfollowed
   */
  void unfollowUser(String currentUserId, String userToUnfollowId) {
    UserFollowersFollowing currentUser = userFollowersFollowingRepository.findById(currentUserId).orElseThrow();
    UserFollowersFollowing userToUnfollow = userFollowersFollowingRepository.findById(userToUnfollowId).orElseThrow();
    removeUserFromFollowerList(currentUserId, userToUnfollowId);
    updateFollowerCount(userToUnfollow, false);
    removeUserFromFollowingList(currentUserId, userToUnfollowId);
    updateFollowingCount(currentUser, false);
  }


}
