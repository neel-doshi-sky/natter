package com.natter.enums.natter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ErrorMessageNatterEnum {

  NATTER_CREATION_ERROR_NULL_BODY("00001","null natter body passed"),
  NATTER_NULL_ID("OOOO2", "natter id cannot be null"),
  EXCEEDED_CHAR_LIMIT("00010", "character limit exceeded, character limit for this field is: "),
  NULL_OR_EMPTY_FIELD("00011", "this field cannot be null/empty"),
  UNABLE_TO_SAVE_RECORD("50000", "unable to process your request"),
  UNABLE_TO_DELETE_RECORD("50001", "unable to delete record"),
  UNAUTHORISED_ACCESS_NATTER("50003", "unauthorised access to this natter or natter does not exist");


  private final String code;
  private final String message;
}
