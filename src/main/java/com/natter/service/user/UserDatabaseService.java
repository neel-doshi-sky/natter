package com.natter.service.user;

import com.natter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDatabaseService {

  private final UserRepository userRepository;

  void updateFollowersForFollow(String authorId, String userToFollowId){
    userRepository.addUserToFollowerList(userToFollowId, authorId);
    userRepository.addUserToFollowingList(userToFollowId, authorId);

  }

  void updateFollowersForUnfollow(String authorId, String userToFollowId){
    userRepository.removeUserFromFollowerList(userToFollowId, authorId);
    userRepository.removeUserFromFollowingList(userToFollowId, authorId);
  }
}
