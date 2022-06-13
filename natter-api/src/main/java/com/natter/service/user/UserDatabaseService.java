package com.natter.service.user;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserFollowersFollowingRepository;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDatabaseService {

  private final UpdateFollowerService updateFollowerService;

  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  private final CqlSession session;



  /**
   * Add user to follower list
   *
   * @param userId     the id of the follower
   */
  public Set<String> getFollowingForUser(@NonNull final String userId) {
    String query =
        String.format("select following from user where id='%s';", userId);
    Set<String> users;
    users = session.execute(query).iterator().next().getSet("following", String.class);
    return users;
  }
}
