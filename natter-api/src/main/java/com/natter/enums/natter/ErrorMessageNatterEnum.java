package com.natter.enums.natter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ErrorMessageNatterEnum {

  NULL_BODY("00001","null natter body passed"),
  NULL_ID("OOOO2", "natter id cannot be null"),
  EXCEEDED_CHAR_LIMIT("00010", "character limit exceeded, character limit for this field is: "),
  NULL_OR_EMPTY_FIELD("00011", "this field cannot be null/empty"),
  DATABASE_ERROR("50000", "unable to process your request"),
  UNABLE_TO_DELETE_RECORD("50001", "unable to delete record"),
  UNAUTHORISED_ACCESS("50003", "unauthorised access to this natter or natter does not exist"),
  RECORD_NOT_FOUND("50004", "record not found");


  private final String code;
  private final String message;
}
