package com.natter;

import com.natter.model.GoogleUserInfo;
import com.natter.model.user.User;
import com.natter.model.user.UserFollowersFollowing;
import com.natter.model.user.UserInfo;
import com.natter.repository.user.UserFollowersFollowingRepository;
import com.natter.repository.user.UserInfoRepository;
import com.natter.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;

  private final UserInfoRepository userInfoRepository;

  private final UserFollowersFollowingRepository userFollowersFollowingRepository;

  /**
   * Override existing load user functionality to add user to database if not exists
   *
   * @param userRequest the user request
   * @return the authenticated user
   * @throws OAuth2AuthenticationException the OAuth2AuthenticationException exception
   */
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    try {
      return processOauth2User(oAuth2User);
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }

  }

  /**
   * Once the user is authenticated via oauth, check if they exist in the database or create a new account
   *
   * @param oAuth2User the oauth user
   * @return the processed user
   */
  private OAuth2User processOauth2User(@NonNull final OAuth2User oAuth2User) {
    GoogleUserInfo googleUserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

    Optional<User> userOptional = userRepository.findById(googleUserInfo.getId());
    if (userOptional.isEmpty()) {


      String givenName = oAuth2User.getAttributes().get("given_name").toString();

      String familyName = oAuth2User.getAttributes().get("family_name").toString();

      User user =
          new User(googleUserInfo.getId(), givenName, familyName,
              googleUserInfo.getEmail(), new HashSet<>(), new HashSet<>(), LocalDateTime.now());
      UserInfo userInfo =
          new UserInfo(googleUserInfo.getId(), givenName, familyName,
              googleUserInfo.getEmail());

      UserFollowersFollowing userFollowersFollowing = new UserFollowersFollowing(googleUserInfo.getId(), givenName, familyName, 0, 0);
      userRepository.save(user);
      userInfoRepository.save(userInfo);
      userFollowersFollowingRepository.save(userFollowersFollowing);

    }
    return oAuth2User;
  }
}
