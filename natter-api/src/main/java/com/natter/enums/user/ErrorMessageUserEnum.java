package com.natter.enums.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ErrorMessageUserEnum {

  USER_NOT_FOUND("60011", "user does not exist"),
  USER_ID_NULL("60012", "user id cannot be null"),
  DATABASE_ERROR("60013", "unable to process your request");


  private final String code;
  private final String message;
}
