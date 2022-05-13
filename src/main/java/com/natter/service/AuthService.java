package com.natter.service;

import com.natter.model.GoogleUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  /**
   * Method to extract user id from oauth user
   *
   * @param user the oauth user
   * @return the id of the authenticated user
   */
  public String getUserIdFromAuth(OAuth2User user) {
    GoogleUserInfo googleUserInfo = new GoogleUserInfo(user.getAttributes());
    return googleUserInfo.getId();
  }
}
