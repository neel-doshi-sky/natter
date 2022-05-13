package com.natter.enums.natter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessMessageEnum {

  CREATED_NEW_NATTER("10000", "Successfully created natter"),
  DELETED_NATTER("10001", "Successfully deleted natter");

  private final String code;
  private final String message;

}
