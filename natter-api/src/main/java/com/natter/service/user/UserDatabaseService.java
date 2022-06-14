package com.natter.service.user;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.repository.user.UserFollowersFollowingRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
   * Get user ids for the authenticated user's following list
   *
   * @param userId the id of the authenticated user
   * @return list of user ids
   */
  public List<String> getFollowingForUser(@NonNull final String userId) {
    String query =
        String.format("select following from user where id='%s';", userId);
    ResultSet resultSet = session.execute(query);
    return new ArrayList<>(
        Objects.requireNonNull(resultSet.iterator().next().getSet("following", String.class)));

  }
}
