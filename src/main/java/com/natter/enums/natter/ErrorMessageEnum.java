package com.natter.enums.natter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ErrorMessageEnum {

  NATTER_CREATION_ERROR_NULL_BODY("00001","null natter body passed"),
  EXCEEDED_CHAR_LIMIT("00010", "character limit exceeded, character limit for this field is: "),
  NULL_OR_EMPTY_FIELD("00011", "this field cannot be null/empty"),
  UNABLE_TO_SAVE_RECORD("50000", "unable to process your request");


  private final String errorCode;
  private final String message;
}
