package com.natter.service;

import com.natter.model.GoogleUserInfo;
import com.natter.model.User;
import com.natter.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {


  @Autowired
  private UserRepository userRepository;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);

    try {
      return processOidcUser(userRequest, oidcUser);
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
    GoogleUserInfo googleUserInfo = new GoogleUserInfo(oidcUser.getAttributes());

    // see what other data from userRequest or oidcUser you need

    Optional<User> userOptional = userRepository.findById(googleUserInfo.getId());
    if (userOptional.isEmpty()) {
      User user =
          new User(googleUserInfo.getId(), oidcUser.getGivenName(), oidcUser.getFamilyName(),
              oidcUser.getEmail(), new HashSet<>(), new HashSet<>(), LocalDateTime.now());


      // set other needed data

      userRepository.save(user);
    }

    return oidcUser;
  }
}
