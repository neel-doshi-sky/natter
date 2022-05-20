package com.natter.service.user;

import com.natter.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDatabaseService {

  private final FollowService followService;

  void updateFollowersForFollow(String authorId, String userToFollowId){
    followService.addUserToFollowerList(authorId, userToFollowId);
    followService.addUserToFollowingList(authorId, userToFollowId);

  }

  void updateFollowersForUnfollow(String authorId, String userToUnfollowId){
    followService.removeUserFromFollowerList(authorId, userToUnfollowId);
    followService.removeUserFromFollowingList(authorId, userToUnfollowId);
  }
}
