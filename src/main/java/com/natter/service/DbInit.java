package com.natter.service;

import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.user.User;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import com.natter.service.natter.NatterService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbInit {

  @Autowired
  NatterService natterService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserInfoRepository userInfoRepository;

  @PostConstruct
  private void postConstruct() {


    NatterCreateRequest natterCreateRequest = new NatterCreateRequest();
    natterCreateRequest.setParentNatterId(null);
    natterCreateRequest.setBody("Test natter 1");


    NatterCreateRequest natterCreateRequest1 = new NatterCreateRequest();
    natterCreateRequest1.setParentNatterId(null);
    natterCreateRequest1.setBody("Test natter 2");

    NatterCreateRequest natterCreateRequest2 = new NatterCreateRequest();
    natterCreateRequest2.setParentNatterId(null);
    natterCreateRequest2.setBody("Test natter 3");

    NatterCreateRequest natterCreateRequest3 = new NatterCreateRequest();
    natterCreateRequest3.setParentNatterId(null);
    natterCreateRequest3.setBody("Test natter 4");

    List<NatterCreateRequest> natterCreateRequests =
        List.of(natterCreateRequest1, natterCreateRequest2, natterCreateRequest3,
            natterCreateRequest);

    for (NatterCreateRequest request : natterCreateRequests) {
      natterService.create(request, "115826771724477311086");
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

  }
}
