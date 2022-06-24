package com.natter.dao;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDao {

  private final CqlSession session;


  /**
   * Get user ids for the authenticated user's following list
   *
   * @param userId the id of the authenticated user
   * @return list of user ids
   */
  public List<String> getFollowingIdsForUser(@NonNull final String userId) {
    String query =
        String.format("select following from user where id='%s';", userId);
    ResultSet resultSet = session.execute(query);
    return new ArrayList<>(
        Objects.requireNonNull(resultSet.iterator().next().getSet("following", String.class)));

  }

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

}
