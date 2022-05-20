package com.natter.enums.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessMessageUserEnum {

  FOLLOWED_USER("20000", "Successfully followed user"),
  UNFOLLOWED_USER("20001", "Successfully unfollowed user");

  private final String code;
  private final String message;

}
