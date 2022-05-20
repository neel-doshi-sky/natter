package com.natter.service.user;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {
  private final CqlSession session;

  public void addUserToFollowerList(@NonNull final String followerId, @NonNull final  String userIdToFollow){
    String query = String.format("UPDATE user SET followers = followers + {'%s'} WHERE id = '%s';", followerId, userIdToFollow);
    session.execute(query);
  }

  public void addUserToFollowingList(@NonNull final String followerId, @NonNull final  String userIdToFollow){
    String query = String.format("UPDATE user SET following = following + {'%s'} WHERE id = '%s';", userIdToFollow, followerId);
    session.execute(query);
  }


  public void removeUserFromFollowerList(@NonNull final String followerId, @NonNull final String userIdToFollow){
    String query = String.format("UPDATE user SET followers = followers - {'%s'} WHERE id = '%s';", followerId, userIdToFollow);
    session.execute(query);
  };


  public void removeUserFromFollowingList(@NonNull final String followerId, @NonNull final String userIdToFollow){
    String query = String.format("UPDATE user SET following = following - {'%s'} WHERE id = '%s';", userIdToFollow, followerId);
    session.execute(query);
  };

}
