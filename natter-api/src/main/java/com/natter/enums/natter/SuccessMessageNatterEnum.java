package com.natter.enums.natter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessMessageNatterEnum {

  CREATED_NEW_NATTER("10000", "Successfully created natter"),
  DELETED_NATTER("10001", "Successfully deleted natter"),
  FETCHED_NATTERS_BY_AUTHOR("10002", "Successfully fetched natters by author"),
  UPDATED_NATTER("10003", "Successfully updated natter"),
  CREATED_COMMENT("10004", "Successfully added comment"),
  FETCHED_NATTER_BY_ID("10005", "Successfully fetched natter by id"),
  FETCHED_All_NATTERS("10006", "Successfully fetched all natters"),
  REACT_SUCCESS("10007", "Successfully reacted to natter");

  private final String code;
  private final String message;

}
