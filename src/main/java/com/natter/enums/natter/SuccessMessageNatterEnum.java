package com.natter.enums.natter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessMessageNatterEnum {

  CREATED_NEW_NATTER("10000", "Successfully created natter"),
  DELETED_NATTER("10001", "Successfully deleted natter"),
  FETCHED_NATTERS_BY_AUTHOR("10002", "Successfully fetched natters by author"),
  UPDATED_NATTER("10003", "Successfully updated natter");

  private final String code;
  private final String message;

}
