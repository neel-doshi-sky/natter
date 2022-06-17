package com.natter;

import com.natter.model.natter.NatterCreateRequest;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserFollowersFollowingRepository;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import com.natter.service.natter.NatterService;
import com.natter.service.user.UserService;
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
public class InitialiseDatabase {

  @Autowired
  NatterService natterService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserInfoRepository userInfoRepository;

  @Autowired
  UserFollowersFollowingRepository userFollowersFollowingRepository;

  @Autowired
  UserService userService;

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

    OAuth2User neel = new DefaultOAuth2User(new ArrayList<>(), Map.of("sub","115826771724477311086", "name", "Neel Doshi"), "name");

    for (NatterCreateRequest request : natterCreateRequests) {
      natterService.create(request, neel);
    }

    User neelUser =
        new User(neel.getAttribute("sub"), "Neel", "Doshi", "neel.doshi.sky@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());

    User fred =
        new User("1", "Fred", "Bloggs", "fb@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    User julie =
        new User("2", "Julie", "Hammond", "jh@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    User user2 =
        new User("3", "test", "user3", "test_user3@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    User user3 =
        new User("4", "test", "user4", "test_user4@gmail.com", new HashSet<>(), new HashSet<>(),
            LocalDateTime.now());
    userRepository.saveAll(List.of(neelUser, fred, julie, user2, user3));

    UserInfo neelUserInfo =
        new UserInfo(neelUser.getId(), neelUser.getFirstName(), neelUser.getLastName(), neelUser.getEmail());

    UserInfo userInfo =
        new UserInfo(fred.getId(), fred.getFirstName(), fred.getLastName(), fred.getEmail());
    UserInfo userInfo2 =
        new UserInfo(julie.getId(), julie.getFirstName(), julie.getLastName(), julie.getEmail());
    UserInfo userInfo3 =
        new UserInfo(user2.getId(), user2.getFirstName(), user2.getLastName(), user2.getEmail());
    UserInfo userInfo4 =
        new UserInfo(user3.getId(), user3.getFirstName(), user3.getLastName(), user3.getEmail());

    userInfoRepository.saveAll(List.of(neelUserInfo, userInfo, userInfo2, userInfo3, userInfo4));

    UserFollowersFollowing neelFollowersFollowing =
        new UserFollowersFollowing(neelUser.getId(), neelUser.getFirstName(), neelUser.getLastName(), 0, 0, neelUser.getEmail());

    UserFollowersFollowing userFollowersFollowing =
        new UserFollowersFollowing(fred.getId(), fred.getFirstName(), fred.getLastName(), 0, 0, fred.getEmail());
    UserFollowersFollowing userFollowersFollowing1 =
        new UserFollowersFollowing(julie.getId(), julie.getFirstName(), julie.getLastName(), 0, 0, julie.getEmail());
    UserFollowersFollowing userFollowersFollowing2 =
        new UserFollowersFollowing(user2.getId(), user2.getFirstName(), user2.getLastName(), 0, 0, user2.getEmail());
    UserFollowersFollowing userFollowersFollowing3 =
        new UserFollowersFollowing(user3.getId(), user3.getFirstName(), user3.getLastName(), 0, 0, user3.getEmail());

    userFollowersFollowingRepository.saveAll(List.of(neelFollowersFollowing, userFollowersFollowing, userFollowersFollowing1, userFollowersFollowing2, userFollowersFollowing3));

    NatterCreateRequest natterCreateRequest5 = new NatterCreateRequest();
    natterCreateRequest5.setParentNatterId(null);
    natterCreateRequest5.setBody("What a day to be alive!");


    NatterCreateRequest natterCreateRequest6 = new NatterCreateRequest();
    natterCreateRequest6.setParentNatterId(null);
    natterCreateRequest6.setBody("Ever thought about why someone decided to try cows milk and thought yeah thats nice?");

    NatterCreateRequest natterCreateRequest7 = new NatterCreateRequest();
    natterCreateRequest7.setParentNatterId(null);
    natterCreateRequest7.setBody("ENGLANDDDDDD!!!");

    NatterCreateRequest natterCreateRequest8 = new NatterCreateRequest();
    natterCreateRequest8.setParentNatterId(null);
    natterCreateRequest8.setBody("Liverpool need to win a CL or PL next season! Up the Reds!");

    List<NatterCreateRequest> natterCreateRequests2 =
        List.of(natterCreateRequest7, natterCreateRequest8);


    OAuth2User fredOauth= new DefaultOAuth2User(new ArrayList<>(), Map.of("sub",fred.getId(), "name", fred.getFirstName() + " " + fred.getLastName()), "name");

    for (NatterCreateRequest request : natterCreateRequests2) {
      natterService.create(request, fredOauth);
    }
    List<NatterCreateRequest> natterCreateRequests3 =
        List.of(natterCreateRequest5, natterCreateRequest6);

    OAuth2User julieOath= new DefaultOAuth2User(new ArrayList<>(), Map.of("sub",julie.getId(), "name", julie.getFirstName() + " " + julie.getLastName()), "name");
    for (NatterCreateRequest request : natterCreateRequests3) {
      natterService.create(request, julieOath);
    }

    userService.followOrUnfollowUserById(neel.getAttribute("sub"), julie.getId(), true);
    userService.followOrUnfollowUserById(fred.getId(), neel.getAttribute("sub"), true);
    userService.followOrUnfollowUserById(julie.getId(), neel.getAttribute("sub"), true);
    userService.followOrUnfollowUserById(julie.getId(), fredOauth.getAttribute("sub"), true);


  }
}
