package com.natter.service;

import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthServiceTest {

  AuthService authService = new AuthService();

  @Test
  void getUserIdFromAuth() {
    OAuth2User oAuth2User = new DefaultOAuth2User(new ArrayList<>(), Map.of("sub", "123", "test", "test"), "test");
    String id = authService.getUserIdFromAuth(oAuth2User);
    Assertions.assertAll(
        () -> Assertions.assertNotNull(id),
        () -> Assertions.assertEquals("123", id)
    );
  }
}