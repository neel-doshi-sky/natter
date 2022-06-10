package com.natter.enums.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ErrorMessageUserEnum {

  USER_NOT_FOUND("00011","user does not exist"),
  USER_ID_NULL("00012","user id cannot be null");



  private final String code;
  private final String message;
}
