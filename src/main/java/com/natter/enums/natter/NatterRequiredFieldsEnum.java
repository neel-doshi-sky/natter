package com.natter.enums.natter;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NatterRequiredFieldsEnum {

  BODY("body", false, 500L),
  AUTHOR_ID("authorId", false, null),
  PARENT_NATTER_ID("parentNatterId",true, null);


  private final String field;
  private final boolean nullable;
  private final Long characterLimit;
}
