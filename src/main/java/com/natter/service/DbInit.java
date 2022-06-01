package com.natter.service;

import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserFollowersFollowingRepository;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import com.natter.service.natter.NatterService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class DbInit {

  @Autowired
  NatterService natterService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserInfoRepository userInfoRepository;

  @Autowired
  UserFollowersFollowingRepository userFollowersFollowingRepository;

  @PostConstruct
  private void postConstruct() {


    NatterCreateRequest natterCreateRequest = new NatterCreateRequest();
    natterCreateRequest.setParentNatterId(null);
    natterCreateRequest.setBody("Cannot believe house prices are this crazy!");


    NatterCreateRequest natterCreateRequest1 = new NatterCreateRequest();
    natterCreateRequest1.setParentNatterId(null);
    natterCreateRequest1.setBody("Fancy some lunch but not sure what to have, any suggestions?");

    NatterCreateRequest natterCreateRequest2 = new NatterCreateRequest();
    natterCreateRequest2.setParentNatterId(null);
    natterCreateRequest2.setBody("Why does it always rain in this country!!!");

    NatterCreateRequest natterCreateRequest3 = new NatterCreateRequest();
    natterCreateRequest3.setParentNatterId(null);
    natterCreateRequest3.setBody("Dr Strange was so over-hyped man, the film was deadddd");

    List<NatterCreateRequest> natterCreateRequests =
        List.of(natterCreateRequest1, natterCreateRequest2, natterCreateRequest3,
            natterCreateRequest);

    OAuth2User oAuth2User = new DefaultOAuth2User(new ArrayList<>(), Map.of("sub","115826771724477311086", "name", "Neel Doshi"), "name");

    for (NatterCreateRequest request : natterCreateRequests) {
      natterService.create(request, oAuth2User);
    }

    User user =
        new User("1", "test", "user1", "test_user1@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    User user1 =
        new User("2", "test", "user2", "test_user2@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    User user2 =
        new User("3", "test", "user3", "test_user3@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    User user3 =
        new User("4", "test", "user4", "test_user4@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    userRepository.saveAll(List.of(user, user1, user2, user3));

    UserInfo userInfo =
        new UserInfo(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    UserInfo userInfo2 =
        new UserInfo(user1.getId(), user1.getFirstName(), user1.getLastName(), user1.getEmail());
    UserInfo userInfo3 =
        new UserInfo(user2.getId(), user2.getFirstName(), user2.getLastName(), user2.getEmail());
    UserInfo userInfo4 =
        new UserInfo(user3.getId(), user3.getFirstName(), user3.getLastName(), user3.getEmail());

    userInfoRepository.saveAll(List.of(userInfo, userInfo2, userInfo3, userInfo4));

    UserFollowersFollowing userFollowersFollowing =
        new UserFollowersFollowing(user.getId(), user.getFirstName(), user.getLastName(), 0, 0, user.getEmail());
    UserFollowersFollowing userFollowersFollowing1 =
        new UserFollowersFollowing(user1.getId(), user1.getFirstName(), user1.getLastName(), 0, 0, user1.getEmail());
    UserFollowersFollowing userFollowersFollowing2 =
        new UserFollowersFollowing(user2.getId(), user2.getFirstName(), user2.getLastName(), 0, 0, user2.getEmail());
    UserFollowersFollowing userFollowersFollowing3 =
        new UserFollowersFollowing(user3.getId(), user3.getFirstName(), user3.getLastName(), 0, 0, user3.getEmail());

    userFollowersFollowingRepository.saveAll(List.of(userFollowersFollowing, userFollowersFollowing1, userFollowersFollowing2, userFollowersFollowing3));

  }
}
